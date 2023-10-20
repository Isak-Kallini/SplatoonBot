package discordBot.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.sql.SQLException;

public class Ping extends Command{
    public Ping(){
        super.name = "ping";
        super.desc = "testing";
    }

    @Override
    public void run(SlashCommandInteractionEvent event) throws SQLException {
        event.reply("pong").queue();
    }
}
