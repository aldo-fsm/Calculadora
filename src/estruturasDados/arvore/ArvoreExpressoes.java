
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
			int index = "^~/".contains(operador + "") ? inicio + subExpressao.lastIndexOf(operador + "")
					: inicio + subExpressao.indexOf(operador + "");
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
	@SuppressWarnings({ "rawtypes", "unchecked" })
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
		Pilha<String> operators = new PilhaArray<String>();
		Pilha<Node<String>> subExpres = new PilhaArray<Node<String>>();
		String termo;
		String operadorEspecial = null;
		Node<String> maisQueEspecial = new Node<String>();
		boolean especialOperator = true;
		int nivel = 0;
		while (!expressao.isEmpty()) {
			termo = expressao.dequeue();
			// caso seja um parenteses abrindo
			if (termo.equals("(")) {
				if (operadorEspecial != null) {
					maisQueEspecial.setElemento(operadorEspecial);
					operadorEspecial = null;
				}
				if (maisQueEspecial.getElemento() != null) {
					nivel++;
					System.out.println(nivel);
				}
				especialOperator = true;
				// caso seja um operador
			} else if (operadores.contains(termo)) {
				if (especialOperator) {
					operadorEspecial = termo;
					// todo e qualquer operador apos o especial deve ser um
					// operador normal (por hora)
					especialOperator = false;
				} else {
					operators.push(termo);
					especialOperator = true;
				}
				// caso seja um parenteses fechando
			} else if (termo.equals(")")) {
				Node<String> subExpressaoAux = new Node<String>(operators.pop());
				subExpressaoAux.setRightNode(subExpres.pop());
				subExpressaoAux.setLeftNode(subExpres.pop());
				if (maisQueEspecial.getElemento() != null) {
					nivel--;
					System.out.println(nivel);
					if (nivel == 0) {
						maisQueEspecial.setRightNode(subExpressaoAux);
						subExpres.push(maisQueEspecial);
						maisQueEspecial = new Node<String>(null);
					} else
						subExpres.push(subExpressaoAux);
				} else
					subExpres.push(subExpressaoAux);
				especialOperator = false;
				// caso seja um valor (numero ou variavel)
			} else {
				Node<String> valor = new Node<String>();
				if (operadorEspecial != null) {
					valor.setElemento(operadorEspecial);
					valor.setRightNode(new Node<String>(termo));
					operadorEspecial = null;
				} else {
					valor.setElemento(termo);
				}
				subExpres.push(valor);
				especialOperator = false;
			}
		}
		adicionarNode(subExpres.pop());
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
				if (expressao1 == null && !(raiz.getElemento().equals("-") || raiz.getElemento().equals("+"))) {
					throw new ExpressaoMalFormadaException(
							"Operador " + raiz.getElemento() + " nao possui interpretacao unaria");
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
			while (procuraErro - 64 < posicaoErro) {
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
