package br.com.bluesoft.desafiov3.desafiov3.pedido.business;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.bluesoft.desafiov3.desafiov3.pedido.model.MovimentoEstoque;
import br.com.bluesoft.desafiov3.desafiov3.pedido.model.Pedido;
import br.com.bluesoft.desafiov3.desafiov3.pedido.model.exception.EstoqueVazioException;
import br.com.bluesoft.desafiov3.desafiov3.pedido.repository.MovimentoEstoqueRepository;
import br.com.bluesoft.desafiov3.desafiov3.pedido.repository.PedidoRepository;

@Service
public class MovimentoEstoqueService {

    private final MovimentoEstoqueRepository movimentoEstoqueRepository;
    private final PedidoRepository pedidoRepository;

    public MovimentoEstoqueService(MovimentoEstoqueRepository movimentoEstoqueRepository,PedidoRepository pedidoRepository) {
        this.movimentoEstoqueRepository = movimentoEstoqueRepository;
        this.pedidoRepository = pedidoRepository;
    }

    @Transactional
    public void movimentarEstoquePedido(final Pedido pedido, boolean simularFalha) throws EstoqueVazioException {
        double quantidadeMovimentada = 0;
        quantidadeMovimentada = pedido.getItens().stream().map(item -> item).count();
        
        MovimentoEstoque movimentoEstoque = new MovimentoEstoque();
        movimentoEstoque.setPedidoId(pedido.getId());
        movimentoEstoque.setQuantidadeReservada(quantidadeMovimentada);

        //NÃO APAGAR ESTE BLOCO DE CÓDIGO DE FALHA!!//
        //Aqui é somente uma simulação de algum problema.
        //Seja no banco, com uma comunicação via API, ou mensageria. Apenas para simular um problema ao movimentar o estoque.
        if (simularFalha) {
        	if (movimentoEstoque != null) {
        		this.pedidoRepository.deletarPedido(pedido);
        	}
            simularFalha();
        }
        //

        movimentoEstoqueRepository.salvarMovimentoEstoque(movimentoEstoque);
    }

    private void simularFalha() throws EstoqueVazioException {
        throw new EstoqueVazioException();
    }

}
