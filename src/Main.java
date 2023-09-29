import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.util.Calendar;
import java.util.EnumSet;
import java.util.GregorianCalendar;
import java.util.List;

import ConnectionPooling.BasicConnectionPool;
import ConnectionPooling.ConnectionPool;
import discordBot.ErrorLogger;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
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
    private static ResultSet resultSet = null;

    public static void main(String[] args) {
        ConnectionPool connectionPool = null;
        String password = null;
        try {
            password = Files.readString(Paths.get("pass.txt")).trim();
        } catch (IOException e) {
            ErrorLogger.log(e);
        }


        try {
            connectionPool = BasicConnectionPool.create("jdbc:mysql://localhost/bridgefour", "splatoonbot@localhost", password);
        } catch (SQLException e) {
            ErrorLogger.log(e);
        }

        discordBot.Main.initialise(connectionPool);

    }
}


