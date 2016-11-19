package numeros;

import java.math.BigInteger;

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

}
