package estruturasDados;

import estruturasDados.tads.Pilha;

public class PilhaArray implements Pilha<Object> {

	private Object[] array;
	private int topo;

	public PilhaArray() {
		array = new Object[10];
		topo = -1;
	}

	@Override
	public void push(Object elemento) {
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
	public Object pop() {

		if (topo == -1) {
			return null;
		} else {
			Object retorno = array[topo];
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
}
