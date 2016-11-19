package numeros;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;

import estruturasDados.fila.Fila;
import estruturasDados.fila.FilaArray;

public class RMath {

	public static Racional fatorial(Racional valor) {
		if (valor.isInteger()) {
			BigInteger aux = valor.bigIntegerValue();
			if (aux.compareTo(BigInteger.ZERO) > 0) {
				BigInteger resultado = BigInteger.ONE;
				while (aux.compareTo(BigInteger.ONE) > 0) {
					resultado = resultado.multiply(aux);
					aux = aux.subtract(BigInteger.ONE);
				}
				return Racional.valueOf(resultado);

			} else
				throw new ArithmeticException("Fatorial de numero negativo");
		} else
			throw new ArithmeticException("Fatorial de numero fracionario");
	}

	public static Racional pow(Racional base, Racional potencia, MathContext mathContext) {
		Racional retorno = Racional.valueOf(raiz(base.bigDecimalValue(mathContext.getPrecision()),
				potencia.getDenominador().intValue(), mathContext));
		retorno = retorno.pow(potencia.getNumerador().intValue());
		return retorno;
	}

	// calcula a raiz de indice indice usando o metodo de newton
	private static BigDecimal raiz(BigDecimal valor, int indice, MathContext mathContext) {
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
	private static int firstDifferentDigit(BigDecimal a, BigDecimal b) {
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
