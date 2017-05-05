package algebraparsing;

public class InputOrOutputTerminal {
	
	private final Terminal terminal;
	private final boolean isInputTerminal;
	
	private InputOrOutputTerminal(Terminal terminal, boolean isInputTerminal) {
		this.terminal = terminal;
		this.isInputTerminal = isInputTerminal;
	}
	
	public Terminal getTerminal() {
		return terminal;
	}
	
	public boolean isInput() {
		return isInputTerminal;
	}
	
	public static InputOrOutputTerminal inputTerminal(Terminal terminal) {
		return new InputOrOutputTerminal(terminal, true);
	}
	
	public static InputOrOutputTerminal outputTerminal(Terminal terminal) {
		return new InputOrOutputTerminal(terminal, false);
	}
}
