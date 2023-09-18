package cadastroclientv2;
import cadastroserver.SaidaFrame;
import java.io.*;
import java.net.*;
import java.util.List;
import model.Produto;


// Autor: Wallace Tavares
public class CadastroClientV2 {
    public static void main(String[] args) {
        try {
            Socket socket = new Socket("localhost", 12345);
            ObjectOutputStream saida = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream entrada = new ObjectInputStream(socket.getInputStream());
            saida.writeObject("op1");
            saida.writeObject("op1");
            BufferedReader teclado = new BufferedReader(new InputStreamReader(System.in));
            SaidaFrame mensagemFrame = new SaidaFrame();
            mensagemFrame.setVisible(true);
            while (true) {
                mensagemFrame.adicionarMensagem("Menu:");
                mensagemFrame.adicionarMensagem("L - Listar");
                mensagemFrame.adicionarMensagem("X - Finalizar");
                mensagemFrame.adicionarMensagem("E - Entrada");
                mensagemFrame.adicionarMensagem("S - Saída");
                System.out.print("Escolha uma opção: ");
                String comando = teclado.readLine();
                if (comando.equalsIgnoreCase("L")) {
                    saida.writeObject("L");
                    Object resposta = entrada.readObject();
                    if (resposta instanceof List) {
                        List<Produto> produtos = (List<Produto>) resposta;
                        mensagemFrame.adicionarMensagem("Lista de produtos: ");
                        for (Produto produto : produtos) {
                            mensagemFrame.adicionarMensagem("ID: " + produto.getIdProduto());
                            mensagemFrame.adicionarMensagem("Nome: " + produto.getNome());
                            mensagemFrame.adicionarMensagem("Preço: " + produto.getPrecoVenda());
                            mensagemFrame.adicionarMensagem("Quantidade: " + produto.getQuantidade());
                            mensagemFrame.adicionarMensagem("============================================");
                        }
                    } else {
                        mensagemFrame.adicionarMensagem("Resposta do servidor não é uma lista de produtos.");
                    }
                } else if (comando.equalsIgnoreCase("X")) {
                    saida.writeObject("X");
                    Object resposta = entrada.readObject();
                    mensagemFrame.adicionarMensagem((String) resposta);
                    break;
                } else if (comando.equalsIgnoreCase("E") || comando.equalsIgnoreCase("S")) {
                    saida.writeObject(comando);
                    mensagemFrame.adicionarMensagem("ID da pessoa: ");
                    int idPessoa = Integer.parseInt(teclado.readLine());
                    mensagemFrame.adicionarMensagem("ID do produto: ");
                    int idProduto = Integer.parseInt(teclado.readLine());
                    mensagemFrame.adicionarMensagem("Quantidade: ");
                    int quantidade = Integer.parseInt(teclado.readLine());
                    mensagemFrame.adicionarMensagem("Valor unitário: ");
                    double valorUnitario = Double.parseDouble(teclado.readLine());
                    saida.writeObject(idPessoa);
                    saida.writeObject(idProduto);
                    saida.writeObject(quantidade);
                    saida.writeObject(valorUnitario);
                    Object resposta = entrada.readObject();
                    mensagemFrame.adicionarMensagem((String) resposta);
                }
            }
            saida.close();
            entrada.close();
            socket.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}

