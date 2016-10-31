package estruturasDados.arvore;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

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
		int nV = 0;
		int nO = 0;
		for (int i = 0; i < expressao.length(); i++) {
			if (expressao.charAt(i) == ' ') {
				continue;
			} else if ("()".contains(expressao.charAt(i) + "")) {
				lista.add(expressao.charAt(i) + "");
			} else if (operadores.contains(expressao.charAt(i) + "") && nO + 1 < nV) {
				lista.add(expressao.charAt(i) + "");
				nO++;
			} else {
				String numero = "";
				if (operadores.contains(expressao.charAt(i) + "") && expressao.charAt(i) != '-') {
					lista.add(expressao.charAt(i) + "");
					nO++;
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
					if (!"01234789".contains(expressao.charAt(i + 1) + "")) {
						variavelJaNomeada = true;
					}
					i++;
				}
				lista.add(numero);
				if (i < expressao.length())
					lista.add(expressao.charAt(i) + "");
				nV++;
			}
		}
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
		tradutorDeExpressoes(lista.get(0));
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

	private void tradutorDeExpressoes(String expressao) {
		int nV = 0;
		int nO = 0;
		root = null;
		if (expressao.length() < 3) {
			root = new Node<String>();
			if (expressao.length() == 1) {
				root.setElemento(expressao);
			} else if (expressao.charAt(0) == '-') {
				root.setElemento("-" + expressao.charAt(1) + "");
			} else {
				root.setElemento(expressao.charAt(1) + "");
			}
			return;
		}
		pilhaArray<String> pilhaOperadores = new pilhaArray<String>();
		pilhaArray<Node<String>> pilhaArvores = new pilhaArray<Node<String>>();
		String valor;
		for (int i = 0; i < expressao.length(); i++) {
			valor = "";
			if (expressao.charAt(i) == ' ')
				continue;
			// caso parenteses abrindo
			if (expressao.charAt(i) == '(') {
				while (operadores.indexOf(expressao.charAt(i)) == -1 && expressao.charAt(i) != ')') {
					if (expressao.charAt(i) == ' ') {
						i++;
						continue;
					}
					if (expressao.charAt(i) == '(') {
						i++;
						if (expressao.charAt(i + 1) != '-') {
							valor += expressao.charAt(i);
							i++;
						}
						continue;
					}
					valor = valor + expressao.charAt(i);
					i++;
				}
				Node<String> nodeAux = new Node<String>();
				nodeAux.setElemento(valor);
				pilhaArvores.push(nodeAux);
				nV++;
				if (expressao.charAt(i) == ')') {
					continue;
				}
				pilhaOperadores.push(expressao.charAt(i) + "");
				nO++;
			}
			// caso seja um operador
			else if (operadores.indexOf(expressao.charAt(i)) != -1) {
				if (nV <= nO + 1) {
					if (i > 0) {
						if (operadores.contains(expressao.charAt(i - 1) + "") && expressao.charAt(i) == '-') {
							valor += expressao.charAt(i++);
							while (operadores.indexOf(expressao.charAt(i)) == -1 && expressao.charAt(i) != ')') {
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
							nV++;
							i--;
							continue;
						}
					}
				}
				pilhaOperadores.push(expressao.charAt(i) + "");
				nO++;
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
				nV++;
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
		adicionarNode(pilhaArvores.pop());
	}

	private void adicionarParenteses(List<String> expressao, int inicio, int fim) {
		int i = 0;
		while (i < operadores.length()) {
			char operador = operadores.charAt(i);
			List<String> subExpressao = expressao.subList(inicio, fim);
			int index = inicio + subExpressao.indexOf(operador + "");
			if (index > inicio && index < fim) {
				String left = index > 0 ? expressao.get(index - 1) : "";
				String right = index < expressao.size() ? expressao.get(index + 1) : "";
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
				if(expressao2.compareTo(BigDecimal.ZERO)<0) {
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
				if(indiceNegativo)return BigDecimal.ONE.divide(retorno);
				return retorno;
			case "~":
				
				if(expressao2.intValue()<0)return BigDecimal.ONE.divide(raiz(expressao1, BigDecimal.ZERO.subtract(expressao2).intValue()),mathContext);
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
			if (!pilha.isEmpty()){
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

	public void setPrecision(int precision) {
		mathContext = new MathContext(precision, mathContext.getRoundingMode());
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
