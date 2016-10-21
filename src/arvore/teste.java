package arvore;

public class teste {
	public static void main(String[] args) {
		ArvoreExpressoes a = new ArvoreExpressoes();
		a.armazeneExpressao("( 3 / 0.5 )");
		a.acrescentaNaExpressao("- ( a / b )");
		System.out.println(a.expressaoEmOrdem());
		System.out.println(a.calcularExpressao());
	}
}
