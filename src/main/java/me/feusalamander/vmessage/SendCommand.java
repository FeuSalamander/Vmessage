package me.feusalamander.vmessage;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public final class SendCommand implements SimpleCommand {
    VMessage main;

    SendCommand(VMessage main) {
        this.main = main;
    }

    @Override
    public void execute(final Invocation invocation) {
        CommandSource source = invocation.source();
        String[] args = invocation.arguments();
        if (!(source instanceof Player)) {
            return;
        }
        if (args.length == 0) {
            source.sendMessage(Component.text("Usage: /sendall *your message*", NamedTextColor.RED));
            return;
        }
        Player p = (Player) source;
        String s = args[0];
        main.listeners.message(p, s);
    }
}
