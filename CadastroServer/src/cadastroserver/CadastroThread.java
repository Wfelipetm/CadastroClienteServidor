package cadastroserver;

import controller.MovimentacaoJpaController;
import controller.PessoaJpaController;
import controller.ProdutoJpaController;
import controller.UsuarioJpaController;
import controller.exceptions.NonexistentEntityException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.net.Socket;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.Movimentacao;
import model.Pessoa;
import model.Produto;

import model.Usuario;

public class CadastroThread extends Thread {
    private ProdutoJpaController ctrlProd;
    private UsuarioJpaController ctrlUsu;
    private MovimentacaoJpaController ctrlMov;
    private PessoaJpaController ctrlPessoa;
    private Socket s1;
    
    public CadastroThread(ProdutoJpaController ctrlProd, UsuarioJpaController ctrlUsu, 
                            MovimentacaoJpaController ctrlMov, PessoaJpaController ctrlPessoa, Socket s1) {
        this.ctrlProd = ctrlProd;
        this.ctrlUsu = ctrlUsu;
        this.ctrlMov = ctrlMov;
        this.ctrlPessoa = ctrlPessoa;
        this.s1 = s1;
    }

    // ...

    @Override
    public void run() {
	try (
	    ObjectOutputStream saida = new ObjectOutputStream(s1.getOutputStream());
	    ObjectInputStream entrada = new ObjectInputStream(s1.getInputStream())
	) {
	    // Obter o login e a senha a partir da entrada
	    String login = (String) entrada.readObject();
	    String senha = (String) entrada.readObject();

	    // Utilizar ctrlUsu para verificar o login
	    Usuario usuario = ctrlUsu.findUsuariosenha(login, senha);

	    if (usuario == null) {
		// Se o usuário for nulo, encerrar a conexão
		System.out.println("Usuário inválido. Conexão encerrada.");
		return;
	    }

	    // Loop de resposta
	    while (true) {
		// Obter o comando a partir da entrada
		String comando = (String) entrada.readObject();

		if ("L".equals(comando)) {
		    // Utilizar ctrlProd para retornar o conjunto de produtos através da saída
		    List<Produto> produtos = ctrlProd.findProdutoEntities();
		    saida.writeObject(produtos);
		    
		} else if ("E".equalsIgnoreCase(comando)) {
		    if (realizarEntrada(entrada, usuario)) {
			saida.writeObject("Entrada realizada com sucesso.");
		    } else {
			saida.writeObject("Erro ao realizar entrada.");
		    }
		} else if ("S".equalsIgnoreCase(comando)) {
		    if (realizarSaida(entrada, usuario)) {
			saida.writeObject("Saída realizada com sucesso.");
		    } else {
			saida.writeObject("Erro ao realizar saída.");
		    }
		}else if ("X".equals(comando)) {
		    saida.writeObject("SAINDO");
		}
	    }
	    } catch (IOException | ClassNotFoundException e) {
		e.printStackTrace();
	    } catch (Exception ex) {
		Logger.getLogger(CadastroThread.class.getName()).log(Level.SEVERE, null, ex);
	    } finally {
		try {
		    s1.close();
		} catch (IOException e) {
		    e.printStackTrace();
		}
	    }
    }

    private boolean realizarEntrada(ObjectInputStream entrada, Usuario usuario) throws IOException, ClassNotFoundException {
	// Receber os dados para entrada de produtos
	Integer idPessoaObj = (Integer) entrada.readObject();
	Integer idProdutoObj = (Integer) entrada.readObject();
	Integer quantidadeObj = (Integer) entrada.readObject();
	Double valorUnitarioObj = (Double) entrada.readObject();
	
	int idPessoa = idPessoaObj.intValue();
	int idProduto = idProdutoObj.intValue();
	int quantidade = quantidadeObj.intValue();
	double valorUnitario = valorUnitarioObj.doubleValue();

	// Obtenha as entidades Pessoa e Produto usando os controladores correspondentes
	Pessoa pessoa = ctrlPessoa.findPessoa(idPessoa);
	Produto produto = ctrlProd.findProduto(idProduto);

	// Verifique se as entidades foram encontradas
	if (pessoa == null || produto == null) {
	    System.out.println("Pessoa ou Produto não encontrado. Movimento não registrado.");
	    return false;
	}

	// Verifique se a quantidade é válida (maior que zero)
	if (quantidade <= 0) {
	    System.out.println("Quantidade inválida. Movimento não registrado.");
	    return false;
	}

	// Crie um objeto Movimentacao para entrada de produtos
	Movimentacao movimento = new Movimentacao();
	movimento.setIdUsuario(usuario);
	movimento.setTipo("E"); // Tipo de movimento de entrada
	movimento.setIdPessoa(pessoa);
	movimento.setIdProduto(produto);
	movimento.setQuantidade(quantidade);
	movimento.setValorUnitario(valorUnitario);
	int novaQuantidade = produto.getQuantidade() + quantidade;
	try {

		// Atualize a quantidade do produto
		produto.setQuantidade(novaQuantidade);
		ctrlProd.edit(produto);

		
	    } catch (Exception ex) {
		System.out.println("Erro ao realizar a persistencia em produto.");
		ex.printStackTrace();
		return false;
	    }
	try{
		// Persista o movimento
		ctrlMov.create(movimento);
		return true;
		}catch (Exception ex) {
		System.out.println("Erro ao realizar a persistencia em movimento.");
		ex.printStackTrace();
		return false;
	    }
    }

    private boolean realizarSaida(ObjectInputStream entrada, Usuario usuario) throws IOException, ClassNotFoundException {
	// Receber os dados para saída de produtos
	Integer idPessoaObj = (Integer) entrada.readObject();
	Integer idProdutoObj = (Integer) entrada.readObject();
	Integer quantidadeObj = (Integer) entrada.readObject();
	Double valorUnitarioObj = (Double) entrada.readObject();
	
	int idPessoa = idPessoaObj.intValue();
	int idProduto = idProdutoObj.intValue();
	int quantidade = quantidadeObj.intValue();
	double valorUnitario = valorUnitarioObj.doubleValue();

	// Obtenha as entidades Pessoa e Produto usando os controladores correspondentes
	Pessoa pessoa = ctrlPessoa.findPessoa(idPessoa);
	Produto produto = ctrlProd.findProduto(idProduto);

	// Verifique se as entidades foram encontradas
	if (pessoa == null || produto == null) {
	    System.out.println("Pessoa ou Produto não encontrado. Movimento não registrado.");
	    return false;
	}

	// Verifique se a quantidade é válida (maior que zero)
	if (quantidade <= 0) {
	    System.out.println("Quantidade inválida. Movimento não registrado.");
	    return false;
	}

	int novaQuantidade = produto.getQuantidade() - quantidade;

	if (novaQuantidade >= 0) {
	    // Crie um objeto Movimentacao para saída de produtos
	    Movimentacao movimento = new Movimentacao();
	    movimento.setIdUsuario(usuario);
	    movimento.setTipo("S"); // Tipo de movimento de saída
	    movimento.setIdPessoa(pessoa);
	    movimento.setIdProduto(produto);
	    movimento.setIdMovimento(quantidade);
	    movimento.setValorUnitario(valorUnitario);
	    
	    try {
		// Atualize a quantidade do produto
		produto.setQuantidade(novaQuantidade);
		ctrlProd.edit(produto);

		
	    } catch (Exception ex) {
		System.out.println("Erro ao realizar a persistencia em produto.");
		ex.printStackTrace();
		return false;
	    }
	    try{
		// Persista o movimento
		ctrlMov.create(movimento);
		return true;
		}catch (Exception ex) {
		System.out.println("Erro ao realizar a persistencia em movimento.");
		ex.printStackTrace();
		return false;
	    }
	} else {
	    System.out.println("Estoque insuficiente para a saída.");
	    return false;
	}
    }
}













/*
import controller.MovimentacaoJpaController;
import controller.PessoaJpaController;
import controller.ProdutoJpaController;
import controller.UsuarioJpaController;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.Movimentacao;
import model.Pessoa;
import model.Produto;



public class CadastroThread extends Thread {
    private final ProdutoJpaController ctrlProduto;
    private final UsuarioJpaController ctrlUsuario;
    private final PessoaJpaController ctrlPessoa;
    private final MovimentacaoJpaController ctrlMovimento;
    private final Socket socket;
    private volatile boolean isRunning = true; // Variável de controle para encerrar a thread

    public CadastroThread(
        ProdutoJpaController ctrlProduto,
        UsuarioJpaController ctrlUsuario,
        PessoaJpaController ctrlPessoa,
        MovimentacaoJpaController ctrlMovimento,
        Socket socket
    ) {
        this.ctrlProduto = ctrlProduto;
        this.ctrlUsuario = ctrlUsuario;
        this.ctrlPessoa = ctrlPessoa;
        this.ctrlMovimento = ctrlMovimento;
        this.socket = socket;
    }

    @Override
    public void run() {
        try (
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
        ) {
            // Autenticação
            String username = in.readLine();
            String password = in.readLine();

            if (validateCredentials(username, password)) {
                out.writeObject("Autenticação bem-sucedida. Aguardando comandos...");

                while (isRunning) { // Use a variável de controle para determinar se a thread deve continuar
                    String command = in.readLine();
                    if (command != null) {
                        if (command.equals("L")) {
                            // Enviar conjunto de produtos
                            sendProductList(out);
                        } else if (command.equals("E") || command.equals("S")) {
                            // Processar entrada (E) ou saída (S) de produtos
                            processMovement(command, in, out);
                        } else if (command.equals("X")) {
                            // Comando para sair
                            break;
                        }
                    }
                }
            } else {
                out.writeObject("Credenciais inválidas. Conexão encerrada.");
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception ex) {
            Logger.getLogger(CadastroThread.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            // Encerre a conexão e a thread
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean validateCredentials(String username, String password) {
        // Adicione sua lógica de validação de credenciais aqui
        // Você pode usar ctrlUsuario para verificar as credenciais do usuário no banco de dados
        return true; // Temporariamente, retorna true para fins de teste
    }

    private void sendProductList(ObjectOutputStream out) throws IOException {
        List<Produto> productList = ctrlProduto.findProdutoEntities();
        out.writeObject("Conjunto de produtos disponíveis:");

        for (Produto product : productList) {
            out.writeObject(product.getNome());
        }
        // Adicione uma linha em branco para indicar o fim da lista
        out.writeObject(" ");
    }

    private void processMovement(String type, ObjectInputStream in, ObjectOutputStream out) throws IOException, Exception {
        try {
            String personIdStr = in.readLine();
            String productIdStr = in.readLine();
            String quantityStr = in.readLine();
            String unitPriceStr = in.readLine();

            int personId = Integer.parseInt(personIdStr);
            int productId = Integer.parseInt(productIdStr);
            int quantity = Integer.parseInt(quantityStr);
            double unitPrice = Double.parseDouble(unitPriceStr);

            Pessoa person = ctrlPessoa.findPessoa(personId);
            Produto product = ctrlProduto.findProduto(productId);

            if (person != null && product != null) {
                // Verificar a quantidade disponível para saída
                if (type.equals("S") && product.getQuantidade() < quantity) {
                    out.writeObject("Quantidade insuficiente para saída. Operação cancelada.");
                } else {
                    // Criar o objeto Movimento
                    Movimentacao movement = new Movimentacao();
                    movement.setIdPessoa(person);
                    movement.setIdProduto(product);
                    movement.setQuantidade(quantity);
                    movement.setValorUnitario(unitPrice);
                    movement.setTipo(type);

                    // Persistir o movimento usando o novo controlador ctrlMovimento
                    ctrlMovimento.create(movement);

                    // Atualizar a quantidade do produto de acordo com o tipo de movimento
                    if (type.equals("E")) {
                        product.setQuantidade(product.getQuantidade() + quantity);
                    } else if (type.equals("S")) {
                        product.setQuantidade(product.getQuantidade() - quantity);
                    }
                    ctrlProduto.edit(product);

                    out.writeObject("Operação concluída com sucesso.");
                }
            } else {
                out.writeObject("Pessoa ou produto não encontrados. Operação cancelada.");
            }
        } catch (NumberFormatException e) {
            out.writeObject("Entrada inválida. Operação cancelada.");
        }
    }

    // Método para encerrar a thread de maneira adequada
    public void stopThread() {
        isRunning = false;
    }
}

*/
