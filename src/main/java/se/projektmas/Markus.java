package se.projektmas;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import discord4j.core.DiscordClient;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.event.domain.message.ReactionAddEvent;
import discord4j.core.event.domain.message.ReactionRemoveEvent;
import discord4j.core.object.entity.User;
import discord4j.core.object.reaction.ReactionEmoji;
import discord4j.core.object.util.Snowflake;

public class Markus {
    private static final Map<String,ICommand> commands = new HashMap<>();
    private static final String satan = "😈";
    private static final String jesus = "👼";
        public static void main( String[] args )
    {
        DiscordClientBuilder builder = new DiscordClientBuilder(args[1]);

        DiscordClient client = builder.build();

        client.getEventDispatcher().on(ReadyEvent.class)
            .subscribe(event -> {
                User self = event.getSelf();
                System.out.println(String.format("Logged in as %s#%s", self.getUsername(),self.getDiscriminator()));
                SPDB.connect();
            });

        commands.put("list", event -> {
            List<TallyRow> tally = SPDB.getSorted();
            final String ufstring = "%1$-18s ";
            final String spfstring = "%1$-3d ";
            String msg =    "```\n" +
                            "Username           SP  JP  \n";
            for(TallyRow tr : tally){
                final String uname = client.getUserById(Snowflake.of(tr.snowflake)).block().getUsername();
                msg += String.format(ufstring, uname);
                msg += String.format(spfstring, tr.sp);
                msg += String.format(spfstring, tr.jp);
                msg += "\n";
            }
            msg += "```";

            event.getMessage().getChannel().block().createMessage(msg).block();
        });

        client.getEventDispatcher().on(ReactionAddEvent.class)
            .subscribe(event -> {
                Optional<User> omu = event.getMessage().block().getAuthor();
                User ru = event.getUser().block();
                if(omu.isPresent() && !ru.equals(omu.get())){
                    Snowflake sf = omu.get().getId();
                    if(event.getEmoji().equals(ReactionEmoji.unicode(satan)))
                        SPDB.increment(sf.asString(), Point.SP);
                    else if(event.getEmoji().equals(ReactionEmoji.unicode(jesus)))
                        SPDB.increment(sf.asString(), Point.JP);
                }
            });

        client.getEventDispatcher().on(ReactionRemoveEvent.class)
            .subscribe(event -> {
                Optional<User> omu = event.getMessage().block().getAuthor();
                User ru = event.getUser().block();
                if(omu.isPresent() && !ru.equals(omu.get())){
                    Snowflake sf = omu.get().getId();
                    if(event.getEmoji().equals(ReactionEmoji.unicode(satan)))
                        SPDB.decrement(sf.asString(), Point.SP);
                    else if(event.getEmoji().equals(ReactionEmoji.unicode(jesus)))
                        SPDB.decrement(sf.asString(), Point.JP);
                }
            });

        client.getEventDispatcher().on(MessageCreateEvent.class)
            .subscribe(event -> {
                Optional<String> content = event.getMessage().getContent();
                if (content.isPresent() && content.get().startsWith("!"))
                    for(Map.Entry<String,ICommand> entry : commands.entrySet())
                        if(entry.getKey().equals(content.get().substring(1)))
                            entry.getValue().execute(event);
            });

        client.login().block();
    }
}
