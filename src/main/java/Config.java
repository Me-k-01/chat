import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Config {
    static Properties prop = new Properties();
    static String FILE_NAME = "config.conf";
    
    public static String get(String key) {
        try (FileInputStream fis = new FileInputStream(FILE_NAME)) {
            prop.load(fis); // On charge le fichier config
            return prop.getProperty(key).trim(); // Récupérer la propriété
        } catch (IOException err) {
            throw new RuntimeException("Fichier config.conf non trouvé.");
        }
    }
    public static int getInt(String key) {
        return Integer.parseInt(Config.get(key));
    }
}
