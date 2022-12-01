package br.com.bluesoft.desafiov3.desafiov3.pedido.model.exception;

public class EstoqueVazioException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public EstoqueVazioException() {
        super("Estoque está vazio!");
    }
}
