package se.projektmas;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import discord4j.core.DiscordClient;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.event.domain.lifecycle.*;
import discord4j.core.event.domain.message.*;

import se.projektmas.modules.Module;
import se.projektmas.modules.quotes.QuoteModule;
import se.projektmas.modules.sjpoints.PointModule;

public class Markus {
    private static List<Module> modules = new LinkedList<Module>(
        Arrays.asList(
            new PointModule(),
            new QuoteModule()
        )
    );
        public static void main( String[] args )
    {
        if(args.length == 0){
            System.err.println("Please pass the discord app token as an argument when running the bot!");
            return;
        }
        DiscordClientBuilder builder = new DiscordClientBuilder(args[0]);
        DiscordClient client = builder.build();

        for (Module m : modules) {
            m.setClient(client);
        }

        client.getEventDispatcher().on(ConnectEvent.class).subscribe(event -> {for (Module m : modules) m.onConnect(event); });
        client.getEventDispatcher().on(ReadyEvent.class).subscribe(event -> {for (Module m : modules) m.onReady(event); });
        client.getEventDispatcher().on(ReactionAddEvent.class).subscribe(event -> {for (Module m : modules) m.onReactionAdd(event);});
        client.getEventDispatcher().on(ReactionRemoveEvent.class).subscribe(event -> {for (Module m : modules) m.onReactionRemove(event);});

        client.getEventDispatcher().on(MessageCreateEvent.class)
            .subscribe(event -> {
                Optional<String> content = event.getMessage().getContent();
                if (content.isPresent() && content.get().startsWith("!"))
                    for(Module m : modules){
                        m.handleCommand(content.get().substring(1),event);
                    }
            });

        client.login().block();
    }
}
