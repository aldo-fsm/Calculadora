package estruturasDados.pilha;

public class pilhaArray<T> {
	private Object[] elementos;
	
	public void push(T elemento){
		if(isEmpty()){
			elementos = new Object[1];
			elementos[0] = elemento;
		}else{
			Object[] aux = new Object[elementos.length + 1];
			for(int i = 0; i<elementos.length; i++){
				aux[i] = elementos[i];
			}
			aux[elementos.length] = elemento;
			elementos = aux;
		}
	}
	
	public T pop(){
		if(isEmpty()){
			throw new NullPointerException("the stack is empty");
		}else if(size() == 1){
			T retorno = (T) elementos[size()-1];
			makeEmpty();
			return retorno;
		}else{
			T retorno = (T) elementos[size()-1];
			Object[] aux = new Object[size() - 1];
			for(int i = 0; i<aux.length;i++){
				aux[i] = elementos[i];
			}
			elementos = aux;
		return retorno;
		}
	}
	
	public T top(){
		if(isEmpty()){
			return null;
		}else {
			return (T) elementos[size() - 1];  
		}
	}
	
	public void makeEmpty() {
		elementos = null;
	}

	public boolean isEmpty(){
		boolean isEmpty = false;
		if(elementos == null){
			isEmpty = true;
		}
		return isEmpty;
	}
	
	public int size(){
		if(elementos == null){
			return 0;
		}
		return elementos.length;
	}
}
