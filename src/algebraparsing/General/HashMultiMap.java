package algebraparsing.General;

import java.util.*;

public class HashMultiMap<S, T> {

	private HashMap<S, HashSet<T>> data = new HashMap<S, HashSet<T>>();
	
	public void put(S key, T value) {
		if (data.containsKey(key)) {
			HashSet<T> val = data.get(key);
			val.add(value);
		} else {
			HashSet<T> val = new HashSet<T>();
			val.add(value);
			data.put(key, val);
		}
	}
	
	public void removeAll(S key) {
		data.remove(key);
	}
	
	public void remove(S key, T value) {
		if (!data.containsKey(key))
			return;
		
		HashSet<T> val = data.get(key);
		if (!val.contains(value))
			return;
		
		val.remove(value);
		if (val.isEmpty())
			data.remove(key);
	}
	
	public Set<T> get(S key) {
		if (data.containsKey(key))
			return Collections.unmodifiableSet(data.get(key));
		else
			return Collections.emptySet();
	}
	
	public int hashCode() {
		return data.hashCode();
	}
	
	public boolean equals(Object o) {
		if (o == null) return false;
		if (!(o instanceof HashMultiMap)) return false;
		HashMultiMap<?, ?> hmm = (HashMultiMap<?, ?>) o;
		return this.data.equals(hmm.data);
	}
}
