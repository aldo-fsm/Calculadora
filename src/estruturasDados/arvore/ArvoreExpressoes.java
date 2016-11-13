package estruturasDados.arvore;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;

import estruturasDados.fila.Fila;
import estruturasDados.fila.FilaArray;
import estruturasDados.lista.Lista;
import estruturasDados.lista.ListaArray;
import estruturasDados.pilha.Pilha;
import estruturasDados.pilha.PilhaArray;
import exceptions.ExpressaoMalFormadaException;
import numeros.Racional;

public class ArvoreExpressoes {
	private Node<String> root;
	private Scanner scanner;
	// determina a precisao e o modo de arredondamento
	private MathContext mathContext = new MathContext(100, RoundingMode.HALF_EVEN);
	// operadores em ordem de precedencia
	private static final String operadores = "^~*%/-+";

	public void acrescentaNaExpressao(String expressao) {
		expressao = "(" + expressaoEmOrdem() + expressao + ")";
		armazenarExpressao(expressao);
	}

	/**
	 * Armazena uma expressÃƒÂ£o infixa na arvore de expressÃƒÂµes.
	 * 
	 * @param expressao
	 *            ExpressÃƒÂ£o em notaÃƒÂ§ÃƒÂ£o infixa
	 */
	@SuppressWarnings("unchecked")
	public void armazenarExpressao(String expressao) {

		validarExpressao(expressao);
		expressao = "(" + expressao + ")";
		ArrayList<Object> lista = new ArrayList<Object>();

		// coloca cada simbolo numa lista, agrupando numeros e nomes de
		// variaveis em strings
		for (int i = 0; i < expressao.length(); i++) {
			if (expressao.charAt(i) == ' ') {
				continue;
				// se for parenteses
			} else if ("()".contains(expressao.charAt(i) + "")) {
				lista.add(expressao.charAt(i) + "");
				// se for operador
			} else if (operadores.contains(expressao.charAt(i) + "")) {
				lista.add(expressao.charAt(i) + "");
				// se for um numero ou nome de variavel
			} else {
				String numero = extractNumber(expressao, i);
				lista.add(numero);
				i += numero.length() - 1;
			}
		}
		// loop para adicionar parenteses
		while (lista.size() > 1) {
			int fim = lista.indexOf(")");
			int inicio;
			for (inicio = fim; inicio > 0; inicio--) {
				if (lista.get(inicio).equals("("))
					break;
			}
			lista.remove(inicio);
			lista.remove(fim - 1);
			adicionarParenteses(lista, inicio, fim - 1);
		}
		Fila<String> fila = new FilaArray<String>();
		if (lista.get(0) instanceof ArrayList) {
			lista = (ArrayList<Object>) lista.get(0);
		} else {
			Object aux = lista.get(0);
			lista = new ArrayList<Object>();
			lista.add(aux);
		}
		for (int i = 0; i < lista.size(); i++) {
			fila.enqueue(lista.get(i).toString());
		}

		tradutorDeExpressoes(fila);
	}

	// coloca parenteses de acordo com a precedencia dos operadores
	private void adicionarParenteses(List<Object> expressao, int inicio, int fim) {
		int i = 0;
		boolean isNotBinary = false;
		while (i < operadores.length()) {
			char operador = operadores.charAt(i);
			List<Object> subExpressao = expressao.subList(inicio, fim);
			int index = inicio + subExpressao.indexOf(operador + "");
			if (index >= inicio && index < fim) {
				isNotBinary = index == inicio || (operadores + "(").contains(expressao.get(index - 1).toString());
				if (isNotBinary) {
					expressao.add(index, listConcat(operador, expressao.get(index + 1)));
					expressao.remove(index + 1);
					expressao.remove(index + 1);
					fim--;
				} else {
					String operador2 = null;
					boolean tmp = (operadores).contains(expressao.get(index + 1).toString());
					Object right = index < expressao.size() ? expressao.get(index + 1) : null;
					if (tmp) {
						operador2 = expressao.get(index + 1).toString();
						right = index < expressao.size() ? expressao.get(index + 2) : null;
					}
					Object left = index > 0 ? expressao.get(index - 1) : null;
					expressao.add(index, listConcat("(", left, operador, operador2, right, ")"));
					expressao.remove(index + 2);
					expressao.remove(index + 1);
					expressao.remove(index - 1);
					if (tmp) {
						expressao.remove(index);
						fim -= 3;
					} else {
						fim -= 2;
					}
				}
			} else {
				i++;
			}
		}
	}

	// concatena objetos em uma lista.
	private ArrayList listConcat(Object... objects) {
		ArrayList lista = new ArrayList();
		for (Object object : objects) {
			if (object != null) {
				if (object instanceof ArrayList) {
					lista.addAll((Collection) object);
				} else {
					lista.add(object);
				}
			}
		}
		return lista;
	}

	// junta caracteres que representam um numero ou variavel em uma string
	private String extractNumber(String expressao, int inicio) {
		String numero = expressao.charAt(inicio) + "";
		// concatena atÃƒÂ© encontrar parenteses, operadores ou espaÃƒÂ§o
		for (int i = inicio + 1; i < expressao.length(); i++) {
			if ((operadores + "( )").contains(expressao.charAt(i) + ""))
				break;
			numero += expressao.charAt(i);
		}
		return numero;

	}

	public String expressaoEmOrdem() {
		return auxExpressaoEmOrdem(root);
	}

	public void informarVariaveis() {
		Lista<String> variaveis = new ListaArray<String>();
		Lista<String> valoresCorrespondentes = new ListaArray<String>();
		auxInformaVariaveis(root, variaveis, valoresCorrespondentes);
	}

	public Racional calcularExpressao() {
		Racional.setBigDecimalConversionPrecision(mathContext.getPrecision());
		if (root.getLeftNode() == null && root.getRightNode() == null)
			return new Racional(root.getElemento());
		Lista<String> variaveis = new ListaArray<String>();
		Lista<Racional> valoresCorrespondentes = new ListaArray<Racional>();
		return auxCalcularExpressao(root, variaveis, valoresCorrespondentes);
	}

	public void setPrecision(int precision) {
		mathContext = new MathContext(precision, mathContext.getRoundingMode());
	}

	private void tradutorDeExpressoes(Fila<String> expressao) {
		root = null;
		Pilha<String> pilhaOperadores = new PilhaArray<String>();
		Pilha<Node<String>> pilhaArvores = new PilhaArray<Node<String>>();
		String simbolo;
		try {
			boolean isBinary = false;
			boolean especialOperatorPresent = false;
			while (!expressao.isEmpty()) {
				simbolo = expressao.dequeue();
				if (simbolo.equals("(")) {
					isBinary = false;
					continue;
				} else if (operadores.contains(simbolo)) {
					if (isBinary) {
						pilhaOperadores.push(simbolo);
						isBinary = false;
					} else if (!"()".contains(expressao.peek())) {
						Node<String> novoElemento = new Node<String>();
						novoElemento.setElemento(simbolo);
						Node<String> newRightElement = new Node<String>();
						newRightElement.setElemento(expressao.dequeue());
						novoElemento.setRightNode(newRightElement);
						pilhaArvores.push(novoElemento);
						isBinary = true;
					} else {
						pilhaOperadores.push(simbolo);
						especialOperatorPresent = true;
					}
				} else if (simbolo.equals(")")) {
					Node<String> novoElemento = new Node<String>();
					novoElemento.setElemento(pilhaOperadores.pop());
					novoElemento.setRightNode(pilhaArvores.pop());
					novoElemento.setLeftNode(pilhaArvores.pop());
					pilhaArvores.push(novoElemento);
					isBinary = true;
					if (especialOperatorPresent) {
						String especialOperator = pilhaOperadores.pop();
						Node<String> novoElemento2 = new Node<String>();
						novoElemento2.setElemento(especialOperator);
						novoElemento2.setRightNode(pilhaArvores.pop());
						pilhaArvores.push(novoElemento2);
						especialOperatorPresent = false;
					}
				} else {
					Node<String> nodeAux;
					nodeAux = new Node<String>(simbolo);
					pilhaArvores.push(nodeAux);
					isBinary = true;
				}
			}
			adicionarNode(pilhaArvores.pop());
		} catch (RuntimeException e) {
			throw new ExpressaoMalFormadaException();
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
		return "";
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

	private Racional auxCalcularExpressao(Node<String> raiz, Lista<String> variaveis,
			Lista<Racional> valoresCorrespondentes) {
		if (raiz != null) {
			if (root != null) {
				if (raiz.getLeftNode() == null && raiz.getRightNode() == null) {
					String numOper = "0123456789" + operadores;
					if (numOper.indexOf(raiz.getElemento().charAt(0)) == -1) {
						if (variaveis.contem(raiz.getElemento().charAt(0) + "")) {
							return valoresCorrespondentes.get(variaveis.indexOf(raiz.getElemento().charAt(0) + ""));
						} else {
							scanner = new Scanner(System.in);
							variaveis.adicionar(raiz.getElemento().charAt(0) + "");
							System.out.println("informe o valor de " + raiz.getElemento().charAt(0));
							Racional tmp = new Racional(scanner.nextLine());
							valoresCorrespondentes.adicionar(tmp);
							return tmp;
						}
					}
					return new Racional(raiz.getElemento());
				}
				Racional expressao1 = auxCalcularExpressao(raiz.getLeftNode(), variaveis, valoresCorrespondentes);
				Racional expressao2 = auxCalcularExpressao(raiz.getRightNode(), variaveis, valoresCorrespondentes);
				if(expressao1 == null && !(raiz.getElemento().equals("-")||raiz.getElemento().equals("+"))){
					throw new ExpressaoMalFormadaException("Operador " + raiz.getElemento() + " nao possui interpretacao unaria");
				}
				switch (raiz.getElemento()) {
				case "*":
					return expressao1.multiplicar(expressao2);
				case "/":
					return expressao1.dividir(expressao2);
				case "-":
					if (expressao1 == null)
						return Racional.ZERO.subtrair(expressao2);
					return expressao1.subtrair(expressao2);
				case "+":
					if (expressao1 == null)
						return Racional.ZERO.somar(expressao2);
					return expressao1.somar(expressao2);
				case "^":
					Racional retorno = Racional.valueOf(raiz(expressao1.bigDecimalValue(mathContext.getPrecision()),
							expressao2.getDenominador().intValue()));
					retorno = retorno.pow(expressao2.getNumerador().intValue());
					return retorno;
				case "~":
					if (expressao2.compareTo(Racional.ZERO) < 0)
						expressao1 = expressao1.inverso();
					return Racional.valueOf(
							raiz(expressao1.bigDecimalValue(mathContext.getPrecision()), expressao2.abs().intValue()));
				case "%":
					return expressao1.resto(expressao2);
				}
			}
		}
		return null;
	}

	private void validarExpressao(String expressao) {
		Pilha<Character> pilha = new PilhaArray<Character>();
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
					if (j >= i - 1) {
						expressaoInvalida = true;
						j++;
						break;
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
			throw new ExpressaoMalFormadaException(expressao + "\n" + indicadorErro);
		}
	}

	private void auxInformaVariaveis(Node<String> raiz, Lista<String> variaveis, Lista<String> valoresCorrespondentes) {
		if (raiz != null) {
			if (raiz.getElemento() != "") {
				auxInformaVariaveis(raiz.getLeftNode(), variaveis, valoresCorrespondentes);
				if (raiz.getLeftNode() == null && raiz.getRightNode() == null) {
					String numOperador = "0123456789" + operadores;
					if (numOperador.indexOf(raiz.getElemento().charAt(0)) == -1) {
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

	// calcula a raiz de indice indice usando o mÃƒÂ©todo de newton
	private BigDecimal raiz(BigDecimal valor, int indice) {

		if (valor.compareTo(BigDecimal.ZERO) < 0 && indice % 2 == 0) {
			throw new ArithmeticException("raiz par de numero negativo");
		} else if (indice == 1) {
			return valor;
		}

		Fila<Integer> fatores = new FilaArray<Integer>();
		int i = 2;
		while (indice >= i) {
			while (indice % i == 0) {
				indice = indice / i;
				fatores.enqueue(i);
			}
			i++;
		}

		int precisao = mathContext.getPrecision();
		RoundingMode roundingMode = mathContext.getRoundingMode();
		MathContext tempMathContext = new MathContext(precisao + 5, roundingMode);

		while (!fatores.isEmpty()) {
			indice = fatores.dequeue();

			// aproximaÃƒÂ§ÃƒÂ£o inicial
			BigDecimal resultado = BigDecimal.ONE;
			BigDecimal auxResultado = resultado;

			int lastCorrectDigit = -1;
			BigDecimal k1 = (BigDecimal.ONE).divide(BigDecimal.valueOf(indice), tempMathContext);
			int k2 = indice - 1;

			do {
				resultado = k1.multiply(resultado.multiply(BigDecimal.valueOf(k2))
						.add(valor.divide(resultado.pow(k2), tempMathContext)));
				lastCorrectDigit = firstDifferentDigit(resultado, auxResultado);
				auxResultado = resultado;
			} while (lastCorrectDigit < precisao);
			valor = resultado.setScale(precisao, roundingMode).stripTrailingZeros();
		}
		return valor.setScale(precisao, roundingMode).stripTrailingZeros();
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
