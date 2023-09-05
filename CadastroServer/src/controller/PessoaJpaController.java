/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import controller.exceptions.NonexistentEntityException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import model.Movimentacao;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import model.Pessoa;

/**
 *
 * @author wfeli
 */
public class PessoaJpaController implements Serializable {

    public PessoaJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Pessoa pessoa) {
        if (pessoa.getMovimentacaoCollection() == null) {
            pessoa.setMovimentacaoCollection(new ArrayList<Movimentacao>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Collection<Movimentacao> attachedMovimentacaoCollection = new ArrayList<Movimentacao>();
            for (Movimentacao movimentacaoCollectionMovimentacaoToAttach : pessoa.getMovimentacaoCollection()) {
                movimentacaoCollectionMovimentacaoToAttach = em.getReference(movimentacaoCollectionMovimentacaoToAttach.getClass(), movimentacaoCollectionMovimentacaoToAttach.getIdMovimento());
                attachedMovimentacaoCollection.add(movimentacaoCollectionMovimentacaoToAttach);
            }
            pessoa.setMovimentacaoCollection(attachedMovimentacaoCollection);
            em.persist(pessoa);
            for (Movimentacao movimentacaoCollectionMovimentacao : pessoa.getMovimentacaoCollection()) {
                Pessoa oldIdPessoaOfMovimentacaoCollectionMovimentacao = movimentacaoCollectionMovimentacao.getIdPessoa();
                movimentacaoCollectionMovimentacao.setIdPessoa(pessoa);
                movimentacaoCollectionMovimentacao = em.merge(movimentacaoCollectionMovimentacao);
                if (oldIdPessoaOfMovimentacaoCollectionMovimentacao != null) {
                    oldIdPessoaOfMovimentacaoCollectionMovimentacao.getMovimentacaoCollection().remove(movimentacaoCollectionMovimentacao);
                    oldIdPessoaOfMovimentacaoCollectionMovimentacao = em.merge(oldIdPessoaOfMovimentacaoCollectionMovimentacao);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Pessoa pessoa) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Pessoa persistentPessoa = em.find(Pessoa.class, pessoa.getIdPessoa());
            Collection<Movimentacao> movimentacaoCollectionOld = persistentPessoa.getMovimentacaoCollection();
            Collection<Movimentacao> movimentacaoCollectionNew = pessoa.getMovimentacaoCollection();
            Collection<Movimentacao> attachedMovimentacaoCollectionNew = new ArrayList<Movimentacao>();
            for (Movimentacao movimentacaoCollectionNewMovimentacaoToAttach : movimentacaoCollectionNew) {
                movimentacaoCollectionNewMovimentacaoToAttach = em.getReference(movimentacaoCollectionNewMovimentacaoToAttach.getClass(), movimentacaoCollectionNewMovimentacaoToAttach.getIdMovimento());
                attachedMovimentacaoCollectionNew.add(movimentacaoCollectionNewMovimentacaoToAttach);
            }
            movimentacaoCollectionNew = attachedMovimentacaoCollectionNew;
            pessoa.setMovimentacaoCollection(movimentacaoCollectionNew);
            pessoa = em.merge(pessoa);
            for (Movimentacao movimentacaoCollectionOldMovimentacao : movimentacaoCollectionOld) {
                if (!movimentacaoCollectionNew.contains(movimentacaoCollectionOldMovimentacao)) {
                    movimentacaoCollectionOldMovimentacao.setIdPessoa(null);
                    movimentacaoCollectionOldMovimentacao = em.merge(movimentacaoCollectionOldMovimentacao);
                }
            }
            for (Movimentacao movimentacaoCollectionNewMovimentacao : movimentacaoCollectionNew) {
                if (!movimentacaoCollectionOld.contains(movimentacaoCollectionNewMovimentacao)) {
                    Pessoa oldIdPessoaOfMovimentacaoCollectionNewMovimentacao = movimentacaoCollectionNewMovimentacao.getIdPessoa();
                    movimentacaoCollectionNewMovimentacao.setIdPessoa(pessoa);
                    movimentacaoCollectionNewMovimentacao = em.merge(movimentacaoCollectionNewMovimentacao);
                    if (oldIdPessoaOfMovimentacaoCollectionNewMovimentacao != null && !oldIdPessoaOfMovimentacaoCollectionNewMovimentacao.equals(pessoa)) {
                        oldIdPessoaOfMovimentacaoCollectionNewMovimentacao.getMovimentacaoCollection().remove(movimentacaoCollectionNewMovimentacao);
                        oldIdPessoaOfMovimentacaoCollectionNewMovimentacao = em.merge(oldIdPessoaOfMovimentacaoCollectionNewMovimentacao);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = pessoa.getIdPessoa();
                if (findPessoa(id) == null) {
                    throw new NonexistentEntityException("The pessoa with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Pessoa pessoa;
            try {
                pessoa = em.getReference(Pessoa.class, id);
                pessoa.getIdPessoa();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The pessoa with id " + id + " no longer exists.", enfe);
            }
            Collection<Movimentacao> movimentacaoCollection = pessoa.getMovimentacaoCollection();
            for (Movimentacao movimentacaoCollectionMovimentacao : movimentacaoCollection) {
                movimentacaoCollectionMovimentacao.setIdPessoa(null);
                movimentacaoCollectionMovimentacao = em.merge(movimentacaoCollectionMovimentacao);
            }
            em.remove(pessoa);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Pessoa> findPessoaEntities() {
        return findPessoaEntities(true, -1, -1);
    }

    public List<Pessoa> findPessoaEntities(int maxResults, int firstResult) {
        return findPessoaEntities(false, maxResults, firstResult);
    }

    private List<Pessoa> findPessoaEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Pessoa.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Pessoa findPessoa(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Pessoa.class, id);
        } finally {
            em.close();
        }
    }

    public int getPessoaCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Pessoa> rt = cq.from(Pessoa.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
