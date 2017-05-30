package algebraparsing;

import static algebraparsing.KleeneAlgebra.RegularExpression.*;

import java.util.*;

import algebraparsing.KleeneAlgebra.KleeneMatrix;
import algebraparsing.KleeneAlgebra.RegularExpression;

public class Grammar<T, S> {

	private Nonterminal startSymbol;
	private List<Production<T>> productions;
	private final TerminalConsolidator<S, T> tc;

	public static interface TerminalConsolidator<S, T> {
		public void consolidateTerminal(T terminal);
		public RegularExpression<TerminalOrNonterminal<S>> asRegexpAndReset();
	}
	
	public Nonterminal startSymbol() {
		return startSymbol;
	}
	
	public Grammar(Nonterminal startSymbol, List<Production<T>> productions, TerminalConsolidator<S, T> tc) {
		if (startSymbol == null)
			throw new RuntimeException("start symbol must not be null");
		
		if (productions == null)
			throw new RuntimeException("production list must not be null");			
		
		if (tc == null)
			throw new RuntimeException("terminal consolidator must not be null");			
		
		this.startSymbol = startSymbol;
		this.productions = productions;
		this.tc = tc;
		
		//let's make sure that every nonterminal that occurs has at least one rule
		
		final Set<Nonterminal> lhss = new HashSet<Nonterminal>();
		//populate set with all production left hand sides
		for (Production<T> production : this.productions) {
			lhss.add(production.nonterminal);
		}
		
		//start symbol must have at least one production
		if (!lhss.contains(startSymbol))
			throw new RuntimeException("start symbol must have at least one production, but doesn't");
		
		for (Production<T> production : this.productions) {
			for (TerminalOrNonterminal<T> ton : production.rhs) {
				if (!ton.isTerminal()) {
					Nonterminal nonterminal = ton.asNonterminal();
					if (!lhss.contains(nonterminal))
						throw new RuntimeException("found nonterminal symbol " + nonterminal +
								", but this doesn't have a production");
				}
			}
		}
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.startSymbol + ":\n");
		for (Production<T> production : this.productions) {
			sb.append(production + "\n");
		}
		return sb.toString();
	}
	
	public MatrixVectorGrammar<S> asAffineEndomorphism() {
		
		final Map<Nonterminal, Integer> nonterminalIndexMap = new HashMap<Nonterminal, Integer>();
		nonterminalIndexMap.put(startSymbol, 0);
		int numNonterminals = 1;
		for (Production<T> production : this.productions) {
			if (!nonterminalIndexMap.containsKey(production.nonterminal)) {
				nonterminalIndexMap.put(production.nonterminal, numNonterminals);
				++numNonterminals;
			}
		}
		
		final List<List<RegularExpression<TerminalOrNonterminal<S>>>> matrixDat =
				new ArrayList<List<RegularExpression<TerminalOrNonterminal<S>>>>();

		for (int i = 0; i < numNonterminals; ++i) {
			final List<RegularExpression<TerminalOrNonterminal<S>>> row =
					new ArrayList<RegularExpression<TerminalOrNonterminal<S>>>();
			for (int j = 0; j < numNonterminals; ++j) {
				row.add(emptyRegexp());
			}
			matrixDat.add(row);
		}
		
		final List<List<RegularExpression<TerminalOrNonterminal<S>>>> vectorDat =
				new ArrayList<List<RegularExpression<TerminalOrNonterminal<S>>>>();
		
		for (int i = 0; i < numNonterminals; ++i) {
			final List<RegularExpression<TerminalOrNonterminal<S>>> row =
					new ArrayList<RegularExpression<TerminalOrNonterminal<S>>>();
			row.add(emptyRegexp());
			vectorDat.add(row);
		}
		
		for (Production<T> production : this.productions) {
			
			if (production.rhs.isEmpty())
				//we need to fix this later...
				throw new RuntimeException("empty productions currently not handled");
			
			// NOTE: Above condition should be extended to exclude productions that
			// are non-input consuming as well...
			
			TerminalOrNonterminal<T> rhsFirst = production.rhs.get(0);
			
			//treatment is different depending on whether the production begins
			//with a terminal or nonterminal
			
			// NOTE: This condition could potentially change in the future too.
			// There could potentially be terminals that don't consume input,
			// so we would be interested in whether any input was consumed before
			// the first nonterminal, as opposed to just checking whether the
			// first symbol is nonterminal
			
			Nonterminal initialNonterminal;
			List<TerminalOrNonterminal<T>> tailOrWhole;
			if (rhsFirst.isTerminal()) {
				initialNonterminal = null;
				tailOrWhole = production.rhs;
			} else {
				initialNonterminal = rhsFirst.asNonterminal();
				//in this case the regular expression term is derived from
				//the rhs minus the nonterminal at the beginning
				tailOrWhole = new ArrayList<TerminalOrNonterminal<T>>(production.rhs);
				tailOrWhole.remove(0);
			}
			
			//we turn the right hand side of the production into a regular expression
			RegularExpression<TerminalOrNonterminal<S>> rhs = emptyString();
			tc.asRegexpAndReset();
			for (TerminalOrNonterminal<T> ton : tailOrWhole) {
				if (ton.isTerminal()) {
					tc.consolidateTerminal(ton.asTerminal());
				} else {
					// the same nonterminal, but of type TerminalOrNonterminal<S>
					TerminalOrNonterminal<S> tonprime = TerminalOrNonterminal.fromNonterminal(ton.asNonterminal());
					rhs = rhs.mul(tc.asRegexpAndReset().mul(RegularExpression.fromAtom(tonprime)));
				}
			}
			rhs = rhs.mul(tc.asRegexpAndReset());

			//find destination index to add regexp into
			int i = nonterminalIndexMap.get(production.nonterminal);
			
			if (initialNonterminal == null) {
				//add into vector
				final List<RegularExpression<TerminalOrNonterminal<S>>> row = vectorDat.get(i);
				row.set(0, row.get(0).add(rhs));
			} else {
				//add into matrix
				int j = nonterminalIndexMap.get(initialNonterminal);
				final List<RegularExpression<TerminalOrNonterminal<S>>> row = matrixDat.get(i);
				row.set(j, row.get(j).add(rhs));
			}
		}
		
		return new MatrixVectorGrammar<S>(
				nonterminalIndexMap,
				new KleeneMatrix<RegularExpression<TerminalOrNonterminal<S>>>(
						matrixDat, RegularExpression.emptyRegexp(), RegularExpression.emptyString()),
				new KleeneMatrix<RegularExpression<TerminalOrNonterminal<S>>>(
						vectorDat, RegularExpression.emptyRegexp(), RegularExpression.emptyString())
		);
	}
}
