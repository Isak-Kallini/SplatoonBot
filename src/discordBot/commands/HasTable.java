package discordBot.commands;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

public interface HasTable {
    void start(ButtonInteractionEvent event);
    void next(ButtonInteractionEvent event);

    void previous(ButtonInteractionEvent event);

    void end(ButtonInteractionEvent event);

}
