package algebraparsing;

public interface KleeneAlgebraElement<T extends KleeneAlgebraElement<T>> {
	public T add(T el);
	public T mul(T el);
	public T close();
}
