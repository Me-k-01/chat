import java.awt.Button;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.ActionListener;

public class Interface extends Frame {
    
    TextArea showMessage;
    TextField input;

    public Interface(ActionListener listener)
    {
        super();
        setTitle("Chat");
        setSize(720, 480);
        setLayout(new FlowLayout());
        setBackground(Color.BLACK);

        showMessage = new TextArea();
        showMessage.setEditable(false);
        showMessage.setBackground(Color.BLACK);
        showMessage.setForeground(Color.WHITE);
        this.add(showMessage);

        input = new TextField("", 20);
        input.setBackground(Color.BLACK);
        input.setForeground(Color.WHITE);
        this.add(input);

        Button envoi = new Button("Envoyer le message");
        envoi.setBackground(Color.BLACK);
        envoi.setForeground(Color.WHITE);
        envoi.addActionListener(listener);
        this.add(envoi);

        setVisible(true);
    }
}