package estruturasDados.lista;

public interface Lista<T> {

	public void adicionar(T elemento);

	public boolean contem(T elemento);

	public int indexOf(T elemento);

	public void remover(int posicao);

	public T get(int posicao);

	public int size();

}
