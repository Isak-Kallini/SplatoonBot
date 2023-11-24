package discordBot.scheduling;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.util.*;

public class ScheduleEmbed {
    Timer t = new Timer();
    TextChannel c;
    Calendar cal = new GregorianCalendar();

    public void schedule(JDA jda){
        //private: 396332195360276484
        //public: 1166399862615588944
        c = jda.getTextChannelById("1166399862615588944");

        Calendar calendar = new GregorianCalendar();
        calendar.set(Calendar.HOUR_OF_DAY, 8);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        if(calendar.before(new GregorianCalendar())){
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                cal = new GregorianCalendar();
                cal.add(Calendar.DAY_OF_YEAR, 7);
                cal.set(Calendar.HOUR_OF_DAY, 19);

                c.sendMessage(cal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.ENGLISH) + " <t:" + (cal.getTimeInMillis()/1000) + ":R>").queue();

                MessageEmbed embed = new EmbedBuilder()
                        .setTitle(cal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.ENGLISH) + " <t:" + (cal.getTimeInMillis()/1000) + ":R>")
                                .setDescription(":white_check_mark:: \n :yellow_square:: \n :x:: ").build();

                c.sendMessageEmbeds(embed).addActionRow(
                        Button.secondary("schedule yes", Emoji.fromUnicode("U+2705")),
                        Button.secondary("schedule maybe", Emoji.fromUnicode("U+1F7E8")),
                        Button.secondary("schedule no", Emoji.fromUnicode("U+274C"))
                ).queue();
            }
        }, calendar.getTime(),24*60*60*1000);



        //------------------------------testing
        /*cal = new GregorianCalendar();
        cal.add(Calendar.DAY_OF_YEAR, 7);
        cal.set(Calendar.HOUR_OF_DAY, 19);

        c.sendMessage(cal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.ENGLISH) + " <t:" + (cal.getTimeInMillis()/1000) + ":R>").queue();

        MessageEmbed embed = new EmbedBuilder()
                .setTitle(cal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.ENGLISH) + " <t:" + (cal.getTimeInMillis()/1000) + ":R>")
                .setDescription(":white_check_mark:: \n :yellow_square:: \n :x:: " ).build();

        c.sendMessageEmbeds(embed).addActionRow(
                Button.secondary("schedule yes", Emoji.fromUnicode("U+2705")),
                Button.secondary("schedule maybe", Emoji.fromUnicode("U+1F7E8")),
                Button.secondary("schedule no", Emoji.fromUnicode("U+274C"))
        ).queue();*/
    }

    public void update(ButtonInteractionEvent event){
        String[] list = event.getMessage().getEmbeds().get(0).getDescription().split("\n");
        List<ArrayList<String>> thingie = Arrays.stream(list).map(s -> new ArrayList<>(List.of(s.split(" ")))).toList();
        thingie.forEach(l -> {
            l.remove(0);
            l.removeIf(String::isEmpty);
            try{
                System.out.println(l.get(1).isEmpty() + ":" + l.get(1));
            }catch(Exception ignored){

            }
        });
        System.out.println(thingie);
        String id = event.getComponentId();
        String username = event.getUser().getEffectiveName();
        if(event.getComponentId().equals("schedule yes")){
            if(!thingie.get(0).contains(username)) {
                thingie.get(0).add(username);
                thingie.get(1).remove(username);
                thingie.get(2).remove(username);
            }
        }else if(id.equals("schedule maybe")) {
            if(!thingie.get(1).contains(username)) {
                thingie.get(1).add(username);
                thingie.get(0).remove(username);
                thingie.get(2).remove(username);
            }
        }else if(id.equals("schedule no")){
            if(!thingie.get(2).contains(username)) {
                thingie.get(2).add(username);
                thingie.get(1).remove(username);
                thingie.get(0).remove(username);
            }
        }

        MessageEmbed embed = new EmbedBuilder()
                .setTitle(event.getMessage().getEmbeds().get(0).getTitle())
                .setDescription(":white_check_mark:: " +
                        thingie.get(0).stream().reduce("", (a, b) -> a + " " + b) + "\n" +
                        thingie.get(1).stream().reduce("", (a, b) -> a + " " + b) + "\n" +
                        thingie.get(2).stream().reduce("", (a, b) -> a + " " + b)).build();
        event.editMessageEmbeds(embed).queue();
    }
}
