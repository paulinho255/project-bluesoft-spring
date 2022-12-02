package br.com.bluesoft.desafiov3.desafiov3.pedido.web.controller;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import br.com.bluesoft.desafiov3.desafiov3.pedido.business.PedidoService;
import br.com.bluesoft.desafiov3.desafiov3.pedido.model.Pedido;
import br.com.bluesoft.desafiov3.desafiov3.pedido.model.exception.EstoqueVazioException;
import br.com.bluesoft.desafiov3.desafiov3.pedido.model.exception.MaximoPedidoException;
import br.com.bluesoft.desafiov3.desafiov3.pedido.web.form.PedidoFormulario;
import br.com.bluesoft.desafiov3.desafiov3.pedido.web.view.PedidoView;

@RestController
@RequestMapping("/pedidos")
public class PedidoController {

    private PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @PostMapping("/novo-pedido")
    public ResponseEntity<PedidoView> novoPedido(@RequestBody PedidoFormulario pedidoFormulario) throws EstoqueVazioException, MaximoPedidoException {
        Pedido pedido = pedidoService.novoPedido(pedidoFormulario);
    	URI uri = ServletUriComponentsBuilder
    				.fromCurrentRequest()
    				.path("/{id}")
    				.buildAndExpand(pedido.getId())
    				.toUri(); 
        
    	return ResponseEntity.created(uri).build();
    }

    @GetMapping("/listar-todos-pedidos")
    public ResponseEntity<List<PedidoView>> listarPedidos() {
    	List<PedidoView> pedidos = pedidoService.listarTodos()
    								.stream()
    								.map(item -> new PedidoView(item))
    								.collect(Collectors.toList());  
        return ResponseEntity.ok().body(pedidos);
    }

    @GetMapping("/obterPedidoPorId/{pedidoId}")
    public ResponseEntity<?> obterPedidoPorId(@PathVariable Long pedidoId) {
        final Pedido pedido = pedidoService.buscarPedido(pedidoId);

        if (pedido == null) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(new PedidoView(pedido));
    }

    @DeleteMapping(value = "/{pedidoId}")
    public ResponseEntity<Void> deletarPedido(@PathVariable Long pedidoId) {
        pedidoService.deletarPedido(pedidoId);
        return ResponseEntity.noContent().build();
    }

}
