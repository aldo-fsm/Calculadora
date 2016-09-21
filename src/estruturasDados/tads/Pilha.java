package estruturasDados.tads;

public interface Pilha<T> {

	public void push(T elemento);

	public T pop();
	
	public int size();
}
