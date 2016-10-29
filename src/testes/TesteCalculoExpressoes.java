package testes;

import java.util.Scanner;

import estruturasDados.arvore.ArvoreExpressoes;

public class TesteCalculoExpressoes {
	public static void main(String[] args) {

		ArvoreExpressoes calculadora = new ArvoreExpressoes();

		System.out.println("\nTeste - Calculadora de Expressões");
		System.out.println("digite exit para sair\n\n");
		String input = "";
		Scanner s = new Scanner(System.in);

		while (true) {

			System.out.printf(">>> ");
			input = s.nextLine();
			if (input.equals("exit")) {
				break;
			}
			try {
				calculadora.armazenarExpressao(input);
				calculadora.informarVariaveis();
				System.out.println(calculadora.expressaoEmOrdem() + " = " + calculadora.calcularExpressao());
			} catch (RuntimeException e) {
				e.printStackTrace(System.out);
			}
		}

	}
}
