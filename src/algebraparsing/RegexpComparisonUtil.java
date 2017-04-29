package algebraparsing;

import java.util.*;

public class RegexpComparisonUtil {

	// can't instantiate
	private RegexpComparisonUtil() {}
	
	// these are defined these way in the platform API's for the Comparable interface
	// we'll name constants to make code clearer
	private static final int LESS_THAN = -1;
	private static final int EQUAL = 0;
	private static final int GREATER_THAN = 1;
	
	// we will DFS through the set product of the states of the two regexps
	// using a stack and a map

	// set element keeps track of which regexp/regexp pairs have been traversed,
	// where the identity of the regexp is defined as its form
	private static class RegexpPair<T> {
		private final RegularExpression<T> regexp1;
		private final RegularExpression<T> regexp2;
		
		public RegexpPair(RegularExpression<T> regexp1, RegularExpression<T> regexp2) {
			this.regexp1 = regexp1;
			this.regexp2 = regexp2;
		}
		
		@Override
		public boolean equals(Object o) {
			if (o == null) return false;
			if (!(o instanceof RegexpPair<?>)) return false;
			RegexpPair<?> otherMapEl = (RegexpPair<?>) o;
			return this.regexp1.equals(otherMapEl.regexp1) && this.regexp2.equals(otherMapEl.regexp2);
		}
		
	}
	
	// simple structure that pairs an atom with a pair of regexps
	private static class AtomRegexpPairPair<T> {
		public final T atom;
		public final RegexpPair<T> regexpPair;
		
		public AtomRegexpPairPair(T atom, RegexpPair<T> regexpPair) {
			this.atom = atom;
			this.regexpPair = regexpPair;
		}
	}
	
	// stack element keeps track of which edges have been traversed in the different regexps
	private static class StackEl<T> {
		public final Queue<AtomRegexpPairPair<T>> queue;
		public StackEl(Queue<AtomRegexpPairPair<T>> queue) {
			this.queue = queue;
		}
	}

	// T is type of atom
	// R is return type of comparison
	private static interface ComparisonStrategy<T, R> {
		Set<T> createKeyset();
		R lessThan();
		R greaterThan();
		R equal();
		boolean isEqual(R r);
	}
	
	// if the atom type is comparable, then we can not only test the regular expressions for semantic equality,
	// but also impose a total ordering
	public static <T extends Comparable<T>> int compareRegexps(RegularExpression<T> regexp1, RegularExpression<T> regexp2) {
		final ComparisonStrategy<T, Integer> cs = new ComparisonStrategy<T, Integer>() {
			@Override public Set<T> createKeyset() { return new TreeSet<T>(); }
			@Override public Integer lessThan() { return LESS_THAN; }
			@Override public Integer greaterThan() { return GREATER_THAN; }
			@Override public Integer equal() { return EQUAL; }
			@Override public boolean isEqual(Integer r) { return r == EQUAL; }
		};
		return compareRegexps(regexp1, regexp2, cs);
	}

	// if the atom type is not comparable we can still decide equality, just not ordering
	public static <T> boolean regexpsSemanticallyEqual(RegularExpression<T> regexp1, RegularExpression<T> regexp2) {
		final ComparisonStrategy<T, Boolean> cs = new ComparisonStrategy<T, Boolean>() {
			@Override public Set<T> createKeyset() { return new HashSet<T>(); }
			@Override public Boolean lessThan() { return false; }
			@Override public Boolean greaterThan() { return false; }
			@Override public Boolean equal() { return true; }
			@Override public boolean isEqual(Boolean r) { return r; }
		};
		return compareRegexps(regexp1, regexp2, cs);
	}
	
	// common code for comparison that involves ordering and code that simple decides equality
	private static <T, R> R compareRegexps(RegularExpression<T> regexp1, RegularExpression<T> regexp2, ComparisonStrategy<T, R> cs) {
		
		final Stack<StackEl<T>> stack = new Stack<StackEl<T>>();
		//set keeps track of every pair that has ever been on the stack
		final Set<RegexpPair<T>> set = new HashSet<RegexpPair<T>>();

		RegexpPair<T> pair = new RegexpPair<T>(regexp1, regexp2); 

		// check the pair to see if they are both or neither final, also check
		// to see that they have the same set of transitions out
		// if so then returns something to push onto the stack
		R compareRes = comparePairAndPopulateDataStructures(pair, stack, set, cs);
		if (!cs.isEqual(compareRes))
			return compareRes;
		
		while (!stack.isEmpty()) {
			StackEl<T> stackEl = stack.peek();
			if (stackEl.queue.isEmpty()) {
				stack.pop();
				continue;
			}
			
			AtomRegexpPairPair<T> arpp = stackEl.queue.poll();
			
			// same check on the pair
			compareRes = comparePairAndPopulateDataStructures(arpp.regexpPair, stack, set, cs);
			if (cs.isEqual(compareRes))
				return compareRes;
		}
		
		// if we reach here then they are equal
		return cs.equal();
	}
	
	private static <T, R> R comparePairAndPopulateDataStructures(
		RegexpPair<T> pair,
		Stack<StackEl<T>> stack,
		Set<RegexpPair<T>> set,
		ComparisonStrategy<T, R> cs
	) {
		RegularExpression<T> regexp1 = pair.regexp1;
		RegularExpression<T> regexp2 = pair.regexp2;
		
		// if they're formally equal then we don't need to test anything
		// or recurse further, we're done
		if (regexp1.equals(regexp2))
			return cs.equal();
		
		// if it's already in the set then it's already been checked,
		// so we want to stop recursion
		if (set.contains(pair))
			return cs.equal();
		
		DecomposedRegexp<T> regexp1decomposed = regexp1.decompose();
		DecomposedRegexp<T> regexp2decomposed = regexp2.decompose();
		
		if (regexp1decomposed.hasEmptyString() && !regexp2decomposed.hasEmptyString()) {
			return cs.lessThan();
		}

		if (!regexp1decomposed.hasEmptyString() && regexp2decomposed.hasEmptyString()) {
			return cs.greaterThan();
		}

		Map<T, RegularExpression<T>> nonemptyTerms1 = regexp1decomposed.nonemptyTerms(); 
		Map<T, RegularExpression<T>> nonemptyTerms2 = regexp2decomposed.nonemptyTerms(); 
		
		// compute union of two sets
		SortedSet<T> keySet = new TreeSet<T>();
		keySet.addAll(nonemptyTerms1.keySet());
		keySet.addAll(nonemptyTerms2.keySet());
		
		// populate a tree map and also check to make sure that key sets are equal
		// we want to check this way because we want to know which has the missing key
		Queue<AtomRegexpPairPair<T>> queue = new LinkedList<AtomRegexpPairPair<T>>();
		for (T key : keySet) {
			if (!nonemptyTerms1.containsKey(key)) {
				return cs.lessThan();
			}
			if (!nonemptyTerms2.containsKey(key)) {
				return cs.greaterThan();
			}
			queue.add(new AtomRegexpPairPair<T>(key, new RegexpPair<T>(
				nonemptyTerms1.get(key),
				nonemptyTerms2.get(key)
			)));
		}

		set.add(pair);
		if (!queue.isEmpty())
			stack.push(new StackEl<T>(queue));

		return cs.equal();
	}
	
}
