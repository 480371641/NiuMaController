package mainFesht3.niuMaManager;
import mainFesht3.niuMaManager.Utils.httpClient;
import org.bukkit.Bukkit ;
import org.bukkit.entity.Player;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import java.util.Map;
import java.util.HashMap;
import java.time.Instant;
import  org.bukkit.ChatColor;

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
//    public void onTick() {
//        ChatColor.BOLD 加粗
//    }



    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player p = event.getPlayer();
        nmm.randomtTpTime.put(p.getName() , 0.0);//add map
        event.setJoinMessage(ChatColor.GREEN +"[NMManager]"+ChatColor.RESET+" 欢迎 " + p.getName() + " 进入服务器！");
        String[] s={"user" , p.getName() , "parent" , "add" , getDefaultGroup(p.getName())};
        NiuMaManager.runStaticCmd("lp" ,s );
//        p.getUniqueId();
//        httpClient hc = new httpClient("http://139.224.250.35:666/mc/nm.php");
//        Map<String, String> params = new HashMap<>();
//        params.put("type" , "checknmb");
//        params.put("name" , NiuMaManager.getPlayerNiuMaServerAccount(p));
//        Bukkit.getLogger().info("服务器输出数据：" + hc.get(params));
//        String res = hc.get();
//        Bukkit.broadcastMessage("juess");
    }
}
