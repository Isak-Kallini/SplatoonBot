package discordBot.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import tablebuilder.Table;

import java.sql.*;

import static discordBot.Main.connectionPool;
import static net.dv8tion.jda.api.interactions.commands.OptionType.STRING;

public class View extends Command implements HasTable {
    public Table table;
    public View(){
        super.name = "view";
        super.desc = "View matches";
        super.options.add(new OptionData(STRING, "team", "filter by team"));
    }
    @Override
    public void run(SlashCommandInteractionEvent event) throws SQLException {
        Connection connect = connectionPool.getConnection();
        Statement statement = connect.createStatement();
        String team = event.getOption("team", null, OptionMapping::getAsString);
        ResultSet resultSet;
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
            table = new Table(resultSet);

            MessageEmbed embed = new EmbedBuilder()
                    .setTitle("Viewing " + (team == null? "all":team))
                    .setDescription("```" + table.table() + "```")
                    .setFooter("Viewing 1 to " + table.end  + " of " + table.size).build();

            event.replyEmbeds(embed).addActionRow(
                    Button.primary(getName() + " previous", "previous"),
                    Button.primary(getName() + " next", "next"),
                    Button.primary(getName() + " end", "end"),
                    Button.primary(getName() + " start", "start")).queue();
        }else{
            event.reply("Couldn't find '" + team + "'").queue();
        }
        connectionPool.releaseConnection(connect);
    }

    @Override
    public void start(ButtonInteractionEvent event) {
        editViewMessage(event, table.start());
    }

    @Override
    public void next(ButtonInteractionEvent event) {
        editViewMessage(event, table.next());
    }

    @Override
    public void previous(ButtonInteractionEvent event) {
        editViewMessage(event, table.previous());
    }

    @Override
    public void end(ButtonInteractionEvent event) {
        editViewMessage(event, table.end());
    }

    private void editViewMessage(ButtonInteractionEvent event, String desc){
        String title = event.getMessage().getEmbeds().get(0).getTitle();
        event.editMessageEmbeds(new EmbedBuilder()
                .setTitle(title)
                .setDescription(desc)
                .setFooter("Viewing " + (table.start + 1) + " to " + table.end + " of " + table.size).build()).queue();
    }
}
