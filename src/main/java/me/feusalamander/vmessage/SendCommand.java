package me.feusalamander.vmessage;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;

import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public final class SendCommand implements SimpleCommand {
    VMessage main;
    SendCommand(VMessage main){
        this.main = main;
    }
    @Override
    public void execute(final Invocation invocation) {
        CommandSource source = invocation.source();
        String[] args = invocation.arguments();
        if(!(source instanceof Player)){
            return;
        }
        if(args.length == 0){
            source.sendMessage(Component.text("§cUsage: /sendall *your message*"));
            return;
        }
        Player p = (Player)source;
        String s = args[0];
        main.listeners.message(p, s);
    }
}
