package discordBot;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import tablebuilder.Table;

import java.util.List;

public class Util {
    public static boolean isTeamMember(List<Role> roles){
        for(Role r: roles){
            if(r.getName().equals("Team")){
                return true;
            }
        }
        return false;
    }

    public static void editViewMessage(ButtonInteractionEvent event, String desc, Table t){
        String title = event.getMessage().getEmbeds().get(0).getTitle();
        event.editMessageEmbeds(new EmbedBuilder()
                .setTitle(title)
                .setDescription(desc)
                .setFooter("Viewing " + (t.start + 1) + " to " + t.end + " of " + t.size).build()).queue();
    }
}
