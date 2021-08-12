package viewer;

import javax.swing.*;

public class SQLiteViewer extends JFrame {

    public SQLiteViewer() {
        super("SQLite Viewer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 900);
        setLayout(null);
        setResizable(false);
        setLocationRelativeTo(null);



        JButton button = new JButton();
        button.setText("Open");
        button.setBounds(700,20,80,40);
        button.setName("OpenFileButton");

        JTextField textField = new JTextField();
        textField.setBounds(10,20, 500, 40 );
        textField.setName("FileNameTextField");
        add(button);
        add(textField);
    }


}
