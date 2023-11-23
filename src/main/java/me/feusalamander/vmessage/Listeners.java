package me.feusalamander.vmessage;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.player.PlayerChatEvent;
import com.velocitypowered.api.event.player.ServerPostConnectEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.cacheddata.CachedMetaData;

import java.util.Objects;
import java.util.Optional;

@SuppressWarnings({"UnstableApiUsage", "deprecation"})
public final class Listeners {
    public static final LegacyComponentSerializer SERIALIZER = LegacyComponentSerializer.builder()
            .character('&')
            .hexColors()
            .build();
    public static final MiniMessage mm = MiniMessage.miniMessage();
    private LuckPerms luckPermsAPI;
    private final Configuration configuration;
    private final ProxyServer proxyServer;

    Listeners(final ProxyServer proxyServer, final Configuration configuration) {
        if (proxyServer.getPluginManager().getPlugin("luckperms").isPresent()) {
            this.luckPermsAPI = LuckPermsProvider.get();
        }
        this.configuration = configuration;
        this.proxyServer = proxyServer;
    }
    @Subscribe
    private void onMessage(final PlayerChatEvent e) {
        if (!configuration.isMessageEnabled()) {
            return;
        }
        if(configuration.isAllEnabled()){
            e.setResult(PlayerChatEvent.ChatResult.denied());
        }
        message(e.getPlayer(), e.getMessage());
    }
    @Subscribe
    private void onLeave(final DisconnectEvent e) {
        if (!configuration.isLeaveEnabled()) {
            return;
        }

        if (!e.getLoginStatus().equals(DisconnectEvent.LoginStatus.SUCCESSFUL_LOGIN)){
            return;
        }
        if(e.getPlayer().hasPermission("vmessage.silent.leave")){
            return;
        }
        final Player p = e.getPlayer();
        final Optional<ServerConnection> server = p.getCurrentServer();
        if (server.isEmpty()) {
            return;
        }
        String message = configuration.getLeaveFormat();
        String servername = server.get().getServerInfo().getName();
        if(configuration.getAliases().contains(servername)){
            servername = configuration.getAliases().getString(servername);
        }
        if(configuration.getLeavecmd() != null&&!configuration.getLeavecmd().isEmpty())
            for(String s : configuration.getLeavecmd()){
                s = s
                        .replace("#player#", p.getUsername())
                        .replace("#oldserver#", servername);
                if (luckPermsAPI != null) {
                    s = luckperms(s, p);
                }
                proxyServer.getCommandManager().executeAsync(proxyServer.getConsoleCommandSource(), s);
            }
        if(message.isEmpty())return;
        message = message
                .replace("#player#", p.getUsername())
                .replace("#oldserver#", servername);
        if (luckPermsAPI != null) {
            message = luckperms(message, p);
        }
        if (configuration.isMinimessageEnabled()) {
            proxyServer.sendMessage(mm.deserialize(message.replace("§", "")));
        } else {
            proxyServer.sendMessage(SERIALIZER.deserialize(message));
        }

    }
    @Subscribe
    private void onChange(final ServerPostConnectEvent e) {
        if (!configuration.isChangeEnabled() && !configuration.isJoinEnabled()) {
            return;
        }
        final RegisteredServer pre = e.getPreviousServer();
        final Player p = e.getPlayer();
        final Optional<ServerConnection> serverConnection = e.getPlayer().getCurrentServer();
        if (pre != null&&serverConnection.isPresent()) {
            if (!configuration.isChangeEnabled()) {
                return;
            }
            if(e.getPlayer().hasPermission("vmessage.silent.change")){
                return;
            }
            final ServerConnection actual = serverConnection.get();
            String message = configuration.getChangeFormat();
            String actualservername = actual.getServerInfo().getName();
            if(configuration.getAliases().contains(actualservername)){
                actualservername = configuration.getAliases().getString(actualservername);
            }
            String oldservername = pre.getServerInfo().getName();
            if(configuration.getAliases().containsTable(oldservername)){
                oldservername = configuration.getAliases().getString(oldservername);
            }
            if(configuration.getChangecmd() != null&&!configuration.getChangecmd().isEmpty())
                for(String s : configuration.getChangecmd()){
                    s = s
                            .replace("#player#", p.getUsername())
                            .replace("#oldserver#", oldservername)
                            .replace("#server#", actualservername);
                    if (luckPermsAPI != null) {
                        s = luckperms(s, p);
                    }
                    proxyServer.getCommandManager().executeAsync(proxyServer.getConsoleCommandSource(), s);
                }
            if(message.isEmpty())return;
            message = message
                    .replace("#player#", p.getUsername())
                    .replace("#oldserver#", oldservername)
                    .replace("#server#", actualservername);
            if (luckPermsAPI != null) {
                message = luckperms(message, p);
            }
            if (configuration.isMinimessageEnabled()) {
                proxyServer.sendMessage(mm.deserialize(message.replace("§", "")));
            } else {
                proxyServer.sendMessage(SERIALIZER.deserialize(message));
            }
        } else if (serverConnection.isPresent()){
            if (!configuration.isJoinEnabled()) {
                return;
            }
            if(e.getPlayer().hasPermission("vmessage.silent.join")){
                return;
            }
            String actualservername = serverConnection.get().getServerInfo().getName();
            if(configuration.getAliases().contains(actualservername)){
                actualservername = configuration.getAliases().getString(actualservername);
            }
            if(configuration.getJoincmd() != null&&!configuration.getJoincmd().isEmpty())
                for(String s : configuration.getJoincmd()){
                    s = s
                            .replace("#player#", p.getUsername())
                            .replace("#server#", actualservername);
                    if (luckPermsAPI != null) {
                        s = luckperms(s, p);
                    }
                    proxyServer.getCommandManager().executeAsync(proxyServer.getConsoleCommandSource(), s);
                }
            String message = configuration.getJoinFormat();
            if(message.isEmpty())return;
            message = message
                    .replace("#player#", p.getUsername())
                    .replace("#server#", actualservername);
            if (luckPermsAPI != null) {
                message = luckperms(message, p);
            }
            if (configuration.isMinimessageEnabled()) {
                proxyServer.sendMessage(mm.deserialize(message.replace("§", "")));
            } else {
                proxyServer.sendMessage(SERIALIZER.deserialize(message));
            }
        }
    }
    private String luckperms(String message, final Player p) {
        final CachedMetaData data = luckPermsAPI.getPlayerAdapter(Player.class).getMetaData(p);
        final String prefix = data.getPrefix();
        final String suffix = data.getSuffix();
        String custom1 = null;
        String custom2 = null;
        if (configuration.isCustom1Enabled()) {
            custom1 = data.getMetaValue(configuration.getCustom1());
        }
        if (configuration.isCustom2Enabled()) {
            custom2 = data.getMetaValue(configuration.getCustom2());
        }
        if (message.contains("#prefix#") && prefix != null) {
            message = message.replace("#prefix#", prefix);
        }
        if (message.contains("#suffix#") && suffix != null) {
            message = message.replace("#suffix#", suffix);
        }
        if (message.contains("#custom1#") && custom1 != null) {
            message = message.replace("#custom1#", custom1);
        }
        if (message.contains("#custom2#") && custom2 != null) {
            message = message.replace("#custom2#", custom2);
        }
        message = message.replace("#prefix#", "").replace("#suffix#", "").replace("#custom1#","").replace("#custom2#","");
        return message;
    }
    public void message(final Player p, final String m) {
        String actualservername = p.getCurrentServer().orElseThrow().getServerInfo().getName();
        if(configuration.getAliases().contains(actualservername)){
            actualservername = configuration.getAliases().getString(actualservername);
        }
        if(configuration.getMessagecmd() != null&&!configuration.getMessagecmd().isEmpty())
            for(String s : configuration.getMessagecmd()){
                s = s
                        .replace("#player#", p.getUsername())
                        .replace("#server#", actualservername);
                if (luckPermsAPI != null) {
                    s = luckperms(s, p);
                }
                proxyServer.getCommandManager().executeAsync(proxyServer.getConsoleCommandSource(), s);
            }
        String message = configuration.getMessageFormat();
        if(message.isEmpty())return;
        final boolean permission = p.hasPermission("vmessage.minimessage");
        message = message
                .replace("#player#", p.getUsername())
                .replace("#server#", actualservername);
        if (luckPermsAPI != null) {
            message = luckperms(message, p);
        }
        if(permission)message = message.replace("#message#", m);
        Component finalMessage;
        if (configuration.isMinimessageEnabled()) {
            finalMessage = mm.deserialize(message.replace("§", ""));
        } else {
            finalMessage = SERIALIZER.deserialize(message);
        }
        if(!permission)finalMessage = finalMessage.replaceText("#message#", Component.text(m));
        if(configuration.isAllEnabled()){
            proxyServer.sendMessage(finalMessage);
        }else {
            final Component FMessage = finalMessage;
            proxyServer.getAllServers().forEach(server -> {
                if (!Objects.equals(p.getCurrentServer().map(ServerConnection::getServerInfo).orElse(null), server.getServerInfo())) {
                    server.sendMessage(FMessage);
                }
            });
        }

    }
}