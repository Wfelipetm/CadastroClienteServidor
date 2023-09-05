package model;

import javax.annotation.Generated;
import javax.persistence.metamodel.CollectionAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import model.Movimentacao;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2023-09-05T04:39:45")
@StaticMetamodel(Produto.class)
public class Produto_ { 

    public static volatile SingularAttribute<Produto, Double> precoVenda;
    public static volatile SingularAttribute<Produto, Integer> idProduto;
    public static volatile SingularAttribute<Produto, String> nome;
    public static volatile SingularAttribute<Produto, Integer> quantidade;
    public static volatile CollectionAttribute<Produto, Movimentacao> movimentacaoCollection;

}