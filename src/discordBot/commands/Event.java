package discordBot.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import tablebuilder.Table;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static discordBot.Main.connectionPool;
import static net.dv8tion.jda.api.interactions.commands.OptionType.STRING;

public class Event extends Command implements HasTable{
    private Table table;
    public Event(){
        super.name = "event";
        super.desc = "The event you want to search";
        super.options.add(new OptionData(STRING, "event", "Event you want to search")
                .setRequired(true));
    }

    @Override
    public void run(SlashCommandInteractionEvent event) throws SQLException {
        Connection connect = connectionPool.getConnection();
        String e = event.getOption("event", null, OptionMapping::getAsString);
        ResultSet resultSet = null;
        if(e != null) {
            PreparedStatement stmnt = connect.prepareStatement("SELECT * FROM bridgefour.matches WHERE event LIKE ?");
            stmnt.setString(1, "%" + e + "%");
            resultSet = stmnt
                    .executeQuery();
        }

        if(resultSet.next()) {
            table = new Table(resultSet);

            MessageEmbed embed = new EmbedBuilder()
                    .setTitle("Viewing " + (e == null ? "all" : e))
                    .setDescription("```" + table.table() + "```")
                    .setFooter("Viewing 1 to " + table.end).build();

            event.replyEmbeds(embed).addActionRow(
                    Button.primary(getName() + " previous", "previous"),
                    Button.primary(getName() + " next", "next"),
                    Button.primary(getName() + " end", "end"),
                    Button.primary(getName() + " start", "start")).queue();
        }else{
            event.reply("Couldn't find '" + e + "'").queue();
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
