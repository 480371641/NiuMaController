package mainFesht3.niuMaManager.Vault;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
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
    // 使用Spigot API的快捷方法统计特定物品数量
    public static int countItemsInInventory(Player player, Material material) {
        ItemStack itemToCount = new ItemStack(material);
        return player.getInventory().all(material).values().stream()
                .mapToInt(ItemStack::getAmount)
                .sum();
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
            ItemStack newitem = new ItemStack(ms,num);
            int max = newitem.getMaxStackSize();
            if( num > max ) {
                while (num/max > 0) {
                    newitem = new ItemStack(ms , max);
                    player.getInventory().addItem(newitem);
                    num -= max;
                }
                newitem = new ItemStack(ms , num);
                player.getInventory().addItem(newitem);
            }else{
                player.getInventory().addItem(newitem);
            }

            player.sendMessage("§l§a订单完成！您成功购买了"+total+"个"+item_name);
            return true;
        }



        return false;
    }


    public boolean re(){
        Map<String, String> params = new HashMap<>();
        params.put("name" , NiuMaManager.getPlayerNiuMaServerAccount(player));
        params.put("type" , "re");
        params.put("type2" , item_name);
        params.put("type3" , ""+num);
        Material item_material = Material.valueOf(item_name.toUpperCase());
//        ItemStack mainhand = player.getInventory().getItemInMainHand();
//
//        player.sendMessage(""+countItemsInInventory(player , mainhand.getType()));


        int have = countItemsInInventory(player, item_material);
        if(have < num){
            player.sendMessage("§4订单交易失败\n你的物品栏中并没有足够的物品可以回收！");
            return false;
        }else {
            String res = hc.get(params);
            JsonObject obj = gson.fromJson(res, JsonObject.class);
            if (obj.get("result").getAsBoolean()) {
                player.getInventory().removeItem(new ItemStack(item_material, num));
                player.sendMessage("§a订单交易成功！\n" + obj.get("reason").getAsString());
                return true;
            } else {
//            Material ms = Material.valueOf(item_name.toUpperCase());
//            ItemStack newitem = new ItemStack(ms, num);
//            int max = newitem.getMaxStackSize();
//            if (num > max) {
//                while (num / max > 0) {
//                    newitem = new ItemStack(ms, max);
//                    player.getInventory().addItem(newitem);
//                    num -= max;
//                }
//                newitem = new ItemStack(ms, num);
//                player.getInventory().addItem(newitem);
//            } else {
//                player.getInventory().addItem(newitem);
//            }

                player.sendMessage("§4订单交易失败！\n" + obj.get("reason").getAsString());
                return false;
            }
        }
    }

    public void change_num(int num){
        this.num = num;
    }
    public void change_item(String item_name){
        this.item_name = item_name;
    }
}
