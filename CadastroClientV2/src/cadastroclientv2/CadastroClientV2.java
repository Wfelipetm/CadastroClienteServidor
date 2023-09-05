package cadastroclientv2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class CadastroClientV2 {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 12345;
    //private static final int SERVER_PORT = 4321; // Porta do servidor

    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            System.out.println("Conectado ao servidor CadastroServer.");
            System.out.print("Digite seu nome de usuário: ");
            String username = consoleInput();
            System.out.print("Digite sua senha: ");
            String password = consoleInput();

            out.println(username);
            out.println(password);

            String response = in.readLine();
            System.out.println(response);

            if (response.equals("Autenticação bem-sucedida. Aguardando comandos...")) {
                while (true) {
                    printMenu();
                    String command = consoleInput();
                    out.println(command);

                    if (command.equalsIgnoreCase("X")) {
                        break;
                    } else if (command.equalsIgnoreCase("L")) {
                        // Receba e exiba todos os produtos de uma vez
                        receiveAndDisplayProductList(in);
                    } else if (command.equalsIgnoreCase("E") || command.equalsIgnoreCase("S")) {
                        System.out.print("Informe o Id da pessoa: ");
                        String personId = consoleInput();
                        out.println(personId);

                        System.out.print("Informe o Id do produto: ");
                        String productId = consoleInput();
                        out.println(productId);

                        System.out.print("Informe a quantidade: ");
                        String quantity = consoleInput();
                        out.println(quantity);

                        System.out.print("Informe o valor unitário: ");
                        String unitPrice = consoleInput();
                        out.println(unitPrice);
                    } else {
                        System.out.println("Comando inválido.");
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void printMenu() {
        System.out.println("Menu:");
        System.out.println("L – Listar");
        System.out.println("X – Finalizar");
        System.out.println("E – Entrada");
        System.out.println("S – Saída");
        System.out.print("Escolha uma opção: ");
    }

    private static String consoleInput() {
        BufferedReader consoleIn = new BufferedReader(new InputStreamReader(System.in));
        try {
            return consoleIn.readLine();
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    private static void receiveAndDisplayProductList(BufferedReader in) throws IOException {
        System.out.println("Conjunto de produtos disponíveis:");
        String line;
        while ((line = in.readLine()) != null && !line.isEmpty()) {
            System.out.println(line);
        }
    }
}
