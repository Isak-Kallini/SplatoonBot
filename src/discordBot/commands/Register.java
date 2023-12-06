package discordBot.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import static discordBot.Main.connectionPool;
import static discordBot.Util.isTeamMember;
import static net.dv8tion.jda.api.interactions.commands.OptionType.INTEGER;
import static net.dv8tion.jda.api.interactions.commands.OptionType.STRING;

public class Register extends Command{
    public Register(){
        super.name = "register";
        super.desc = "Register a new match (only team members)";
        super.options.addAll(List.of(new OptionData[]{
                new OptionData(STRING, "team", "Enemy team name")
                    .setRequired(true),
                new OptionData(INTEGER, "we", "Our score")
                        .setRequired(true),
                new OptionData(INTEGER, "them", "Opponents score")
                        .setRequired(true),
                new OptionData(STRING, "date", "yyyy-mm-dd"),
                new OptionData(STRING, "event", "Event, leave empty for scrim")}));
    }
    @Override
    public void run(SlashCommandInteractionEvent event) throws SQLException {
        if(isTeamMember(event.getMember().getRoles())) {
            String team = event.getOption("team", null, OptionMapping::getAsString);
            int we = event.getOption("we", 0, OptionMapping::getAsInt);
            int them = event.getOption("them", 0, OptionMapping::getAsInt);
            Calendar c = new GregorianCalendar();
            String date = event.getOption("date", c.get(Calendar.YEAR) + "-" + (c.get(Calendar.MONTH) + 1) + "-" + c.get(Calendar.DAY_OF_MONTH), OptionMapping::getAsString);
            String e = event.getOption("event", "Scrim", OptionMapping::getAsString);


            Connection connect = connectionPool.getConnection();
            PreparedStatement stmnt = connect.prepareStatement("INSERT INTO matches VALUES (default, ?, ?, ?, ?, ?);");
            System.out.println(date);
            stmnt.setDate(1, java.sql.Date.valueOf(date));
            stmnt.setString(2, team);
            stmnt.setInt(3, we);
            stmnt.setInt(4, them);
            stmnt.setString(5, e);
            stmnt.executeUpdate();
            event.reply("Registered " + we + "-" + them + " " + team + " " + e + " " + date).queue();
            connectionPool.releaseConnection(connect);
        }else{
            event.reply("Matches can only be registered by team members").queue();
        }
    }
}
