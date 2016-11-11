package estruturasDados.pilha;

public class PilhaArray<T> implements Pilha<T> {

	private Object[] array;
	private int topo;

	public PilhaArray() {
		array = new Object[10];
		topo = -1;
	}

	@Override
	public void push(T elemento) {
		if (topo == -1) {
			topo = 0;
			array[topo] = elemento;
		} else {
			if (topo == array.length - 1) {
				redimensionar(array.length * 2);
			}
			array[++topo] = elemento;
		}
	}

	@Override
	public T pop() {

		if (topo == -1) {
			return null;
		} else {
			T retorno = (T) array[topo];
			array[topo--] = null;
			return retorno;
		}

	}

	@Override
	public String toString() {
		String retorno = "";
		for (int i = topo; i >= 0; i--) {
			retorno += array[i] + "\n";
		}
		return retorno;
	}

	private void redimensionar(int novoTamanho) {
		Object[] novoArray = new Object[novoTamanho];
		for (int i = 0; i <= topo && i < novoTamanho; i++) {
			novoArray[i] = array[i];
		}
		array = novoArray;
	}

	@SuppressWarnings("unchecked")
	@Override
	public T top() {
		if (topo >= 0)
			return (T) array[topo];
		else
			return null;
	}

	@Override
	public void makeEmpty() {
		topo = -1;
		array = new Object[array.length];
	}

	@Override
	public boolean isEmpty() {
		return topo == -1;
	}

	@Override
	public int size() {
		return topo + 1;
	}
}