
package arvore;

import java.math.BigDecimal;
import java.util.Scanner;
import java.util.Stack;

public class ArvoreExpressoes {
	private Node<String> root;

	private void realocarNode(Node<String> novoElemento) {
		if (root == null) {
			root = novoElemento;
		} else if (root.getRightNode() == null) {
			root.setRightNode(novoElemento);
		} else if (root.getLeftNode() == null) {
			root.setLeftNode(novoElemento);
		}
	}
	public void armazeneExpressao(String expressao) {
		String Operadores = "+-*/", valor;
		Stack<String> pilhaOperadores = new Stack<String>();
		Stack<Node<String>> pilhaArvores = new Stack<Node<String>>();
		for (int i = 0; i < expressao.length(); i++) {
			valor = "";
			if (expressao.charAt(i) == ("(").charAt(0) && Operadores.indexOf(expressao.charAt(i)) == -1) {
				while (Operadores.indexOf(expressao.charAt(i)) == -1 && expressao.charAt(i) != (")").charAt(0)) {
					if (expressao.charAt(i) == ("(").charAt(0)) {
						i++;
						continue;
					}
					valor = valor + expressao.charAt(i);
					i++;
				}
				if(expressao.charAt(i) == (")").charAt(0)){
					continue;
				}
				Node<String> nodeAux = new Node<String>();
				nodeAux.setElemento(valor);
				pilhaArvores.push(nodeAux);
				pilhaOperadores.push(expressao.charAt(i) + "");
			} else if (expressao.charAt(i) != (")").charAt(0) && Operadores.indexOf(expressao.charAt(i)) == -1) {
				valor = "";
				while (expressao.charAt(i) != (")").charAt(0)) {
					valor = valor + expressao.charAt(i);
					i++;
				}
				i--;
				Node<String> nodeAux = new Node<String>();
				nodeAux.setElemento(valor);
				pilhaArvores.push(nodeAux);
			} else if(Operadores.indexOf(expressao.charAt(i)) != -1){
				pilhaOperadores.push(expressao.charAt(i) + "");
				i++;
				valor = "";
				while (expressao.charAt(i) != (")").charAt(0)) {
					valor = valor + expressao.charAt(i);
					i++;
				}
				i--;
				Node<String> novoElemento = new Node<String>();
				novoElemento.setElemento(valor);
				pilhaArvores.push(novoElemento);
			}else{
				Node<String> novoElemento = new Node<String>();
				novoElemento.setElemento(pilhaOperadores.pop());
				if(expressao.length() > i+1){
					novoElemento.setRightNode(pilhaArvores.pop());
					novoElemento.setLeftNode(pilhaArvores.pop());
					pilhaArvores.push(novoElemento);
				}else{
					realocarNode(novoElemento);
					realocarNode(pilhaArvores.pop());
					realocarNode(pilhaArvores.pop());
				}
			}
		}
	}

	public void expressaoArmazenada() {
		auxExpressaoArmazenada(root);
		System.out.println("");
	}

	private void auxExpressaoArmazenada(Node<String> node) {
		if (node != null) {
			if (node.getLeftNode() == null && node.getRightNode() == null) {
				System.out.print(node.getElemento());
			} else {
				System.out.print("(");
				auxExpressaoArmazenada(node.getLeftNode());
				System.out.print(node.getElemento());
				auxExpressaoArmazenada(node.getRightNode());
				System.out.print(")");
			}
		}
	}

	public BigDecimal calcularExpressao() {
		return auxCalcularExpressao(root);
	}

	private BigDecimal auxCalcularExpressao(Node<String> raiz){
		if(root != null){
			if(raiz.getLeftNode() == null && raiz.getRightNode() == null){
				String numeros = "0123456789";
				if(numeros.indexOf(raiz.getElemento().charAt(0)) == -1){
					System.out.println("informe o valor de " + raiz.getElemento().charAt(0));
					Scanner sc = new Scanner(System.in);
					BigDecimal tmp = new BigDecimal(sc.nextInt());
					return tmp;
				}
				return new BigDecimal(raiz.getElemento());
			}
			switch (raiz.getElemento()) {
			case "*":
				return auxCalcularExpressao(raiz.getLeftNode()).multiply(auxCalcularExpressao(raiz.getRightNode()));
			case "/":
				return auxCalcularExpressao(raiz.getLeftNode()).divide(auxCalcularExpressao(raiz.getRightNode()));
			case "-":
				return auxCalcularExpressao(raiz.getLeftNode()).subtract(auxCalcularExpressao(raiz.getRightNode()));
			case "+":
				return auxCalcularExpressao(raiz.getLeftNode()).add(auxCalcularExpressao(raiz.getRightNode()));
			}
		}
		return BigDecimal.ZERO;
	}
}