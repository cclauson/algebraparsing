package algebraparsing;

import java.util.List;

/**
 * Represents a production in a grammar
 * 
 * @author C. Clauson
 */
public class Production<T> {
	public Nonterminal nonterminal;
	public List<TerminalOrNonterminal<T>> rhs;
	
	public Production(Nonterminal nonterminal, List<TerminalOrNonterminal<T>> rhs) {
		if (nonterminal == null)
			throw new IllegalArgumentException("nonterminal must not be null");
		//let's require at least some list be passed, even though it could be empty
		if (rhs == null)
			throw new IllegalArgumentException("right hand side must not be null");
		
		this.nonterminal = nonterminal;
		this.rhs = rhs;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (TerminalOrNonterminal<T> ton : rhs) {
			sb.append(ton.toString());
		}
		return nonterminal.toString() + " -> " + sb.toString();
	}
}
