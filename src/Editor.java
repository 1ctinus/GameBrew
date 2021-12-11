import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

class Editor {
    // COPIED CODE FROM SO
//    static boolean isSaved = true;
    static  JFrame frame;
    // END OF COPIED CODE
    static JTextPane ta;
    static String currentFile = "";
    private static JTextArea lines;
    static JMenuItem menuItemOpen;
    static JMenuItem menuItemSave;
    static JMenuItem menuItemFind;
    static JMenuItem menuItemReplace;
    static JMenuBar menuBar;
    static JMenu menuFile;
    static JPanel find;
    static JTextArea findTA;
    static JButton findButton;
    static JMenu menuEdit;
    static JPanel replace;
    static JButton replaceButton;
    static JTextArea replaceTA;
    final static int[] currentWord = {0}; // what the f$#@?
    private static void styleGUI(){
        menuBar.setBorder(BorderFactory.createLineBorder(new Color(60,60,60)));
        ta.setBackground(new Color(30,30,30));
        ta.setBorder(BorderFactory.createEmptyBorder());
        ta.setCaretColor(Color.WHITE);
        menuFile.getPopupMenu().setBackground(Color.PINK);
menuFile.getPopupMenu().setBorder(BorderFactory.createLineBorder(new Color(40,40,40), 1));
        findTA.setBorder( BorderFactory.createLineBorder(new Color(40,40,40), 1));
    }
    private static void displayGUI() {
        String[] styledElements = {"Menu", "TextArea", "MenuBar", "MenuItem", "TextPane", "Button"};
        for(String elem : styledElements){
            UIManager.put(elem+".foreground", Color.WHITE);
            UIManager.put(elem+".background", new Color(50,50,50));
        }
        UIManager.put("MenuItem.border",BorderFactory.createLineBorder(new Color(50,50,50), 1));
        UIManager.put("PopupMenu.border",BorderFactory.createLineBorder(new Color(50,50,50), 1));
        UIManager.put("Button.border",BorderFactory.createLineBorder(new Color(40,40,40), 1));
        UIManager.put("TextPane.border",BorderFactory.createLineBorder(new Color(40,40,40), 1));
        ta = new JTextPane();
       frame = new JFrame("New File");
        final FileDialog fileDialog = new FileDialog(frame,"Select file");
        menuBar = new JMenuBar();
        menuFile = new JMenu("File");
        menuEdit = new JMenu("Edit");
        findTA = new JTextArea();
        replace = new JPanel();
        replace.setLayout(new BorderLayout());
        find = new JPanel();
        find.setLayout(new BorderLayout());
        findButton = new JButton("Next");
        replaceButton = new JButton("Replace");
        replaceButton.addActionListener(e -> {
            if(!Objects.equals(findTA.getText(), "") && !Objects.equals(replaceTA.getText(), "")) {
                ta.setText(ta.getText().replace(findTA.getText(), replaceTA.getText()));
            }
        });
        replaceTA = new JTextArea();
        JScrollPane replaceScroll = new JScrollPane(replaceTA); //place the JTextArea in a replaceScroll pane
        replaceScroll.setBorder(BorderFactory.createEmptyBorder());
        replace.add(replaceScroll, BorderLayout.CENTER); //add the JScrollPane to the panel
// CENTER will use up all available space
        replace.add(replaceButton, BorderLayout.EAST);
        replace.setBorder(BorderFactory.createEmptyBorder());
        replace.setVisible(false);
        JScrollPane findScroll = new JScrollPane(findTA);
        findScroll.setBorder(BorderFactory.createEmptyBorder());
        find.add(findScroll, BorderLayout.CENTER); //add the JScrollPane to the panel
// CENTER will use up all available space
        find.add(findButton, BorderLayout.EAST);
        find.setBorder(BorderFactory.createEmptyBorder());
        find.setVisible(false);
        menuFile.setMnemonic(KeyEvent.VK_F);
        menuBar.add(menuFile);
        menuBar.add(menuEdit);
        menuItemOpen = new JMenuItem("Open");
        menuItemOpen.setMnemonic(KeyEvent.VK_O);
        menuFile.add(menuItemOpen);
        menuItemSave = new JMenuItem("Save");
        menuItemSave.setMnemonic(KeyEvent.VK_S);
        menuFile.add(menuItemSave);
        menuItemFind = new JMenuItem("Find");
        menuItemReplace = new JMenuItem("Replace");
        menuEdit.add(menuItemFind);
        menuEdit.add(menuItemReplace);
        KeyStroke keyStrokeToSave
                = KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK);
        menuItemSave.setAccelerator(keyStrokeToSave);
        KeyStroke keyStrokeToOpen
                = KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_DOWN_MASK);
        menuItemOpen.setAccelerator(keyStrokeToOpen);
        menuItemSave.setAccelerator(keyStrokeToSave);
        menuItemFind.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, KeyEvent.CTRL_DOWN_MASK));
        menuItemReplace.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, KeyEvent.CTRL_DOWN_MASK));
        menuItemOpen.addActionListener(evt -> {
            fileDialog.setVisible(true);
            if (fileDialog.getFile() != null) {
                currentFile = fileDialog.getDirectory() + fileDialog.getFile();
                byte[] encoded;
                try {
                    encoded = Files.readAllBytes(Paths.get(currentFile));
                    ta.setText(new String(encoded));
                } catch (IOException e) {
                    ta.setText("");
                }
                frame.setTitle(currentFile);
            }
        });
        menuItemFind.addActionListener(evt -> {
            find.setVisible(!find.isVisible());
            currentWord[0] = 0;
        });
        menuItemReplace.addActionListener(e -> {
            find.setVisible(!replace.isVisible());
            replace.setVisible(!replace.isVisible());
        });
        menuItemSave.addActionListener(evt -> {
            if(!Objects.equals(currentFile, "")){
                try {
                    System.out.println("i am running");
                    FileWriter myWriter = new FileWriter(currentFile);
                    myWriter.write(ta.getText());
                    myWriter.close();
                    frame.setTitle(currentFile);
                    System.out.println("Successfully wrote to the file.");
                } catch (IOException e) {
                    System.out.println("An error occurred.");
                    e.printStackTrace();
                }
            }
        });
        lines = new JTextArea("1");
        lines.setEditable(false);
        UndoTool.addUndoFunctionality(ta);
        final JScrollPane[] jsp = {new JScrollPane(ta)};
        findTA.setMaximumSize(new Dimension(Integer.MAX_VALUE, findTA.getMinimumSize().height));
        replace.setMaximumSize(new Dimension(Integer.MAX_VALUE, findTA.getMinimumSize().height));
           find.setMaximumSize(new Dimension(Integer.MAX_VALUE, findTA.getMinimumSize().height));

        replaceButton.setPreferredSize(new Dimension(replaceButton.getPreferredSize().width, findTA.getMinimumSize().height));
        findButton.setPreferredSize(new Dimension(replaceButton.getPreferredSize().width, findTA.getMinimumSize().height));
//        ((FlowLayout)replace.getLayout()).setVgap(0);
        findTA.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                try {
                    Highlighting.highlightFind(ta, findTA.getText(), 0);
                } catch (BadLocationException ex) {
                    ex.printStackTrace();
                }
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                try {
                    if(!Objects.equals(findTA.getText(), "")) {
                        Highlighting.highlightFind(ta, findTA.getText(), 0);
                    } else {
                        ta.getHighlighter().removeAllHighlights();
                    }
                } catch (BadLocationException ex) {
                    ex.printStackTrace();
                }
            }

            @Override
            public void changedUpdate(DocumentEvent e) {

            }
        });
        Action sendAction = new AbstractAction("Send"){
            public void actionPerformed(ActionEvent ae){
                            currentWord[0]++;
                            try {
                                Highlighting.highlightFind(ta, findTA.getText(), currentWord[0]);
                            } catch (BadLocationException ex) {
                                ex.printStackTrace();
                            }
                        }}
        ;
        findButton.addActionListener(sendAction);
        findTA.registerKeyboardAction(sendAction,
                KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), JComponent.WHEN_FOCUSED);
        jsp[0].setBorder(BorderFactory.createLineBorder(new Color(30,30,30)));
        ta.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
        lines.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));

        jsp[0].getVerticalScrollBar().setUI(new BasicScrollBarUI() {
            @Override protected void configureScrollBarColors() { this.thumbColor = new Color(50,50,50);this.trackColor = new Color(35,35,35); }
            @Override
            protected JButton createDecreaseButton(int orientation) {
                return createZeroButton();
            }

            @Override
            protected JButton createIncreaseButton(int orientation) {
                return createZeroButton();
            }

            private JButton createZeroButton() {
                JButton jbutton = new JButton();
                jbutton.setPreferredSize(new Dimension(0, 0));
                jbutton.setMinimumSize(new Dimension(0, 0));
                jbutton.setMaximumSize(new Dimension(0, 0));
                return jbutton;
            }
        });
        jsp[0].getHorizontalScrollBar().setBackground(Color.BLACK);
        jsp[0].setRowHeaderView(lines);
        styleGUI();
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.add(find, BorderLayout.CENTER);
        p.add(replace, BorderLayout.CENTER);
        p.add(jsp[0], BorderLayout.CENTER);
        frame.setJMenuBar(menuBar);
        frame.add(p);
        frame.setSize(400,400);
        frame.setIconImage(new ImageIcon("src/tinylogo.gif").getImage().getScaledInstance(24, 24, 2));
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setVisible(true);
        ta.getDocument().addDocumentListener(new DocumentListener() {
            // tutorialspoint.com is a godsend
            public String getText() {
                int caretPosition = ta.getDocument().getLength();
                Element root = ta.getDocument().getDefaultRootElement();
                StringBuilder text = new StringBuilder("1" + System.getProperty("line.separator"));
                for (int i = 2; i < root.getElementIndex(caretPosition) + 2; i++) {
                    text.append(i).append(System.getProperty("line.separator"));
                }
                return text.toString();
            }
            @Override
            public void insertUpdate(DocumentEvent e) {
                ta.getHighlighter().removeAllHighlights();
                Highlighting mm = new Highlighting(ta);
                mm.execute();
                lines.setText(getText());
                frame.setTitle(currentFile+"*");
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                ta.getHighlighter().removeAllHighlights();

                Highlighting mm = new Highlighting(ta);
                mm.execute();
                lines.setText(getText());
                frame.setTitle(currentFile+"*");
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
            }
        });
    }
    public static void main(String[] a) {
        Runnable r = Editor::displayGUI;
        SwingUtilities.invokeLater(r);
    }
}
