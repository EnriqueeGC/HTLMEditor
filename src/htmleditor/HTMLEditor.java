/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package htmleditor;

import java.awt.*;
import java.awt.event.*;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Element;
import java.io.*;

public class HTMLEditor extends JFrame implements ActionListener {
    private JTextArea textArea;
    private JFileChooser fileChooser;
    private File currentFile;
    private JTextArea lineNumberArea;

    public HTMLEditor() {
        setTitle("Editor HTML");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Crear el área de texto
        textArea = new JTextArea();
        textArea.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateLineNumberArea();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateLineNumberArea();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateLineNumberArea();
            }
        });

        JScrollPane scrollPane = new JScrollPane(textArea);
        add(scrollPane, BorderLayout.CENTER);

        // Crear el área de números de línea
        lineNumberArea = new JTextArea("1");
        lineNumberArea.setBackground(Color.LIGHT_GRAY);
        lineNumberArea.setEditable(false);
        lineNumberArea.setFocusable(false);
        scrollPane.setRowHeaderView(lineNumberArea);

        // Crear la barra de menú
        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        // Menú Archivo
        JMenu fileMenu = new JMenu("Archivo");
        menuBar.add(fileMenu);

        JMenuItem newItem = new JMenuItem("Nuevo");
        newItem.addActionListener(this);
        fileMenu.add(newItem);

        JMenuItem openItem = new JMenuItem("Abrir");
        openItem.addActionListener(this);
        fileMenu.add(openItem);

        JMenuItem saveItem = new JMenuItem("Guardar");
        saveItem.addActionListener(this);
        fileMenu.add(saveItem);

        JMenuItem saveAsItem = new JMenuItem("Guardar Como");
        saveAsItem.addActionListener(this);
        fileMenu.add(saveAsItem);

        // Menú Editar
        JMenu editMenu = new JMenu("Editar");
        menuBar.add(editMenu);

        JMenuItem findItem = new JMenuItem("Buscar");
        findItem.addActionListener(this);
        editMenu.add(findItem);

        JMenuItem replaceItem = new JMenuItem("Reemplazar");
        replaceItem.addActionListener(this);
        editMenu.add(replaceItem);

        JMenuItem goToItem = new JMenuItem("Ir a");
        goToItem.addActionListener(this);
        editMenu.add(goToItem);

        // Menú Imprimir
        JMenu printMenu = new JMenu("Imprimir");
        menuBar.add(printMenu);

        JMenuItem printItem = new JMenuItem("Imprimir");
        printItem.addActionListener(this);
        printMenu.add(printItem);

        setVisible(true);

        // Configurar el selector de archivos
        fileChooser = new JFileChooser();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();

        if (command.equals("Nuevo")) {
            newFile();
        } else if (command.equals("Abrir")) {
            openFile();
        } else if (command.equals("Guardar")) {
            saveFile();
        } else if (command.equals("Guardar Como")) {
            saveFileAs();
        } else if (command.equals("Buscar")) {
            find();
        } else if (command.equals("Reemplazar")) {
            replace();
        } else if (command.equals("Ir a")) {
            goTo();
        } else if (command.equals("Imprimir")) {
            print();
        }
    }

    private void newFile() {
        currentFile = null;
        textArea.setText("");
    }

    private void openFile() {
        int returnValue = fileChooser.showOpenDialog(this);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            currentFile = file;
            try {
                BufferedReader reader = new BufferedReader(new FileReader(file));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                    sb.append("\n");
                }
                reader.close();
                textArea.setText(sb.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveFile() {
        if (currentFile != null) {
            try {
                BufferedWriter writer = new BufferedWriter(new FileWriter(currentFile));
                writer.write(textArea.getText());
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            saveFileAs();
        }
    }

    private void saveFileAs() {
        int returnValue = fileChooser.showSaveDialog(this);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            currentFile = file;
            try {
                BufferedWriter writer = new BufferedWriter(new FileWriter(file));
                writer.write(textArea.getText());
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void find() {
        // Implementar la funcionalidad de búsqueda
    }

    private void replace() {
        // Implementar la funcionalidad de reemplazo
    }

    private void goTo() {
        // Implementar la funcionalidad de ir a una línea específica
    }

    private void print() {
        PrinterJob job = PrinterJob.getPrinterJob();
        job.setPrintable(textArea.getPrintable(null, null));
        if (job.printDialog()) {
            try {
                job.print();
            } catch (PrinterException e) {
                e.printStackTrace();
            }
        }
    }

    private void updateLineNumberArea() {
        String[] lines = textArea.getText().split("\n");
        int lineCount = lines.length;
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= lineCount; i++) {
            sb.append(i);
            sb.append("\n");
        }
        lineNumberArea.setText(sb.toString());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new HTMLEditor();
            }
        });
    }
}

