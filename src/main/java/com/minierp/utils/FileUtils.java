package com.minierp.utils;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;

public class FileUtils {
    public static File chooseFile(String... extensions) {
        JFileChooser chooser = new JFileChooser();
        if (extensions.length > 0) {
            chooser.setFileFilter(new FileNameExtensionFilter("Files", extensions));
        }
        int result = chooser.showOpenDialog(null);
        return result == JFileChooser.APPROVE_OPTION ? chooser.getSelectedFile() : null;
    }

    public static File chooseSaveFile(String defaultName, String ext) {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File(defaultName));
        int result = chooser.showSaveDialog(null);
        return result == JFileChooser.APPROVE_OPTION ? chooser.getSelectedFile() : null;
    }

    private FileUtils() {}
}
