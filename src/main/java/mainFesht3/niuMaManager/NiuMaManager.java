package mainFesht3.niuMaManager;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.tr7zw.nbtapi.NBT;
import mainFesht3.niuMaManager.EquipmentPro.equipmentEventHandler;
import mainFesht3.niuMaManager.Utils.httpClient;
import mainFesht3.niuMaManager.Vault.*;
import mainFesht3.niuMaManager.qqBot.Main;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import org.bukkit.command.CommandSender;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import net.milkbowl.vault.economy.Economy; // 此时应无报错
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;



public final class NiuMaManager extends JavaPlugin {
    double last_tick_time = 0;
    static double tick_late = 100;//此项数值用于计算两tick间的延迟，以此计算瞬时tick值！

    static JsonArray vip_player = new JsonArray();
    static JsonArray gem_data = new JsonArray();
    static JsonArray shopData = new JsonArray();
    static JsonArray gemlegal = new JsonArray();

    Map<String, Double> randomtTpTime = new ConcurrentHashMap<>();
    private static Economy econ;

    private Main qqbot_webSocketServer;
    private int webSocketPort = 8080; // WebSocket服务器端口
    private Gson gson = new Gson();
    BukkitTask task; // 保存任务引用
    BukkitTask disableInvisiableTask;
    BukkitTask tickCounterTask;

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
    public static double getTime(){
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
        getServer().getPluginManager().registerEvents(new equipmentEventHandler(), this);
        getCommand("randomtp").setExecutor(new RandomTp(this));

        // 初始化Vault经济服务
        if (!setupEconomy()) {
            getLogger().severe("未检测到经济系统！插件已禁用。");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        if (!NBT.preloadApi()) {
            getLogger().warning("NBT-API wasn't initialized properly, disabling the plugin");
            getPluginLoader().disablePlugin(this);
            return;
        }
        // 注册命令处理器
        getCommand("nmb").setExecutor(new NMB(this));
        // 注册命令补全器
        getCommand("nmb").setTabCompleter(new NMBCommandTab());


        getCommand("test").setExecutor(new Test());
        getCommand("shop").setExecutor(new ShopGUI());
        getCommand("exui").setExecutor(new ShopGUI());

        getCommand("zs").setExecutor(new killPlayerSelf());
        // 注册事件监听器
        getServer().getPluginManager().registerEvents(new GUIEvent(), this);
//        AETHER_INVISIBILITY_CLOAK

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
        task = Bukkit.getScheduler().runTaskTimer(this, this::TTask , 0L , 60L);
        disableInvisiableTask = Bukkit.getScheduler().runTaskTimer(this, this::disableInvisiable , 0L , 3L);
        tickCounterTask = Bukkit.getScheduler().runTaskTimer(this, this::tickCounter , 0L , 1L);


//        registerForgeEvent(MinecraftForge.EVENT_BUS);
//        MohistEventBus.register((EventBus) MinecraftForge.EVENT_BUS,new ForgeEvent());//!!!!!!!!!!!!!Main code!!!!!!!!!!!!!!!!!!!


//        EventListener el = new EventListener();
//        Bukkit.getScheduler().runTaskTimer(this,this::dealSetVTask, 0L , 10L);
//        Sidebar sdb = new Sidebar();
//        sidebarTask = Bukkit.getScheduler().runTaskTimer(this, sdb::updateEveryOnlinePlayerSidebar , 0L , 10L);
//        失败的侧边栏，不搞了
    }

    @Override
    public void onDisable() {
//        // Plugin shutdown logic
//        task.cancel();
//        disableInvisiableTask.cancel();
//        try {
//            qqbot_webSocketServer.stop();
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
//        qqbot_webSocketServer = null;
//        econ = null;
//        randomtTpTime.clear();
//        online_player_time.clear();
    }

    public static double getTickLate(){
        return tick_late;
    }

    public void tickCounter(){
        double nowtime = getTime();
        tick_late = nowtime - last_tick_time;
        last_tick_time = nowtime;
    }

    /**
     * 定时调用并刷新各时效性强的数据
     */
    public static void getAutoRefreshData(){
        Gson gson = new Gson();
        httpClient hc = new httpClient("http://139.224.250.35:666/mc/nm.php");
        Map<String, String> params = new HashMap<>();
        params.put("type", "viplist");
        vip_player = gson.fromJson(hc.get(params), JsonArray.class);
        params.put("type","get_gem_data");
        JsonObject js = gson.fromJson(hc.get(params), JsonObject.class);
        gem_data = js.getAsJsonArray("Gem");
        gemlegal = js.getAsJsonArray("gemlegal");

        params.put("type","data");
        shopData = gson.fromJson(hc.get(params), JsonArray.class);


    }

    public static boolean isVIP(Player player){
        for(JsonElement ele : vip_player){
            if( ele.getAsString().equals(NiuMaManager.getPlayerNiuMaServerAccount(player)) ){
                return true;
            }
        }
        return false;
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

    public static JsonObject getPlayerInfo(Player p){
        Gson gson = new Gson();
        httpClient hc = new httpClient("http://139.224.250.35:666/mc/nm.php");
        Map<String, String> params = new HashMap<>();
        params.put("type", "player_info");
        params.put("name", NiuMaManager.getPlayerNiuMaServerAccount(p));
        JsonObject res = gson.fromJson(hc.get(params),JsonObject.class);
        return res;
    }
    public static JsonObject getPlayerInfo(long qq){
        Gson gson = new Gson();
        httpClient hc = new httpClient("http://139.224.250.35:666/mc/nm.php");
        Map<String, String> params = new HashMap<>();

        params.put("type", "player_info");
        params.put("name", qq+"");
        JsonObject res = gson.fromJson(hc.get(params),JsonObject.class);
        return res;
    }

    public static int getPlayerNMB(Player p){
        try {
            JsonObject obj = getPlayerInfo(p);

//        Bukkit.getLogger().info("服务器输出数据：" + hc.get(params));
            int res = obj.get("nmb").getAsInt();
            return  res;
        }catch (Exception e){
            return 0;
        }
    }


    public void disableInvisiable(){
        Collection<? extends Player> olp = Bukkit.getOnlinePlayers();
        //getLogger().info("test!!!!");
        for(Player p : olp) {
//            getLogger().info(String.valueOf(p.isInvisible()));
            if (p.isInvisible() && !p.isOp()) {
//                p.sendMessage("You has been appeared");
                Location footpos = p.getLocation();

                World wd = footpos.getWorld();
                wd.spawnParticle(Particle.FLAME , p.getEyeLocation(),
                        1 ,
                        0.01,
                        0.01,
                        0.01,
                        0.01);
                wd.spawnParticle(Particle.GLOW ,
                        footpos,//new Location(wd , footpos.getX() , footpos.getY()+1.6, footpos.getZ()) ,
                        10 ,
                        0.1,
                        1.1,
                        0.1,
                        3);
                p.sendTitle(ChatColor.RED+"服务器禁用隐身!" , ChatColor.RED+"请卸下隐身斗篷等物品！",0,10,0);

                PotionEffect slowEffect = new PotionEffect(
                        PotionEffectType.SLOW,  // 效果类型
                        20,                     // 持续20刻（1秒）
                        20,                       // II级（amplifier=1）
                        true,                    // 环境效果
                        true,                    // 显示粒子
                        true                     // 显示图标
                );
                PotionEffect jumplessEffect = new PotionEffect(
                        PotionEffectType.JUMP,  // 效果类型
                        20,                     // 持续20刻（1秒）
                        249,                       // 级
                        true,                    // 环境效果
                        true,                    // 显示粒子
                        true                     // 显示图标
                );
                p.setGliding(false);
                p.setSprinting(false);
                p.removePotionEffect(PotionEffectType.INVISIBILITY);
                p.addPotionEffect(slowEffect);
                p.addPotionEffect(jumplessEffect);
//                for(Player otherp : olp){
//                    otherp.showPlayer(p);
//                }
//                p.setInvisible(false);
//                getLogger().info(p.getName() + " try to be invisible !");
            }
        }
    }

//
//    List<List> tasks =  new CopyOnWriteArrayList<>();

    public void setVauto(Player player , double adb){
        if (player.isGliding()) {
            // 玩家正在使用烟火火箭加速鞘翅飞行


            // 在这里可以修改飞行速度
            double nowv = player.getVelocity().length();
            if(nowv<1){
                nowv=1.51;
            }
            Vector v = player.getLocation().getDirection().multiply(nowv);
//            double maxTime = getTime() + 1;
//            BukkitTask bid = Bukkit.getScheduler().runTaskTimer(nmm,this::dealSetVTask, 0L , 1L);
//            tasks.add(Arrays.asList(bid , maxTime , player));
            player.setVelocity(v.multiply(adb)); // 设置更高的速度倍率
        }
    }
//
//    public void dealSetVTask(){
//        for(List ls : tasks){
//            double maxtime = (double) ls.get(0);
////            BukkitTask bid = (BukkitTask) ls.get(0);
//            if(getTime()>=maxtime){
////                bid.cancel();
//                tasks.remove(ls);
//                Bukkit.getLogger().info(maxtime+" is cancelled");
//            }else{
//                try {
//                    Collection<? extends Player> olp = Bukkit.getOnlinePlayers();
//                    //getLogger().info("test!!!!");
//                    Player player = (Player) ls.get(1);
//                    for (Player p : olp) {
//                        if (p.getUniqueId() == player.getUniqueId()) {
//                            setVauto(p);
//                        }
//                    }
//                }catch (Exception e){
//                    Bukkit.getLogger().info(
//                            e.getMessage()
//                    );
//                }
//
//            }
//        }
////        Collection<? extends Player> olp = Bukkit.getOnlinePlayers();
////        //getLogger().info("test!!!!");
////        for (Player p : olp) {
////            if (p.isSneaking()) {
////                setVauto(p);
////            }
////        }
//    }
//
//    public void addATask(List lst){
//        tasks.add(lst);
//    }
//


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

        getAutoRefreshData();


//        Bukkit.getLogger().info(gson.toJson(params));
    }
    public static JsonArray getData(){
//        Gson gson = new Gson();
//        httpClient hc = new httpClient("http://139.224.250.35:666/mc/nm.php");
//        Map<String, String> params = new HashMap<>();
//        params.put("type" , "data");
//        JsonArray js = gson.fromJson(hc.get(params),JsonArray.class);
//        return js;
        return shopData;
    }

    public static JsonArray getGemData(){
        return gem_data;
    }

    public static JsonArray getGemlegal(){return gemlegal;}




    public static double getTax(){//此处获取的tax是直接用于乘售价的部分，并不需要1-
        try {
            httpClient hc = new httpClient("http://139.224.250.35:666/mc/nm.php");
            Map<String, String> params = new HashMap<>();
            params.put("type", "get_tax");
//        Bukkit.getLogger().info("服务器输出数据：" + hc.get(params));
            //Integer.parseInt(hc.get(params));
            return Double.parseDouble(hc.get(params));
        }catch (Exception e){
            return 0.1;
        }
    }
}
