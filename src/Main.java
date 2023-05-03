import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.util.Calendar;
import java.util.EnumSet;
import java.util.GregorianCalendar;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import net.dv8tion.jda.api.requests.GatewayIntent;
import tablebuilder.Table;

import static net.dv8tion.jda.api.interactions.commands.OptionType.*;

public class Main extends ListenerAdapter {
    private static JDA jda;
    private static Connection connect = null;
    private static Statement statement = null;
    private static ResultSet resultSet = null;
    public static void main(String[] args) {
        String token = null;
        String password = null;
        try {
            token = Files.readString(Paths.get("spltoken.txt")).trim();
            password = Files.readString(Paths.get("pass.txt")).trim();
        } catch (IOException e) {
            ErrorLogger.log(e);
        }

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            ErrorLogger.log(e);
        }
        try {
            connect = DriverManager
                    .getConnection("jdbc:mysql://localhost/bridgefour?"
                            + "user=root&password=" + password);
        } catch (SQLException e) {
            ErrorLogger.log(e);
        }
        try {
            statement = connect.createStatement();
        } catch (SQLException e) {
            ErrorLogger.log(e);
        }

        jda = JDABuilder.createLight(token, EnumSet.noneOf(GatewayIntent.class))
                .addEventListeners(new Main())
                .build();
        CommandListUpdateAction commands = jda.updateCommands();

        commands.addCommands(
                Commands.slash("view", "View matches")
                        .addOptions(new OptionData(STRING, "team", "filter by team"))
        );

        commands.addCommands(
                Commands.slash("register", "Toggle if the bot should send daily quotes")
                        .addOptions(new OptionData(STRING, "team", "Enemy team name")
                                .setRequired(true))
                        .addOptions(new OptionData(INTEGER, "we", "Our score")
                                .setRequired(true))
                        .addOptions(new OptionData(INTEGER, "them", "Opponents score")
                                .setRequired(true))
                        .addOptions(new OptionData(STRING, "date", "yyyy-mm-dd"))
                        .addOptions(new OptionData(STRING, "event", "Event, leave empty for scrim"))
        );

        commands.addCommands(
                Commands.slash("stats", "Some stats")
        );

        commands.addCommands(
                Commands.slash("event", "The event you want to search")
                        .addOptions(new OptionData(STRING, "event", "Event you want to search")
                                .setRequired(true))
        );

        commands.addCommands(
                Commands.slash("teams", "All teams we've played")
        );

        commands.addCommands(
                Commands.slash("events", "All events we've played")
        );

        commands.addCommands(
                Commands.slash("blacklist", "Add team to blacklist/show blacklist")
                        .addOptions(new OptionData(STRING, "team", "Team you want to blacklist"))
        );

        commands.queue();
        try {
            jda.awaitReady();
        } catch (InterruptedException e) {
            ErrorLogger.log(e);
        }
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event){
        if (event.getGuild() == null)
            return;
        try {
        switch (event.getName()) {
                case "view":
                    view(event);
                    return;
                case "teams":
                    teams(event);
                    return;
                case "event":
                    event(event);
                    return;
                case "stats":
                    stats(event);
                    return;
                case "events":
                    events(event);
                    return;
                case "register":
                    register(event);
                    return;
                case "blacklist":
                    blacklist(event);
            }
        }catch (SQLException e) {
            ErrorLogger.log(e);
        }
    }

    public static void blacklist(SlashCommandInteractionEvent event) throws SQLException {
        String team = event.getOption("team", null, OptionMapping::getAsString);

        if(team == null){
            resultSet = statement.executeQuery("SELECT * from bridgefour.blacklist");
            String result = "";
            while(resultSet.next()){
                result += resultSet.getString("team") + "\n";
            }
            MessageEmbed embed = new EmbedBuilder()
                    .setTitle("Blacklist")
                    .setDescription(result).build();
            event.replyEmbeds(embed).queue();
        }else{
            PreparedStatement stmnt = connect.prepareStatement("INSERT INTO bridgefour.blacklist VALUES (default, ?);");
            stmnt.setString(1, team);
            stmnt.executeUpdate();
            event.reply(team + " added to blacklist").queue();
        }
    }

    public static void register(SlashCommandInteractionEvent event) throws SQLException {
        String team = event.getOption("team", null, OptionMapping::getAsString);
        int we = event.getOption("we", 0, OptionMapping::getAsInt);
        int them = event.getOption("them", 0, OptionMapping::getAsInt);
        Calendar c = new GregorianCalendar();
        String date = event.getOption("date", c.get(Calendar.YEAR) + "-" + (c.get(Calendar.MONTH) + 1) + "-" + c.get(Calendar.DAY_OF_MONTH), OptionMapping::getAsString);
        String e = event.getOption("event", "Scrim", OptionMapping::getAsString);

        PreparedStatement stmnt = connect.prepareStatement("INSERT INTO matches VALUES (default, ?, ?, ?, ?, ?);");
        System.out.println(date);
        stmnt.setDate(1, java.sql.Date.valueOf(date));
        stmnt.setString(2, team);
        stmnt.setInt(3, we);
        stmnt.setInt(4, them);
        stmnt.setString(5, e);
        stmnt.executeUpdate();
        event.reply("Registered").queue();
    }

    public static void events(SlashCommandInteractionEvent event) throws SQLException {
        resultSet = statement
                .executeQuery("SELECT DISTINCT event FROM bridgefour.matches");
        String result = "";
        while(resultSet.next()){
            result += resultSet.getString("event") + "\n";
        }
        MessageEmbed embed = new EmbedBuilder()
                .setTitle("Events")
                .setDescription(result).build();
        event.replyEmbeds(embed).queue();
    }

    public static void stats(SlashCommandInteractionEvent event) throws SQLException {
        resultSet = statement
                .executeQuery("SELECT COUNT(DISTINCT team) FROM bridgefour.matches");
        resultSet.next();
        int nteams = resultSet.getInt("COUNT(DISTINCT team)");

        resultSet = statement
                .executeQuery("SELECT COUNT(team) FROM bridgefour.matches");
        resultSet.next();
        int nmatches = resultSet.getInt("COUNT(team)");

        resultSet = statement
                .executeQuery("SELECT * FROM bridgefour.matches");

        int wins = 0;
        int losses = 0;
        while(resultSet.next()){
            if(resultSet.getInt("we") > resultSet.getInt("them")){
                wins++;
            }else if(resultSet.getInt("we") < resultSet.getInt("them")){
                losses++;
            }
        }

        MessageEmbed embed = new EmbedBuilder()
                .setTitle("Stats ")
                .setDescription("Matches played: " + nmatches + "\n" +
                        "Teams played: " + nteams + "\n" +
                        "Wins: " + wins + "\n" +
                        "Losses: " + losses + "\n" +
                        "Win/lose ratio: " + ((float) wins/losses)).build();
        event.replyEmbeds(embed).queue();
    }

    public static void teams(SlashCommandInteractionEvent event) throws SQLException {
        resultSet = statement
                .executeQuery("SELECT DISTINCT team FROM bridgefour.matches");
        String result = "";
        while(resultSet.next()){
            result += resultSet.getString("team") + "\n";
        }
        MessageEmbed embed = new EmbedBuilder()
                .setTitle("Teams")
                .setDescription(result).build();
        event.replyEmbeds(embed).queue();
    }

    private static Table eventTable;
    public static void event(SlashCommandInteractionEvent event) throws SQLException {
        String e = event.getOption("event", null, OptionMapping::getAsString);
        if(e != null) {
            PreparedStatement stmnt = connect.prepareStatement("SELECT * FROM bridgefour.matches WHERE event LIKE ?");
            stmnt.setString(1, "%" + e + "%");
            resultSet = stmnt
                    .executeQuery();
        }

        if(resultSet.next()) {
            eventTable = new Table(resultSet);

            MessageEmbed embed = new EmbedBuilder()
                    .setTitle("Viewing " + (e == null ? "all" : e))
                    .setDescription("```" + eventTable.table() + "```")
                    .setFooter("Viewing 1 to " + eventTable.end).build();

            event.replyEmbeds(embed).addActionRow(Button.primary("previouse", "previous"),
                    Button.primary("nexte", "next"),
                    Button.primary("ende", "end")).queue();
        }else{
            event.reply("Couldn't find '" + e + "'").queue();
        }
    }

    private static Table viewtable;
    public static void view(SlashCommandInteractionEvent event) throws SQLException {
        String team = event.getOption("team", null, OptionMapping::getAsString);
        if(team == null) {
            resultSet = statement
                    .executeQuery("select * from bridgefour.matches");
        }else{
            PreparedStatement stmnt = connect.prepareStatement("SELECT * FROM bridgefour.matches WHERE team LIKE ?");
            stmnt.setString(1, "%" + team + "%");
            resultSet = stmnt.executeQuery();
        }
        //'; DROP TABLE bridgefour.test; --

        if(resultSet.next()) {
            viewtable = new Table(resultSet);

            MessageEmbed embed = new EmbedBuilder()
                    .setTitle("Viewing " + (team == null? "all":team))
                    .setDescription("```" + viewtable.table() + "```")
                    .setFooter("Viewing 1 to " + viewtable.end  + " of " + viewtable.size).build();

            event.replyEmbeds(embed).addActionRow(Button.primary("previous", "previous"),
                    Button.primary("next", "next"),
                    Button.primary("end", "end")).queue();
        }else{
            event.reply("Couldn't find '" + team + "'").queue();
        }
    }

    public void editViewMessage(ButtonInteractionEvent event, String desc, Table t){
        String title = event.getMessage().getEmbeds().get(0).getTitle();
        event.editMessageEmbeds(new EmbedBuilder()
                .setTitle(title)
                .setDescription(desc)
                .setFooter("Viewing " + (t.start + 1) + " to " + t.end + " of " + t.size).build()).queue();
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event){
        Table table = event.getComponentId().endsWith("e") ? eventTable : viewtable;
        if(event.getComponentId().startsWith("next")){
            editViewMessage(event,
                    "```" + table.next() + "```", table);
        }else if(event.getComponentId().startsWith("previous")){
            editViewMessage(event,
                    "```" + table.previous() + "```", table);
        }else if(event.getComponentId().startsWith("end")){
            editViewMessage(event,
                    "```" + table.end() + "```", table);
        }
    }
}
