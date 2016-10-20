package arvore;

public class teste {
	public static void main(String[] args) {
		ArvoreExpressoes a = new ArvoreExpressoes();
		/*a.armazeneExpressao("(((0.00000000000000000000000000000000000009+0.0000000002)-b)/0.00000000000000001)");
		a.expressaoArmazenada();
		*/
		a.armazeneExpressao("(((6 / 3) + 2) * 3)");
		System.out.println(a.calcularExpressao());
		
		
	}
}
