package discordBot.scheduling;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;

import java.util.*;

public class ScheduleReminder {
    Timer t = new Timer();
    private JDA jda;
    public ScheduleReminder(JDA jda){
        this.jda = jda;
    }

    public void addReminder(String messageId){
        Calendar eventTime = getTime(messageId);
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                remindToReact(messageId);
            }
        };
    }

    public void remindToReact(String messageId){
        Message message = jda.getTextChannelById("396332195360276484").retrieveMessageById(messageId).complete();
        String[] list = message.getEmbeds().get(0).getDescription().split("\n");
        List<String> names = new ArrayList<>(List.of(new String[]{"Fuselet"/*, "Velvet", "twinbe", "PartySquid"*/}));
        List<ArrayList<String>> reactions = Arrays.stream(list).map(s -> new ArrayList<>(List.of(s.split(" ")))).toList();
        reactions.forEach(l -> {
            l.remove(0);
            l.removeIf(String::isEmpty);
        });

        reactions.forEach(l -> l.forEach(names::remove));

        if(!names.isEmpty()){
            names.forEach(name -> {
                System.out.println(jda.getGuildById(396332195360276481L).loadMembers().get());
                jda.getGuildById(396332195360276481L).loadMembers().get().stream().filter(m -> m.getEffectiveName()
                    .equals("Fuselet")).findFirst().get().getUser().openPrivateChannel().complete().sendMessage("yo").queue();
            });
        }
    }

    public Calendar getTime(String messageId){
        Message message = jda.getTextChannelById("396332195360276484").retrieveMessageById(messageId).complete();
        String time = message.getEmbeds().get(0).getTitle().split(" ")[1].split(":")[1];
        Calendar c = new GregorianCalendar();
        c.setTimeInMillis(Integer.parseInt(time) * 1000L);

        return c;
    }
}
