package discordBot;

import ConnectionPooling.ConnectionPool;
import discordBot.commands.CommandHandler;
import discordBot.scheduling.ScheduleEmbed;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import static net.dv8tion.jda.api.interactions.commands.OptionType.INTEGER;
import static net.dv8tion.jda.api.interactions.commands.OptionType.STRING;

public class Main {
    public static ConnectionPool connectionPool;

    public static void initialise(ConnectionPool pool) {
        connectionPool = pool;
        String token = null;

        try {
            token = Files.readString(Paths.get("token.txt")).trim();
        } catch (IOException e) {
            ErrorLogger.log(e);
        }

        JDA jda = JDABuilder.createLight(token, EnumSet.noneOf(GatewayIntent.class))
                .addEventListeners(new CommandHandler())
                .build();

        CommandListUpdateAction commands = jda.updateCommands();
        CommandHandler.init(commands);
        commands.queue();
        try {
            jda.awaitReady();
        } catch (InterruptedException e) {
            ErrorLogger.log(e);
        }

        CommandHandler.scheduleThing(jda);

    }
}


