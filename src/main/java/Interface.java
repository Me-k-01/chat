import java.awt.Button;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.TextArea;
import java.awt.TextField;

public class Interface extends Frame {
    
    TextArea showMessage;

    public Interface()
    {
        super();
        setTitle("Chat");
        setSize(720, 480);
        setLayout(new FlowLayout());

        showMessage = new TextArea();
        showMessage.setEditable(false);
        this.add(showMessage);

        TextField input = new TextField("", 20);
        this.add(input);

        Button envoi = new Button("Envoyer le message");
        this.add(envoi);

        setVisible(true);
    }
}