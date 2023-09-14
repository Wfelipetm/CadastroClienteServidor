package cadastroclient;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;
import model.Produto;

/*

 Autor: Wallace Tavares

 */

public class CadastroClient {

    public static void main(String[] args) {
        String servidorIP = "localhost";
        int servidorPorta = 12345;

        try (Socket clienteSocket = new Socket(servidorIP, servidorPorta);
                ObjectOutputStream saida = new ObjectOutputStream(clienteSocket.getOutputStream());
                ObjectInputStream entrada = new ObjectInputStream(clienteSocket.getInputStream())) {

            // Escrever o login e a senha na saída 
            saida.writeObject("op1"); // Login
            saida.writeObject("op1"); // Senha

            // Enviar o comando "L" no canal de saída
            saida.writeObject("L");

            // Receber a coleção de entidades no canal de entrada
            List<Produto> produtos = (List<Produto>) entrada.readObject();

            // Apresentar o nome de cada entidade recebida
            System.out.println("Produtos:");
            produtos.stream().map((produto) -> {
                System.out.println("ID: " + produto.getIdProduto());
                return produto;
            }).map((produto) -> {
                System.out.println("Nome: " + produto.getNome());
                return produto;
            }).map((produto) -> {
                System.out.println("Preço: " + produto.getPrecoVenda());
                return produto;
            }).map((produto) -> {
                System.out.println("Quantidade: " + produto.getQuantidade());
                return produto;
            }).forEachOrdered((_item) -> {
                System.out.println("---------------------------");
            });

        } catch (IOException | ClassNotFoundException e) {
        }
    }
}
