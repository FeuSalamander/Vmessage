package me.feusalamander.vmessage;
import com.velocitypowered.api.command.SimpleCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;

import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.List;

public final class ReloadCommand implements SimpleCommand {
    final Path dataDirectory;
    final Configuration config;
    ReloadCommand(Path dataDirectory, Configuration config){
        this.dataDirectory = dataDirectory;
        this.config = config;
    }

    @Override
    public void execute(final Invocation invocation) {
        String[] args = invocation.arguments();
        if(args.length == 0){
            invocation.source().sendMessage(Component.text("§cUsage: /vmessage reload"));
            return;
        }
        String s = args[0];
        if(s.equalsIgnoreCase("reload")){
            config.reload();
            invocation.source().sendMessage(Component.text("The Vmessage's config has been succefully reloaded"));
        }
    }

    @Override
    public boolean hasPermission(final Invocation invocation) {
        return invocation.source().hasPermission("*");
    }

    @Override
    public List<String> suggest(final Invocation invocation) {
        return List.of("reload");
    }

    @Override
    public CompletableFuture<List<String>> suggestAsync(final Invocation invocation) {
        return CompletableFuture.completedFuture(List.of("reload"));
    }
}
