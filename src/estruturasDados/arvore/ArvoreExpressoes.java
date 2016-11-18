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
	// raiz da arvore de expressoes
	private Node<String> root;
	private Scanner scanner;
	// determina a precisao e o modo de arredondamento
	private MathContext mathContext = new MathContext(100, RoundingMode.HALF_EVEN);
	// operadores em ordem de precedencia
	private static final String operadores = "!^~*%/-+";

	/**
	 * acrescenta uma expressao exemplo: ((2*3)+5) acrescentar *3 + 5 =>
	 * ((((2*3)+5)*3)+5)
	 * 
	 * @param expressao
	 *            expressao a ser acrescentada
	 */
	public void acrescentaNaExpressao(String expressao) {
		expressao = "(" + expressaoEmOrdem() + expressao + ")";
		armazenarExpressao(expressao);
	}

	/**
	 * Armazena uma expressão infixa na arvore de expressões.
	 * 
	 * @param expressao
	 *            Expressão em notação infixa
	 */
	@SuppressWarnings("unchecked")
	public void armazenarExpressao(String expressao) {
		ArrayList<Object> lista = corretor(expressao);
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
		// converte a fila de simbolos em uma arvore de expressoes
		putOnTree(fila);
	}

	/**
	 * metodo recebe uma expressao e acrescenta a ela os tratamentos possiveis,
	 * como parenteses associativos em expressoes que nao os possuem como
	 * (2+2+2)
	 * 
	 * @param expressao
	 *            Expressão em notação infixa
	 */
	private ArrayList<Object> corretor(String expressao) {
		// verifica se a expressao esta escrita corretamente
		validarExpressao(expressao);
		expressao = "(" + expressao + ")";
		// lista de simbolos
		ArrayList<Object> lista = new ArrayList<Object>();
		// coloca cada simbolo numa lista, agrupando numeros e nomes de
		// variaveis em strings
		for (int i = 0; i < expressao.length(); i++) {
			// ignora espaços
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
		// loop para adicionar parenteses faltando
		// try {
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
		// } catch (RuntimeException e) {
		// throw new ExpressaoMalFormadaException();
		// }
		return lista;
	}

	/**
	 * coloca parenteses de acordo com a precedencia dos operadores utilizando
	 * um loop no corretor
	 * 
	 * @expressao representa a lista a ser tratada (que inicialmente era a
	 *            expressao digitada pelo usuario)
	 * @inicio , @fim variaveis que limitaram a analise da lista e o acrescimo
	 *         relativo adequado
	 */
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
				if (operador == '!') {
					expressao.add(index, listConcat("(", expressao.get(index - 1), operador, ")"));
					expressao.remove(index - 1);
					expressao.remove(index);
					fim--;
				} else if (isNotBinary) {
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

	// junta caracteres que representam um numero ou nome variavel em uma string
	private String extractNumber(String expressao, int inicio) {
		String numero = expressao.charAt(inicio) + "";
		// concatena ate encontrar parenteses, operadores ou espaco
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

	// metodo utilizado em calcularExpressao para informar variaveis
	public void informarVariaveis() {
		Lista<String> variaveis = new ListaArray<String>();
		Lista<String> valoresCorrespondentes = new ListaArray<String>();
		auxInformaVariaveis(root, variaveis, valoresCorrespondentes);
	}

	/**
	 * calcula o valor da expressao
	 * 
	 * @return O resultado da expressao
	 */
	public Racional calcularExpressao() {
		informarVariaveis();
		Racional.setBigDecimalConversionPrecision(mathContext.getPrecision());
		if (root.getLeftNode() == null && root.getRightNode() == null)
			return new Racional(root.getElemento());
		Lista<String> variaveis = new ListaArray<String>();
		Lista<Racional> valoresCorrespondentes = new ListaArray<Racional>();
		return auxCalcularExpressao(root, variaveis, valoresCorrespondentes);
	}

	/**
	 * Determina a precisao da calculadora
	 * 
	 * @param precision
	 */
	public void setPrecision(int precision) {
		mathContext = new MathContext(precision, mathContext.getRoundingMode());
	}

	/**
	 * converte uma fila de simbolos(parenteses, operadores, valores e
	 * variaveis) em uma arvore de expressaoes
	 * 
	 * @expressao representa a expressao digitada pelo usuario no formato de
	 *            fila, mais adequado para o metodo
	 */
	private void putOnTree(Fila<String> expressao) {
		// pilhas de operadores e de expressoes (arvores) que irao servir para
		// costruir a arvore de expressao
		Pilha<String> operators = new PilhaArray<String>();
		Pilha<Node<String>> subExpres = new PilhaArray<Node<String>>();
		// termo e a string que representara o simbolo a medida que se anda na
		// fila
		String termo;
		// cotroladores de sinais para compreensao de expressoes com inumeras
		// subexpressoes negativas
		String operadorEspecial = null;
		boolean especialOperator = true;
		List<Node<String>> maisQueEspecial = new ArrayList<Node<String>>();
		List<Integer> niveis = new ArrayList<Integer>();
		// loop de leitura da fila e modificacao e criacao da arvore de
		// expressao
		while (!expressao.isEmpty()) {
			termo = expressao.dequeue();
			// caso seja um parenteses abrindo
			if (termo.equals("(")) {
				if (operadorEspecial != null) {
					maisQueEspecial.add(new Node<String>(operadorEspecial));
					niveis.add(0);
					operadorEspecial = null;
				}
				for (int i = 0; i < maisQueEspecial.size(); i++)
					if (maisQueEspecial.get(i).getElemento() != null) {
						niveis.set(i, niveis.get(i) + 1);
					}
				especialOperator = true;
				// caso seja um operador
			} else if (operadores.contains(termo) && !termo.contains("!")) {
				if (especialOperator) {
					operadorEspecial = termo;
					// todo e qualquer operador apos o especial deve ser um
					// operador normal (por hora)
					especialOperator = false;
				} else {
					operators.push(termo);
					especialOperator = true;
				}

			}
			// caso seja fatorial
			else if (termo.contains("!")) {
				Node<String> subExpressaoAux = new Node<String>(termo);
				subExpressaoAux.setLeftNode(subExpres.pop());
				subExpres.push(subExpressaoAux);
				especialOperator = false;
				if (!maisQueEspecial.isEmpty()) {
					for (int i = 0; i < maisQueEspecial.size(); i++) {
						niveis.set(i, niveis.get(i) - 1);
						if (niveis.get(i) == 0) {
							maisQueEspecial.get(i).setRightNode(subExpres.pop());
							subExpres.push(maisQueEspecial.get(i));
							maisQueEspecial.set(i, new Node<String>(null));
						}
					}
				}
				// caso seja um parenteses fechando
			} else if (termo.equals(")")) {
				if (!operators.isEmpty()) {
					Node<String> subExpressaoAux = new Node<String>(operators.pop());
					subExpressaoAux.setRightNode(subExpres.pop());
					subExpressaoAux.setLeftNode(subExpres.pop());
					especialOperator = false;
					subExpres.push(subExpressaoAux);
				}
				if (!maisQueEspecial.isEmpty()) {
					for (int i = 0; i < maisQueEspecial.size(); i++) {
						niveis.set(i, niveis.get(i) - 1);
						if (niveis.get(i) == 0) {
							maisQueEspecial.get(i).setRightNode(subExpres.pop());
							subExpres.push(maisQueEspecial.get(i));
							maisQueEspecial.set(i, new Node<String>(null));
						}
					}
				}
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
		if (!operators.isEmpty())
			throw new ExpressaoMalFormadaException("Existem operadores sobrando");
		// a arvore de expressao criada e deve ser a atual, logo se adiciona o
		// node guardado na pilha de subExpressoes na Arvore de Expressoes
		root = subExpres.pop();

	}

	// metodo recursivo para percusso em ordem
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

	// metodo recursivo para o calculo da expressao
	private Racional auxCalcularExpressao(Node<String> raiz, Lista<String> variaveis,
			Lista<Racional> valoresCorrespondentes) {
		if (raiz != null) {
			if (raiz.getLeftNode() == null && raiz.getRightNode() == null) {
				return new Racional(raiz.getElemento());
			}
			Racional expressao1 = auxCalcularExpressao(raiz.getLeftNode(), variaveis, valoresCorrespondentes);
			Racional expressao2 = auxCalcularExpressao(raiz.getRightNode(), variaveis, valoresCorrespondentes);
			if (expressao1 == null && !("-+".contains(raiz.getElemento()))) {
				if ("!".contains(raiz.getElemento())) {
					throw new ExpressaoMalFormadaException(raiz.getElemento() + "e unario a direita");
				} else {
					throw new ExpressaoMalFormadaException(
							"Operador " + raiz.getElemento() + " nao possui interpretacao unaria");
				}
			} else if (expressao2 != null && "!".contains(raiz.getElemento())) {
				throw new ExpressaoMalFormadaException(
						"Operador " + raiz.getElemento() + " nao possui interpretacao binaria");
			}
			Racional retorno;
			switch (raiz.getElemento()) {
			// multiplicacao
			case "*":
				return expressao1.multiplicar(expressao2);
			// divisao
			case "/":
				return expressao1.dividir(expressao2);
			// subtracao
			case "-":
				if (expressao1 == null)
					return Racional.ZERO.subtrair(expressao2);
				return expressao1.subtrair(expressao2);
			// adicao
			case "+":
				if (expressao1 == null)
					return Racional.ZERO.somar(expressao2);
				return expressao1.somar(expressao2);
			// potenciacao
			case "^":
				retorno = Racional.valueOf(raiz(expressao1.bigDecimalValue(mathContext.getPrecision()),
						expressao2.getDenominador().intValue()));
				retorno = retorno.pow(expressao2.getNumerador().intValue());
				return retorno;
			// raiz
			case "~":
				expressao2 = expressao2.inverso();
				retorno = Racional.valueOf(raiz(expressao1.bigDecimalValue(mathContext.getPrecision()),
						expressao2.getDenominador().intValue()));
				retorno = retorno.pow(expressao2.getNumerador().intValue());
				return retorno;
			// resto
			case "%":
				return expressao1.resto(expressao2);
			case "!":
				return Racional.valueOf(fatorial(expressao1.bigDecimalValue()));
			}
		}
		return null;
	}

	private BigDecimal fatorial(BigDecimal valor) {
		if (valor.compareTo(BigDecimal.ONE) <= 0) {
			return BigDecimal.ONE;
		} else {
			return valor.multiply(fatorial(valor.subtract(BigDecimal.ONE)));
		}
	}

	// verifica se a expressao esta escrita corretamente
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

	// calcula a raiz de indice indice usando o metodo de newton
	private BigDecimal raiz(BigDecimal valor, int indice) {
		boolean negativo = false;
		if (indice < 0) {
			valor = BigDecimal.ONE.divide(valor);
			indice = -indice;
		}
		if (valor.compareTo(BigDecimal.ZERO) < 0)
			negativo = true;
		if (negativo && indice % 2 == 0) {
			throw new ArithmeticException("raiz par de numero negativo");
		} else if (indice == 1) {
			return valor;
		}
		valor = valor.abs();
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
			// aproximação inicial
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
		if (negativo)
			valor = valor.negate();
		return valor.setScale(precisao, roundingMode).stripTrailingZeros();
	}

	// metodo ira auxiliar o calculo da raiz
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
