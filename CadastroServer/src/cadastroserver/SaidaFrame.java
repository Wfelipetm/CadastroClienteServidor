package cadastroserver;

import javax.swing.JDialog;
import javax.swing.JTextArea;
import javax.swing.*;
import java.awt.*;

// Wallace Tavares

public class SaidaFrame extends JDialog {

    private final JTextArea texto;

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
            mensagemFrame.adicionarMensagem("Mensagem 1");
            mensagemFrame.adicionarMensagem("Mensagem 2");
        });
    }

}
