package mainFesht3.niuMaManager.Vault;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import mainFesht3.niuMaManager.NiuMaManager;
import mainFesht3.niuMaManager.Utils.httpClient;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;

import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.inventory.meta.ItemMeta;

public class shopProgress {
    //处理购买命令
    //由此创建ui（等写好以后。。。）
    Player player;
    String item_name;
    int num ;
//    Gson gson = new Gson();
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
    // 获取玩家物品栏中完全空的格子数量
    public static int getEmptySlots(Player player) {
        Inventory inventory = player.getInventory();
        return inventory.firstEmpty(); // 返回第一个空槽的索引，如果没有则返回-1
    }
    // 计算物品栏中指定物品的剩余可堆叠容量
    public static int getRemainingCapacity(Player player, ItemStack item) {
        Inventory inventory = player.getInventory();
        int maxStackSize = item.getMaxStackSize();
        int remaining = 0;

        // 1. 检查已存在的同类型物品的堆叠空间
        for (ItemStack stack : inventory.getContents()) {
            if (stack != null && stack.isSimilar(item)) {
                remaining += maxStackSize - stack.getAmount();
            }
        }

        // 2. 加上空槽位的最大堆叠数量
        int emptySlots = getEmptySlots(player);
        if (emptySlots > 0) {
            remaining += emptySlots * maxStackSize;
        }

        return remaining;
    }

    public ItemStack createSpecialItem(Material material , JsonObject nbt_meta){

        ItemStack item = new ItemStack(material);

        NBTItem nbt = new NBTItem(item);
        nbt.setInteger("special_id",nbt_meta.get("special_id").getAsInt());
        nbt.setString("special_type" , nbt_meta.get("special_type").getAsString());
        item = nbt.getItem();

        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(nbt_meta.get("displayName").getAsString());
//
        List<String> loreList = gson.fromJson(nbt_meta.get("lore").getAsJsonArray() , List.class);

        meta.setLore(loreList);

        JsonArray enchantList = nbt_meta.get("enchant").getAsJsonArray();
        for(JsonElement endata : enchantList){
            JsonArray ed = endata.getAsJsonArray();
            NamespacedKey key = NamespacedKey.minecraft(ed.get(0).getAsString());
            Enchantment enchantment = Enchantment.getByKey(key);
            int level = ed.get(1).getAsInt();
            meta.addEnchant(enchantment , level,true);

        }

//        // 方法 1：遍历转换（适合简单类型）
//        List<String> stringList = new ArrayList<>();
//        for (Object obj : rawList) {
//            stringList.add(String.valueOf(obj)); // 确保 Object 可转为 String
//        }
        item.setItemMeta(meta);
        return item;
    }


    Gson gson = new Gson();
    httpClient hc = new httpClient("http://139.224.250.35:666/mc/nm.php");

    public boolean buy(){
        //代号型返回（ 以后）
        //0为其他错误 1为成功 2为余额不足
        Map<String, String> params = new HashMap<>();
        params.put("name" , NiuMaManager.getPlayerNiuMaServerAccount(player));
        params.put("type" , "buy");
        params.put("type2" , item_name);

        if( !item_name.equals("nmc") ) {
            try {
                ItemStack item = new ItemStack(Material.valueOf(item_name.toUpperCase()));
                int left = getRemainingCapacity(player, item);
//            Bukkit.getLogger().info("left :==== "+left);
                if (num > left) {
                    num = left;
                    player.sendMessage("§4你的存储空间不足！已为您自动调整数量为" + num + "个");
                }
            } catch (Exception e) {
//                player.sendMessage("在处理您的命令时出现问题，请联系腐竹，异常如下：" + e);
//                return false;
            }
        }
        params.put("type3" , ""+num);
        String res = hc.get(params);
        JsonObject obj = gson.fromJson(res, JsonObject.class);
        int total = num;
        if(!obj.get("result").getAsBoolean()){
            player.sendMessage("§4购买出现了错误，也许你的牛马币余额不足? 可以发送/nmb查看！具体原因：\n"+obj.get("reason").getAsString());
        }else{
            if(item_name.equals("nmc")){
                //nmc add
                Economy eco = NiuMaManager.getEconomy();
                eco.depositPlayer(player , num*10.0d);

            }else {
                JsonObject raw_data = obj.get("raw").getAsJsonObject();
                Material ms;
                ItemStack newitem;
//                Bukkit.getLogger().info(gson.toJson(raw_data));
                if(raw_data.get("special").getAsBoolean()){

                    ms = Material.valueOf(raw_data.get("item_type").getAsString().toUpperCase());
                    newitem = createSpecialItem(ms,raw_data.getAsJsonObject("meta"));
                }else {
                    ms = Material.valueOf(item_name.toUpperCase());
                    newitem = new ItemStack(ms, num);
                }
                int max = newitem.getMaxStackSize();
                if (num > max) {
                    while (num / max > 0) {
                        newitem = new ItemStack(ms, max);
                        player.getInventory().addItem(newitem);
                        num -= max;
                    }
                    newitem = new ItemStack(ms, num);
                    player.getInventory().addItem(newitem);
                } else {
                    player.getInventory().addItem(newitem);
                }
            }

            player.sendMessage(obj.get("context").getAsString());//"§l§a订单完成！您成功购买了"+total+"个"+item_name);
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
                player.getInventory().removeItem(new ItemStack(item_material, obj.get("ic").getAsInt()));
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
