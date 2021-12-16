import java.awt.*;
import java.awt.event.*;

public class Interface extends Frame {
    
    TextArea showMessage;
    TextField input;

    public Interface(ActionListener listener, WindowAdapter wa) {
        super();
        setTitle("Chat");
        setSize(720, 480);
        setLayout(new FlowLayout());
        setBackground(Color.BLACK);
        addWindowListener(wa);

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
    public void write(String str) { 
        this.showMessage.append(str + "\n");
    }
}