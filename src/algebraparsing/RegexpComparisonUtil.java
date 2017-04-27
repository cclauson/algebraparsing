package algebraparsing;

import java.util.*;

public class RegexpComparisonUtil {

	// can't instantiate
	private RegexpComparisonUtil() {}
	
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
	
	// stack element keeps track of which edges have been traversed in the different regexps
	private static class StackEl<T extends Comparable<T>> {
		public final SortedMap<T, RegexpPair<T>> map;
		
		public StackEl(SortedMap<T, RegexpPair<T>> map) {
			this.map = map;
		}
	}
	
	private static class CompareResOrStackEl<T extends Comparable<T>> {
		public final int compareRes;
		public final StackEl<T> stackEl;
		
		public CompareResOrStackEl(int compareRes, StackEl<T> stackEl) {
			this.compareRes = compareRes;
			this.stackEl = stackEl;
		}
		
		public static <T extends Comparable<T>> CompareResOrStackEl<T> stackEl(StackEl<T> stackEl) {
			return new CompareResOrStackEl<T>(0, stackEl);
		}
		
		public static <T extends Comparable<T>> CompareResOrStackEl<T> compareResult(int compareRes) {
			if (compareRes == 0)
				throw new RuntimeException("can't use this method to return 0, if equal must return stackEl");
				
			return new CompareResOrStackEl<T>(compareRes, null);
		}

	}
	
	// if the atom type is comparable, then we can not only test the regular expressions for semantic equality,
	// but also impose a total ordering
	public static <T extends Comparable<T>> int compareRegexps(RegularExpression<T> regexp1, RegularExpression<T> regexp2) {
		
		final Stack<StackEl<T>> stack = new Stack<StackEl<T>>();
		//set keeps track of every pair that has ever been on the stack
		final Set<RegexpPair<T>> set = new HashSet<RegexpPair<T>>();

		RegexpPair<T> pair = new RegexpPair<T>(regexp1, regexp2); 

		// check the pair to see if they are both or neither final, also check
		// to see that they have the same set of transitions out
		// if so then returns something to push onto the stack
		CompareResOrStackEl<T> res = comparePairAndBuildStackEl(pair);
		if (res.compareRes != 0)
			return res.compareRes;
		
		set.add(pair);
		if (!res.stackEl.map.isEmpty())
			stack.push(res.stackEl);
		
		while (!stack.isEmpty()) {
			StackEl<T> stackEl = stack.peek();
			if (stackEl.map.isEmpty()) {
				stack.pop();
				continue;
			}
			
			T key = stackEl.map.firstKey();
			pair = stackEl.map.get(key);
			stackEl.map.remove(key);

			// same check on the pair
			res = comparePairAndBuildStackEl(pair);
			if (res.compareRes != 0)
				return res.compareRes;

			set.add(pair);
			if (!res.stackEl.map.isEmpty())
				stack.push(res.stackEl);
		}
		
		// if we reach here then they are equal
		return 0;
	}
	
	private static <T extends Comparable<T>> CompareResOrStackEl<T> comparePairAndBuildStackEl(RegexpPair<T> pair) {
		
		DecomposedRegexp<T> regexp1decomposed = pair.regexp1.decompose();
		DecomposedRegexp<T> regexp2decomposed = pair.regexp2.decompose();
		
		if (regexp1decomposed.hasEmptyString() != regexp2decomposed.hasEmptyString()) {
			// then they're not equal, but at the moment I'm not sure what to return
			return CompareResOrStackEl.compareResult(-1);
		}
		
		Map<T, RegularExpression<T>> nonemptyTerms1 = regexp1decomposed.nonemptyTerms(); 
		Map<T, RegularExpression<T>> nonemptyTerms2 = regexp2decomposed.nonemptyTerms(); 
		
		// compute union of two sets
		SortedSet<T> keySet = new TreeSet<T>();
		keySet.addAll(nonemptyTerms1.keySet());
		keySet.addAll(nonemptyTerms2.keySet());
		
		// populate a tree map and also check to make sure that key sets are equal
		// we want to check this way because we want to know which has the missing key
		SortedMap<T, RegexpPair<T>> treeMapForStack = new TreeMap<T, RegexpPair<T>>();
		for (T key : keySet) {
			if (!nonemptyTerms1.containsKey(key)) {
				// then they're not equal, but at the moment I'm not sure what to return
				return CompareResOrStackEl.compareResult(-1);
			}
			if (!nonemptyTerms2.containsKey(key)) {
				// then they're not equal, but at the moment I'm not sure what to return
				return CompareResOrStackEl.compareResult(-1);
			}
			treeMapForStack.put(key, new RegexpPair<T>(nonemptyTerms1.get(key), nonemptyTerms2.get(key)));
		}
		
		return CompareResOrStackEl.stackEl(new StackEl<T>(treeMapForStack));
	}
	
}
