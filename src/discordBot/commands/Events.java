package discordBot.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static discordBot.Main.connectionPool;
import static net.dv8tion.jda.api.interactions.commands.OptionType.STRING;

public class Events extends Command{

    public Events(){
        super.name = "events";
        super.desc = "All events we've played";
    }

    @Override
    public void run(SlashCommandInteractionEvent event) throws SQLException {
        Connection connect = connectionPool.getConnection();
        Statement statement = connect.createStatement();
        ResultSet resultSet = statement
                .executeQuery("SELECT DISTINCT event FROM bridgefour.matches");
        StringBuilder result = new StringBuilder();
        while(resultSet.next()){
            result.append(resultSet.getString("event")).append("\n");
        }
        MessageEmbed embed = new EmbedBuilder()
                .setTitle("Events")
                .setDescription(result.toString()).build();
        event.replyEmbeds(embed).queue();
        connectionPool.releaseConnection(connect);
    }
}
