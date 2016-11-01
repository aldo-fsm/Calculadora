package estruturasDados.arvore;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import estruturaDados.fila.Fila;
import estruturasDados.lista.ListaArray;
import estruturasDados.pilha.pilhaArray;

public class ArvoreExpressoes {
	private Node<String> root;
	private Scanner scanner;
	private MathContext mathContext = new MathContext(100, RoundingMode.HALF_EVEN);
	// operadores em ordem de precedencia
	private static final String operadores = "%^~*/-+";

	public void acrescentaNaExpressao(String expressao) {
		expressao = "(" + expressaoEmOrdem() + expressao + ")";
		armazenarExpressao(expressao);
	}

	public void armazenarExpressao(String expressao) {
		validarExpressao(expressao);
		ArrayList<String> lista = new ArrayList<String>();
		expressao = "(" + expressao + ")";
		boolean eSinal = false;
		for (int i = 0; i < expressao.length(); i++) {
			if (expressao.charAt(i) == ' ') {
				continue;
			} else if ("()".contains(expressao.charAt(i) + "")) {
				lista.add(expressao.charAt(i) + "");
			} else {
				String numero = "";
				if (operadores.contains(expressao.charAt(i) + "") && expressao.charAt(i) != '-' && !eSinal) {
					lista.add(expressao.charAt(i) + "");
					eSinal = true;
					continue;
				} else {
					numero += expressao.charAt(i);
					i++;
				}
				boolean variavelJaNomeada = false;
				while (i < expressao.length()) {
					if ("()".contains(expressao.charAt(i) + "") || operadores.contains(expressao.charAt(i) + "")) {
						break;
					}
					if (!variavelJaNomeada) {
						numero += expressao.charAt(i);
					}
					if (!"0123456789".contains(expressao.charAt(i + 1) + "")) {
						variavelJaNomeada = true;
					}
					i++;
				}
				lista.add(numero);
				if (i < expressao.length())
					lista.add(expressao.charAt(i) + "");
				eSinal = false;
			}
		}
		Fila<String> filaExpressao = adicionarParenteses(lista);
		tradutorDeExpressoes(filaExpressao);
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
		if (root.getLeftNode() == null)
			return new BigDecimal(root.getElemento());
		ListaArray<String> variaveis = new ListaArray<String>();
		ListaArray<BigDecimal> valoresCorrespondentes = new ListaArray<BigDecimal>();
		return auxCalcularExpressao(root, variaveis, valoresCorrespondentes);
	}

	public void setPrecision(int precision) {
		mathContext = new MathContext(precision, mathContext.getRoundingMode());
	}

	private void tradutorDeExpressoes(Fila<String> expressao) {
		root = null;
		pilhaArray<String> pilhaOperadores = new pilhaArray<String>();
		pilhaArray<Node<String>> pilhaArvores = new pilhaArray<Node<String>>();
		String valor;
		while (!expressao.isEmpty()) {
			if (expressao.espia().equals(" ")) {
				expressao.dequeue();
				continue;
			}
			// caso parenteses abrindo
			else if (expressao.espia().equals("(")) {
				expressao.dequeue();
				if (expressao.espia().equals(")") || expressao.espia().equals("(") || expressao.espia().equals(" ")) {
					expressao.dequeue();
					continue;
				}
				Node<String> nodeAux = new Node<String>();
				valor = expressao.dequeue();
				nodeAux.setElemento(valor);
				pilhaArvores.push(nodeAux);
				if (expressao.espia().equals(")") || expressao.espia().equals("(") || expressao.espia().equals(" ")) {
					expressao.dequeue();
					continue;
				}
				pilhaOperadores.push(expressao.dequeue() + "");
			}
			// caso seja um operador
			else if (operadores.contains(expressao.espia())) {
				pilhaOperadores.push(expressao.dequeue() + "");
			}
			// caso seja um valor
			else if (!expressao.espia().equals(")")) {
				Node<String> nodeAux = new Node<String>();
				valor = expressao.dequeue();
				nodeAux.setElemento(valor);
				pilhaArvores.push(nodeAux);
			}
			// caso seja um parenteses fechando
			else {
				expressao.dequeue();
				Node<String> novoElemento = new Node<String>();
				novoElemento.setElemento(pilhaOperadores.pop());
				novoElemento.setRightNode(pilhaArvores.pop());
				novoElemento.setLeftNode(pilhaArvores.pop());
				pilhaArvores.push(novoElemento);
			}
		}
		adicionarNode(pilhaArvores.pop());
	}

	private Fila<String> adicionarParenteses(List<String> expressao) {
		Fila<String> retorno = new Fila<String>();
		if (expressao.size() == 3) {
			retorno.enqueue(expressao.get(1));
			return retorno;
		} else {
			String left = "", right = "";
			char operador = ' ';
			int index, i, inicio, fim;
			Fila<String> retornoAuxtoRight = new Fila<String>(), retornoAuxtoLeft = new Fila<String>();
			while (expressao.size() > 1) {
				fim = expressao.indexOf(")");
				for (inicio = fim; inicio > 0; inicio--) {
					if (expressao.get(inicio).equals("("))
						break;
				}
				expressao.remove(inicio);
				expressao.remove(fim - 1);
				i = 0;
				while (i < operadores.length()) {
					operador = operadores.charAt(i);
					List<String> subExpressao = expressao.subList(inicio, fim - 1);
					index = inicio + subExpressao.indexOf(operador + "");
					left = index > 0 ? expressao.get(index - 1) : "";
					right = index < expressao.size() ? expressao.get(index + 1) : "";
					if (index > inicio && index < fim) {
						boolean colocaAdireita = retornoAuxtoRight.isEmpty();
						if (colocaAdireita) {
							while (!retorno.isEmpty()) {
								retornoAuxtoRight.enqueue(retorno.dequeue());
							}
							colocaAdireita = false;
						} else{
							while (!retorno.isEmpty()) {
								retornoAuxtoLeft.enqueue(retorno.dequeue());
							}
						}
						if (left.charAt(0) == '(' && right.charAt(0) != '(') {
							retorno.enqueue("(");
							if (!colocaAdireita) {
								while (!retornoAuxtoRight.isEmpty()) {
									retorno.enqueue(retornoAuxtoRight.dequeue());
								}
								colocaAdireita = true;
							} else{
								while (!retornoAuxtoLeft.isEmpty()) {
									retorno.enqueue(retornoAuxtoLeft.dequeue());
								}
							}
							retorno.enqueue(operador + "");
							retorno.enqueue(right);
							retorno.enqueue(")");
						} else if (right.charAt(0) == '(' && left.charAt(0) != '(') {
							retorno.enqueue("(");
							retorno.enqueue(left);
							retorno.enqueue(operador + "");
							if (!colocaAdireita) {
								while (!retornoAuxtoRight.isEmpty()) {
									retorno.enqueue(retornoAuxtoRight.dequeue());
								}
								colocaAdireita = false;
							} else{
								while (!retornoAuxtoLeft.isEmpty()) {
									retorno.enqueue(retornoAuxtoLeft.dequeue());
								}
							}
							retorno.enqueue(")");
						} else if (right.charAt(0) != '(' && left.charAt(0) != '(') {
							retorno.enqueue("(");
							retorno.enqueue(left);
							retorno.enqueue(operador + "");
							retorno.enqueue(right);
							retorno.enqueue(")");
						} else {
							retorno.enqueue("(");
							System.out.println(1);
							while (!retornoAuxtoLeft.isEmpty()) {
								System.out.print(retornoAuxtoLeft.espia());
								retorno.enqueue(retornoAuxtoLeft.dequeue());
							}
							System.out.print(operador + "");
							retorno.enqueue(operador + "");
							while (!retornoAuxtoRight.isEmpty()) {
								System.out.print(retornoAuxtoRight.espia());
								retorno.enqueue(retornoAuxtoRight.dequeue());
							}
							System.out.println();
							retorno.enqueue(")");
						}
						expressao.add(index, "(" + left + operador + right + ")");
						expressao.remove(index + 2);
						expressao.remove(index + 1);
						expressao.remove(index - 1);
						fim -= 2;
					} else {
						i++;
					}
				}
			}
			return retorno;
		}
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
				String numeros = "-0123456789";
				if (numeros.indexOf(raiz.getElemento().charAt(0)) == -1) {
					if (variaveis.contem(raiz.getElemento().charAt(0) + "")) {
						return valoresCorrespondentes.get(variaveis.indexOf(raiz.getElemento().charAt(0) + ""));
					} else {
						scanner = new Scanner(System.in);
						variaveis.adicionar(raiz.getElemento().charAt(0) + "");
						System.out.println("informe o valor de " + raiz.getElemento().charAt(0));
						BigDecimal tmp = new BigDecimal(scanner.nextLine());
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
				boolean indiceNegativo = false;
				if (expressao2.compareTo(BigDecimal.ZERO) < 0) {
					indiceNegativo = true;
					expressao2 = BigDecimal.ZERO.subtract(expressao2);
				}
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
				if (indiceNegativo)
					return BigDecimal.ONE.divide(retorno, mathContext);
				return retorno;
			case "~":

				if (expressao2.intValue() < 0)
					return BigDecimal.ONE.divide(raiz(expressao1, BigDecimal.ZERO.subtract(expressao2).intValue()),
							mathContext);
				return raiz(expressao1, expressao2.intValue());
			case "%":
				return expressao1.remainder(expressao2);
			}
		}
		return BigDecimal.ZERO;
	}

	private void validarExpressao(String expressao) {
		pilhaArray<Character> pilha = new pilhaArray<Character>();
		boolean expressaoInvalida = false;
		int posicaoErro = -1;
		try {
			int j = -4;
			for (int i = 0; i < expressao.length(); i++) {
				posicaoErro = i;
				if (expressao.charAt(i) == '(') {
					j = i;
					pilha.push('(');
				} else if (expressao.charAt(i) == ')') {
					if (j >= i - 3) {
						expressaoInvalida = true;
					}
					pilha.pop();
				}
			}
			if (!pilha.isEmpty()) {
				posicaoErro = j;
				expressaoInvalida = true;
			}
		} catch (RuntimeException e) {
			expressaoInvalida = true;
		}
		if (expressaoInvalida) {
			String indicadorErro = "";
			int procuraErro = 0;
			while (procuraErro < posicaoErro) {
				indicadorErro += " ";
				procuraErro++;
			}
			indicadorErro += "^";
			throw new InvalidParameterException("Expressao mal formada\n" + expressao + "\n" + indicadorErro);
		}
	}

	private void auxInformaVariaveis(Node<String> raiz, ListaArray<String> variaveis,
			ListaArray<String> valoresCorrespondentes) {
		if (raiz != null) {
			if (raiz.getElemento() != "") {
				auxInformaVariaveis(raiz.getLeftNode(), variaveis, valoresCorrespondentes);
				if (raiz.getLeftNode() == null && raiz.getRightNode() == null) {
					String numeros = "-0123456789";
					if (numeros.indexOf(raiz.getElemento().charAt(0)) == -1) {
						if (variaveis.contem(raiz.getElemento().charAt(0) + "")) {
							raiz.setElemento(
									valoresCorrespondentes.get(variaveis.indexOf(raiz.getElemento().charAt(0) + "")));
						} else {
							scanner = new Scanner(System.in);
							variaveis.adicionar(raiz.getElemento().charAt(0) + "");
							System.out.println("informe o valor de " + raiz.getElemento().charAt(0));
							valoresCorrespondentes.adicionar(scanner.nextLine());
							raiz.setElemento(
									valoresCorrespondentes.get(variaveis.indexOf(raiz.getElemento().charAt(0) + "")));
						}
					}
				}
				auxInformaVariaveis(raiz.getRightNode(), variaveis, valoresCorrespondentes);
			}
		}

	}

	private BigDecimal raiz(BigDecimal valor, int indice) {
		if (valor.intValue() < 0 && indice % 2 == 0) {
			throw new ArithmeticException("raiz par de numero negativo");
		} else if (indice == 1) {
			return valor;
		}
		int k2 = indice - 1;
		int lastCorrectDigit = -1;
		int precisao = mathContext.getPrecision();
		RoundingMode roundingMode = mathContext.getRoundingMode();
		MathContext tempMathContext = new MathContext(precisao + 5, roundingMode);
		BigDecimal resultado = BigDecimal.ONE;
		BigDecimal auxResultado = resultado;
		BigDecimal k1 = (BigDecimal.ONE).divide(BigDecimal.valueOf(indice), tempMathContext);
		do {
			resultado = k1.multiply(
					resultado.multiply(BigDecimal.valueOf(k2)).add(valor.divide(resultado.pow(k2), tempMathContext)));
			lastCorrectDigit = firstDifferentDigit(resultado, auxResultado);
			auxResultado = resultado;
		} while (lastCorrectDigit < precisao);
		return resultado.setScale(precisao, roundingMode).stripTrailingZeros();
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
