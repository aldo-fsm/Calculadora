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
	private static final String operadores = "+-*/^~";

	public void acrescentaNaExpressao(String expressao) {
		tradutorDeExpressoes("(" + expressaoEmOrdem() + expressao + ")");
	}

	public void armazenarExpressao(String expressao) {
		int i = 0;
		boolean precisaAesquerda = false;
		boolean precisaAdireita = false;
		while (expressao.charAt(i) == ' ')
			i++;
		if (expressao.charAt(i) != '(')
			precisaAesquerda = true;
		i = expressao.length() - 1;
		while (expressao.charAt(i) == ' ')
			i--;
		if (expressao.charAt(i) != '(')
			precisaAesquerda = true;
		if (expressao.charAt(0) != '(' && expressao.charAt(expressao.length() - 1) != ')'
				|| precisaAdireita && precisaAesquerda) {
			tradutorDeExpressoes("(" + expressao + ")");
		} else if (expressao.charAt(0) != '(' && expressao.charAt(expressao.length() - 1) == ')' || precisaAesquerda) {
			tradutorDeExpressoes("(" + expressao);
		} else if (expressao.charAt(0) == '(' && expressao.charAt(expressao.length() - 1) != ')' || precisaAesquerda) {
			tradutorDeExpressoes(expressao + ")");
		} else {
			tradutorDeExpressoes(expressao);
		}
	}

	public String expressaoEmOrdem() {
		return auxExpressaoEmOrdem(root);
	}

	public void informarVariaveis() {
		ListaArray<String> variaveis = new ListaArray<String>();
		ListaArray<String> valoresCorrespondentes = new ListaArray<String>();
		auxInformaVariaveis(root, variaveis, valoresCorrespondentes);
	}

	public BigDecimal calcularExpressao() {
		ListaArray<String> variaveis = new ListaArray<String>();
		ListaArray<BigDecimal> valoresCorrespondentes = new ListaArray<BigDecimal>();
		return auxCalcularExpressao(root, variaveis, valoresCorrespondentes);
	}

	private void tradutorDeExpressoes(String expressao) {
		root = null;
		pilhaArray<String> pilhaOperadores = new pilhaArray<String>();
		pilhaArray<Node<String>> pilhaArvores = new pilhaArray<Node<String>>();
		String valor;
		for (int i = 0; i < expressao.length(); i++) {
			if (expressao.charAt(i) == ' ')
				continue;
			valor = "";
			// caso parenteses abrindo
			if (expressao.charAt(i) == '(') {
				while (operadores.indexOf(expressao.charAt(i)) == -1 && expressao.charAt(i) != (")").charAt(0)) {
					if (expressao.charAt(i) == ' ') {
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
				if (expressao.charAt(i) == ')') {
					continue;
				}
				pilhaOperadores.push(expressao.charAt(i) + "");
			}
			// caso seja um operador
			else if (operadores.indexOf(expressao.charAt(i)) != -1) {
				pilhaOperadores.push(expressao.charAt(i) + "");
			}
			// caso seja um valor
			else if (expressao.charAt(i) != ')') {
				valor = "";
				while (expressao.charAt(i) != ')') {
					if (expressao.charAt(i) == ' ') {
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
			// caso seja um parenteses fechando
			else {
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
				return "(" + auxExpressaoEmOrdem(node.getLeftNode()) + node.getElemento()
						+ auxExpressaoEmOrdem(node.getRightNode()) + ")";
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

	private BigDecimal auxCalcularExpressao(Node<String> raiz, ListaArray<String> variaveis,
			ListaArray<BigDecimal> valoresCorrespondentes) {
		if (root != null) {
			if (raiz.getLeftNode() == null && raiz.getRightNode() == null) {
				String numeros = "0123456789";
				if (numeros.indexOf(raiz.getElemento().charAt(0)) == -1) {
					if (variaveis.contem(raiz.getElemento().charAt(0) + "")) {
						return valoresCorrespondentes.get(variaveis.indexOf(raiz.getElemento().charAt(0) + ""));
					} else {
						Scanner sc = new Scanner(System.in);
						variaveis.adicionar(raiz.getElemento().charAt(0) + "");
						System.out.println("informe o valor de " + raiz.getElemento().charAt(0));
						BigDecimal tmp = new BigDecimal(sc.nextLine());
						valoresCorrespondentes.adicionar(tmp);
						return tmp;
					}
				}
				return new BigDecimal(raiz.getElemento());
			}
			BigDecimal expressao1 = auxCalcularExpressao(raiz.getLeftNode(), variaveis, valoresCorrespondentes);
			BigDecimal expressao2 = auxCalcularExpressao(raiz.getRightNode(), variaveis, valoresCorrespondentes);
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
				for (int i = 0; i < numerosAposVirgula.toString().length() - 2; i++) {
					valorRaiz = valorRaiz * 10;
					numerosAposVirgula = numerosAposVirgula.multiply(BigDecimal.TEN);
				}
				BigDecimal retornoDecimal = BigDecimal.ONE;
				if (numerosAposVirgula.intValue() != 0) {
					retornoDecimal = auxCalcularExpressao(raiz.getLeftNode(), variaveis, valoresCorrespondentes);
					retornoDecimal = retornoDecimal.pow(numerosAposVirgula.intValue());
					retorno = retorno.multiply(raiz(retornoDecimal, valorRaiz));
				}
				return retorno;
			case "~":
				return raiz(expressao1, expressao2.intValue());
			}
		}
		return BigDecimal.ZERO;
	}

	private void auxInformaVariaveis(Node<String> raiz, ListaArray<String> variaveis,
			ListaArray<String> valoresCorrespondentes) {
		if (raiz != null) {
			auxInformaVariaveis(raiz.getLeftNode(), variaveis, valoresCorrespondentes);
			if (raiz.getLeftNode() == null && raiz.getRightNode() == null) {
				String numeros = "0123456789";
				if (numeros.indexOf(raiz.getElemento().charAt(0)) == -1) {
					if (variaveis.contem(raiz.getElemento().charAt(0) + "")) {
						raiz.setElemento(
								valoresCorrespondentes.get(variaveis.indexOf(raiz.getElemento().charAt(0) + "")));
					} else {
						Scanner sc = new Scanner(System.in);
						variaveis.adicionar(raiz.getElemento().charAt(0) + "");
						System.out.println("informe o valor de " + raiz.getElemento().charAt(0));
						valoresCorrespondentes.adicionar(sc.nextLine());
						raiz.setElemento(
								valoresCorrespondentes.get(variaveis.indexOf(raiz.getElemento().charAt(0) + "")));
					}
				}
			}
			auxInformaVariaveis(raiz.getRightNode(), variaveis, valoresCorrespondentes);
		}

	}

	public void setPrecision(int precision) {
		mathContext = new MathContext(precision, mathContext.getRoundingMode());
	}

	private BigDecimal raiz(BigDecimal valor, int indice) {

		BigDecimal resultado = BigDecimal.ONE;
		BigDecimal auxResultado = resultado;

		BigDecimal k1 = (BigDecimal.ONE).divide(BigDecimal.valueOf(indice), mathContext);
		int k2 = indice - 1;

		int lastCorrectDigit = -1;

		while (true) {
			resultado = k1.multiply(
					resultado.multiply(BigDecimal.valueOf(k2)).add(valor.divide(resultado.pow(k2), mathContext)));

			lastCorrectDigit = firstDifferentDigit(resultado, auxResultado);
			resultado = resultado.setScale(lastCorrectDigit, RoundingMode.DOWN);
			auxResultado = resultado;

			if (lastCorrectDigit >= mathContext.getPrecision()) {
				break;
			}
		}
		return resultado.setScale(mathContext.getPrecision(), mathContext.getRoundingMode());
	}

	private int firstDifferentDigit(BigDecimal a, BigDecimal b) {
		String strA = a.toString();
		String strB = b.toString();
		int i = 0;
		while (i < strA.length() && i < strB.length()) {
			if (strA.charAt(i) != strB.charAt(i))
				break;
			i++;
		}
		return i;
	}
}
