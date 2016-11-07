package numeros;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
import java.security.InvalidParameterException;

public class Racional extends Number implements Comparable<Racional> {

	private static final long serialVersionUID = 1L;

	private BigInteger numerador;
	private BigInteger denominador;
	private static int bigDecimalConversionPrecision = 10;

	public static final Racional ZERO = new Racional("0");
	public static final Racional ONE = new Racional("1");
	public static final Racional TEN = new Racional("10");

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

	public Racional(BigInteger numerador, BigInteger denominador) {
		if (denominador.equals(BigInteger.ZERO))
			throw new InvalidParameterException("O denominador não pode ser zero");

		this.numerador = numerador;
		this.denominador = denominador;

		simplificar();
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

	public Racional inverso() {
		if (numerador.equals(BigInteger.ZERO))
			throw new ArithmeticException("divisão por zero");
		Racional resultado = new Racional();
		resultado.numerador = denominador;
		resultado.denominador = numerador;
		return resultado;
	}

	public Racional pow(int potencia) {
		if (potencia < 0) {
			return this.inverso().pow(-potencia);
		}
		Racional resultado = new Racional();
		resultado.numerador = numerador.pow(potencia);
		resultado.denominador = denominador.pow(potencia);
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

	private BigInteger mmc(BigInteger a, BigInteger b) {
		if (a.equals(ZERO) && b.equals(ZERO))
			return BigInteger.ZERO;
		else {
			return (a.multiply(b)).abs().divide(mdc(a, b));
		}
	}

	public BigDecimal bigDecimalValue(int precision) {
		BigDecimal numerador = new BigDecimal(this.numerador);
		BigDecimal denominador = new BigDecimal(this.denominador);
		MathContext m = new MathContext(precision, RoundingMode.HALF_EVEN);
		return numerador.divide(denominador, m).stripTrailingZeros();
	}

	public BigDecimal bigDecimalValue() {
		return bigDecimalValue(bigDecimalConversionPrecision);
	}

	public BigInteger bigIntegerValue() {
		return numerador.divide(denominador);
	}

	public Racional resto(Racional divisor) {
		Racional divisaoInteira = new Racional(this.dividir(divisor).bigIntegerValue(), BigInteger.ONE);
		return this.subtrair(divisaoInteira.multiplicar(divisor));
	}

	public Racional abs() {
		Racional resultado = new Racional();
		resultado.numerador = numerador.abs();
		resultado.denominador = denominador.abs();
		return resultado;
	}

	public static Racional valueOf(int numero) {
		return new Racional(BigInteger.valueOf(numero), BigInteger.ONE);
	}

	public static Racional valueOf(BigDecimal numero) {
		return new Racional(numero.toString());
	}

	@Override
	public String toString() {
		return bigDecimalValue().toString();
	}

	@Override
	public int intValue() {
		return numerador.divide(denominador).intValue();
	}

	public static int getBigDecimalConversionPrecision() {
		return bigDecimalConversionPrecision;
	}

	public static void setBigDecimalConversionPrecision(int precision) {
		bigDecimalConversionPrecision = precision;
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

	@Override
	public int compareTo(Racional o) {
		if (this.equals(o))
			return 0;
		else {
			BigInteger denominadorComum = mmc(denominador, o.getDenominador());
			BigInteger a = denominadorComum.divide(denominador).multiply(numerador);
			BigInteger b = denominadorComum.divide(o.getDenominador()).multiply(o.getNumerador());

			return a.compareTo(b);
		}
	}
}