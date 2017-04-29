package algebraparsing;

// T is type of terminal
public final class TerminalOrNonterminal<T> {
	private final Nonterminal nonterminal;
	private T terminal;
	
	private TerminalOrNonterminal(Nonterminal nonterminal, T terminal) {
		if (nonterminal == null && terminal == null)
			throw new IllegalArgumentException("both nonterminal and terminal null");
		if (nonterminal != null && terminal != null)
			throw new IllegalArgumentException("neither nonterminal or terminal is null");
		this.nonterminal = nonterminal;
		this.terminal = terminal;
	}
	
	public static <T> TerminalOrNonterminal<T> fromTerminal(T terminal) {
		return new TerminalOrNonterminal<T>(null, terminal);
	}
	
	public static <T> TerminalOrNonterminal<T> fromNonterminal(Nonterminal nonterminal) {
		return new TerminalOrNonterminal<T>(nonterminal, null);
	}
	
	public boolean isTerminal() { return terminal != null; }
	
	public T asTerminal() {
		if (!this.isTerminal())
			throw new RuntimeException("is not a terminal");
		return this.terminal;
	}

	public Nonterminal asNonterminal() {
		if (this.isTerminal())
			throw new RuntimeException("is a terminal");
		return this.nonterminal;
	}

	@Override
	public String toString() {
		Object o;
		if (this.isTerminal()) {
			o = this.terminal;
		} else {
			o = this.nonterminal;
		}
		return o.toString();
	}
}
