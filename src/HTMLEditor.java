import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.print.PrinterException;
import java.io.*;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;

public class HTMLEditor extends JFrame {
    private RSyntaxTextArea textArea;
    private JLabel lineCountLabel;
    private File currentFile; // Variable para almacenar el archivo actual

    public HTMLEditor() {
        setTitle("HTML Editor");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        textArea = new RSyntaxTextArea();
        textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_HTML);
        textArea.setCodeFoldingEnabled(true);
        textArea.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateLineCount();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateLineCount();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateLineCount();
            }
        });

        RTextScrollPane scrollPane = new RTextScrollPane(textArea);
        getContentPane().add(scrollPane, BorderLayout.CENTER);

        lineCountLabel = new JLabel();
        lineCountLabel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
        updateLineCount();
        getContentPane().add(lineCountLabel, BorderLayout.WEST);

        JMenuBar menuBar = new JMenuBar();

        // Menu File
        JMenu fileMenu = new JMenu("File");

        JMenuItem newItem = new JMenuItem("New");
        newItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                newFile();
            }
        });
        fileMenu.add(newItem);

        JMenuItem openItem = new JMenuItem("Open");
        openItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openFile();
            }
        });
        fileMenu.add(openItem);

        JMenuItem saveItem = new JMenuItem("Save");
        saveItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveFile();
            }
        });
        fileMenu.add(saveItem);

        JMenuItem saveAsItem = new JMenuItem("Save As");
        saveAsItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveFileAs();
            }
        });
        fileMenu.add(saveAsItem);

        JMenuItem printItem = new JMenuItem("Print");
        printItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                printFile();
            }
        });
        fileMenu.add(printItem);

        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        fileMenu.add(exitItem);

        menuBar.add(fileMenu);

        // Menu Edit
        JMenu editMenu = new JMenu("Edit");

        JMenuItem findItem = new JMenuItem("Find");
        findItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showFindDialog();
            }
        });
        editMenu.add(findItem);

        JMenuItem replaceItem = new JMenuItem("Replace");
        replaceItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showReplaceDialog();
            }
        });
        editMenu.add(replaceItem);

        JMenuItem goToItem = new JMenuItem("Go To");
        goToItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showGoToDialog();
            }
        });
        editMenu.add(goToItem);

        menuBar.add(editMenu);

        setJMenuBar(menuBar);
    }

    private void newFile() {
        textArea.setText("");
        currentFile = null; // Reinicia el archivo actualmente abierto
    }

    private void openFile() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try {
                FileReader reader = new FileReader(file);
                textArea.read(reader, null);
                reader.close();
                currentFile = file; // Guarda el archivo actualmente abierto
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveFile() {
        if (textArea.getText().isEmpty()) {
            return;
        }
        if (currentFile == null) {
            saveFileAs();
        } else {
            try {
                FileWriter writer = new FileWriter(currentFile);
                textArea.write(writer);
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveFileAs() {
        if (textArea.getText().isEmpty()) {
            return;
        }
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try {
                FileWriter writer = new FileWriter(file);
                textArea.write(writer);
                writer.close();
                currentFile = file; // Guarda el nuevo archivo seleccionado
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void printFile() {
        try {
            textArea.print();
        } catch (PrinterException e) {
            e.printStackTrace();
        }
    }

    private void showFindDialog() {
        String searchText = JOptionPane.showInputDialog(this, "Find:");
        if (searchText != null && !searchText.isEmpty()) {
            String content = textArea.getText();
            int index = content.indexOf(searchText);
            if (index != -1) {
                textArea.setSelectionStart(index);
                textArea.setSelectionEnd(index + searchText.length());
                textArea.requestFocusInWindow();
            } else {
                JOptionPane.showMessageDialog(this, "Text not found.");
            }
        }
    }

    private void showReplaceDialog() {
        String searchText = JOptionPane.showInputDialog(this, "Find:");
        if (searchText != null && !searchText.isEmpty()) {
            String replaceText = JOptionPane.showInputDialog(this, "Replace with:");
            if (replaceText != null) {
                String content = textArea.getText();
                content = content.replace(searchText, replaceText);
                textArea.setText(content);
            }
        }
    }

    private void showGoToDialog() {
        String lineNumberText = JOptionPane.showInputDialog(this, "Go to line:");
        if (lineNumberText != null && !lineNumberText.isEmpty()) {
            try {
                int lineNumber = Integer.parseInt(lineNumberText);
                Element root = textArea.getDocument().getDefaultRootElement();
                int lineCount = root.getElementCount();
                if (lineNumber >= 1 && lineNumber <= lineCount) {
                    try {
                        int offset = textArea.getLineStartOffset(lineNumber - 1);
                        textArea.setCaretPosition(offset);
                        textArea.requestFocusInWindow();
                    } catch (BadLocationException e) {
                        e.printStackTrace();
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid line number.");
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid line number.");
            }
        }
    }

    private void updateLineCount() {
        Element root = textArea.getDocument().getDefaultRootElement();
        int lineCount = root.getElementCount();
        lineCountLabel.setText(getLineCountText(lineCount));
    }

    private String getLineCountText(int lineCount) {
        return "Lines: " + lineCount;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                HTMLEditor editor = new HTMLEditor();
                editor.setVisible(true);
            }
        });
    }
}

