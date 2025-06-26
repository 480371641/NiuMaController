package mainFesht3.niuMaManager;
import org.bukkit.Bukkit ;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import java.util.Map;
import java.util.HashMap;


public class EventListener implements Listener {

    public String getDefaultGroup(String name ){
        return "default";
    }


    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player p = event.getPlayer();
        event.setJoinMessage("[NMManager]欢迎 " + p.getName() + " 进入服务器！");
        String[] s={"user" , p.getName() , "parent" , "add" , getDefaultGroup(p.getName())};
        NiuMaManager.runStaticCmd("lp" ,s );
//        p.getUniqueId();
        httpClient hc = new httpClient("http://139.224.250.35:666/mc/nm.php");
        Map<String, String> params = new HashMap<>();
        params.put("type" , "checknmb");
        params.put("name" , NiuMaManager.getPlayerNiuMaServerAccount(p));
        Bukkit.getLogger().info("服务器输出数据：" + hc.get(params));
//        String res = hc.get();
//        Bukkit.broadcastMessage("juess");
    }
}
