package estruturasDados.arvore;

public class Node<T> {

	private Node<T> rightNode;
	private Node<T> leftNode;
	private T elemento;

	public Node() {}
	
	public Node(T elemento){
		this.elemento = elemento;
	}
	
	public Node<T> getRightNode() {
		return rightNode;
	}

	public void setRightNode(Node<T> rightNode) {
		this.rightNode = rightNode;
	}

	public Node<T> getLeftNode() {
		return leftNode;
	}

	public void setLeftNode(Node<T> leftNode) {
		this.leftNode = leftNode;
	}

	public T getElemento() {
		return elemento;
	}

	public void setElemento(T elemento) {
		this.elemento = elemento;
	}

}
