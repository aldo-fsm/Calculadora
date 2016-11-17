package exceptions;

public class ExpressaoMalFormadaException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ExpressaoMalFormadaException() {
		super("Expressao mal formada!");
	}

	public ExpressaoMalFormadaException(String causa) {
		super("Expressao mal formada: " + causa);
	}
}
