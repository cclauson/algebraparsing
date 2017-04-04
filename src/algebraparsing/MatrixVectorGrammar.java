package algebraparsing;

import java.util.Map;

public class MatrixVectorGrammar {
	public final Map<Nonterminal, Integer> nonterminalIndexMap;
	public final KleeneMatrix<RegularExpression<TerminalOrNonterminal>> matrix;
	public final KleeneMatrix<RegularExpression<TerminalOrNonterminal>> vector;
	
	public MatrixVectorGrammar(
		Map<Nonterminal, Integer> nonterminalIndexMap,
		KleeneMatrix<RegularExpression<TerminalOrNonterminal>> matrix,
		KleeneMatrix<RegularExpression<TerminalOrNonterminal>> vector
	) {
		this.nonterminalIndexMap = nonterminalIndexMap;
		this.matrix = matrix;
		this.vector = vector;
	}
}
