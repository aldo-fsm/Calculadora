package estruturasDados.arvore;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Scanner;

import estruturasDados.lista.ListaArray;
import estruturasDados.pilha.pilhaArray;

public class ArvoreExpressoes {
	private Node<String> root;
	private MathContext mathContext = new MathContext(100, RoundingMode.HALF_EVEN);
	
	public void acrescentaNaExpressao(String expressao){
		tradutorDeExpressoes("(" + expressaoEmOrdem() + expressao + ")");
	}
	
	public void armazeneExpressao(String expressao) {
		if(expressao.charAt(0)!='('){
			tradutorDeExpressoes("(" + expressao + ")");
		}else{
			tradutorDeExpressoes(expressao);
		}
	}
		
	public String expressaoEmOrdem() {
		return auxExpressaoEmOrdem(root);
	}
	
	public BigDecimal calcularExpressao() {
		ListaArray<String> variaveis = new ListaArray<String>(); 
		ListaArray<BigDecimal> valoresCorrespondentes = new ListaArray<BigDecimal>();
		return auxCalcularExpressao(root,variaveis,valoresCorrespondentes);
	}
	
	private void tradutorDeExpressoes(String expressao){
		root = null;
		pilhaArray<String> pilhaOperadores = new pilhaArray<String>();
		pilhaArray<Node<String>> pilhaArvores = new pilhaArray<Node<String>>();
		String Operadores = "+-*/^", valor;
		for (int i = 0; i < expressao.length(); i++) {
			if(expressao.charAt(i) == ' '){
				continue;
			}
			valor = "";
			//caso parenteses abrindo
			if (expressao.charAt(i) == '(') {
				while (Operadores.indexOf(expressao.charAt(i)) == -1 && expressao.charAt(i) != (")").charAt(0)) {
					if(expressao.charAt(i) == ' '){
						i++;
						continue;
					}
					if (expressao.charAt(i) == '(') {
						i++;
						continue;
					}
					valor = valor + expressao.charAt(i);
					i++;
				}
				Node<String> nodeAux = new Node<String>();
				nodeAux.setElemento(valor);
				pilhaArvores.push(nodeAux);
				if(expressao.charAt(i) == ')'){
					continue;
				}
				pilhaOperadores.push(expressao.charAt(i) + "");
			} 
			//caso seja um operador
			else if(Operadores.indexOf(expressao.charAt(i)) != -1){
				pilhaOperadores.push(expressao.charAt(i) + "");
			}
			//caso seja um valor
			else if (expressao.charAt(i) != ')') {
				valor = "";
				while (expressao.charAt(i) != ')') {
					if(expressao.charAt(i) == ' '){
						i++;
						continue;
					}
					valor = valor + expressao.charAt(i);
					i++;
				}
				i--;
				Node<String> nodeAux = new Node<String>();
				nodeAux.setElemento(valor);
				pilhaArvores.push(nodeAux);
			}
			//caso seja um parenteses fechando
			else{
				Node<String> novoElemento = new Node<String>();
				novoElemento.setElemento(pilhaOperadores.pop());
				novoElemento.setRightNode(pilhaArvores.pop());
				novoElemento.setLeftNode(pilhaArvores.pop());
				pilhaArvores.push(novoElemento);
			}
		}
		Node<String> novoElemento = new Node<String>();
		adicionarNode(pilhaArvores.pop());
	}

	private String auxExpressaoEmOrdem(Node<String> node) {
		if (node != null) {
			if (node.getLeftNode() == null && node.getRightNode() == null) {
				return node.getElemento();
			} else {
				return "(" + auxExpressaoEmOrdem(node.getLeftNode()) + node.getElemento() + auxExpressaoEmOrdem(node.getRightNode()) + ")";
			}
		}
		return null;
	}

	private void adicionarNode(Node<String> novoElemento) {
		if (root == null) {
			root = novoElemento;
		} else if (root.getRightNode() == null) {
			root.setRightNode(novoElemento);
		} else if (root.getLeftNode() == null) {
			root.setLeftNode(novoElemento);
		}
	}
	
	private BigDecimal auxCalcularExpressao(Node<String> raiz, ListaArray<String> variaveis,ListaArray<BigDecimal> valoresCorrespondentes){
		if(root != null){
			if(raiz.getLeftNode() == null && raiz.getRightNode() == null){
				String numeros = "0123456789";
				if(numeros.indexOf(raiz.getElemento().charAt(0)) == -1){
					if(variaveis.contem(raiz.getElemento().charAt(0)+"")){
						return valoresCorrespondentes.get(variaveis.indexOf(raiz.getElemento().charAt(0)+""));
					}else{
						variaveis.adicionar(raiz.getElemento().charAt(0) + "");
						System.out.println("informe o valor de " + raiz.getElemento().charAt(0));
						Scanner sc = new Scanner(System.in);
						BigDecimal tmp = new BigDecimal(sc.nextLine());
						valoresCorrespondentes.adicionar(tmp);
						return tmp;
					}
				}
				return new BigDecimal(raiz.getElemento());
			}
			BigDecimal expressao1 = auxCalcularExpressao(raiz.getLeftNode(),variaveis,valoresCorrespondentes);
			BigDecimal expressao2 = auxCalcularExpressao(raiz.getRightNode(),variaveis,valoresCorrespondentes);
			switch (raiz.getElemento()) {
			case "*":
				return expressao1.multiply(expressao2);
			case "/":
				return expressao1.divide(expressao2, mathContext);
			case "-":
				return expressao1.subtract(expressao2);
			case "+":
				return expressao1.add(expressao2);
			case "^":
				BigDecimal retorno = expressao1.pow(expressao2.intValue());
				BigDecimal numerosAposVirgula = expressao2.subtract(BigDecimal.valueOf(expressao2.intValue()));
				int valorRaiz = 1;
				for(int i=0; i< numerosAposVirgula.toString().length()-2;i++){
					valorRaiz = valorRaiz*10;
					numerosAposVirgula = numerosAposVirgula.multiply(BigDecimal.TEN);
				}
				BigDecimal retornoDecimal = BigDecimal.ZERO;
				if(numerosAposVirgula.intValue()!=0){
					retornoDecimal = auxCalcularExpressao(raiz.getLeftNode(),variaveis,valoresCorrespondentes);
					retornoDecimal = retornoDecimal.pow(numerosAposVirgula.intValue());
				}
				
				retorno = retorno.multiply(raiz(retornoDecimal, valorRaiz, 20));
				return retorno;
			}
		}
		return BigDecimal.ZERO;
	}
	
	private BigDecimal raiz(BigDecimal valor,int indice,int iteracoes){
		BigDecimal resultado = BigDecimal.ONE;
		for(int i = 0; i < iteracoes; i++){
			resultado = resultado.subtract(resultado.pow(indice).subtract(valor).divide(resultado.pow(indice-1).multiply(BigDecimal.valueOf(indice)),mathContext));
		}
		return resultado;
	}
}
