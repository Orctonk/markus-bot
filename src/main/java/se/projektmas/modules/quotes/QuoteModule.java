package se.projektmas.modules.quotes;

import discord4j.core.event.domain.lifecycle.ReadyEvent;
import se.projektmas.modules.Module;

public class QuoteModule extends Module {

    public QuoteModule(){
        commands.put("quote", event -> {
            Quote sq = QuoteDB.getRandomQuote();
            event.getMessage().getChannel().block().createMessage(
                "> " + sq.quote + "\n"+
                "\t- " + sq.quotee
            ).block();
        });
        commands.put("newquote", event -> {
            String gq = QuoteDB.getRandomGenQuote();
            event.getMessage().getChannel().block().createMessage(
                "> " + gq + "\n"+
                "\t- Generated"
            ).block();
        });
    }

    @Override
    public void onReady(ReadyEvent event) {
        QuoteDB.connect();
    }
    
}