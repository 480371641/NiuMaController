package mainFesht3.niuMaManager;

import mainFesht3.niuMaManager.Vault.NMB;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import org.bukkit.command.CommandSender;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.milkbowl.vault.economy.Economy; // 此时应无报错
import org.bukkit.plugin.RegisteredServiceProvider;


public final class NiuMaManager extends JavaPlugin {
    int tick = 0;
    Map<String, Double> randomtTpTime = new ConcurrentHashMap<>();
    private static Economy econ;

    public void setRTT(String name , double time){
        randomtTpTime.put(name , time);
    }
    public Double getRTT(String name){
        if (randomtTpTime.get(name) != null ){
            return randomtTpTime.get(name);
        }else {
            return 0.0;
        }
    }

    private boolean setupEconomy() {
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) return false;
        econ = rsp.getProvider();
        return econ != null;
    }
    public static Economy getEconomy() {
        return econ;
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        getLogger().info("插件已启用！"); // 服务器启动时执行
        getServer().getPluginManager().registerEvents(new EventListener(this), this);
        getCommand("randomtp").setExecutor(new RandomTp(this));

        // 初始化Vault经济服务
        if (!setupEconomy()) {
            getLogger().severe("未检测到经济系统！插件已禁用。");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        // 注册命令处理器
        getCommand("nmb").setExecutor(new NMB(this));
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
