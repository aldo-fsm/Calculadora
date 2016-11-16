package testes;

import java.util.Scanner;

import estruturasDados.arvore.ArvoreExpressoes;

public class TesteCalculoExpressoes {
	public static void main(String[] args) {
		ArvoreExpressoes calculadora = new ArvoreExpressoes();
		System.out.println("\n\t- Teste - Calculadora de Expressões -\n");
		System.out.println("- digite exit para sair");
		System.out.println("- digite & para acrescentar a uma expressao previamente incluida");
		System.out.println("- digite precisao para mudar a precisao");
		System.out.println("\n");

		String input = "";
		Scanner s = new Scanner(System.in);
		while (true) {
			try {
				System.out.printf(">>> ");
				input = s.nextLine();
				switch (input) {
				case "":
					continue;
				case "acrescentar":
					System.out.println("digite a expressao a ser acrescentada sem parenteses");
					System.out.printf(">>> ");
					input = s.nextLine();
					calculadora.acrescentaNaExpressao(input);
					calculadora.informarVariaveis();
					System.out.println(calculadora.expressaoEmOrdem() + " = " + calculadora.calcularExpressao());
					break;
				case "exit":
					s.close();
					return;
				case "precisao":
					System.out.println("digite a precisao desejada.");
					System.out.printf(">>> ");
					input = s.nextLine();
					calculadora.setPrecision(Integer.parseInt(input));
					break;
				default:
					calculadora.armazenarExpressao(input);
					calculadora.informarVariaveis();
					System.out.println(calculadora.expressaoEmOrdem() + " = " + calculadora.calcularExpressao());
					break;
				}
			} catch (RuntimeException e) {
				e.printStackTrace(System.out);
			}
		}
	}
}
