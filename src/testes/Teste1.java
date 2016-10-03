package testes;

import calculadora.CalculadoraPosfixaBigDecimal;

public class Teste1 {
	public static void main(String[] args) {

		CalculadoraPosfixaBigDecimal cal = new CalculadoraPosfixaBigDecimal();

		System.out.println(cal.calcular("10000 ! 9999 ! /"));

	}
}
