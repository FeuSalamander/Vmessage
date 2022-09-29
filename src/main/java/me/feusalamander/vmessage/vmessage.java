package me.feusalamander.vmessage;
import com.google.inject.Inject;
import com.velocitypowered.api.event.player.PlayerChatEvent;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import net.kyori.adventure.text.Component;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import org.slf4j.Logger;
import java.util.Objects;
@Plugin(
        id = "vmessage",
        name = "Vmessage",
        version = "1.0",
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
    public vmessage(ProxyServer proxy, Logger logger, Metrics.Factory metricsFactory) {
        this.proxy = proxy;
        this.logger = logger;
        this.metricsFactory = metricsFactory;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        logger.info("Vmessage by FeuSalamander is working !");
        Metrics metrics = metricsFactory.make(this, 16527);
    }
    @Subscribe
    public void onMessage(PlayerChatEvent e){
        proxy.getAllServers().stream().forEach(registeredServer -> sendMessage(registeredServer, e.getPlayer(), e.getMessage()));
    }
    public void sendMessage(RegisteredServer s, Player p, String m){
        if(!(Objects.equals(p.getCurrentServer().get().getServerInfo().getName(), s.getServerInfo().getName()))){
            if(proxy.getPluginManager().getPlugin("luckperms").isPresent()){
                LuckPerms api = LuckPermsProvider.get();
                User user = api.getPlayerAdapter(Player.class).getUser(p);
                String f = user.getCachedData().getMetaData().getPrefix().replaceAll("&", "§");
                s.sendMessage(Component.text("§a("+p.getCurrentServer().get().getServerInfo().getName()+") "+f+" "+p.getUsername()+" §8§l> §r"+m));
            }else{
                s.sendMessage(Component.text("§a("+p.getCurrentServer().get().getServerInfo().getName()+") "+"§r "+p.getUsername()+" §8§l> §r"+m));
            }
        }
    }
}
