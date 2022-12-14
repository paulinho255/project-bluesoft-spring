package br.com.bluesoft.desafiov3.desafiov3.pedido.business;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import br.com.bluesoft.desafiov3.desafiov3.pedido.model.FormaPagamento;
import br.com.bluesoft.desafiov3.desafiov3.pedido.model.ItemPedido;
import br.com.bluesoft.desafiov3.desafiov3.pedido.model.Pedido;
import br.com.bluesoft.desafiov3.desafiov3.pedido.model.exception.EstoqueVazioException;
import br.com.bluesoft.desafiov3.desafiov3.pedido.model.exception.MaximoPedidoException;
import br.com.bluesoft.desafiov3.desafiov3.pedido.repository.PedidoRepository;
import br.com.bluesoft.desafiov3.desafiov3.pedido.web.form.ItemPedidoFormulario;
import br.com.bluesoft.desafiov3.desafiov3.pedido.web.form.PedidoFormulario;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;

    private final MovimentoEstoqueService movimentoEstoqueService;

    private List<ItemPedido> itens = new ArrayList<>();

    public PedidoService(PedidoRepository pedidoRepository, MovimentoEstoqueService movimentoEstoqueService) {
        this.pedidoRepository = pedidoRepository;
        this.movimentoEstoqueService = movimentoEstoqueService;
    }

    public Pedido novoPedido(PedidoFormulario pedidoFormulario) throws EstoqueVazioException, MaximoPedidoException {
        if(pedidoFormulario.getItens().size() > 50 ) {
        	throw new MaximoPedidoException("Não é possivel criar mais de 50 itens para o estoque. Não cabe no caminhão.");
        }
        
    	Pedido pedido = new Pedido();
        pedido.setFormaPagamento(pedidoFormulario.getFormaPagamento());
        pedido.setRetiradaNaLoja(pedidoFormulario.isRetiradaNaLoja());

        for (ItemPedidoFormulario item : pedidoFormulario.getItens()) {
            ItemPedido itemPedido = new ItemPedido();
            itemPedido.setDescricaoProduto(item.getDescricaoProduto());
            itemPedido.setQuantidade(item.getQuantidade());
            itemPedido.setPedido(pedido);
            itens.add(itemPedido);
            pedido.getItens().add(itemPedido);
        }

        final Pedido pedidoCriado = pedidoRepository.salvarPedido(pedido);

        movimentoEstoqueService.movimentarEstoquePedido(pedidoCriado, pedidoFormulario.isSimularFalha());

        return pedidoCriado;
    }

    public List<Pedido> listarTodos() {
        return pedidoRepository.listarTodos();
    }

    public Map<FormaPagamento, Long> listarQuantidadeDePedidosPorFormaDePagamento() {
        final List<Pedido> todosOsPedidos = pedidoRepository.listarTodos();
        return agruparPedidoPorFormaDePagamento(todosOsPedidos);
    }

    private Map<FormaPagamento, Long> agruparPedidoPorFormaDePagamento(List<Pedido> todosOsPedidos) {
        Map<FormaPagamento, Long> map = new LinkedHashMap<FormaPagamento, Long>();
    	List<FormaPagamento> formaPagamento = new ArrayList<>();
    	
    	todosOsPedidos.forEach(obj -> formaPagamento.add(obj.getFormaPagamento()));
    	    	
    	formaPagamento.stream()
    		.map(obj -> map.put(obj, (long) Collections.frequency(formaPagamento,obj)))
    		.collect(Collectors.toList());    		
        return map;
    }

    public Pedido buscarPedido(Long pedidoId) {
        return pedidoRepository.buscarPedido(pedidoId);
    }

    public Boolean deletarPedido(Long pedidoId) {
        final Pedido pedido = pedidoRepository.buscarPedido(pedidoId);
        
        if (pedido != null) {
        	pedidoRepository.deletarPedido(pedido);
        	return true;
        }       
        
        return false;
    }
}
