package testes;


import estruturasDados.arvore.ArvoreExpressoes;

public class TesteArvoreExpressoes {
	public static void main(String[] args) {
		ArvoreExpressoes a = new ArvoreExpressoes();
		ArvoreExpressoes b = new ArvoreExpressoes();
		a.armazenarExpressao("(2 ~ 10000)");
		b.armazenarExpressao("1+2/3*4-5^2+3");
		/*
		 * obs metodo de newton se demonstra impreciso com raizes como a acima,
		 * ou em casos de expoentes de mais de 3 casas decimais alem de
		 * apresentar lentidao exagerada e necessidade de um alto numero de
		 * interações para alcancar a precisao da calculadora android, analisar
		 * outro algoritimo para funcionalidade
		 */
		System.out.println(b.expressaoEmOrdem());
		System.out.println(b.calcularExpressao());
		
		System.out.println(a.expressaoEmOrdem());
		System.out.println(a.calcularExpressao());
		
	}
}
