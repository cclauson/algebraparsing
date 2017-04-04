package algebraparsing;

import static algebraparsing.RegularExpression.*;

import java.util.*;

public class Grammar {

	private Nonterminal startSymbol;
	private List<Production> productions;
	
	public Grammar(Nonterminal startSymbol, List<Production> productions) {
		if (startSymbol == null)
			throw new RuntimeException("start symbol must not be null");
		
		if (productions == null)
			throw new RuntimeException("production list must not be null");			
		
		this.startSymbol = startSymbol;
		this.productions = productions;
		
		//let's make sure that every nonterminal that occurs has at least one rule
		
		final Set<Nonterminal> lhss = new HashSet<Nonterminal>();
		//populate set with all production left hand sides
		for (Production production : this.productions) {
			lhss.add(production.nonterminal);
		}
		
		//start symbol must have at least one production
		if (!lhss.contains(startSymbol))
			throw new RuntimeException("start symbol must have at least one production, but doesn't");
		
		for (Production production : this.productions) {
			for (TerminalOrNonterminal ton : production.rhs) {
				if (ton instanceof Nonterminal) {
					Nonterminal nonterminal = (Nonterminal) ton;
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
		for (Production production : this.productions) {
			sb.append(production + "\n");
		}
		return sb.toString();
	}
	
	public MatrixVectorGrammar asAffineEndomorphism() {
		final Map<Nonterminal, Integer> nonterminalIndexMap = new HashMap<Nonterminal, Integer>();
		nonterminalIndexMap.put(startSymbol, 0);
		int numNonterminals = 1;
		for (Production production : this.productions) {
			if (!nonterminalIndexMap.containsKey(production.nonterminal)) {
				nonterminalIndexMap.put(production.nonterminal, numNonterminals);
				++numNonterminals;
			}
		}
		
		final List<List<RegularExpression<TerminalOrNonterminal>>> matrixDat =
				new ArrayList<List<RegularExpression<TerminalOrNonterminal>>>();

		for (int i = 0; i < numNonterminals; ++i) {
			final List<RegularExpression<TerminalOrNonterminal>> row =
					new ArrayList<RegularExpression<TerminalOrNonterminal>>();
			for (int j = 0; j < numNonterminals; ++j) {
				row.add(emptyRegexp());
			}
			matrixDat.add(row);
		}
		
		final List<List<RegularExpression<TerminalOrNonterminal>>> vectorDat =
				new ArrayList<List<RegularExpression<TerminalOrNonterminal>>>();
		
		for (int i = 0; i < numNonterminals; ++i) {
			final List<RegularExpression<TerminalOrNonterminal>> row =
					new ArrayList<RegularExpression<TerminalOrNonterminal>>();
			row.add(emptyRegexp());
			vectorDat.add(row);
		}
		
		for (Production production : this.productions) {
			
			if (production.rhs.isEmpty())
				//we need to fix this later...
				throw new RuntimeException("empty productions currently not handled");
			
			TerminalOrNonterminal rhsFirst = production.rhs.get(0);
			
			//treatment is different depending on whether the production begins
			//with a terminal or nonterminal
			Nonterminal initialNonterminal;
			List<TerminalOrNonterminal> tailOrWhole;
			if (rhsFirst instanceof Nonterminal) {
				initialNonterminal = (Nonterminal)rhsFirst;
				//in this case the regular expression term is derived from
				//the rhs minus the nonterminal at the beginning
				tailOrWhole = new ArrayList<TerminalOrNonterminal>(production.rhs);
				tailOrWhole.remove(0);
			} else {
				initialNonterminal = null;
				tailOrWhole = production.rhs;
			}
			
			//we turn the right hand side of the production into a regular expression
			RegularExpression<TerminalOrNonterminal> rhs = emptyString();
			for (TerminalOrNonterminal ton : tailOrWhole) {
				rhs = rhs.mul(RegularExpression.fromAtom(ton));
			}

			//find destination index to add regexp into
			int i = nonterminalIndexMap.get(production.nonterminal);
			
			if (initialNonterminal == null) {
				//add into vector
				final List<RegularExpression<TerminalOrNonterminal>> row = vectorDat.get(i);
				row.set(0, row.get(0).add(rhs));
			} else{
				//add into matrix
				int j = nonterminalIndexMap.get(initialNonterminal);
				final List<RegularExpression<TerminalOrNonterminal>> row = matrixDat.get(i);
				row.set(j, row.get(j).add(rhs));
			}
		}
		
		return new MatrixVectorGrammar(
				nonterminalIndexMap,
				new KleeneMatrix<RegularExpression<TerminalOrNonterminal>>(matrixDat, RegularExpression.emptyRegexp(), RegularExpression.emptyString()),
				new KleeneMatrix<RegularExpression<TerminalOrNonterminal>>(vectorDat, RegularExpression.emptyRegexp(), RegularExpression.emptyString())
		);
	}
	
}
