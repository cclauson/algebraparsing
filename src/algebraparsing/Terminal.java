package algebraparsing;

public class Terminal implements TerminalOrNonterminal {
	
	private final String s;
	
	public Terminal(char c) {
		this.s = Character.toString(c);
	}
	
	public Terminal(String s) {
		this.s = s;
	}
	
	@Override
	public String toString() {
		return "'" + s + "'";
	}
	
	@Override
	public boolean equals(Object other) {
		if (other == null) return false;
		if (!(other instanceof Terminal)) return false;
		return this.s.equals(((Terminal)other).s);
	}
	
	@Override
	public int hashCode() {
		return s.hashCode();
	}
}
