package testes;

import calculadora.CalculadoraPosfixa;

public class Teste1 {
	public static void main(String[] args) {

		CalculadoraPosfixa cal = new CalculadoraPosfixa();

		cal.setPrecision(10);
		System.out.println(cal.calcular("2 5 + ! 5 ^"));
	}
}
