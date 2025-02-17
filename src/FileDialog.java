import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;

public class FileDialog {
    public static File openFileDialog(String title, String[] extensions, String description) {
        String tempPath = "C:\\Users\\" + System.getProperty("user.name") + "\\Documents\\Dolphin Emulator\\Dolphin Normal\\User\\Wii\\shared2\\menu\\FaceLib";
        System.out.println(tempPath);
        JFileChooser fileChooser = new JFileChooser(tempPath);
        FileNameExtensionFilter filter = new FileNameExtensionFilter(description, extensions);
        fileChooser.setFileFilter(filter);
        fileChooser.setDialogTitle(title);

        int returnValue = fileChooser.showOpenDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            return fileChooser.getSelectedFile();
        }
        return null;
    }

    public static File saveFileDialog(String title, String[] extensions, String description) {
        String tempPath = "C:\\Users\\" + System.getProperty("user.name") + "\\Documents\\Dolphin Emulator\\Dolphin Normal\\User\\Wii\\shared2\\menu\\FaceLib";
        System.out.println(tempPath);
        JFileChooser fileChooser = new JFileChooser(tempPath);
        FileNameExtensionFilter filter = new FileNameExtensionFilter(description, extensions);
        fileChooser.setFileFilter(filter);
        fileChooser.setDialogTitle(title);

        int returnValue = fileChooser.showSaveDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            return fileChooser.getSelectedFile();
        }
        return null;
    }
}