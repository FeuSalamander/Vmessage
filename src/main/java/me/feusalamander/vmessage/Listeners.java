package me.feusalamander.vmessage;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import com.velocitypowered.api.event.connection.PreLoginEvent;
import com.velocitypowered.api.event.player.PlayerChatEvent;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import com.velocitypowered.api.proxy.LoginPhaseConnection;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;

import java.util.Objects;
import java.util.Optional;

public final class Listeners {
    private static final LegacyComponentSerializer SERIALIZER = LegacyComponentSerializer.builder()
            .character('&')
            .hexColors()
            .build();
    private LuckPerms luckPermsAPI;
    private final Configuration configuration;
    private final ProxyServer proxyServer;

    Listeners(ProxyServer proxyServer, Configuration configuration) {
        if (proxyServer.getPluginManager().getPlugin("luckperms").isPresent()){
            this.luckPermsAPI = LuckPermsProvider.get();
        }
        this.configuration = configuration;
        this.proxyServer = proxyServer;
    }

    @Subscribe
    private void onMessage(PlayerChatEvent e){
        if (!configuration.isMessageEnabled()) {
            return;
        }
        Player p = e.getPlayer();
        String m = e.getMessage();
        String message = configuration.getMessageFormat()
                .replace("#player#", p.getUsername())
                .replace("#message#", m)
                .replace("#server#", p.getCurrentServer().orElseThrow().getServerInfo().getName());
        if (luckPermsAPI != null){
            User user = luckPermsAPI.getPlayerAdapter(Player.class).getUser(p);
            message = message.replace("#prefix#", Objects.requireNonNull(user.getCachedData().getMetaData().getPrefix()));
        }
        final String finalMessage = message;
        proxyServer.getAllServers().forEach(server -> {
            if (!Objects.equals(p.getCurrentServer().map(ServerConnection::getServerInfo).orElse(null), server.getServerInfo())){
                server.sendMessage(SERIALIZER.deserialize(finalMessage));
            }
        });
    }
    @Subscribe
    private void onLeave(DisconnectEvent e){
        if (!configuration.isLeaveEnabled()) {
            return;
        }
        Player p = e.getPlayer();
        String message = configuration.getLeaveFormat()
                .replace("#player#", p.getUsername());
        if (luckPermsAPI != null){
            User user = luckPermsAPI.getPlayerAdapter(Player.class).getUser(p);
            message = message.replace("#prefix#", Objects.requireNonNull(user.getCachedData().getMetaData().getPrefix()));
        }
        proxyServer.sendMessage(SERIALIZER.deserialize(message));
    }

    @Subscribe
    private void onChange(ServerConnectedEvent e){
        if (!configuration.isChangeEnabled()&&!configuration.isJoinEnabled()) {
            return;
        }
        Optional<RegisteredServer> server = e.getPreviousServer();
        Player p = e.getPlayer();
        RegisteredServer actual = e.getServer();
        if(server.isPresent()){
            if (!configuration.isChangeEnabled()) {
                return;
            }
            RegisteredServer pre = server.get();
            String message = configuration.getChangeFormat()
                    .replace("#player#", p.getUsername())
                    .replace("#oldserver#", pre.getServerInfo().getName())
                    .replace("#server#", actual.getServerInfo().getName());
            if (luckPermsAPI != null){
                User user = luckPermsAPI.getPlayerAdapter(Player.class).getUser(p);
                message = message.replace("#prefix#", Objects.requireNonNull(user.getCachedData().getMetaData().getPrefix()));
            }
            proxyServer.sendMessage(SERIALIZER.deserialize(message));
        }else{
            if (!configuration.isJoinEnabled()) {
                return;
            }
            String message = configuration.getJoinFormat().replace("#player#", p.getUsername());
            if (luckPermsAPI != null){
                User user = luckPermsAPI.getPlayerAdapter(Player.class).getUser(p);
                message = message.replace("#prefix#", Objects.requireNonNull(user.getCachedData().getMetaData().getPrefix()));
            }
            proxyServer.sendMessage(SERIALIZER.deserialize(message));
        }
    }
}