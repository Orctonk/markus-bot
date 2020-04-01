package se.projektmas;

import discord4j.core.event.domain.message.MessageCreateEvent;

interface ICommand{
    void execute(MessageCreateEvent event);
}