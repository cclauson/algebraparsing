package algebraparsing;

import java.util.*;

import algebraparsing.KleeneAlgebra.KleeneAlgebraElement;

// object describes a mapping of a finite set of objects of type S
// to arguments in T, where T is a Kleene algebra
// instances of this type are unmodifiable
public class FreeKleeneModuleElement<S, T extends KleeneAlgebraElement<T>> {
	
	private Map<S, T> data;
	
	public FreeKleeneModuleElement(Map<S, T> data) {
		if (data == null)
			throw new IllegalArgumentException("data is null");
		
		this.data = Collections.unmodifiableMap(data);
	}
	
	public T get(S s) {
		if (!data.containsKey(s))
			throw new IllegalArgumentException(s +
					" is not in the domain of this FreeKleeneModuleElement");
		
		return data.get(s);
	}
	
	public FreeKleeneModuleElement<S, T> add(FreeKleeneModuleElement<S, T> other) {
		if (!this.data.keySet().equals(other.data.keySet()))
			throw new IllegalArgumentException("not compatible for addition, ranges don't match");
		
		Map<S, T> dataNew = new HashMap<S, T>();
		
		for (S key : this.data.keySet()) {
			dataNew.put(key, this.get(key).add(other.get(key)));
		}
		return new FreeKleeneModuleElement<S, T>(dataNew);
	}
	
	public Set<S> getDomain() {
		return this.data.keySet();
	}
}
