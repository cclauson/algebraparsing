package algebraparsing;

import java.util.*;
import java.util.function.Function;

import algebraparsing.Grammar.TerminalConsolidator;

public class TestMain {

	private static final TerminalConsolidator<Terminal, Terminal> TC = new TerminalConsolidator<Terminal, Terminal>() {
		
		private RegularExpression<TerminalOrNonterminal<Terminal>> re = RegularExpression.emptyString();
		
		@Override
		public void consolidateTerminal(Terminal terminal) {
			re = re.mul(RegularExpression.fromAtom(TerminalOrNonterminal.fromTerminal(terminal)));
		}

		@Override
		public RegularExpression<TerminalOrNonterminal<Terminal>> asRegexpAndReset() {
			RegularExpression<TerminalOrNonterminal<Terminal>> ret = re;
			re = RegularExpression.emptyString();
			return ret;
		}
	};

	
	private static TerminalOrNonterminal<Terminal> nonterminal(char c) {
		return TerminalOrNonterminal.fromNonterminal(new Nonterminal(c));
	}

	private static TerminalOrNonterminal<Terminal> terminal(char c) {
		return TerminalOrNonterminal.fromTerminal(new Terminal(c));
	}

	private static TerminalOrNonterminal<Terminal> terminal(String s) {
		return TerminalOrNonterminal.fromTerminal(new Terminal(s));
	}

	private static Grammar<Terminal, Terminal> grammar1() {
		//For the grammar:
		// Z -> E $
		// E -> E '+' T
		// E -> T
		// T -> '(' E ')'
		// T -> 'i'
		
		Nonterminal startSymbol = new Nonterminal('Z');
		// Z -> E $
		Production<Terminal> p1 = new Production<Terminal>(new Nonterminal('Z'), Arrays.asList(
			nonterminal('E'), terminal('$')
		));
		// E -> E '+' T
		Production<Terminal> p2 = new Production<Terminal>(new Nonterminal('E'), Arrays.asList(
			nonterminal('E'), terminal('+'), nonterminal('T')
		));
		// E -> T
		Production<Terminal> p3 = new Production<Terminal>(new Nonterminal('E'), Arrays.asList(
			nonterminal('T')
		));
		// T -> '(' E ')'
		Production<Terminal> p4 = new Production<Terminal>(new Nonterminal('T'), Arrays.asList(
			terminal('('), nonterminal('E'), terminal(')')
		));
		// T -> 'i'
		Production<Terminal> p5 = new Production<Terminal>(new Nonterminal('T'), Arrays.asList(
			terminal('i')
		));
		
		return new Grammar<Terminal, Terminal>(startSymbol, Arrays.asList(p1, p2, p3, p4, p5), TC);		
	}
	
	private static Grammar<Terminal, Terminal> grammar2() {
		//For the grammar:
		// S -> A
		// S -> 'x' 'b'
		// A -> 'a' A 'b'
		// A -> B
		// B -> 'x'
		
		Nonterminal startSymbol = new Nonterminal('S');
		// S -> A
		Production<Terminal> p1 = new Production<Terminal>(new Nonterminal('S'), Arrays.asList(
			nonterminal('A')
		));
		// S -> 'x' 'b'
		Production<Terminal> p2 = new Production<Terminal>(new Nonterminal('S'), Arrays.asList(
			terminal('x'), terminal('b')
		));
		// A -> 'a' A 'b'
		Production<Terminal> p3 = new Production<Terminal>(new Nonterminal('A'), Arrays.asList(
			terminal('a'), nonterminal('A'), terminal('b')
		));
		// A -> B
		Production<Terminal> p4 = new Production<Terminal>(new Nonterminal('A'), Arrays.asList(
			nonterminal('B')
		));
		// B -> 'x'
		Production<Terminal> p5 = new Production<Terminal>(new Nonterminal('B'), Arrays.asList(
			terminal('x')
		));
		
		return new Grammar<Terminal, Terminal>(startSymbol, Arrays.asList(p1, p2, p3, p4, p5), TC);		
	}
	
	private static Grammar<Terminal, Terminal> logicExpression() {
		//For the grammar:
		// A -> B '$'
		// B -> 'for all' H B
		// B -> 'exists' H 'such that' B
		// B -> C
		// C -> C iff D
		// C -> D
		// D -> 'if' B 'then' D
		// D -> E 'implies' D
		// D -> E
		// E -> E 'or' F
		// E -> F
		// F -> F 'and' G
		// F -> G
		// G -> not G
		// G -> H
		// G -> '(' B ')'
		// H -> 'sym'
		
		Nonterminal startSymbol = new Nonterminal('A');
		List<Production<Terminal>> productions = new ArrayList<Production<Terminal>>();
		// A -> B '$'
		productions.add(
			new Production<Terminal>(new Nonterminal('A'), Arrays.asList(
				nonterminal('B'), terminal('$')
			))
		);
		// B -> 'for all' H B
		productions.add(
			new Production<Terminal>(new Nonterminal('B'), Arrays.asList(
				terminal("for all"), nonterminal('H'), nonterminal('B')
			))
		);
		// B -> 'exists' H 'such that' B
		productions.add(
			new Production<Terminal>(new Nonterminal('B'), Arrays.asList(
				terminal("exists"), nonterminal('H'),
				terminal("such that"), nonterminal('B')
			))
		);
		// B -> C
		productions.add(
			new Production<Terminal>(new Nonterminal('B'), Arrays.asList(
				nonterminal('C')
			))
		);
		// C -> C iff D
		productions.add(
			new Production<Terminal>(new Nonterminal('C'), Arrays.asList(
				nonterminal('C'), terminal("iff"), nonterminal('D')
			))
		);
		// C -> D
		productions.add(
			new Production<Terminal>(new Nonterminal('C'), Arrays.asList(
				nonterminal('D')
			))
		);
			// D -> 'if' B 'then' D
		productions.add(
			new Production<Terminal>(new Nonterminal('D'), Arrays.asList(
				terminal("if"), nonterminal('B'),
				terminal("then"), nonterminal('D')
			))
		);
		// D -> E 'implies' D
		productions.add(
			new Production<Terminal>(new Nonterminal('D'), Arrays.asList(
				nonterminal('E'), terminal("implies"), nonterminal('D')
			))
		);
		// D -> E
		productions.add(
			new Production<Terminal>(new Nonterminal('D'), Arrays.asList(
				nonterminal('E')
			))
		);
		// E -> E 'or' F
		productions.add(
			new Production<Terminal>(new Nonterminal('E'), Arrays.asList(
				nonterminal('E'), terminal("or"), nonterminal('F')
			))
		);
		// E -> F
		productions.add(
			new Production<Terminal>(new Nonterminal('E'), Arrays.asList(
				nonterminal('F')
			))
		);
		// F -> F 'and' G
		productions.add(
			new Production<Terminal>(new Nonterminal('F'), Arrays.asList(
				nonterminal('F'), terminal("and"), nonterminal('G')
			))
		);
		// F -> G
		productions.add(
			new Production<Terminal>(new Nonterminal('F'), Arrays.asList(
				nonterminal('G')
			))
		);
		// G -> not G
		productions.add(
			new Production<Terminal>(new Nonterminal('G'), Arrays.asList(
				terminal("not"), nonterminal('G')
			))
		);
		// G -> H
		productions.add(
			new Production<Terminal>(new Nonterminal('G'), Arrays.asList(
				nonterminal('H')
			))
		);
		// G -> '(' B ')'
		productions.add(
			new Production<Terminal>(new Nonterminal('G'), Arrays.asList(
				terminal("("), nonterminal('B'), terminal(")")
			))
		);
		// H -> 'sym'
		productions.add(
			new Production<Terminal>(new Nonterminal('H'), Arrays.asList(
				terminal("sym")
			))
		);
		return new Grammar<Terminal, Terminal>(startSymbol, productions, TC);		
	}

	public static void main(String[] args) {
		
		//Grammar grammar = grammar2();
		Grammar<Terminal, Terminal> grammar = logicExpression();
		System.out.println(grammar.toString());
		
		System.out.println();
		
		MatrixVectorGrammar<Terminal> mvg = grammar.asAffineEndomorphism();
		System.out.println(mvg.matrix);
		System.out.println();
		System.out.println(mvg.vector);
		
		System.out.println();
		
		Function<RegularExpression<TerminalOrNonterminal<Terminal>>, RegularExpression<TerminalOrNonterminal<Terminal>>> reversal =
				new Function<RegularExpression<TerminalOrNonterminal<Terminal>>, RegularExpression<TerminalOrNonterminal<Terminal>>>() {
			@Override
			public RegularExpression<TerminalOrNonterminal<Terminal>> apply(RegularExpression<TerminalOrNonterminal<Terminal>> t) {
				return RegularExpression.reversal(t);
			}
		};
		
		KleeneMatrix<RegularExpression<TerminalOrNonterminal<Terminal>>> m =
				mvg.matrix.projectionThroughMorphism(reversal);
		
		System.out.println(m);
		System.out.println(m.close());
		
		KleeneMatrix<RegularExpression<TerminalOrNonterminal<Terminal>>> vec = mvg.vector.projectionThroughMorphism(reversal);

		System.out.println(vec);
		
		System.out.println(m.close().mul(vec));

	}
	
	private static void test2x2() {
		List<List<RegularExpression<Character>>> mDat = new ArrayList<List<RegularExpression<Character>>>();
		List<RegularExpression<Character>> row1 = new ArrayList<RegularExpression<Character>>();
		row1.add(RegularExpression.fromAtom('a'));
		row1.add(RegularExpression.emptyRegexp());
		List<RegularExpression<Character>> row2 = new ArrayList<RegularExpression<Character>>();
		row2.add(RegularExpression.emptyRegexp());
		row2.add(RegularExpression.fromAtom('d'));
		mDat.add(row1);
		mDat.add(row2);
		KleeneMatrix<RegularExpression<Character>> m = new KleeneMatrix<RegularExpression<Character>>(mDat,
				RegularExpression.emptyRegexp(), RegularExpression.emptyString());
		System.out.println(m.close());
	}
	
}
