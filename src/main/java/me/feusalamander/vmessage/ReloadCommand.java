package me.feusalamander.vmessage;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.List;

public final class ReloadCommand implements SimpleCommand {
    private final Configuration config;

    ReloadCommand(Configuration config) {
        this.config = config;
    }

    @Override
    public void execute(final Invocation invocation) {
        final CommandSource source = invocation.source();
        final String[] args = invocation.arguments();
        if (args.length == 0) {
            source.sendMessage(Component.text("Usage: /vmessage reload", NamedTextColor.RED));
            return;
        }
        final String s = args[0];
        if (s.equalsIgnoreCase("reload")) {
            config.reload();
            source.sendMessage(Component.text("The Vmessage's config has been succefully reloaded"));
        }
    }

    @Override
    public boolean hasPermission(final Invocation invocation) {
        return invocation.source().hasPermission("vmessage.command");
    }

    private static final List<String> suggestion = List.of("reload");

    @Override
    public List<String> suggest(final Invocation invocation) {
        final String[] args = invocation.arguments();
        if (args.length == 0 || (args.length == 1 && "reload".startsWith(args[0]))) {
            return suggestion;
        }
        return List.of();
    }
}
