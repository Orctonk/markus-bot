package se.projektmas.modules.quotes;

import java.util.List;
import java.util.Random;

import discord4j.core.event.domain.lifecycle.ReadyEvent;
import se.projektmas.modules.Module;

public class QuoteModule extends Module {
    private List<Quote> quotes;
    private Random rand = new Random();

    public QuoteModule(){
        commands.put("quote", event -> {
            Quote sq = quotes.get(rand.nextInt(quotes.size()));
            event.getMessage().getChannel().block().createMessage(
                "> " + sq.quote + "\n"+
                "\t- " + sq.quotee
            ).block();
        });
    }

    @Override
    public void onReady(ReadyEvent event) {
        QuoteDB.connect();
        quotes = QuoteDB.getAllQuotes();
    }
    
}