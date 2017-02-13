package algebraparsing;

import java.util.*;
import java.util.function.*;

public class KleeneMatrix<T extends KleeneAlgebraElement<T>>
		implements KleeneAlgebraElement<KleeneMatrix<T>> {

	private final int m;
	private final int n;
	private final List<List<T>> data;

	private final T zero;
	private final T one;
	
	private KleeneMatrix(List<List<T>> data, int m, int n, T zero, T one) {
		this.data = data;
		this.m = m;
		this.n = n;
		this.zero = zero;
		this.one = one;
	}
	
	public KleeneMatrix(T[][] data, T zero, T one) {
		if (data == null)
			throw new IllegalArgumentException("data may not be null");
		if (data.length == 0)
			throw new IllegalArgumentException("data must not be empty array");
		this.m = data.length;
		final List<List<T>> dataCopy = new ArrayList<List<T>>(this.m);
		if (data[0] == null)
			throw new IllegalArgumentException("first member of data array is null");
		this.n = data[0].length;
		if (n == 0)
			throw new IllegalArgumentException("first member of data array is zero-length");
		for (T[] arry : data) {
			if (arry == null)
				throw new IllegalArgumentException("found null array in data");
			if (arry.length != n)
				throw new IllegalArgumentException("array of arrays is not a rectangle");
			dataCopy.add(new ArrayList<T>(Arrays.asList(arry)));
		}
		this.data = dataCopy;
		if (zero == null)
			throw new IllegalArgumentException("zero is null");
		this.zero = zero;
		if (one == null)
			throw new IllegalArgumentException("one is null");
		this.one = one;
	}
	
	@Override
	public KleeneMatrix<T> add(KleeneMatrix<T> el) {
		if (this.m != el.m)
			throw new IllegalArgumentException("m's are not compatible");
		if (this.n != el.n)
			throw new IllegalArgumentException("n's are not compatible");
		return createInstance(m, n, new BiFunction<Integer, Integer, T>() {
			@Override
			public T apply(Integer i, Integer j) {
				return KleeneMatrix.this.getAt(i, j).add(el.getAt(i, j));
			}
		});
	}

	@Override
	public KleeneMatrix<T> mul(KleeneMatrix<T> el) {
		if (this.n != el.m) {
			throw new IllegalArgumentException("matrices are not compatible for multiplication");
		}
		return createInstance(m, n, new BiFunction<Integer, Integer, T>() {
			@Override
			public T apply(Integer i, Integer j) {
				T acc = zero;
				for (int k = 0; k < KleeneMatrix.this.n; ++k) {
					acc = acc.add(KleeneMatrix.this.getAt(i, k).mul(el.getAt(k, j)));
				}
				return acc;
			}
		});
	}

	private KleeneMatrix<T> createIdentity() {
		return this.createInstance(m, n, new BiFunction<Integer, Integer, T>() {
			@Override
			public T apply(Integer i, Integer j) {
				return i == j ? one : zero;
			}
		});
	}

	private KleeneMatrix<T> createClone() {
		return this.createInstance(m, n, new BiFunction<Integer, Integer, T>() {
			@Override
			public T apply(Integer i, Integer j) {
				return KleeneMatrix.this.getAt(i, j);
			}
		});
	}

	private void leftMulRowByFactor(int i, T factor) {
		for (int j = 0; j < this.n; ++j) {
			this.setAt(i, j, factor.mul(this.getAt(i, j)));
		}
	}

	private void addLeftScaledRowToRow(int isrc, T factor, int idst) {
		for (int j = 0; j < this.n; ++j) {
			this.setAt(idst, j, factor.mul(this.getAt(isrc, j))
					.add(this.getAt(idst, j)));
		}
	}

	@Override
	public KleeneMatrix<T> close() {
		if (n != m) {
			//actually it's possible we should have only allowed square matrices but anyways
			throw new RuntimeException("can only close square matrices");
		}
		//clone matrix
		final KleeneMatrix<T> left = this.createClone();
		final KleeneMatrix<T> right = this.createIdentity();
		
		//forward triangularization
		for (int k = 0; k < n; ++k) {
			final T factor = left.getAt(k, k).close();
			left.setAt(k, k, zero);
			left.leftMulRowByFactor(k, factor);
			right.leftMulRowByFactor(k, factor);
			for (int i = k + 1; i < n; ++i) {
				final T otherFactor = left.getAt(i, k);
				left.setAt(i, k, zero);
				left.addLeftScaledRowToRow(k, otherFactor, i);
				right.addLeftScaledRowToRow(k, otherFactor, i);
			}
		}
		//back substitution
		for (int j = n - 1; j >= 0; --j) {
			for (int i = j - 1; i >= 0; --i) {
				final T factor = left.getAt(i, j);
				left.setAt(j - 1, j, zero);
				left.addLeftScaledRowToRow(j, factor, i);
				right.addLeftScaledRowToRow(j, factor, i);
			}
		}
		return right;
	}
	
	public KleeneMatrix<T> transpose() {
		//this is not efficient, but
		//let's just go with it for now
		return createInstance(n, m, new BiFunction<Integer, Integer, T>() {
			@Override
			public T apply(Integer i, Integer j) {
				return KleeneMatrix.this.getAt(i, j);
			}
		});
	}
	
	public int getM() { return this.m; }
	public int getN() { return this.n; }

	private KleeneMatrix<T> createInstance(int m, int n,
			BiFunction<Integer, Integer, T> entryPolicy) {
		final KleeneMatrix<T> ret = new KleeneMatrix<T>(
				new ArrayList<List<T>>(m), m, n, zero, one);
		for (int i = 0; i < m; ++i) {
			final List<T> row = new ArrayList<T>(m);
			ret.data.add(row);
			for (int j = 0; j < n; ++j) {
				row.add(entryPolicy.apply(i, j));
			}
		}
		return ret;
	}
	
	public T getAt(int i, int j) {
		indexCheck(i, j);
		return data.get(i).get(j);
	}

	public void setAt(int i, int j, T val) {
		indexCheck(i, j);
		data.get(i).set(j, val);
	}

	private void indexCheck(int i, int j) {
		if (i < 0) {
			throw new IllegalArgumentException("i less than 0");
		}
		if (j < 0) {
			throw new IllegalArgumentException("j less than 0");
		}
		if (i >= m) {
			throw new IllegalArgumentException("i >= m (" + m + ")");
		}
		if (j >= n) {
			throw new IllegalArgumentException("j >= n (" + n + ")");
		}
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (List<T> row : data) {
			sb.append("[");
			for(T el : row) {
				sb.append(el + "\t");
			}
			sb.append("]\n");
		}
		return sb.toString();
	}
	
}
