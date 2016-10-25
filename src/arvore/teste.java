package arvore;

public class teste {
	public static void main(String[] args) {
		ArvoreExpressoes a = new ArvoreExpressoes();
		a.armazeneExpressao("(2^3)");
		System.out.println(a.expressaoEmOrdem());
		System.out.println(a.calcularExpressao());
	}
}
