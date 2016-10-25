package testes;

import estruturasDados.arvore.ArvoreExpressoes;

public class TesteArvoreExpressoes {
	public static void main(String[] args) {
		ArvoreExpressoes a = new ArvoreExpressoes();
		a.armazeneExpressao("(2^3)");
		System.out.println(a.expressaoEmOrdem());
		System.out.println(a.calcularExpressao());
	}
}
