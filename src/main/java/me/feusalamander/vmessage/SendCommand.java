package me.feusalamander.vmessage;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public final class SendCommand implements SimpleCommand {
    private final VMessage main;

    SendCommand(VMessage main) {
        this.main = main;
    }

    @Override
    public void execute(final Invocation invocation) {
        if(!hasPermission(invocation)){
            return;
        }
        final CommandSource source = invocation.source();
        final String[] args = invocation.arguments();
        if (args.length == 0) {
            source.sendMessage(Component.text("Usage: /sendall *your message*", NamedTextColor.RED));
            return;
        }
        final Player p = (Player) source;
        final String s = args[0];
        main.listeners.message(p, s);
    }

    @Override
    public boolean hasPermission(final Invocation invocation) {
        return invocation.source() instanceof Player;
    }
}
