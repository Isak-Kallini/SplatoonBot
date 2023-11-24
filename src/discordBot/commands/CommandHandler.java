package discordBot.commands;

import discordBot.ErrorLogger;
import discordBot.scheduling.ScheduleEmbed;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ScanResult;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import tablebuilder.Table;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static discordBot.Util.editViewMessage;

public class CommandHandler extends ListenerAdapter {
    static Map<String, Command> commandMap = new HashMap<>();
    static ScheduleEmbed scheduleEmbed;
    public static void init(CommandListUpdateAction commands){


        var classGraph = new ClassGraph().acceptPackages("discordBot.commands");
        try(ScanResult result = classGraph.scan()){
            ClassInfoList list = result.getSubclasses(Command.class);
            List<Class<?>> classes = list.loadClasses();
            classes.forEach(c -> {
                try {
                    Command command = (Command) c.getDeclaredConstructor().newInstance();
                    commandMap.put(command.getName(), command);
                    commands.addCommands(
                            Commands.slash(command.getName(), command.getDesc())
                                    .addOptions(command.getOptions())
                    );
                } catch (Exception e) {
                    ErrorLogger.log(e);
                }
            });
        }
    }


    public static void scheduleThing(JDA jda){
        scheduleEmbed = new ScheduleEmbed();
        scheduleEmbed.schedule(jda);
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event){
        if (event.getGuild() == null)
            return;
        try {
            commandMap.get(event.getName()).run(event);
        }catch (Exception e) {
            ErrorLogger.log(e);
        }
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event){
        String[] s = event.getComponentId().split(" ");
        if(s[0].equals("schedule")){
            scheduleEmbed.update(event);
        }else {
            Command c = commandMap.get(s[0]);
            String func = s[1];
            switch (func) {
                case "start" -> ((HasTable) c).start(event);
                case "next" -> ((HasTable) c).next(event);
                case "previous" -> ((HasTable) c).previous(event);
                case "end" -> ((HasTable) c).end(event);
            }
        }
    }
}
