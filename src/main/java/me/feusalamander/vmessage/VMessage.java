package me.feusalamander.vmessage;

import com.google.inject.Inject;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.plugin.Dependency;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import org.slf4j.Logger;
import java.nio.file.Path;

@Plugin(
        id = "vmessage",
        name = "Vmessage",
        version = "1.4",
        description = "A velocity plugin that creates a multi server chat for the network",
        authors = {"FeuSalamander"},
        dependencies = { @Dependency(id = "luckperms", optional = true) }
)
public class VMessage {
    private final ProxyServer proxy;
    private final Logger logger;
    private final Metrics.Factory metricsFactory;
    private final Path dataDirectory;

    @Inject
    public VMessage(ProxyServer proxy, Logger logger, Metrics.Factory metricsFactory, @DataDirectory Path dataDirectory) {
        this.proxy = proxy;
        this.logger = logger;
        this.metricsFactory = metricsFactory;
        this.dataDirectory = dataDirectory;
    }

    @Subscribe
    private void onProxyInitialization(ProxyInitializeEvent event) {
        Configuration configuration = Configuration.load(dataDirectory);
        if (configuration == null) {
            return;
        }
        metricsFactory.make(this, 16527);
        proxy.getEventManager().register(this, new Listeners(proxy, configuration));
        logger.info("Vmessage by FeuSalamander is working !");
        CommandManager commandManager = proxy.getCommandManager();
        CommandMeta commandMeta = commandManager.metaBuilder("Vmessage")
                .plugin(this)
                .build();
        SimpleCommand command = new ReloadCommand(dataDirectory, configuration);
        commandManager.register(commandMeta, command);
    }
}
