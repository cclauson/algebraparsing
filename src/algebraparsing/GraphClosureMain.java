package algebraparsing;

import algebraparsing.General.WeightedDigraph;
import algebraparsing.KleeneAlgebra.KleeneAlgebraElement;
import algebraparsing.KleeneAlgebra.KleeneMatrix;
import algebraparsing.KleeneAlgebra.RegularExpression;

public class GraphClosureMain {
	
	// can't instantiate
	private GraphClosureMain() {}
	
	private static <T extends KleeneAlgebraElement<T>> WeightedDigraph<Integer, T>
			digraphFromKleeneMatrix(KleeneMatrix<T> m) {
		
		final WeightedDigraph<Integer, T> digraph = new WeightedDigraph<Integer, T>();
		if (m.getM() != m.getN())
			throw new RuntimeException("matrix expected to be square, but not");
		
		for (int i = 0; i < m.getM(); ++i) {
			digraph.addNode(i);
		}
		for (int i = 0; i < m.getM(); ++i) {
			for (int j = 0; j < m.getM(); ++j) {
				digraph.addEdgeWithWeightOrSetWeight(i, j, m.getAt(i, j));
			}
		}
		return digraph;
	}
	
	public static void main(String[] args) {
		Grammar<InputOrOutputTerminal, Translation> grammar = TestMain.logicExpressionSSDTS();
		MatrixVectorGrammar<Translation> mvg = grammar.asAffineEndomorphism();
		KleeneMatrix<RegularExpression<TerminalOrNonterminal<Translation>>> m = mvg.matrix;
		
	}
		
	public static <S, T extends KleeneAlgebraElement<T>>
			void CloseGraph(WeightedDigraph<S, T> graph) {
		// TODO: Implement this
	}
}
