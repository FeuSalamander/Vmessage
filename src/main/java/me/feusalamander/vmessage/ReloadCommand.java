package me.feusalamander.vmessage;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.nio.file.Path;

public final class ReloadCommand implements SimpleCommand {
    final Path dataDirectory;
    final Configuration config;

    ReloadCommand(Path dataDirectory, Configuration config) {
        this.dataDirectory = dataDirectory;
        this.config = config;
    }

    @Override
    public void execute(final Invocation invocation) {
        CommandSource source = invocation.source();
        String[] args = invocation.arguments();
        if (args.length == 0) {
            source.sendMessage(Component.text("Usage: /vmessage reload", NamedTextColor.RED));
            return;
        }
        String s = args[0];
        if (s.equalsIgnoreCase("reload")) {
            if (!source.hasPermission("*")) {
                source.sendMessage(Component.text("You don't have the permission to do that", NamedTextColor.RED));
                return;
            }
            config.reload();
            source.sendMessage(Component.text("The Vmessage's config has been succefully reloaded"));
        }
    }
}
