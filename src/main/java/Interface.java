import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;

public class Interface extends Frame {
    
    TextArea showMessage;
    TextField input;

    public Interface(ActionListener listener, WindowAdapter wa)
    {
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
        input.addActionListener(listener);
        this.add(input);

        Button envoi = new Button("Envoyer le message");
        envoi.setBackground(Color.BLACK);
        envoi.setForeground(Color.WHITE);
        envoi.addActionListener(listener);
        this.add(envoi);

        setVisible(true);
    }
}