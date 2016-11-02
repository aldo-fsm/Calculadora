package estruturasDados.pilha;

public class pilhaArray<T> implements Pilha<T> {
	private Object[] elementos;

	@Override
	public void push(T elemento) {
		if (isEmpty()) {
			elementos = new Object[1];
			elementos[0] = elemento;
		} else {
			Object[] aux = new Object[elementos.length + 1];
			for (int i = 0; i < elementos.length; i++) {
				aux[i] = elementos[i];
			}
			aux[elementos.length] = elemento;
			elementos = aux;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public T pop() {
		if (isEmpty()) {
			throw new NullPointerException("the stack is empty");
		} else if (size() == 1) {
			T retorno = (T) elementos[size() - 1];
			makeEmpty();
			return retorno;
		} else {
			T retorno = (T) elementos[size() - 1];
			Object[] aux = new Object[size() - 1];
			for (int i = 0; i < aux.length; i++) {
				aux[i] = elementos[i];
			}
			elementos = aux;
			return retorno;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public T top() {
		if (isEmpty()) {
			return null;
		} else {
			return (T) elementos[size() - 1];
		}
	}

	@Override
	public void makeEmpty() {
		elementos = null;
	}

	@Override
	public boolean isEmpty() {
		boolean isEmpty = false;
		if (elementos == null) {
			isEmpty = true;
		}
		return isEmpty;
	}

	@Override
	public int size() {
		if (elementos == null) {
			return 0;
		}
		return elementos.length;
	}
}
