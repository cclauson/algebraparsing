package algebraparsing;

public class Terminal implements TerminalOrNonterminal {
	private final char c;
	public Terminal(char c) {
		this.c = c;
	}
	
	@Override
	public String toString() {
		return "'" + c + "'";
	}
	
	@Override
	public boolean equals(Object other) {
		if (other == null) return false;
		if (!(other instanceof Terminal)) return false;
		return this.c == ((Terminal)other).c;
	}
	
	@Override
	public int hashCode() {
		return Character.hashCode(c);
	}
}
