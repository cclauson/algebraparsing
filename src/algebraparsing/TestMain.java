package algebraparsing;

import java.util.*;
import java.util.function.Function;

public class TestMain {

	private static Grammar grammar1() {
		//For the grammar:
		// Z -> E $
		// E -> E '+' T
		// E -> T
		// T -> '(' E ')'
		// T -> 'i'
		
		Nonterminal startSymbol = new Nonterminal('Z');
		// Z -> E $
		Production p1 = new Production(new Nonterminal('Z'), Arrays.asList(
			new Nonterminal('E'), new Terminal('$')
		));
		// E -> E '+' T
		Production p2 = new Production(new Nonterminal('E'), Arrays.asList(
			new Nonterminal('E'), new Terminal('+'), new Nonterminal('T')
		));
		// E -> T
		Production p3 = new Production(new Nonterminal('E'), Arrays.asList(
			new Nonterminal('T')
		));
		// T -> '(' E ')'
		Production p4 = new Production(new Nonterminal('T'), Arrays.asList(
			new Terminal('('), new Nonterminal('E'), new Terminal(')')
		));
		// T -> 'i'
		Production p5 = new Production(new Nonterminal('T'), Arrays.asList(
			new Terminal('i')
		));
		
		return new Grammar(startSymbol, Arrays.asList(p1, p2, p3, p4, p5));		
	}
	
	private static Grammar grammar2() {
		//For the grammar:
		// S -> A
		// S -> 'x' 'b'
		// A -> 'a' A 'b'
		// A -> B
		// B -> 'x'
		
		Nonterminal startSymbol = new Nonterminal('S');
		// S -> A
		Production p1 = new Production(new Nonterminal('S'), Arrays.asList(
			new Nonterminal('A')
		));
		// S -> 'x' 'b'
		Production p2 = new Production(new Nonterminal('S'), Arrays.asList(
			new Terminal('x'), new Terminal('b')
		));
		// A -> 'a' A 'b'
		Production p3 = new Production(new Nonterminal('A'), Arrays.asList(
			new Terminal('a'), new Nonterminal('A'), new Terminal('b')
		));
		// A -> B
		Production p4 = new Production(new Nonterminal('A'), Arrays.asList(
			new Nonterminal('B')
		));
		// B -> 'x'
		Production p5 = new Production(new Nonterminal('B'), Arrays.asList(
			new Terminal('x')
		));
		
		return new Grammar(startSymbol, Arrays.asList(p1, p2, p3, p4, p5));		
	}
	
	private static Grammar logicExpression() {
		//For the grammar:
		// A -> B '$'
		// B -> 'for all' H B
		// B -> 'exists' H 'such that' B
		// B -> C
		// C -> C iff D
		// C -> D
		// D -> 'if' E 'then' D
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
		List<Production> productions = Arrays.asList(
			// A -> B '$'
			new Production(new Nonterminal('A'), Arrays.asList(
				new Nonterminal('B'), new Terminal('$')
			)),
			// B -> 'for all' H B
			new Production(new Nonterminal('B'), Arrays.asList(
				new Terminal("for all"), new Nonterminal('H'), new Nonterminal('B')
			)),
			// B -> 'exists' H 'such that' B
			new Production(new Nonterminal('B'), Arrays.asList(
				new Terminal("exists"), new Nonterminal('H'),
				new Terminal("such that"), new Nonterminal('B')
			)),
			// B -> C
			new Production(new Nonterminal('B'), Arrays.asList(
				new Nonterminal('C')
			)),
			// C -> C iff D
			new Production(new Nonterminal('C'), Arrays.asList(
				new Nonterminal('C'), new Terminal("iff"), new Nonterminal('D')
			)),
			// C -> D
			new Production(new Nonterminal('C'), Arrays.asList(
				new Nonterminal('D')
			)),
			// D -> 'if' E 'then' D
			new Production(new Nonterminal('D'), Arrays.asList(
				new Terminal("if"), new Nonterminal('E'),
				new Terminal("then"), new Nonterminal('D')
			)),
			// D -> E 'implies' D
			new Production(new Nonterminal('D'), Arrays.asList(
				new Nonterminal('E'), new Terminal("implies"), new Nonterminal('D')
			)),
			// D -> E
			new Production(new Nonterminal('D'), Arrays.asList(
				new Nonterminal('E')
			)),
			// E -> E 'or' F
			new Production(new Nonterminal('E'), Arrays.asList(
					new Nonterminal('E'), new Terminal("or"), new Nonterminal('F')
			)),
			// E -> F
			new Production(new Nonterminal('E'), Arrays.asList(
				new Nonterminal('F')
			)),
			// F -> F 'and' G
			new Production(new Nonterminal('F'), Arrays.asList(
				new Nonterminal('F'), new Terminal("and"), new Nonterminal('G')
			)),
			// F -> G
			new Production(new Nonterminal('F'), Arrays.asList(
				new Nonterminal('G')
			)),
			// G -> not G
			new Production(new Nonterminal('G'), Arrays.asList(
				new Terminal("not"), new Nonterminal('G')
			)),
			// G -> H
			new Production(new Nonterminal('G'), Arrays.asList(
				new Nonterminal('H')
			)),
			// G -> '(' B ')'
			new Production(new Nonterminal('G'), Arrays.asList(
				new Terminal("("), new Nonterminal('B'), new Terminal(")")
			)),
			// H -> 'sym'
			new Production(new Nonterminal('H'), Arrays.asList(
				new Terminal("sym")
			))
		);
		
		return new Grammar(startSymbol, productions);		
	}

	public static void main(String[] args) {
		
		//Grammar grammar = grammar2();
		Grammar grammar = logicExpression();
		System.out.println(grammar.toString());
		
		System.out.println();
		
		MatrixVectorGrammar mvg = grammar.asAffineEndomorphism();
		System.out.println(mvg.matrix);
		System.out.println();
		System.out.println(mvg.vector);
		
		System.out.println();
				
		/*
		List<List<RegularExpression<TerminalOrNonterminal>>> mDat = new ArrayList<List<RegularExpression<TerminalOrNonterminal>>>();
		List<RegularExpression<TerminalOrNonterminal>> row1 = new ArrayList<RegularExpression<TerminalOrNonterminal>>();
		List<RegularExpression<TerminalOrNonterminal>> row2 = new ArrayList<RegularExpression<TerminalOrNonterminal>>();
		List<RegularExpression<TerminalOrNonterminal>> row3 = new ArrayList<RegularExpression<TerminalOrNonterminal>>();

		row1.add(emptyRegexp()); row1.add(fromAtom(new Terminal('$'))); row1.add(emptyRegexp());

		row2.add(emptyRegexp());
		row2.add(fromAtom((TerminalOrNonterminal)new Terminal('+'))
				.mul(fromAtom(new Nonterminal('T'))));
		row2.add(emptyString());

		row3.add(emptyRegexp()); row3.add(emptyRegexp()); row3.add(emptyRegexp());
		
		//row1.add(emptyRegexp()); row1.add(emptyString()); row1.add(emptyRegexp());
		//row2.add(emptyRegexp()); row2.add(emptyRegexp()); row2.add(emptyString());
		//row3.add(emptyRegexp()); row3.add(emptyRegexp()); row3.add(emptyRegexp());

		mDat.add(row1);
		mDat.add(row2);
		mDat.add(row3);

		for (List<RegularExpression<TerminalOrNonterminal>> dat : mDat) {
			for (int i = 0; i < dat.size(); ++i) {
				dat.set(i, RegularExpression.reversal(dat.get(i)));
			}
		}
		
		KleeneMatrix<RegularExpression<TerminalOrNonterminal>> m = new KleeneMatrix<RegularExpression<TerminalOrNonterminal>>(
				mDat,
				RegularExpression.reversal(RegularExpression.emptyRegexp()),
				RegularExpression.reversal(RegularExpression.emptyString()));
		*/
		
		Function<RegularExpression<TerminalOrNonterminal>, RegularExpression<TerminalOrNonterminal>> reversal =
				new Function<RegularExpression<TerminalOrNonterminal>, RegularExpression<TerminalOrNonterminal>>() {
			@Override
			public RegularExpression<TerminalOrNonterminal> apply(RegularExpression<TerminalOrNonterminal> t) {
				return RegularExpression.reversal(t);
			}
		};
		
		KleeneMatrix<RegularExpression<TerminalOrNonterminal>> m =
				mvg.matrix.projectionThroughMorphism(reversal);
		
		System.out.println(m);
		System.out.println(m.close());
		
		/*
		List<List<RegularExpression<TerminalOrNonterminal>>> vectorDat = new ArrayList<List<RegularExpression<TerminalOrNonterminal>>>();
		List<RegularExpression<TerminalOrNonterminal>> vecrow1 = new ArrayList<RegularExpression<TerminalOrNonterminal>>();
		List<RegularExpression<TerminalOrNonterminal>> vecrow2 = new ArrayList<RegularExpression<TerminalOrNonterminal>>();
		List<RegularExpression<TerminalOrNonterminal>> vecrow3 = new ArrayList<RegularExpression<TerminalOrNonterminal>>();

		//vecrow1.add(RegularExpression.fromAtom((TerminalOrNonterminal)new Terminal('x')).mul(RegularExpression.fromAtom((TerminalOrNonterminal)new Terminal('b'))));
		//vecrow2.add(RegularExpression.fromAtom((TerminalOrNonterminal)new Terminal('a'))
		//		.mul(RegularExpression.fromAtom(new Nonterminal('A')))
		//		.mul(RegularExpression.fromAtom(new Terminal('b'))));
		//vecrow3.add(RegularExpression.fromAtom((TerminalOrNonterminal)new Terminal('x')));

		vecrow1.add(RegularExpression.emptyRegexp());
		vecrow2.add(RegularExpression.emptyRegexp());
		vecrow3.add(
				RegularExpression.fromAtom((TerminalOrNonterminal)new Terminal('('))
				.mul(RegularExpression.fromAtom(new Nonterminal('E')))
				.mul(RegularExpression.fromAtom(new Terminal(')')))
				.add(RegularExpression.fromAtom(new Terminal('i'))));
		
		vectorDat.add(vecrow1);
		vectorDat.add(vecrow2);
		vectorDat.add(vecrow3);

		for (List<RegularExpression<TerminalOrNonterminal>> dat : vectorDat) {
			for (int i = 0; i < dat.size(); ++i) {
				dat.set(i, RegularExpression.reversal(dat.get(i)));
			}
		}
		
		KleeneMatrix<RegularExpression<TerminalOrNonterminal>> vec = new KleeneMatrix<RegularExpression<TerminalOrNonterminal>>(
				vectorDat,
				RegularExpression.reversal(RegularExpression.emptyRegexp()),
				RegularExpression.reversal(RegularExpression.emptyString()));
		*/
		
		KleeneMatrix<RegularExpression<TerminalOrNonterminal>> vec = mvg.vector.projectionThroughMorphism(reversal);

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
