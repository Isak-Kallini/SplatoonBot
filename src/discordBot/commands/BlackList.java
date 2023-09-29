package discordBot.commands;

import discordBot.Main;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.sql.*;

import static discordBot.Main.connectionPool;
import static discordBot.Util.isTeamMember;
import static net.dv8tion.jda.api.interactions.commands.OptionType.STRING;

public class BlackList extends Command{

    public BlackList(){
        super.name = "blacklist";
        super.desc = "Add team to blacklist/show blacklist";
        super.options.add(new OptionData(STRING, "team", "Team you want to blacklist"));
    }

    @Override
    public void run(SlashCommandInteractionEvent event) throws SQLException {

        String team = event.getOption("team", null, OptionMapping::getAsString);
        Connection connect = connectionPool.getConnection();
        Statement statement = connect.createStatement();
        if(team == null){
            ResultSet resultSet = statement.executeQuery("SELECT * from bridgefour.blacklist");
            StringBuilder result = new StringBuilder();
            while(resultSet.next()){
                result.append(resultSet.getString("team")).append("\n");
            }
            MessageEmbed embed = new EmbedBuilder()
                    .setTitle("Blacklist")
                    .setDescription(result.toString()).build();
            event.replyEmbeds(embed).queue();
        }else{
            if(isTeamMember(event.getMember().getRoles())) {
                PreparedStatement stmnt = connect.prepareStatement("INSERT INTO bridgefour.blacklist VALUES (default, ?);");
                stmnt.setString(1, team);
                stmnt.executeUpdate();
                event.reply(team + " added to blacklist").queue();
            }else{
                event.reply("Only team members can add teams to the blacklist").queue();
            }
        }
        connectionPool.releaseConnection(connect);
    }
}
