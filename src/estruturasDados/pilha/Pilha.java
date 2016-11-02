package estruturasDados.pilha;

public interface Pilha<T> {

	public void push(T elemento);

	public T pop();

	public T top();

	public void makeEmpty();

	public boolean isEmpty();

	public int size();
}
