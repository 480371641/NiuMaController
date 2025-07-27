package mainFesht3.niuMaManager;


import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class killPlayerSelf implements CommandExecutor {
    @Override
    public boolean onCommand( CommandSender commandSender, Command command, String s, String[] strings) {
        try{
            Player player = (Player) commandSender;
            player.setHealth(0);
            String[] ss={"有个傻逼自杀了hhhhhhh"};
            NiuMaManager.runStaticCmd("say" ,ss );
        }catch (Exception e){}
        return true;
    }
}
