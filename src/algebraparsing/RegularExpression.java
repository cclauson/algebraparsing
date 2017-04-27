package algebraparsing;

import java.util.*;

public abstract class RegularExpression<T> implements KleeneAlgebraElement<RegularExpression<T>> {

	private RegularExpression() {}

	@Override
	public RegularExpression<T> mul(RegularExpression<T> el) {
		if (el.equals(emptyRegexp()))
			return emptyRegexp();
		if (el.equals(emptyString()))
			return this;
		if (this instanceof CatRegularExpression)
			throw new IllegalArgumentException("can't make concatenation with cat as left child");
		return new CatRegularExpression<T>(this, el);
	}
	
	@Override
	public RegularExpression<T> add(RegularExpression<T> el) {
		if (el.equals(emptyRegexp()))
			return this;
		if (this instanceof UnionRegularExpression)
			throw new IllegalArgumentException("can't make union with union as left child");
		return new UnionRegularExpression<T>(this, el);
	}
	
	@Override
	public RegularExpression<T> close() {
		return new ClosureRegularExpression<T>(this);
	}

	public abstract DecomposedRegexp<T> decompose();
	
	
	private static class AtomRegularExpression<T> extends RegularExpression<T> {
		private final T atom;

		public AtomRegularExpression(T atom) {
			if (atom == null)
				throw new IllegalArgumentException("atom is null");
			this.atom = atom;
		}
		
		@Override
		public String toString() {
			return atom.toString();
		}
		
		@Override
		public boolean equals(Object other) {
			if (other == null) return false;
			if (other instanceof AtomRegularExpression) {
				AtomRegularExpression<?> are = (AtomRegularExpression<?>) other;
				Object atomOther = are.atom;
				return this.atom.equals(atomOther);
			}
			return false;
		}
		
		@Override
		public int hashCode() {
			return atom.hashCode();
		}

		@Override
		public DecomposedRegexp<T> decompose() {
			Map<T, RegularExpression<T>> map = new HashMap<T, RegularExpression<T>>();
			map.put(atom, RegularExpression.emptyString());
			return new DecomposedRegexp<T>(map, false);
		}
	}

	public static <T> RegularExpression<T> fromAtom(T atom) {
		return new AtomRegularExpression<T>(atom);
	}

	private static class EmptyStringRegularExpression<T> extends RegularExpression<T> {

		private static final String STRREP = "[empty string]";
		
		@Override
		public RegularExpression<T> add(RegularExpression<T> el) {
			if (this.equals(el))
				return this;
			return el.add(this);
		}

		@Override
		public RegularExpression<T> mul(RegularExpression<T> el) {
			return el;
		}

		@Override
		public RegularExpression<T> close() {
			return this;
		}
		
		@Override
		public String toString() {
			return STRREP;
		}
		
		@Override
		public boolean equals(Object other) {
			return other != null && other instanceof EmptyStringRegularExpression;
		}
		
		@Override
		public int hashCode() {
			return STRREP.hashCode();
		}

		@Override
		public DecomposedRegexp<T> decompose() {
			return new DecomposedRegexp<T>(Collections.emptyMap(), true);
		}
	}

	private static final RegularExpression<?> EMPTY_STRING = new EmptyStringRegularExpression<Object>();

	@SuppressWarnings("unchecked")
	public static <T> RegularExpression<T> emptyString() {
		return (RegularExpression<T>) EMPTY_STRING;
	}
	
	private static class EmptyRegularExpression<T> extends RegularExpression<T> {

		private static final String STRREP = "[empty regexp]";
		
		@Override
		public RegularExpression<T> add(RegularExpression<T> el) {
			return el;
		}

		@Override
		public RegularExpression<T> mul(RegularExpression<T> el) {
			return this;
		}

		@Override
		public RegularExpression<T> close() {
			return RegularExpression.emptyString();
		}
		
		@Override
		public String toString() {
			return STRREP;
		}
		
		@Override
		public boolean equals(Object other) {
			return other != null && other instanceof EmptyRegularExpression;
		}
		
		@Override
		public int hashCode() {
			return STRREP.hashCode();
		}
		
		@Override
		public DecomposedRegexp<T> decompose() {
			return new DecomposedRegexp<T>(Collections.emptyMap(), false);
		}
	}

	private static final RegularExpression<?> EMPTY_REGEXP = new EmptyRegularExpression<Object>();

	@SuppressWarnings("unchecked")
	public static <T> RegularExpression<T> emptyRegexp() {
		return (RegularExpression<T>) EMPTY_REGEXP;
	}

	private static class ClosureRegularExpression<T> extends RegularExpression<T> {

		private final RegularExpression<T> child;
		
		public ClosureRegularExpression(RegularExpression<T> child) {
			if (child == null) throw new RuntimeException("child is null");
			this.child = child;
		}
		
		@Override
		public RegularExpression<T> close() {
			return this;
		}
		
		@Override
		public String toString() {
			if (child instanceof CatRegularExpression || child instanceof UnionRegularExpression)
				return "(" + child.toString() + ")*";
			return child.toString() + "*";
		}
		
		@Override
		public boolean equals(Object other) {
			if (other != null && other instanceof ClosureRegularExpression) {
				ClosureRegularExpression<?> cre = (ClosureRegularExpression<?>) other;
				return this.child.equals(cre.child);
			}
			return false;
		}
		
		@Override
		public int hashCode() {
			return child.hashCode();
		}

		@Override
		public DecomposedRegexp<T> decompose() {
			DecomposedRegexp<T> childDecomposed = child.decompose();
			//note we'll never check whether the child contains the empty string
			//since it never matters in a closure
			
			// the concept here is based on the fact that x* = [empty string] + xx*
			
			Map<T, RegularExpression<T>> terms = new HashMap<T, RegularExpression<T>>();
			for (Map.Entry<T, RegularExpression<T>> entry : childDecomposed.nonemptyTerms().entrySet()) {
				terms.put(entry.getKey(), entry.getValue().mul(this));
			}
			return new DecomposedRegexp<T>(terms, true); // true because closure always includes empty string
		}
		
	}
	
	private static class CatRegularExpression<T> extends RegularExpression<T> {

		private final RegularExpression<T> leftChild, rightChild;
		
		public CatRegularExpression(RegularExpression<T> leftChild,
				RegularExpression<T> rightChild) {
			if (leftChild == null) throw new RuntimeException("left child is null");
			this.leftChild = leftChild;
			if (rightChild == null) throw new RuntimeException("right child is null");
			this.rightChild = rightChild;
		}
		
		@Override
		public RegularExpression<T> mul(RegularExpression<T> el) {
			return leftChild.mul(rightChild.mul(el));			
		}

		@Override
		public String toString() {
			String left;
			if (leftChild instanceof UnionRegularExpression<?>) {
				left = "(" + leftChild.toString() + ")";
			} else {
				left = leftChild.toString();
			}
			return left + rightChild.toString();
		}
		
		@Override
		public boolean equals(Object other) {
			if (other != null && other instanceof CatRegularExpression) {
				CatRegularExpression<?> cre = (CatRegularExpression<?>) other;
				return this.rightChild.equals(cre.rightChild) && this.leftChild.equals(cre.leftChild);
			}
			return false;
		}
		
		@Override
		public int hashCode() {
			return this.rightChild.hashCode() * 209 ^ this.leftChild.hashCode();
		}

		@Override
		public DecomposedRegexp<T> decompose() {
			DecomposedRegexp<T> leftChildDecomposed = leftChild.decompose();
			
			Map<T, RegularExpression<T>> terms = new HashMap<T, RegularExpression<T>>();
			for (Map.Entry<T, RegularExpression<T>> entry : leftChildDecomposed.nonemptyTerms().entrySet()) {
				terms.put(entry.getKey(), entry.getValue().mul(rightChild));
			}
			
			boolean hasEmptyString;
			if (leftChildDecomposed.hasEmptyString()) {
				//we also need to include decomposition of right child
				DecomposedRegexp<T> rightChildDecomposed = rightChild.decompose();
				for (Map.Entry<T, RegularExpression<T>> entry : rightChildDecomposed.nonemptyTerms().entrySet()) {
					T key = entry.getKey();
					RegularExpression<T> curValue, newValue;
					curValue = entry.getValue();
					if (terms.containsKey(key)) {
						newValue = terms.get(key).add(curValue);
					} else {
						newValue = curValue;
					}
					terms.put(key, newValue);
				}
				hasEmptyString = rightChildDecomposed.hasEmptyString();
			} else {
				hasEmptyString = false;
			}
			return new DecomposedRegexp<T>(terms, hasEmptyString);
		}
	}

	private static class UnionRegularExpression<T> extends RegularExpression<T> {

		private final RegularExpression<T> leftChild, rightChild;
		
		public UnionRegularExpression(RegularExpression<T> leftChild,
				RegularExpression<T> rightChild) {
			if (leftChild == null) throw new RuntimeException("left child is null");
			this.leftChild = leftChild;
			if (rightChild == null) throw new RuntimeException("right child is null");
			this.rightChild = rightChild;
		}
		
		@Override
		public RegularExpression<T> add(RegularExpression<T> el) {
			return leftChild.add(rightChild.add(el));			
		}

		@Override
		public String toString() {
			return leftChild.toString() + " + " + rightChild.toString();
		}
		
		@Override
		public boolean equals(Object other) {
			if (other != null && other instanceof CatRegularExpression) {
				CatRegularExpression<?> cre = (CatRegularExpression<?>) other;
				return this.rightChild.equals(cre.rightChild) && this.leftChild.equals(cre.leftChild);
			}
			return false;
		}
		
		@Override
		public int hashCode() {
			return this.rightChild.hashCode() * 313 ^ this.leftChild.hashCode();
		}

		@Override
		public DecomposedRegexp<T> decompose() {
			DecomposedRegexp<T> leftChildDecomposed = leftChild.decompose();
			DecomposedRegexp<T> rightChildDecomposed = rightChild.decompose();
			
			//populate initially with members from left child
			Map<T, RegularExpression<T>> terms =
					new HashMap<T, RegularExpression<T>>(leftChildDecomposed.nonemptyTerms());

			//union in members from right child
			for (Map.Entry<T, RegularExpression<T>> entry : rightChildDecomposed.nonemptyTerms().entrySet()) {
				T key = entry.getKey();
				RegularExpression<T> curValue, newValue;
				curValue = entry.getValue();
				if (terms.containsKey(key)) {
					newValue = terms.get(key).add(curValue);
				} else {
					newValue = curValue;
				}
				terms.put(key, newValue);
			}
			
			boolean hasEmptyString =
					leftChildDecomposed.hasEmptyString() || rightChildDecomposed.hasEmptyString();
			return new DecomposedRegexp<T>(terms, hasEmptyString);
		}
	}
	
	//kind of a hack, at least for now, would probably have to be improved to be solid
	private static class RegularExpressionReversal<T> extends RegularExpression<T> {

		private final RegularExpression<T> childRegexp;
		
		public RegularExpressionReversal(RegularExpression<T> childRegexp) {
			this.childRegexp = childRegexp;
		}
		
		@Override
		public RegularExpression<T> add(RegularExpression<T> re) {
			if (re == null || !(re instanceof RegularExpressionReversal)) {
				throw new RuntimeException("reversals can only interoperate with other reversals");
			}
			return new RegularExpressionReversal<T>(this.childRegexp.add(((RegularExpressionReversal<T>)re).childRegexp));
		}
		
		@Override
		public RegularExpression<T> mul(RegularExpression<T> re) {
			if (re == null || !(re instanceof RegularExpressionReversal)) {
				throw new RuntimeException("reversals can only interoperate with other reversals");
			}
			return new RegularExpressionReversal<T>(((RegularExpressionReversal<T>)re).childRegexp.mul(this.childRegexp));
		}
		
		@Override
		public RegularExpression<T> close() {
			return new RegularExpressionReversal<T>(childRegexp.close());
		}
		
		@Override
		public String toString() {
			return childRegexp.toString();
		}
		
		@Override
		public int hashCode() {
			return childRegexp.hashCode();
		}
		
		@Override
		public boolean equals(Object other) {
			if (other == null || !(other instanceof RegularExpressionReversal))
				return false;
			RegularExpressionReversal<?> rer = (RegularExpressionReversal<?>) other;
			return this.childRegexp.equals(rer.childRegexp);
		}

		@Override
		public DecomposedRegexp<T> decompose() {
			// "reversal" is kind of a hack anyways, I don't think we need this operation
			// here so for now don't implement
			throw new RuntimeException("not implemented");
		}
	}
	
	public static <T> RegularExpression<T> reversal(RegularExpression<T> re) {
		return new RegularExpressionReversal<T>(re);
	}

}
