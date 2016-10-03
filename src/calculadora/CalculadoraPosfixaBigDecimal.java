package calculadora;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;

import estruturasDados.PilhaArray;
import estruturasDados.PilhaVaziaException;
import estruturasDados.tads.Pilha;

public class CalculadoraPosfixaBigDecimal {

	private String[] operadores = { "+", "-", "*", "/", "^", "!" };
	private Pilha<BigDecimal> pilha = new PilhaArray<BigDecimal>();
	private MathContext mathContext = new MathContext(7, RoundingMode.HALF_EVEN);

	/**
	 * Calcula uma expressão escrita em notação pos-fixa.
	 * 
	 * @param expressao
	 *            expressão a ser calculada.
	 * @return resultado da expressão.
	 */
	public BigDecimal calcular(String expressao) {
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
				pilha.push(new BigDecimal(termo));
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

	private BigDecimal calcularOperador(Pilha<BigDecimal> pilha, String operador) {

		BigDecimal[] numeros;

		switch (operador) {

		case "+":
			numeros = new BigDecimal[] { pilha.pop(), pilha.pop() };
			return numeros[1].add(numeros[0]);
		case "-":
			numeros = new BigDecimal[] { pilha.pop(), pilha.pop() };
			return numeros[1].subtract(numeros[0]);
		case "*":
			numeros = new BigDecimal[] { pilha.pop(), pilha.pop() };
			return numeros[1].multiply(numeros[0]);
		case "/":
			numeros = new BigDecimal[] { pilha.pop(), pilha.pop() };
			return numeros[1].divide(numeros[0], mathContext);
		case "^":
			numeros = new BigDecimal[] { pilha.pop(), pilha.pop() };
			if (isInteger(numeros[0])) {
				return numeros[1].pow(numeros[0].intValue());
			} else {
				throw new UnsupportedOperationException("Potencia de BigDecimal com expoente fracionario.");
			}
		case "!":
			BigDecimal numero = pilha.pop();
			if (isInteger(numero)) {
				BigInteger resultado = BigInteger.ONE;
				for (BigInteger i = numero.toBigInteger(); i.compareTo(BigInteger.ONE) > 0; i = i
						.subtract(BigInteger.ONE)) {
					resultado = resultado.multiply(i);
				}
				return new BigDecimal(resultado);
			} else {
				throw new ArithmeticException("Fatorial de numero fracionario.");
			}
		default:
			return null;
		}
	}

	private boolean isInteger(BigDecimal number) {
		if (number.compareTo(new BigDecimal(number.toBigInteger())) == 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Determina a precisão dos calculos.
	 * 
	 * @param precision
	 *            Numero de casas decimais para numeros sem representação exata.
	 */
	public void setPrecision(int precision) {
		mathContext = new MathContext(precision, RoundingMode.HALF_EVEN);
	}

}
