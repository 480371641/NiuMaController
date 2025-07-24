package mainFesht3.niuMaManager;
import com.mohistmc.api.event.BukkitHookForgeEvent;
import com.mohistmc.forge.ForgeEventHandler;
import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.NBTItem;
import mainFesht3.niuMaManager.Utils.httpClient;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.Event.Result;

import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.net.http.HttpConnectTimeoutException;
import java.util.*;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.ServerLoadEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
//import org.bukkit.

public class EventListener implements Listener {
    NiuMaManager nmm ;
    public  EventListener (NiuMaManager nmm){
        this.nmm = nmm;
    }
    public  EventListener (){}

    public String getDefaultGroup(String name ){
        return "default";
    }
    public double getTime(){
        return  (double) Instant.now().toEpochMilli() /1000;
//        System.out.println("毫秒级时间戳: " + timestampMillis);
    }


//    @EventHandler
//    public void onServerDone(ServerLoadEvent event){
//        Bukkit.getLogger().info("start register Forge Event!!!!!!!!!!!!!!!!!!!!!!!!");
//        // 获取Forge的类加载器（负责加载模组类）
//        ClassLoader forgeClassLoader = MinecraftForge.class.getClassLoader();
//
//        try {
//            ForgeEvent listenerInstance = new ForgeEvent();
//            MinecraftForge.EVENT_BUS.register(listenerInstance);
//            Bukkit.getLogger().info("监听器已通过Forge类加载器注册");
//        } catch (Exception e) {
//            Bukkit.getLogger().severe("Forge类加载器注册失败：" + e.getMessage());
//            e.printStackTrace();
//        }
//    }


//    @EventHandler
//    public void onTest(BukkitHookForgeEvent event) {
//
//        if(event.getEventName().equals("GunShootEvent")) {
//            Bukkit.getLogger().info(event.getEventName());
//            Bukkit.getLogger().info("shoot!!");
////            event.getEvent().setCanceled(true);
//        }
////        Result s = event.getEvent().getResult();
////        Bukkit.getLogger().info(s.getShooter().getName());
////        event.getPlayer().sendMessage(event.getEventName());
//    }


    @EventHandler
    public void onAnvil(PrepareAnvilEvent event) {
        try {
            ItemStack result = event.getResult();  // 获取系统计算的初始输出
            AnvilInventory anvilInv = event.getInventory();
            ItemStack bookItem = anvilInv.getItem(1);
            if (result != null  && bookItem != null) {
                NBTItem nbt = new NBTItem(bookItem);
                if("enchanted_book".equals(nbt.getString("special_type"))) {
                    ItemMeta meta = result.getItemMeta();
                    EnchantmentStorageMeta esm = (EnchantmentStorageMeta) bookItem.getItemMeta();
                    Map<Enchantment, Integer> storedEnchants = esm.getStoredEnchants();
                    for (Map.Entry<Enchantment, Integer> entry : storedEnchants.entrySet()) {
                        Enchantment enchant = entry.getKey();
                        int level = entry.getValue();
                        meta.addEnchant(enchant, level, true); // 强制附加
                    }

                    result.setItemMeta(meta);

                    event.setResult(result);  // 修改最终输出
                }
            }
        }catch (Exception e){
            event.setResult(null);
        }
    }

//    @EventHandler
//    public void onTick() {
//        ChatColor.BOLD 加粗
//    }
    httpClient hc = new httpClient("http://139.224.250.35:666/mc/nm.php");

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event){
        try {
            Player player = event.getPlayer();
            String msg = event.getMessage();
            Map<String, String> params = new HashMap<>();
            params.put("account", NiuMaManager.getPlayerNiuMaServerAccount(player));
            params.put("name" , player.getName());
            params.put("type", "chat");
            params.put("type2", msg);
            String res = hc.get(params);
            if(res.equals("cantchat")){
                player.sendMessage("You have been baned!");
            }else {
                Bukkit.broadcastMessage(res);
            }
            event.setCancelled(true);//baned raw message
        }catch (Exception e){
            Bukkit.getLogger().info("errors from geting url :\n"+e);
//            pass
        }

    }


    Map<String,Double> pls = new ConcurrentHashMap<>();
    double cooldown = 3.5;
    // 监听烟花火箭发射事件
    @EventHandler
    public void onFireworkLaunch(ProjectileLaunchEvent event) {
        // 检查发射的实体是否为烟花火箭
        if (event.getEntityType() == EntityType.FIREWORK) {
            Entity firework = event.getEntity();
            Player player = (Player) event.getEntity().getShooter();
            ItemStack chestplate = player.getInventory().getChestplate();

            NBTItem nbt = new NBTItem(chestplate);
//            Bukkit.getLogger().info(nbt.getString("special_type"));
//            Bukkit.getLogger().info(nbt.getInteger("special_id")+"");
            // 检查物品是否为鞘翅
            if (chestplate != null && chestplate.getType() == Material.ELYTRA && Objects.equals(nbt.getString("special_type"), "elytra")) {

                Double last_atime = pls.get(player.getName());
                if(last_atime==null) {
                    pls.put(player.getName(), 0.0);
                    last_atime = 0.0;
                }
                // 检查玩家是否正在使用鞘翅飞行
                if (player.isGliding() && getTime() >last_atime) {
                    // 玩家正在使用烟火火箭加速鞘翅飞行
                    player.sendTitle("", ChatColor.BLUE+"使用技能加速飞行!冷却" + cooldown + "秒！" , 0 , 20 , 0);
                    pls.put(player.getName(), getTime() + cooldown);
                    // 在这里可以修改飞行速度
    //                    Vector v = player.getVelocity();
    //                    double maxTime = getTime() + 4.5;

                    nmm.setVauto(player, 8.5);
                    event.setCancelled(true);
    //                    player.setVelocity(v.multiply(3.0)); // 设置更高的速度倍率
                }

            }
        }

    }



    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player p = event.getPlayer();
        nmm.randomtTpTime.put(p.getName() , 0.0);//add map
        event.setJoinMessage(ChatColor.GREEN +"[NMManager]"+ChatColor.RESET+" 欢迎 " + p.getName() + " 进入服务器！");
        String[] s={"user" , p.getName() , "parent" , "add" , getDefaultGroup(p.getName())};
        NiuMaManager.runStaticCmd("lp" ,s );

//        Map<String, String> params = new HashMap<>();
//        params.put("name" , p.getName());
//        params.put("type" , "checknmb");
//        params.put("name" , NiuMaManager.getPlayerNiuMaServerAccount(p));
////        Bukkit.getLogger().info("服务器输出数据：" + hc.get(params));
//        String res = hc.get(params);
//        Bukkit.broadcastMessage("juess");
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event){
//        Player p = event.getPlayer();
        event.setQuitMessage("有混蛋偷偷溜走了哦......");
    }

}
