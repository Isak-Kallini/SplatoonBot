package discordBot.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static discordBot.Main.connectionPool;

public class Teams extends Command{

    public Teams(){
        super.name = "teams";
        super.desc = "All teams we've played";
    }

    @Override
    public void run(SlashCommandInteractionEvent event) throws SQLException {
        Connection connect = connectionPool.getConnection();
        Statement statement = connect.createStatement();
        ResultSet resultSet = statement
                .executeQuery("SELECT DISTINCT team FROM bridgefour.matches");
        StringBuilder result = new StringBuilder();
        while(resultSet.next()){
            result.append(resultSet.getString("team")).append("\n");
        }
        MessageEmbed embed = new EmbedBuilder()
                .setTitle("Teams")
                .setDescription(result.toString()).build();
        event.replyEmbeds(embed).queue();
        connectionPool.releaseConnection(connect);
    }
}
