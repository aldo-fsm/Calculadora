package exceptions;

public class ExpressaoMalFormadaException extends RuntimeException{
	public ExpressaoMalFormadaException() {
		super("Expressao mal formada!");
	}
	public ExpressaoMalFormadaException(String causa) {
		super("Expressao mal formada: " + causa);
	}
}
