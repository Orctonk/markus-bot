package se.projektmas.modules.sjpoints;

import java.util.List;
import java.util.Optional;

import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.event.domain.message.ReactionAddEvent;
import discord4j.core.event.domain.message.ReactionRemoveEvent;
import discord4j.core.object.entity.User;
import discord4j.core.object.reaction.ReactionEmoji;
import discord4j.core.object.util.Snowflake;
import se.projektmas.modules.Module;

public class PointModule extends Module{
    private static final String satan = "😈";
    private static final String jesus = "👼";

    public PointModule(){
        commands.put("list",event -> {
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
    }

    @Override
    public void onReady(ReadyEvent event) {
        SPDB.connect();
    }

    @Override
    public void onReactionAdd(ReactionAddEvent event) {
        Optional<User> omu = event.getMessage().block().getAuthor();
        User ru = event.getUser().block();
        if(omu.isPresent() && !ru.equals(omu.get())){
            Snowflake sf = omu.get().getId();
            if(event.getEmoji().equals(ReactionEmoji.unicode(satan)))
                SPDB.increment(sf.asString(), Point.SP);
            else if(event.getEmoji().equals(ReactionEmoji.unicode(jesus)))
                SPDB.increment(sf.asString(), Point.JP);
        }
    }

    @Override
    public void onReactionRemove(ReactionRemoveEvent event) {
        Optional<User> omu = event.getMessage().block().getAuthor();
        User ru = event.getUser().block();
        if(omu.isPresent() && !ru.equals(omu.get())){
            Snowflake sf = omu.get().getId();
            if(event.getEmoji().equals(ReactionEmoji.unicode(satan)))
                SPDB.decrement(sf.asString(), Point.SP);
            else if(event.getEmoji().equals(ReactionEmoji.unicode(jesus)))
                SPDB.decrement(sf.asString(), Point.JP);
        }
    }

    
}