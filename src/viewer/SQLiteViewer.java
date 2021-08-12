package viewer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;


public class SQLiteViewer extends JFrame {

    public SQLiteViewer() {
        super("SQLite Viewer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 900);
        setLayout(null);
        setResizable(false);
        setLocationRelativeTo(null);
        initializeUI();

    }

    public void initializeUI() {
        JTextField textField = new JTextField();
        textField.setBounds(20, 20 , 550, 40);
        textField.setName("FileNameTextField");

        JComboBox<String> comboBox = new JComboBox<>();
        comboBox.setBounds(20, 70, 650, 40);
        comboBox.setName("TablesComboBox");

        JTextArea query = new JTextArea();
        query.setBounds(20, 120, 550, 80);
        query.setName("QueryTextArea");



        JButton button = new JButton();
        button.setText("Open");
        button.setBounds(590, 20 , 80, 40);
        button.setName("OpenFileButton");
        ActionListener action = actionEvent -> {
            comboBox.removeAllItems();
            String dbName = textField.getText();
            DB.connection(dbName);
            ArrayList<String> tableNames = DB.getTableNames();
            for (String s : tableNames) {
                comboBox.addItem(s);
            }
        };
        button.addActionListener(action);
        comboBox.addActionListener(e -> {
            String table = (String) comboBox.getSelectedItem();
            query.setText(String.format("SELECT * FROM %s;", table));
        });


        JButton execute = new JButton();
        execute.setText("Execute");
        execute.setName("ExecuteQueryButton");
        execute.setBounds(590, 120, 80, 40);


        add(button);
        add(textField);
        add(comboBox);
        add(execute);
        add(query);
    }


}
