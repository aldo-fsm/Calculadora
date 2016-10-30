package testes;

import java.util.Scanner;

import estruturasDados.arvore.ArvoreExpressoes;

public class TesteCalculoExpressoes {
	public static void main(String[] args) {
		ArvoreExpressoes calculadora = new ArvoreExpressoes();
		System.out.println("\nTeste - Calculadora de ExpressÃµes");
		System.out.println("digite exit para sair");
		System.out.println("digite acrescentar para acrescentar a uma expressao previamente incluida\n\n");
		String input = "";
		Scanner s = new Scanner(System.in);
		while (true) {
			try {
				System.out.printf(">>> ");
				input = s.nextLine();
				if (input.equals("exit")) {
					break;
				} else if (input.equals("acrescentar")) {
					System.out.println("digite a expressao a ser acrescentada sem parenteses");
					System.out.printf(">>> ");
					input = s.nextLine();
					calculadora.acrescentaNaExpressao(input);
				}else{
					calculadora.armazenarExpressao(input);
				}
				calculadora.informarVariaveis();
				System.out.println(calculadora.expressaoEmOrdem() + " = " + calculadora.calcularExpressao());
			} catch (RuntimeException e) {
				e.printStackTrace(System.out);
			}
		}
		s.close();
	}
}
