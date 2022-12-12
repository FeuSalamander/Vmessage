package me.feusalamander.vmessage;
import com.google.inject.Inject;
import com.moandjiezana.toml.Toml;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.LoginEvent;
import com.velocitypowered.api.event.player.*;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import net.kyori.adventure.text.Component;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.event.player.PlayerLoginProcessEvent;
import net.luckperms.api.model.user.User;
import org.slf4j.Logger;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
@Plugin(
        id = "vmessage",
        name = "Vmessage",
        version = "1.2",
        description = "A velocity plugin that creates a multi server chat for the network",
        authors = {"FeuSalamander"}
)
public class vmessage {
    @Inject
    private final ProxyServer proxy;
    @Inject
    private final Logger logger;
    @Inject
    private final Metrics.Factory metricsFactory;
    @Inject
    private final @DataDirectory Path dataDirectory;
    @Inject
    public vmessage(ProxyServer proxy, Logger logger, Metrics.Factory metricsFactory, @DataDirectory Path dataDirectory) {
        this.proxy = proxy;
        this.logger = logger;
        this.metricsFactory = metricsFactory;
        this.dataDirectory = dataDirectory;
    }
    private String message = "";
    private boolean messageb = false;
    private boolean joinb = false;
    private boolean leaveb = false;
    private boolean changeb = false;
    @Subscribe
    private void onProxyInitialization(ProxyInitializeEvent event) {
        logger.info("Vmessage by FeuSalamander is working !");
        Metrics metrics = metricsFactory.make(this, 16527);
        File directory = dataDirectory.toFile();
        File f = new File(directory, "config.toml");
        Toml config = new Toml().read(f);
        createconfig();
        if(config.getBoolean("Message.enabled"))messageb = true;
        if(config.getBoolean("Join.enabled"))joinb = true;
        if(config.getBoolean("Leave.enabled"))leaveb = true;
        if(config.getBoolean("Server-change.enabled"))changeb = true;
        message = getmessage("Message.format");
    }
    @Subscribe
    private void onMessage(PlayerChatEvent e){
        proxy.getAllServers().stream().forEach(registeredServer -> sendMessage(registeredServer, e.getPlayer(), e.getMessage()));
    }
    @Subscribe
    private void onJoin(LoginEvent e){

    }
    @Subscribe
    private void onLeave(DisconnectEvent e){

    }
    @Subscribe
    private void onChange(ServerConnectedEvent e){

    }
    private void sendMessage(RegisteredServer s, Player p, String m){
        message = message.replaceAll("#player#", p.getUsername());
        message = message.replaceAll("#message#",m);
        message = message.replaceAll("#server#",p.getCurrentServer().get().getServerInfo().getName());
        message = message.replaceAll("&", "§");
        if(!(Objects.equals(p.getCurrentServer().get().getServerInfo().getName(), s.getServerInfo().getName()))){
            if(proxy.getPluginManager().getPlugin("luckperms").isPresent()){
                LuckPerms api = LuckPermsProvider.get();
                User user = api.getPlayerAdapter(Player.class).getUser(p);
                message = message.replaceAll("#prefix#", user.getCachedData().getMetaData().getPrefix().replaceAll("&", "§"));
                s.sendMessage(Component.text(message));
                message = getmessage("Message.format");
            }else{
                s.sendMessage(Component.text(message));
                message = getmessage("Message.format");
            }
        }
    }
    private void createconfig(){
        File dataDirectoryFile = this.dataDirectory.toFile();
        if (!dataDirectoryFile.exists()){
            dataDirectoryFile.mkdir();
        }
        File f = new File(dataDirectoryFile, "config.toml");
        if(!(f.exists())){
            try{
                InputStream file = this.getClass().getResourceAsStream("/config.toml");
                Files.copy(file, f.toPath());
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }
    private String getmessage(String s){
        File directory = dataDirectory.toFile();
        File f = new File(directory, "config.toml");
        Toml config = new Toml().read(f);
        return config.getString(s);
    }
}
