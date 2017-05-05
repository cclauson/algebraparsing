package algebraparsing;

import java.util.*;

// represents a string of terminals
// that translates to a second string
// of terminals
public class Translation {

	private final List<Terminal> inputs;
	private final List<Terminal> outputs;
	
	public Translation(List<Terminal> inputs, List<Terminal> outputs) {
		this.inputs = Collections.unmodifiableList(inputs);
		this.outputs = Collections.unmodifiableList(outputs);
	}

	public List<Terminal> getInputs() {
		return inputs;
	}
	
	public List<Terminal> getOutputs() {
		return outputs;
	}
	
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		for (Terminal terminal : inputs) {
			sb.append(terminal.toString());
		}
		for (Terminal terminal : outputs) {
			sb.append(" out(" + terminal.toString() + ")");
		}
		return sb.toString();
	}
	
	@Override
	public int hashCode() {
		return inputs.hashCode() ^ outputs.hashCode();
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == null) return false;
		if (!(o instanceof Translation)) return false;
		Translation t = (Translation) o;
		return t.inputs.equals(this.inputs) && t.outputs.equals(this.outputs);
	}
}
