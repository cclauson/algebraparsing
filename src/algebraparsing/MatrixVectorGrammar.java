package algebraparsing;

import java.util.Map;

public class MatrixVectorGrammar<T> {
	public final Map<Nonterminal, Integer> nonterminalIndexMap;
	public final KleeneMatrix<RegularExpression<TerminalOrNonterminal<T>>> matrix;
	public final KleeneMatrix<RegularExpression<TerminalOrNonterminal<T>>> vector;
	
	public MatrixVectorGrammar(
		Map<Nonterminal, Integer> nonterminalIndexMap,
		KleeneMatrix<RegularExpression<TerminalOrNonterminal<T>>> matrix,
		KleeneMatrix<RegularExpression<TerminalOrNonterminal<T>>> vector
	) {
		this.nonterminalIndexMap = nonterminalIndexMap;
		this.matrix = matrix;
		this.vector = vector;
	}
}
