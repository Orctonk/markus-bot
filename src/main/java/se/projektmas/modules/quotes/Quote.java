package se.projektmas.modules.quotes;

import java.time.Instant;

public class Quote {
    public String quote;
    public String quotee;
    public String sender;
    public Instant at;

    public Quote(String quote, String quotee, String sender, String timestamp){
        this.quote = quote;
        this.quotee = quotee;
        this.sender = sender;
        this.at = Instant.ofEpochMilli(Long.parseLong(timestamp));
    }
}