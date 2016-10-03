package calculadora;

import estruturasDados.PilhaArray;
import estruturasDados.PilhaVaziaException;
import estruturasDados.tads.Pilha;

public class CalculadoraPosfixa {

	private String[] operadores = { "+", "-", "*", "/", "^", "!" };
	private Pilha<Double> pilha = new PilhaArray<Double>();

	/**
	 * Calcula uma expressão escrita em notação pos-fixa.
	 * 
	 * @param expressao
	 *            expressão a ser calculada.
	 * @return resultado da expressão.
	 */
	public Double calcular(String expressao) {
		String[] termos = expressao.split(" ");
		for (String termo : termos) {
			if (termo.equals(""))
				continue;
			if (isOperator(termo)) {
				try {
					pilha.push(calcularOperador(pilha, termo));
				} catch (PilhaVaziaException e) {
					throw new IllegalArgumentException("Expressão pos-fixa mal formada");
				}
			} else {
				pilha.push(new Double(termo));
			}

		}
		if (pilha.size() > 1) {
			throw new IllegalArgumentException("Expressão pos-fixa mal formada");
		}
		if (pilha.size() == 0)
			throw new IllegalArgumentException("Expressão pos-fixa mal formada");
		return pilha.pop();
	}

	private boolean isOperator(String termo) {
		for (String string : operadores) {
			if (string.equals(termo)) {
				return true;
			}
		}
		return false;
	}

	private Double calcularOperador(Pilha<Double> pilha, String operador) {

		Double[] numeros;

		switch (operador) {

		case "+":
			return pilha.pop() + pilha.pop();
		case "-":
			numeros = new Double[] { pilha.pop(), pilha.pop() };
			return numeros[1] - numeros[0];
		case "*":
			return pilha.pop() * pilha.pop();
		case "/":
			numeros = new Double[] { pilha.pop(), pilha.pop() };
			return numeros[1] / numeros[0];
		case "^":
			numeros = new Double[] { pilha.pop(), pilha.pop() };
			return Math.pow(numeros[1], numeros[0]);
		case "!":
			Double numero = pilha.pop();
			if (numero.intValue() == numero) {
				double resultado = 1;
				for (int i = numero.intValue(); i > 1; i--) {
					resultado *= i;
				}
				return resultado;
			} else {
				throw new ArithmeticException("Fatorial de numero fracionario.");
			}
		default:
			return null;
		}
	}

}
