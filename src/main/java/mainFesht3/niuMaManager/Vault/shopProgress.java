package mainFesht3.niuMaManager.Vault;

import com.google.gson.Gson;
import mainFesht3.niuMaManager.NiuMaManager;
import mainFesht3.niuMaManager.Utils.httpClient;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class shopProgress {
    //处理购买命令
    //由此创建ui（等写好以后。。。）
    Player player;
    String item_name;
    int num ;
    public shopProgress(Player player , String item_name , int num){
        this.player = player;
        this.item_name = item_name;
        this.num = num;
    }


    Gson gson = new Gson();
    httpClient hc = new httpClient("http://139.224.250.35:666/mc/nm.php");

    public boolean buy(){
        //代号型返回（ 以后）
        //0为其他错误 1为成功 2为余额不足
        Map<String, String> params = new HashMap<>();
        params.put("name" , NiuMaManager.getPlayerNiuMaServerAccount(player));
        params.put("type" , "buytype");
        params.put("type2" , item_name);
        params.put("type3" , ""+num);
        String res = hc.get(params);
        int total = num;
        if(res.equals("none")){
            player.sendMessage("§4出现了错误，也许你购买的东西并不存在");
        }else if(res.equals("false")){
            player.sendMessage("§4出现了错误，也许你的牛马币余额不足，可以发送/nmb查看！");
        }else if(res.equals(NiuMaManager.getPlayerNiuMaServerAccount(player))){
            Material ms = Material.valueOf(item_name.toUpperCase());
            ItemStack newitem = new ItemStack(ms);
            int max = newitem.getMaxStackSize();
            if( num > max ) {
                while (num/max > 0) {
                    newitem = new ItemStack(ms , max);
                    player.getInventory().addItem(newitem);
                    num -= max;
                }
                newitem = new ItemStack(ms , num);
                player.getInventory().addItem(newitem);
            }

            player.sendMessage("§l§a订单完成！您成功购买了"+total+"个"+newitem.getItemMeta().getDisplayName());
        }



        return false;
    }


    public void change_num(int num){
        this.num = num;
    }
    public void change_item(String item_name){
        this.item_name = item_name;
    }
}
