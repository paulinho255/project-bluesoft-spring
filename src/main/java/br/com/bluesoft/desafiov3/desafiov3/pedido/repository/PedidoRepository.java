package br.com.bluesoft.desafiov3.desafiov3.pedido.repository;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import br.com.bluesoft.desafiov3.desafiov3.pedido.model.Pedido;

@Repository
public class PedidoRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public Pedido salvarPedido(Pedido pedido) {
        entityManager.persist(pedido);
        return pedido;
    }
    public List<Pedido> listarTodos() {
    	String sql = "SELECT p FROM Pedido p JOIN FETCH p.itens";
    	
        final TypedQuery<Pedido> query = entityManager.createQuery(sql, Pedido.class);
        return query.getResultList();
    }

    public Pedido buscarPedido(Long pedidoId) {
    	
    	String sql = "SELECT p FROM Pedido p JOIN FETCH p.itens WHERE p.id = :pedidoId ";

        final Query query = entityManager.createQuery(sql);

        query.setParameter("pedidoId", pedidoId);
        try {
            return (Pedido) query.getSingleResult();
        } catch (NoResultException ex) {
            return null;
        }
    }
    @Transactional
    public void deletarPedido(Pedido pedido) {
        entityManager.remove(pedido);
    }

}
