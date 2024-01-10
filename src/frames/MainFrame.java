package frames;

import components.MoleculeConstructor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;

public class MainFrame extends JFrame {
    private final static int HEIGHT = 1080;
    private final static int WIDTH = 1920;

    MoleculeConstructor moleculeConstructor = new MoleculeConstructor();

    File saveFile;

    public MainFrame() throws HeadlessException {
        setJMenuBar(createMenuBar());

        moleculeConstructor.registerKeyboardAction(e -> save(), KeyStroke.getKeyStroke("control S"), JComponent.WHEN_IN_FOCUSED_WINDOW);
        moleculeConstructor.registerKeyboardAction(e -> load(), KeyStroke.getKeyStroke("control O"), JComponent.WHEN_IN_FOCUSED_WINDOW);

        setSize(WIDTH, HEIGHT);
        add(moleculeConstructor);
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");

        JMenuItem saveItem = new JMenuItem(new AbstractAction("Save") {
            @Override
            public void actionPerformed(ActionEvent e) {
                save();
            }
        });
        JMenuItem saveAsItem = new JMenuItem(new AbstractAction("Save as") {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectFile("select save file name", FileDialog.SAVE);
                moleculeConstructor.saveToFile(saveFile);
            }
        });
        JMenuItem loadItem = new JMenuItem(new AbstractAction("Load") {
            @Override
            public void actionPerformed(ActionEvent e) {
                load();
            }
        });

        fileMenu.add(saveItem);
        fileMenu.add(saveAsItem);
        fileMenu.add(loadItem);

        menuBar.add(fileMenu);

        return menuBar;
    }

    private void save() {
        if (saveFile == null) selectFile("select save file name", FileDialog.SAVE);
        moleculeConstructor.saveToFile(saveFile);
    }
    private void load() {
        selectFile("select file to open", FileDialog.LOAD);
        moleculeConstructor.restoreFromFile(saveFile);
    }

    private void selectFile(String title, int mode) {
        while (true) {
            FileDialog fileChooser = new FileDialog(this, title, mode);

            fileChooser.setMultipleMode(false);

//        String directory = fileChooser.getDirectory();
//
//        File[] files = new File(directory).listFiles();
//        TreeSet<Integer> numbers = new TreeSet<>();
//        if (files != null) {
//            RegularExpression fileMatcher = new RegularExpression("untitled(\\d*)\\.mol");
//            for (File file : files) {
//                Match match = new Match();
//                boolean matches = fileMatcher.matches(file.getName(), match);
//
//                if (matches) numbers.add(Integer.parseInt(match.getCapturedText(0)));
//            }
//        }

            int mex = 0;

//        while (numbers.contains(mex)) mex++;

            fileChooser.setFile("default" + mex + ".mol");
            fileChooser.setVisible(true);

            if (fileChooser.getFiles().length == 0) continue;

            saveFile = fileChooser.getFiles()[0];
            break;
        }
    }
}
