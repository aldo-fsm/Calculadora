package calculadora;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;

import estruturasDados.PilhaArray;
import estruturasDados.tads.Pilha;

public class CalculadoraPosfixa {
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
				pilha.push(calcularOperador(pilha, termo));
			} else {
				pilha.push(new BigDecimal(termo));
			}

		}
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
			return numeros[1].pow(numeros[0].intValue());
		case "!":
			BigDecimal numero = pilha.pop();
			BigInteger resultado = BigInteger.ONE;
			for (BigInteger i = numero.toBigInteger(); i.compareTo(BigInteger.ONE) > 0; i = i
					.subtract(BigInteger.ONE)) {
				resultado = resultado.multiply(i);
			}
			return new BigDecimal(resultado);
		default:
			return null;
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
