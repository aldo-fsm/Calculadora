package estruturasDados.fila;

import java.util.List;

import estruturasDados.lista.ListaArray;

public class FilaArray<T> implements Fila<T> {

	private Object[] array;
	private int fim = -1;

	public FilaArray() {
		array = new Object[10];
	}

	public FilaArray(ListaArray<T> lista) {
		for (int i = 0; i < lista.size(); i++) {
			enqueue(lista.get(i));
		}
	}

	@Override
	public void enqueue(T elemento) {
		if (fim < 0) {
			array[++fim] = elemento;
		} else {
			if (fim + 1 >= array.length) {
				redimensionar(array.length * 2);
			}
			array[++fim] = elemento;
		}

	}

	@Override
	public T dequeue() {

		if (fim >= 0) {
			T retorno = (T) array[0];
			int aux = 0;
			while (aux < fim) {
				array[aux] = array[aux + 1];
				aux++;
			}
			fim--;
			return retorno;
		}

		return null;
	}

	@Override
	public int size() {
		return fim + 1;
	}

	@Override
	public String toString() {
		if (fim >= 0) {
			String retorno = "[ ";
			for (int i = 0; i < fim; i++) {
				retorno += array[i] + ", ";
			}
			retorno += array[fim];
			return retorno + " ]";
		} else {
			return "[]";
		}
	}

	@Override
	public boolean isEmpty() {
		return fim > -1 ? false : true;
	}

	private void redimensionar(int novoTamanho) {
		Object[] novoArray = (Object[]) new Object[novoTamanho];
		for (int i = 0; i <= fim && i < novoTamanho; i++) {
			novoArray[i] = array[i];
		}
		array = novoArray;
	}

	@Override
	public T peek() {
		if (fim > -1) {
			return (T) array[fim];
		}
		return null;
	}

}
