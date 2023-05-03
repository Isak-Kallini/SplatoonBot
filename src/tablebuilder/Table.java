package tablebuilder;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class Table {

    ArrayList<String> date = new ArrayList<>();
    ArrayList<String> team = new ArrayList<>();
    ArrayList<Integer> we = new ArrayList<>();
    ArrayList<Integer> them = new ArrayList<>();
    ArrayList<String> event = new ArrayList<>();
    public int size;
    public int start = 0;

    public int end = 10;

    public Table(ResultSet s) throws SQLException {
        do{
            date.add(s.getString("datum"));
            team.add(s.getString("team"));
            we.add(s.getInt("we"));
            them.add(s.getInt("them"));
            event.add(s.getString("event"));
        }while(s.next());
        size = team.size();
        end = Math.min(10, size);
    }

    public String table() {

        int maxLengthTeam = team.get(start).length();
        for(int i = start; i < Math.min(start + 10, size); i++){
            int length = team.get(i).length();
            if(length > maxLengthTeam){
                maxLengthTeam = length;
            }
        }

        int maxLengthEvent = event.get(0).length();
        for(int i = start; i < Math.min(start + 10, size); i++){
            int length = event.get(i).length();
            if(length > maxLengthEvent){
                maxLengthEvent = length;
            }
        }
        String result = "|Date      |w-l|Team" + spaces(maxLengthTeam - 4) + "|Event" + spaces(maxLengthEvent - 5) + "|\n" +
                        "---------------------" + dashes(maxLengthTeam - 4) + "------" + dashes(maxLengthEvent - 5) + "\n";

        for(int i = start; i < Math.min(start + 10, event.size()); i++){
            result += "|" + date.get(i) + "|" + we.get(i) + "-" + them.get(i) + "|" + team.get(i) + spaces(maxLengthTeam - team.get(i).length()) +
                    "|" + event.get(i) + spaces(maxLengthEvent - event.get(i).length()) + "|\n";
        }
        return result;
    }

    public String next(){
        if(start + 10 < size)
            start += 10;
        start = Math.min(start, size);
        end += 10;
        end = Math.min(end, size);

        return table();
    }

    public String previous(){
        start -= 10;
        start = Math.max(start, 0);
        end = Math.min(start + 10, end);

        return table();
    }

    public String end(){
        start = size - 10;
        end = size;
        return table();
    }

    private static String spaces(int n){
        String res = "";
        for(int i = 0; i < n; i++)
            res += " ";
        return res;
    }

    private static String dashes(int n){
        String res = "";
        for(int i = 0; i < n; i++)
            res += "-";
        return res;
    }
}
