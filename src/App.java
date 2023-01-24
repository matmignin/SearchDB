import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.*;
import java.util.Scanner;
import java.io.File;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class App {
    public static void main(String[] args) throws Exception {
        try {
            // Load the XML file
            File xmlFile = new File("PRD.xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);

            // optional, but recommended
            // read this -
            // http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
            doc.getDocumentElement().normalize();

            // Get the connection details from the XML file
            NodeList nList = doc.getElementsByTagName("connection");
            Node nNode = nList.item(0);
            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) nNode;
                String driver = eElement.getElementsByTagName("driver").item(0).getTextContent();
                String url = eElement.getElementsByTagName("url").item(0).getTextContent();
                String user = eElement.getElementsByTagName("user").item(0).getTextContent();
                String password = eElement.getElementsByTagName("password").item(0).getTextContent();

                // Load the Oracle JDBC driver
                Class.forName(driver);

                // Connect to the Oracle database
                Connection connection = DriverManager.getConnection(url, user, password);

                // Create a prepared statement
                String sql = "SELECT * FROM TESTREQUEST WHERE BATCHNUMBER LIKE ?";
                PreparedStatement statement = connection.prepareStatement(sql);

                // Set the user input variable as the parameter of the prepared statement
                // statement.setString(1, batchNumber);

                if (args.length > 0) {
                    String batchNumber = args[0];
                    // Set the user input variable as the parameter of the prepared statement
                    statement.setString(1, batchNumber);
                } else {
                    // System.out.println("You have to pass the batchNumber as an argument when you run the file
                    Scanner sc = new Scanner(System.in);
                    System.out.print("Enter the BATCHNUMBER:");
                    String batchNumber = sc.nextLine();
                    statement.setString(1, batchNumber);
                }
                // Execute the query
                ResultSet resultSet = statement.executeQuery();
                // String sampleType = resultSet.getString("SPECIFICATIONID");

                // get the input variable
                // Process the result set
                while (resultSet.next()) {
                    System.out.print(resultSet.getString(1));
                    try {
                    System.out.println("\t " + resultSet.getString("SPECIFICATIONID").substring(resultSet.getString("SPECIFICATIONID").lastIndexOf(", ") + 1));
                    }
                    catch (Exception e) {
                        System.out.println("\t  CoA");
                        continue;
                    }
                }

                // print all columns
                /*
                 * /// get the ResultSetMetaData
                 * ResultSetMetaData metaData = resultSet.getMetaData();
                 *
                 * /// Get the number of columns
                 * int columnCount = metaData.getColumnCount();
                 * while (resultSet.next()) {
                 * for (int i = 1; i <= columnCount; i++) {
                 * System.out.print(metaData.getColumnName(i) + ": " + resultSet.getString(i) +
                 * " ");
                 * }
                 * System.out.println();
                 * }
                 */

                // Close the connection
                connection.close();
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}