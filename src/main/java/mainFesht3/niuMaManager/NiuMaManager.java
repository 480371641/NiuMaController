package mainFesht3.niuMaManager;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import org.bukkit.command.CommandSender;
import com.google.gson.Gson;


public final class NiuMaManager extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        getLogger().info("插件已启用！"); // 服务器启动时执行
        getServer().getPluginManager().registerEvents(new EventListener(), this);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }




    public static boolean runStaticCmd(String cmd_start , String[] arg){
        String hcs = cmd_start ;
        for(int i = 0 ; i< arg.length ; i++){
            hcs += " " + arg[i] ;
        }
        CommandSender console = Bukkit.getConsoleSender();
        boolean b = Bukkit.dispatchCommand(console , hcs);
        return b;
    }

    public static String getPlayerNiuMaServerAccount(Player p){//获取服务器认可的账号名
        return p.getName() + p.getUniqueId() ;
    }

}
