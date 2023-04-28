import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.util.List;

public class ImportData {
    private static Connection connect = null;
    private static Statement statement = null;
    private static ResultSet resultSet = null;
    public static void imp() throws IOException, ClassNotFoundException, SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        connect = DriverManager
                .getConnection("jdbc:mysql://localhost/BridgeFour?"
                        + "user=root&password=11kosali1");
        statement = connect.createStatement();
        resultSet = statement
                .executeQuery("select * from BridgeFour.matches");

        while(resultSet.next()){
            String date = resultSet.getString("datum");
            String team = resultSet.getString("team");
            System.out.println(date + " " + team);
        }

        List<String> in = (Files.readAllLines(Paths.get("Bridge Four History - Data.csv")));

        for(String e: in){
            String[] list = e.split(",");
            String[] date = list[0].split("-");
            System.out.println(Integer.parseInt(date[0]) + "" + Integer.parseInt(date[1]) + date[2]);
            PreparedStatement stmnt = connect.prepareStatement("INSERT INTO matches VALUES (default, ?, ?, ?, ?, ?, ?);");
            stmnt.setDate(1, java.sql.Date.valueOf(list[0]));
            stmnt.setString(2, list[3]);
            stmnt.setInt(3, Integer.parseInt(list[1]));
            stmnt.setInt(4, Integer.parseInt(list[2]));
            stmnt.setString(5, list[4]);
            stmnt.setBoolean(6, list[5].equals("W"));
            stmnt.executeUpdate();
        }

    }
}