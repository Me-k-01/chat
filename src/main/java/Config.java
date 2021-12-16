import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Config {
    Properties prop;
    public String fileName;

    public Config(String fileName) {
        this.fileName = fileName + ".conf";
        prop = new Properties();

        try (FileInputStream fis = new FileInputStream(this.fileName)) {
            prop.load(fis); // On charge le fichier config
        } catch (IOException err) {
            throw new RuntimeException("Fichier" + this.fileName + " non trouvé.");
        }
    }

    public String get(String key) {
        return prop.getProperty(key).trim(); // Récupérer la propriété
    }
    public int getInt(String key) {
        return Integer.parseInt(get(key));
    }
}
