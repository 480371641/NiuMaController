package mainFesht3.niuMaManager;

import mainFesht3.niuMaManager.Utils.RandomUtils;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.lang.Object;

import org.bukkit.block.Block;

public class RandomTp implements CommandExecutor {
    int rand = 30000;
    double cd = 60.0;
    NiuMaManager nmm ;
    BukkitTask task; // 保存任务引用
    Player[] waitingPlayerList = {};


    public  RandomTp (NiuMaManager nmm){
        this.nmm = nmm;
        task = Bukkit.getScheduler().runTaskTimer(nmm, this::getLandPos , 0L , 10L);
    }

    public double getTime(){
        return  (double) Instant.now().toEpochMilli() /1000;
//        System.out.println("毫秒级时间戳: " + timestampMillis);
    }

    public Player[] removeArrayValue(Player[] arr, int index){
        List<Player> list = new ArrayList<>(Arrays.asList(arr));
        list.remove(index);
        return list.toArray(new Player[0]);
    }

    public Player[] addArrayValue(Player[] arr , Player news){
        List<Player> list = new ArrayList<>(Arrays.asList(arr));
        list.add(news);
        return list.toArray(new Player[0]);
    }

    private void getLandPos(){
//        Bukkit.getLogger().info(""+waitingPlayerList.length);
        try{
            for(int i = 0 ; i< waitingPlayerList.length ; i++){
                Player p = waitingPlayerList[i];
                Location pos = p.getLocation();
                for(int j = 300 ; j >-64 ; j--){
                    Location newPos = new Location(p.getWorld(),pos.getX() , j , pos.getZ());
                    Block block =  newPos.getBlock();
    //                Bukkit.getLogger().info(""+block.getType());
                    if( !block.getType().isAir() ) {
                        //找到最近的非空气方块
                        // 创建抗性6效果，持续30秒（600刻）
                        PotionEffect resistance = new PotionEffect(
                                PotionEffectType.DAMAGE_RESISTANCE, // 效果类型：抗性
                                20 * 4,                                 // 持续时间（20刻=1秒）
                                5,                                   // 等级（注意：等级0是效果I，所以5是效果VI）
                                true,                               // 是否显示粒子效果
                                true                                // 是否显示图标
                        );

                        // 应用效果
                        p.addPotionEffect(resistance);


                        p.sendMessage(ChatColor.GREEN + "已传送成功！");
                        p.sendTitle(ChatColor.GREEN + "" + ChatColor.BOLD + "传送成功！", ChatColor.GREEN + "...您已到站...", 10, 80, 10);
                        newPos.setY(j+2);
                        p.teleport(newPos);
                        waitingPlayerList = removeArrayValue(waitingPlayerList, i);
                    }
                }
            }
        }catch (Exception e){
            Bukkit.getLogger().info(e+"");
        }
    }



    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        // 判断执行者类型（玩家/控制台）
        if (!(sender instanceof Player)) {
            sender.sendMessage("该指令只能由玩家执行！");
            return true;
        }

        Player player = (Player) sender;
        RandomUtils utls = new RandomUtils();
        // 处理指令参数
        if (args.length == 0 ) {
            // 无参数时
//            Bukkit.getLogger().info("Start ::"+player.getName()+" "+nmm.getRTT(player.getName()) );
            if(nmm.getRTT(player.getName()) + cd < getTime()) {

                if (player.isOp()) {
                    nmm.setRTT(player.getName(), 0.0);
                    player.sendMessage(ChatColor.DARK_RED + "" + ChatColor.BOLD + "OP操作，CD已重置");
                } else {
                    nmm.setRTT(player.getName(), getTime());
                    player.sendMessage(ChatColor.RED  + " 此功能进入60秒冷却时间");
                }

//                Bukkit.getLogger().info("End ::"+player.getName()+" "+nmm.getRTT(player.getName()) );
                Location pos = player.getLocation();
                Location newPos = new Location(player.getWorld(),pos.getX() + utls.randomInt(-rand, rand) , 1000.0, pos.getZ() + utls.randomDouble(-rand, rand));
                player.sendMessage(ChatColor.RED + "正在为您传送........");
                player.sendTitle(ChatColor.DARK_BLUE + "===== 正在为您传送 =====", ChatColor.BLUE + "请勿做其他动作，已将您列入排列区", 10, 80, 10);

                player.teleport(newPos);
//
//                int chunkX = (int)(pos.getX() + utls.randomInt(-rand, rand)) >> 4;// = /16
//                int chunkZ = (int)(pos.getZ() + utls.randomInt(-rand, rand)) >> 4;// = /16
//                int loadRadius = 2;//预加载周围
//                World world = pos.getWorld();;
//                player.teleport(newPos);
//                Bukkit.getScheduler().runTaskAsynchronously(nmm,()->{
//                    try {
//                        // 异步加载目标区块及周围区域
//                        for (int x = chunkX - loadRadius; x <= chunkX + loadRadius; x++) {
//                            for (int z = chunkZ - loadRadius; z <= chunkZ + loadRadius; z++) {
//                                world.getChunkAt(x, z, true).load();
//                            }
//                        }
//
//                        // 区块加载完成后切回主线程传送
//                        Bukkit.getScheduler().runTask(nmm, () -> {
//                            // 二次验证区块是否加载
//                            if (world.isChunkLoaded(chunkX, chunkZ)) {
//                                player.teleport(findSafeLocation(randomLoc));
//                                player.sendMessage("§a传送成功！");
//                            } else {
//                                player.sendMessage("§c区块加载失败，请重试");
//                            }
//                        });
//                    } catch (Exception e) {
//                        // 异常处理
//                        player.sendMessage("§c传送过程中发生错误");
//                        e.printStackTrace();
//                    }
//                });



                waitingPlayerList = addArrayValue(waitingPlayerList , player);
//              public abstract void sendTitle(
//                    @Nullable String title,     // 主标题文本
//                    @Nullable String subtitle,  // 副标题文本
//              int fadeIn,                 // 淡入时间（Ticks）
//              int stay,                   // 停留时间（Ticks）
//              int fadeOut                 // 淡出时间（Ticks）
//              );

//            player.sendMessage(ChatColor.GREEN + "===== 指令帮助 =====");
//            player.sendMessage(ChatColor.YELLOW + "/" + label + " help " + ChatColor.GRAY + "显示帮助");
//            player.sendMessage(ChatColor.YELLOW + "/" + label + " reload " + ChatColor.GRAY + "重载插件");
            }else{
                player.sendMessage(ChatColor.RED + "您操作太过频繁了哦！剩余等待时间：" + (cd- getTime() + nmm.randomtTpTime.get(player.getName()) ) +"s");
            }
            return true;
        }

//        String subCommand = args[0].toLowerCase();
//
//        // 处理子指令
//        switch (subCommand) {
//            case "help":
//                player.sendMessage(ChatColor.GREEN + "无需参数，输入指令即可在服务器随机畅游！");
//                break;
//            default:
//                player.sendMessage(ChatColor.RED + "未知子指令，请使用 /" + label + " help");
//        }

        return true;
    }
}
