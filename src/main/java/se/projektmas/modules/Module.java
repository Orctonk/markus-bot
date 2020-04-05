package se.projektmas.modules;

import java.util.Map;
import java.util.HashMap;

import discord4j.core.DiscordClient;
import discord4j.core.event.domain.*;
import discord4j.core.event.domain.lifecycle.*;
import discord4j.core.event.domain.channel.*;
import discord4j.core.event.domain.guild.*;
import discord4j.core.event.domain.message.*;
import discord4j.core.event.domain.role.*;

import se.projektmas.ICommand;

public abstract class Module {
    protected final Map<String,ICommand> commands = new HashMap<>();
    protected DiscordClient client;

    public void handleCommand(String command, MessageCreateEvent event){
        if(commands.containsKey(command))
            commands.get(command).execute(event);
    }
    public void setClient(DiscordClient client){
        this.client = client;
    }

    // General events
    public void onPresenceUpdate(PresenceUpdateEvent event) {}
    public void onUserUpdate(UserUpdateEvent event) {}
    public void onVoiceServerUpdate(VoiceServerUpdateEvent event) {}
    public void onVoiceStateUpdate(VoiceStateUpdateEvent event) {}
    public void onWebhooksUpdate(WebhooksUpdateEvent event) {}

    // Lifecycle events
    public void onConnect(ConnectEvent event) {}
    public void onDisconnect(DisconnectEvent event) {}
    public void onReady(ReadyEvent event) {}
    public void onReconnect(ReconnectEvent event) {}
    public void onReconnectFail(ReconnectFailEvent event) {}
    public void onReconnectStart(ReconnectStartEvent event) {}
    public void onResume(ResumeEvent event) {}

    // Channel events
    public void onCategoryCreate(CategoryCreateEvent event) {}
    public void onCategoryDelete(CategoryDeleteEvent event) {}
    public void onCategoryUpdate(CategoryUpdateEvent event) {}
    public void onPinsUpdate(PinsUpdateEvent event) {}
    public void onPrivateChannelCreate(PrivateChannelCreateEvent event) {}
    public void onPrivateChannelDelete(PrivateChannelDeleteEvent event) {}
    public void onTextChannelCreate(TextChannelCreateEvent event) {}
    public void onTextChannelDelete(TextChannelDeleteEvent event) {}
    public void onTextChannelUpdate(TextChannelUpdateEvent event) {}
    public void onTypingStart(TypingStartEvent event) {}
    public void onVoiceChannelCreate(VoiceChannelCreateEvent event) {}
    public void onVoiceChannelDelete(VoiceChannelDeleteEvent event) {}
    public void onVoiceChannelUpdate(VoiceChannelUpdateEvent event) {}

    // Guild events
    public void onBan(BanEvent event) {}
    public void onEmojisUpdate(EmojisUpdateEvent event) {}
    public void onGuildCreate(GuildCreateEvent event) {}
    public void onGuildDelete(GuildDeleteEvent event) {}
    public void onGuildUpdate(GuildUpdateEvent event) {}
    public void onIntegrationsUpdate(IntegrationsUpdateEvent event) {}
    public void onMemberChunk(MemberChunkEvent event) {}
    public void onMemberJoin(MemberJoinEvent event) {}
    public void onMemberLeave(MemberLeaveEvent event) {}
    public void onMemberUpdate(MemberUpdateEvent event) {}
    public void onUnban(UnbanEvent event) {}

    // Message events
    public void onMessageBulkDelete(MessageBulkDeleteEvent event) {}
    public void onMessageCreate(MessageCreateEvent event) {}
    public void onMessageDelete(MessageDeleteEvent event) {}
    public void onMessageUpdate(MessageUpdateEvent event) {}
    public void onReactionAdd(ReactionAddEvent event) {}
    public void onReactionRemoveAll(ReactionRemoveAllEvent event) {}
    public void onReactionRemove(ReactionRemoveEvent event) {}

    // Role events
    public void onRoleCreate(RoleCreateEvent event) {}
    public void onRoleDelete(RoleDeleteEvent event) {}
    public void onRoleUpdate(RoleUpdateEvent event) {}
}