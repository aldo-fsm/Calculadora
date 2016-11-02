package numeros;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;

public class Racional extends Number {

	private static final long serialVersionUID = 1L;

	BigInteger numerador;
	BigInteger denominador;

	public Racional() {
	}

	public Racional(String numero) {
		if (numero.contains(".")) {
			String[] aux = numero.split("\\.");
			int casasDecimais = aux[1].length();

			while (aux[1].charAt(casasDecimais - 1) == '0')
				casasDecimais--;

			String strN = aux[0];
			String strD = "1";
			for (int i = 0; i < casasDecimais; i++) {
				strD += "0";
				strN += aux[1].charAt(i);
			}

			numerador = new BigInteger(strN);
			denominador = new BigInteger(strD);
			simplificar();

		} else {
			numerador = new BigInteger(numero);
			denominador = BigInteger.ONE;
		}
	}

	// simplifica a fração
	private void simplificar() {
		BigInteger mdc = mdc(numerador, denominador);
		numerador = numerador.divide(mdc);
		denominador = denominador.divide(mdc);
	}

	public Racional somar(Racional outro) {
		BigInteger a = outro.getNumerador();
		BigInteger b = outro.getDenominador();
		Racional resultado = new Racional();

		resultado.numerador = (numerador.multiply(b)).add(a.multiply(denominador));
		resultado.denominador = denominador.multiply(b);
		resultado.simplificar();

		return resultado;
	}

	public Racional subtrair(Racional outro) {
		BigInteger a = outro.getNumerador();
		BigInteger b = outro.getDenominador();
		Racional resultado = new Racional();

		resultado.numerador = (numerador.multiply(b)).subtract(a.multiply(denominador));
		resultado.denominador = denominador.multiply(b);
		resultado.simplificar();

		return resultado;
	}

	public Racional multiplicar(Racional outro) {
		BigInteger a = outro.getNumerador();
		BigInteger b = outro.getDenominador();
		Racional resultado = new Racional();

		resultado.numerador = numerador.multiply(a);
		resultado.denominador = denominador.multiply(b);
		resultado.simplificar();

		return resultado;
	}

	public Racional dividir(Racional outro) {
		BigInteger a = outro.getNumerador();

		if (a.equals(BigInteger.ZERO))
			throw new ArithmeticException("divisão por zero");

		BigInteger b = outro.getDenominador();
		Racional resultado = new Racional();

		resultado.numerador = numerador.multiply(b);
		resultado.denominador = denominador.multiply(a);
		resultado.simplificar();

		return resultado;
	}

	// maximo multiplo comum pelo algoritmo de Euclides
	private BigInteger mdc(BigInteger a, BigInteger b) {
		if (b.compareTo(a) > 0) {
			BigInteger aux = a;
			a = b;
			b = aux;
		}
		BigInteger resto;
		while (true) {
			if (a.equals(BigInteger.ZERO))
				return b;
			if (b.equals(BigInteger.ZERO))
				return a;
			resto = a.remainder(b);
			a = b;
			b = resto;
		}
	}

	public BigDecimal bigDecimalValue(int precision) {
		BigDecimal numerador = new BigDecimal(this.numerador);
		BigDecimal denominador = new BigDecimal(this.denominador);
		MathContext m = new MathContext(precision, RoundingMode.HALF_EVEN);
		return numerador.divide(denominador, m);
	}

	@Override
	public int intValue() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long longValue() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float floatValue() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double doubleValue() {
		// TODO Auto-generated method stub
		return 0;
	}

	public BigInteger getNumerador() {
		return numerador;
	}

	public BigInteger getDenominador() {
		return denominador;
	}

	@Override
	public boolean equals(Object obj) {
		Racional outro = (Racional) obj;
		return numerador.equals(outro.getNumerador()) && denominador.equals(outro.getDenominador()) ? true : false;
	}

}
