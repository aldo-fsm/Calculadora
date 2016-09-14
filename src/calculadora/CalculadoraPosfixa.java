package calculadora;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import estruturasDados.PilhaArray;

public class CalculadoraPosfixa {
	private String[] operadores2 = { "+", "-", "*", "/", "^" };
	private PilhaArray pilha = new PilhaArray();
	private MathContext mathContext = new MathContext(7, RoundingMode.HALF_EVEN);

	public BigDecimal calcularExpressao(String expressao) {
		String[] termos = expressao.split(" ");
		for (String termo : termos) {
			if (termo.equals(""))
				continue;
			if (isOperator2(termo)) {
				BigDecimal[] numeros = new BigDecimal[] { (BigDecimal) pilha.pop(), (BigDecimal) pilha.pop() };
				pilha.push(calcularOperador2(numeros, termo));
			} else {
				pilha.push(new BigDecimal(termo));
			}

		}
		return (BigDecimal) pilha.pop();
	}

	private boolean isOperator2(String termo) {
		for (String string : operadores2) {
			if (string.equals(termo)) {
				return true;
			}
		}
		return false;
	}

	private BigDecimal calcularOperador2(BigDecimal[] numeros, String operador) {
		if (operador.equals("+")) {
			return numeros[1].add(numeros[0]);
		} else if (operador.equals("-")) {
			return numeros[1].subtract(numeros[0]);
		} else if (operador.equals("*")) {
			return numeros[1].multiply(numeros[0]);
		} else if (operador.equals("/")) {
			return numeros[1].divide(numeros[0], mathContext);
		} else if (operador.equals("^")) {
			return numeros[1].pow(numeros[0].intValue());
		}

		return null;
	}

	public void setPrecision(int precision) {
		mathContext = new MathContext(precision, RoundingMode.HALF_EVEN);
	}

	public void createOperation() {

	}

}
