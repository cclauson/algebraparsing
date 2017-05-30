package algebraparsing.KleeneAlgebra;

import java.util.*;

// represents a regular expression decomposed into
// one of the following forms:
// a1*R1 + a2*R2 + a3*R3 + ... + an*Rn
// OR
// a1*R1 + a2*R2 + a3*R3 + ... + an*Rn + [empty string]
// depending on whether or not the original regular expression
// contains the empty string.
// In the above formulas, a1, a2, ... an represent members
// of the alphabet that the regular expression is over, and
// R1, R2, ... Rn are regular expressions, each of which can
// possibly be the empty regular expression (similar to zero
// on multiplication)
public class DecomposedRegexp<T> {
	
	private final Map<T, RegularExpression<T>> nonemptyTerms;
	private final boolean hasEmptyString;
	
	public DecomposedRegexp(Map<T, RegularExpression<T>> nonemptyTerms, boolean hasEmptyString) {
		this.nonemptyTerms = new HashMap<T, RegularExpression<T>>(nonemptyTerms);
		this.hasEmptyString = hasEmptyString;
	}
	
	public boolean hasEmptyString() { return this.hasEmptyString; }
	
	public Map<T, RegularExpression<T>> nonemptyTerms() {
		return Collections.unmodifiableMap(nonemptyTerms);
	}
	
}
