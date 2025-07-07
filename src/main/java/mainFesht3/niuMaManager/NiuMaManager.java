package mainFesht3.niuMaManager;

import com.google.gson.Gson;
import mainFesht3.niuMaManager.Utils.httpClient;
import mainFesht3.niuMaManager.Vault.NMB;
import mainFesht3.niuMaManager.Vault.NMBCommandTab;
import mainFesht3.niuMaManager.Vault.Sidebar;
import mainFesht3.niuMaManager.qqBot.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import org.bukkit.command.CommandSender;

import java.time.Instant;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.milkbowl.vault.economy.Economy; // 此时应无报错
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.scheduler.BukkitTask;



public final class NiuMaManager extends JavaPlugin {
    int tick = 0;
    Map<String, Double> randomtTpTime = new ConcurrentHashMap<>();
    private static Economy econ;

    private Main qqbot_webSocketServer;
    private int webSocketPort = 8080; // WebSocket服务器端口
    private Gson gson = new Gson();
    BukkitTask task; // 保存任务引用
    BukkitTask sidebarTask;

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
    public double getTime(){
        return  (double) Instant.now().toEpochMilli() /1000;
        //        System.out.println("毫秒级时间戳: " + timestampMillis);
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
        // 注册命令补全器
        getCommand("nmb").setTabCompleter(new NMBCommandTab());


        // 在异步线程中启动WebSocket服务器
        Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
            try {
                qqbot_webSocketServer = new Main(this, webSocketPort);
                qqbot_webSocketServer.start();
                getLogger().info("WebSocket服务器已启动，访问 ws://localhost:" + webSocketPort);
            } catch (Exception e) {
                getLogger().severe("启动WebSocket服务器失败: " + e.getMessage());
                e.printStackTrace();
                setEnabled(false); // 插件加载失败
            }
        });
        task = Bukkit.getScheduler().runTaskTimer(this, this::TTask , 0L , 30L);
//        Sidebar sdb = new Sidebar();
//        sidebarTask = Bukkit.getScheduler().runTaskTimer(this, sdb::updateEveryOnlinePlayerSidebar , 0L , 10L);
//        失败的侧边栏，不搞了
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        task.cancel();
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

    public static int getPlayerNMB(Player p){
        try {
            httpClient hc = new httpClient("http://139.224.250.35:666/mc/nm.php");
            Map<String, String> params = new HashMap<>();
            params.put("name", p.getName());
            params.put("type", "checknmb");
            params.put("name", NiuMaManager.getPlayerNiuMaServerAccount(p));
//        Bukkit.getLogger().info("服务器输出数据：" + hc.get(params));
            int res = Integer.parseInt(hc.get(params));
            return  res;
        }catch (Exception e){
            return 0;
        }
    }



    Map<String, Double> online_player_time = new HashMap<>();
    httpClient hc = new httpClient("http://139.224.250.35:666/mc/nm.php");
    double last_postTime = getTime();


    public void TTask(){
        double now_time = getTime();
        Collection<? extends Player> olp = Bukkit.getOnlinePlayers();
        Map<String, String> params = new HashMap<>();
        params.put("type" , "post_time");

        Map<String, Double> post_stss = new HashMap<>();

        Map<String, Double> newmapOPT = new HashMap<>();
        for(Player p : olp){
            if(online_player_time.containsKey(p.getName())){
                //在上次记录的ol玩家中，如果这次依旧在线，则增加游戏时长，在接口中待命
                post_stss.put(NiuMaManager.getPlayerNiuMaServerAccount(p) , now_time - last_postTime);
            }
            newmapOPT.put(p.getName() , now_time);
        }
        online_player_time = newmapOPT;
        params.put("type2" ,gson.toJson(post_stss));
        hc.get(params);
        last_postTime = now_time;

//        Bukkit.getLogger().info(gson.toJson(params));
    }
}
