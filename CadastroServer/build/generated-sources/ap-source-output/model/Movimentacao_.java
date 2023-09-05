package model;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import model.Pessoa;
import model.Produto;
import model.Usuario;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2023-09-05T04:39:45")
@StaticMetamodel(Movimentacao.class)
public class Movimentacao_ { 

    public static volatile SingularAttribute<Movimentacao, Integer> idMovimento;
    public static volatile SingularAttribute<Movimentacao, Pessoa> idPessoa;
    public static volatile SingularAttribute<Movimentacao, String> tipo;
    public static volatile SingularAttribute<Movimentacao, Produto> idProduto;
    public static volatile SingularAttribute<Movimentacao, Usuario> idUsuario;
    public static volatile SingularAttribute<Movimentacao, Integer> quantidade;
    public static volatile SingularAttribute<Movimentacao, Double> valorUnitario;

}