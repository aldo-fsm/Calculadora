package estruturasDados.lista;

import estruturasDados.fila.Fila;

public class ListaArray<T> implements Lista<T> {
	private Object[] elementos;
	private int size = 0;

	public ListaArray() {
	}

	public ListaArray(Fila<T> fila) {
		while (!fila.isEmpty()) {
			adicionar(fila.dequeue());
		}
	}

	@Override
	public void adicionar(T elemento) {
		size++;
		Object[] aux = new Object[size];
		int i = 1;
		for (; i < size; i++) {
			aux[i - 1] = elementos[i - 1];
		}
		aux[i - 1] = elemento;
		elementos = aux;
	}

	@Override
	public boolean contem(T elemento) {
		if (elementos != null) {
			int i = 0;
			while (i < elementos.length) {
				if (elementos[i].equals(elemento)) {
					return true;
				}
				i++;
			}
		}
		return false;
	}

	@Override
	public int indexOf(T elemento) {
		int i = 0;
		if (elementos != null) {
			while (i < elementos.length) {
				if (elementos[i].equals(elemento)) {
					break;
				}
				i++;
			}
		}
		return i;
	}

	@Override
	public void remover(int posicao) {
		Object[] aux = new Object[size - 1];
		int j = 0;
		for (int i = 0; i < size; i++) {
			if (i == posicao) {
				continue;
			} else {
				aux[j] = elementos[i];
				j++;
			}
		}
		elementos = aux;
		size--;
	}

	@SuppressWarnings("unchecked")
	@Override
	public T get(int posicao) {
		return (T) elementos[posicao];
	}

	@Override
	public int size() {
		return size;
	}

}
