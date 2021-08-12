import org.assertj.swing.fixture.JButtonFixture;
import org.assertj.swing.fixture.JComboBoxFixture;
import org.assertj.swing.fixture.JTextComponentFixture;
import org.hyperskill.hstest.dynamic.DynamicTest;
import org.hyperskill.hstest.exception.outcomes.WrongAnswer;
import org.hyperskill.hstest.stage.SwingTest;
import org.hyperskill.hstest.testcase.CheckResult;
import org.hyperskill.hstest.testing.swing.SwingComponent;
import org.junit.AfterClass;
import viewer.SQLiteViewer;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Locale;

import static org.hyperskill.hstest.testcase.CheckResult.correct;
import static org.hyperskill.hstest.testcase.CheckResult.wrong;

public class ViewerTest extends SwingTest {

    public ViewerTest() {
        super(new SQLiteViewer());
    }

    @SwingComponent(name = "FileNameTextField")
    private JTextComponentFixture fileNameTextField;

    @SwingComponent(name = "OpenFileButton")
    private JButtonFixture openFileButton;

    @SwingComponent(name = "TablesComboBox")
    private JComboBoxFixture tablesComboBox;

    @SwingComponent(name = "QueryTextArea")
    private JTextComponentFixture queryTextArea;

    @SwingComponent(name = "ExecuteQueryButton")
    private JButtonFixture executeQueryButton;

    private static final String firstDatabaseFileName = "firstDatabase.db";
    private static final String secondDatabaseFileName = "secondDatabase.db";

    @DynamicTest(order = 1)
    CheckResult test1() {

        requireEditable(fileNameTextField);
        requireEnabled(fileNameTextField);
        requireVisible(fileNameTextField);
        requireEmpty(fileNameTextField);

        requireEnabled(openFileButton);
        requireVisible(openFileButton);

        requireVisible(tablesComboBox);
        requireEnabled(tablesComboBox);

        requireVisible(queryTextArea);
        requireEnabled(queryTextArea);
        requireEditable(queryTextArea);
        requireEmpty(queryTextArea);

        return correct();
    }

    @DynamicTest(order = 2)
    CheckResult testDatabaseTablesDisplay() {

        try {
            initDatabase();
        } catch (SQLException exception) {
            return wrong("Can't create database files!\n" + exception.getSQLState());
        }

        fileNameTextField.setText(firstDatabaseFileName);
        openFileButton.click();

        if (tablesComboBox.contents().length != 2) {
            return wrong("Wrong number of items of 'TablesComboBox' combo box.\n" +
                "Expected 2 tables\n" +
                "Found " + tablesComboBox.contents().length);
        }

        checkIfComboBoxContainsItems("contacts", "groups");

        return correct();
    }

    @DynamicTest(order = 3)
    CheckResult testQueryTextArea() {
        String selectedTable = tablesComboBox.selectedItem();

        if (selectedTable == null) {
            return wrong("After opening database any table from it should be selected in the combo box!");
        }

        if (!queryTextArea.text().toLowerCase(Locale.ROOT).equals("select * from " + selectedTable + ";")) {
            return wrong("Wrong query in the 'QueryTextArea'. There should be query to select all rows from the selected table!\n" +
                "Expected query: " + "SELECT * FROM " + selectedTable + ";\n" +
                "    Your query: " + queryTextArea.text());
        }

        if (selectedTable.equals("contacts")) {
            tablesComboBox.selectItem("groups");
        } else {
            tablesComboBox.selectItem("contacts");
        }

        selectedTable = tablesComboBox.selectedItem();

        if (!queryTextArea.text().toLowerCase(Locale.ROOT).equals("select * from " + selectedTable + ";")) {
            return wrong("Wrong query in the 'QueryTextArea' after selecting a table. There should be query to select all rows from the selected table!\n" +
                "Expected query: " + "SELECT * FROM " + selectedTable + ";\n" +
                "    Your query: " + queryTextArea.text());
        }

        return correct();
    }

    @DynamicTest(order = 4)
    CheckResult changeDatabaseFileAndTestQueryTextArea() {

        fileNameTextField.deleteText().setText(secondDatabaseFileName);
        openFileButton.click();

        if (tablesComboBox.contents().length != 1) {
            return wrong("Wrong number of items of 'TablesComboBox' combo box.\n" +
                "Expected 1 tables\n" +
                "Found " + tablesComboBox.contents().length);
        }

        String selectedTable = tablesComboBox.selectedItem();

        if (selectedTable == null) {
            return wrong("After opening database any table from it should be selected in the combo box!");
        }

        if (!queryTextArea.text().toLowerCase(Locale.ROOT).equals("select * from " + selectedTable + ";")) {
            return wrong("Wrong query in the 'QueryTextArea'. There should be query to select all rows from the selected table!\n" +
                "Expected query: " + "SELECT * FROM " + selectedTable + ";\n" +
                "    Your query: " + queryTextArea.text());
        }

        return correct();
    }

    private static void initDatabase() throws SQLException {

        deleteDatabaseFiles();

        Connection connection = DriverManager.getConnection("jdbc:sqlite:" + firstDatabaseFileName);
        Statement statement = connection.createStatement();
        statement.execute("CREATE TABLE IF NOT EXISTS contacts (\n" +
            "\tcontact_id INTEGER PRIMARY KEY,\n" +
            "\tfirst_name TEXT NOT NULL,\n" +
            "\tlast_name TEXT NOT NULL,\n" +
            "\temail TEXT NOT NULL UNIQUE,\n" +
            "\tphone TEXT NOT NULL UNIQUE\n" +
            ");");
        statement.execute("CREATE TABLE IF NOT EXISTS groups (\n" +
            "   group_id INTEGER PRIMARY KEY,\n" +
            "   name TEXT NOT NULL\n" +
            ");");
        connection.close();

        connection = DriverManager.getConnection("jdbc:sqlite:" + secondDatabaseFileName);
        statement = connection.createStatement();
        statement.execute("CREATE TABLE IF NOT EXISTS projects (\n" +
            "\tid integer PRIMARY KEY,\n" +
            "\tname text NOT NULL,\n" +
            "\tbegin_date text,\n" +
            "\tend_date text\n" +
            ");");
        connection.close();
    }

    @AfterClass
    public static void deleteDatabaseFiles() {
        File firstFile = new File(firstDatabaseFileName);
        if (firstFile.exists()) {
            boolean ignored = firstFile.delete();
        }

        File secondFile = new File(secondDatabaseFileName);
        if (secondFile.exists()) {
            boolean ignored = secondFile.delete();
        }
    }

    private void checkIfComboBoxContainsItems(String... items) {
        String[] comboBoxItems = tablesComboBox.contents();

        for (String item : items) {
            boolean isItemFound = false;
            for (String comboBoxItem : comboBoxItems) {
                if (item.equals(comboBoxItem)) {
                    isItemFound = true;
                    break;
                }
            }
            if (!isItemFound) {
                throw new WrongAnswer("Can't find '" + item + "' table in the combo box!");
            }
        }
    }
}