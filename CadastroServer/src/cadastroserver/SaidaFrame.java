package cadastroserver;

/**
 *
 * @author wfeli
 */
import javax.swing.*;
import java.awt.*;

public class SaidaFrame extends JDialog {

    private JTextArea texto;

    public SaidaFrame() {
        setTitle("Mensagens do Servidor");
        setSize(400, 300);
        setLayout(new BorderLayout());

        texto = new JTextArea();
        texto.setEditable(false);

        JScrollPane scroll = new JScrollPane(texto);
        add(scroll, BorderLayout.CENTER);
    }

    public void adicionarMensagem(String mensagem) {
        texto.append(mensagem + "\n");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SaidaFrame mensagemFrame = new SaidaFrame();
            mensagemFrame.setVisible(true);

            // Exemplo de como adicionar mensagens à janela
            mensagemFrame.adicionarMensagem("Mensagem 1");
            mensagemFrame.adicionarMensagem("Mensagem 2");
        });
    }
}
