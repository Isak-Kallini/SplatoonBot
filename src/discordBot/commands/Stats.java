package discordBot.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static discordBot.Main.connectionPool;

public class Stats extends Command{

    public Stats(){
        super.name = "stats";
        super.desc = "Some stats";
    }

    @Override
    public void run(SlashCommandInteractionEvent event) throws SQLException {
        Connection connect = connectionPool.getConnection();
        Statement statement = connect.createStatement();
        ResultSet resultSet = statement
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
        connectionPool.releaseConnection(connect);
    }
}
