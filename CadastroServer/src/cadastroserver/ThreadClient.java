package cadastroserver;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.List;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import model.Produto;

public class ThreadClient extends Thread {

    private ObjectInputStream entrada;
    private JTextArea textArea;

    public ThreadClient(ObjectInputStream entrada, JTextArea textArea) {
        this.entrada = entrada;
        this.textArea = textArea;
    }

    @Override
    public void run() {
        try {
            while (true) {
                Object object = entrada.readObject();

                if (object instanceof String) {
                    SwingUtilities.invokeLater(() -> textArea.append(object + "\n"));
                }

                if (object instanceof List) {
                    List<Produto> produtos = (List<Produto>) object;

                    for (Produto produto : produtos) {
                        SwingUtilities.invokeLater(() -> textArea.append(produto.getNome() + " - " + produto.getQuantidade() + "\n"));
                    }
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
