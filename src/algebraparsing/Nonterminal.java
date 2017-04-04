package algebraparsing;

public class Nonterminal implements TerminalOrNonterminal {
	private final char c;
	public Nonterminal(char c) {
		this.c = c;
	}
	@Override
	public String toString() {
		return Character.toString(c);
	}
	
	@Override
	public boolean equals(Object other) {
		if (other == null) return false;
		if (!(other instanceof Nonterminal)) return false;
		return this.c == ((Nonterminal )other).c;
	}
	
	@Override
	public int hashCode() {
		return Character.hashCode(c);
	}

}
