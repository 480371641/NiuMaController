package mainFesht3.niuMaManager.Vault;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.iface.ReadWriteItemNBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
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

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.checkerframework.checker.units.qual.C;


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
    /**
     * 替换背包中一个匹配的物品
      */
    public static void replaceItemsInInventory(Inventory inventory,
                                               ItemStack targetItem,
                                               ItemStack newItem) {
        // 遍历背包所有槽位
        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack item = inventory.getItem(i);
            // 比较物品是否匹配（考虑类型和数量）
            if (item != null && item.isSimilar(targetItem)) {
                inventory.setItem(i, newItem);
                break;//只替换一次
            }
        }
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
        ItemStack[] items = inventory.getStorageContents();
        int empty = 0;
        for(ItemStack item : items){
            if (item == null || item.getType() == Material.AIR) {
                empty++;
            }
        }
//        player.sendMessage("你有"+empty+"个空槽位");
        return empty; // 返回第一个空槽的索引，如果没有则返回-1
    }
    // 计算物品栏中指定物品的剩余可堆叠容量
    public static int getRemainingCapacity(Player player, ItemStack item) {
        Inventory inventory = player.getInventory();
        int maxStackSize = item.getMaxStackSize();
        int remaining = 0;

        // 1. 检查已存在的同类型物品的堆叠空间
        for (ItemStack stack : inventory.getContents()) {
            if (stack != null && stack.isSimilar(item) && (!player.getInventory().getItemInOffHand().isSimilar(item))) {
                remaining += maxStackSize - stack.getAmount();
            }
        }

        // 2. 加上空槽位的最大堆叠数量
        int emptySlots = getEmptySlots(player);
        if (emptySlots > 0) {
            remaining += emptySlots * maxStackSize;
        }
//        player.sendMessage("你有"+remaining+"个余量");
        return remaining;
    }
    // 计算物品栏中指定物品的堆叠数量（支持special物品）
    public static int getItemNum(Player player, ItemStack item) {
        Inventory inventory = player.getInventory();
//        int maxStackSize = item.getMaxStackSize();
        int remaining = 0;

        // 1. 检查已存在的同类型物品的堆叠空间
        for (ItemStack stack : inventory.getContents()) {
            if (stack != null && stack.isSimilar(item) && (!player.getInventory().getItemInOffHand().isSimilar(item)) ) {
                remaining += stack.getAmount();
            }
        }
//        player.sendMessage("你有"+remaining+"个item");
        return remaining;
    }

    /**
     *仅服务器装载了TaczMod可用
     * @param name 完整名称，如minecraft:diamond
     * @return 返回生成好的item对象s
     */
    @Deprecated
    public static ItemStack getGunItem(String name , Map<String , Object> other){
//        ItemStack item = new ItemStack(Material.getMaterial("modern_kinetic_gun".toUpperCase()));
//        NBTItem nbt = new NBTItem(item);
////        nbt.setString("id","tacz:modern_kinetic_gun");
////        nbt.addCompound()
//        nbt.addCompound("tag").setString("GunId",name);
//        if(!other.isEmpty()){
//
//        }
//        item = nbt.getItem();
        ReadWriteNBT rwnbt = NBT.createNBTObject();
        rwnbt.setString("id" ,"tacz:modern_kinetic_gun" );
        rwnbt.getOrCreateCompound("tag").setString("GunId",name);
        ItemStack item = NBT.itemStackFromNBT(rwnbt);
        return item;
    }

    public static ItemStack createSpecialItem(ItemStack item , JsonObject nbt_meta){
        Gson gson = new Gson();
//        ItemStack item = new ItemStack(material);

        if(nbt_meta.has("special_id")&&nbt_meta.has("special_type")) {
            NBTItem nbt = new NBTItem(item);
            nbt.setInteger("special_id", nbt_meta.get("special_id").getAsInt());
            nbt.setString("special_type", nbt_meta.get("special_type").getAsString());

            item = nbt.getItem();
        }
        if(nbt_meta.has("gem_list")){
            NBTItem nbt = new NBTItem(item);
            JsonArray gem_list = nbt_meta.getAsJsonArray("gem_list");
            int[] gemList = new int[gem_list.size()];
            for (int i = 0; i < gem_list.size(); i++) {
                gemList[i] = gem_list.get(i).getAsInt();
            }
            nbt.setIntArray("gem_list", gemList);
            item = nbt.getItem();
        }

        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(nbt_meta.get("displayName").getAsString());
//
        if(nbt_meta.has("lore")) {
            List<String> loreList = gson.fromJson(nbt_meta.get("lore").getAsJsonArray(), List.class);

            meta.setLore(loreList);
        }

        if(nbt_meta.has("StoredEnchantments")) {
            JsonArray enchantList = nbt_meta.get("StoredEnchantments").getAsJsonArray();
            if(item.getType() == Material.ENCHANTED_BOOK) {
                EnchantmentStorageMeta esm = (EnchantmentStorageMeta) meta;
                for (JsonElement endata : enchantList) {
                    JsonArray ed = endata.getAsJsonArray();
                    NamespacedKey key = NamespacedKey.minecraft(ed.get(0).getAsString());
                    Enchantment enchantment = Enchantment.getByKey(key);
                    int level = ed.get(1).getAsInt();
                    esm.addStoredEnchant(enchantment,level,true);
//                    meta.addEnchant(enchantment, level, true);

                }
            }
        }

        if(nbt_meta.has("Enchantments")) {
            JsonArray enchantList = nbt_meta.get("Enchantments").getAsJsonArray();
            for (JsonElement endata : enchantList) {
                JsonArray ed = endata.getAsJsonArray();
                NamespacedKey key = NamespacedKey.minecraft(ed.get(0).getAsString());
                Enchantment enchantment = Enchantment.getByKey(key);
                int level = ed.get(1).getAsInt();

                meta.addEnchant(enchantment, level, true);

            }
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
        Map<String, String> params = new ConcurrentHashMap<>();
        params.put("name" , NiuMaManager.getPlayerNiuMaServerAccount(player));
        params.put("type" , "buy");
        params.put("type2" , item_name);

        if( !item_name.equals("nmc") ) {
            try {
                ItemStack item = new ItemStack(Material.valueOf(item_name.toUpperCase()));
                int left = getRemainingCapacity(player, item);
            Bukkit.getLogger().info("num :==== "+num);
                if (num > left) {
                    num = left;
                    player.sendMessage("§4你的存储空间不足！已为您自动调整数量为" + left + "个");
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
                ItemStack raw_item;
//                Bukkit.getLogger().info(gson.toJson(raw_data));
                if(raw_data.get("special").getAsBoolean()){
                    raw_item = new ItemStack( Material.getMaterial(raw_data.get("item_type").getAsString().toUpperCase()) );
                    newitem = createSpecialItem(raw_item ,raw_data.getAsJsonObject("meta"));
                }else {
                    ms = Material.valueOf(item_name.toUpperCase());
                    newitem = new ItemStack(ms, num);
                }
                int max = newitem.getMaxStackSize();
                Bukkit.getLogger().info("max:"+max +"  num :"+num);

                if (num > max) {
                    while (num / max > 0) {
                        newitem.setAmount(max);
                        player.getInventory().addItem(newitem);
                        num -= max;
                    }
                    newitem.setAmount(num);
                    player.getInventory().addItem(newitem);
                } else {
                    newitem.setAmount(num);
                    player.getInventory().addItem(newitem);
                }
            }

            player.sendMessage(obj.get("context").getAsString());//"§l§a订单完成！您成功购买了"+total+"个"+item_name);
            return true;
        }



        return false;
    }


    public boolean re(){
        Map<String, String> params = new ConcurrentHashMap<>();
        params.put("name" , NiuMaManager.getPlayerNiuMaServerAccount(player));
        params.put("type" , "re");
        params.put("type2" , item_name);

        ItemStack item = new ItemStack(Material.AIR);
        // auto count item num !!!!!!!!!!!! === all
        ItemStack raw_item ;
        JsonArray allthingtype = NiuMaManager.getData();
        try{
            for (JsonElement ele : allthingtype) {
                String item_name1 = ele.getAsJsonObject().get("item_name").getAsString();
                Bukkit.getLogger().info("start match " + item_name1);
                if (item_name1.equals(item_name)) {
                    Bukkit.getLogger().info("matched " + item_name);
                    JsonObject raw_data = ele.getAsJsonObject();
                    if (raw_data.get("special").getAsBoolean()) {
                        raw_item = new ItemStack(Material.valueOf(raw_data.get("item_type").getAsString().toUpperCase()));
                        item = createSpecialItem(raw_item, raw_data.getAsJsonObject("meta"));
                    } else {
                        item = new ItemStack(Material.valueOf(raw_data.get("item_name").getAsString().toUpperCase()));
                    }
                    if(num == -1) {
                        num = getItemNum(player, item);
                    }
                    Bukkit.getLogger().info("num" + num);
                    break;
                }
            }
        }catch (Exception e){
//            item = new ItemStack(Material.AIR);
            Bukkit.getLogger().info(e+"");
        }

        params.put("type3" , ""+num);
//        Material item_material = Material.valueOf(item_name.toUpperCase());
//        ItemStack mainhand = player.getInventory().getItemInMainHand();
//
//        player.sendMessage(""+countItemsInInventory(player , mainhand.getType()));
//        if(item.getType() == Material.AIR){
//            player.sendMessage("§4订单交易失败\n未找到你想交易的物品！");
//            return false;
//        }

        Bukkit.getLogger().info("reing");
        int have = getItemNum(player, item);
        if(have < num){
            player.sendMessage("§4订单交易失败\n你的物品栏中并没有足够的物品可以回收！");
            return false;
        }else {
            String res = hc.get(params);
            JsonObject obj = gson.fromJson(res, JsonObject.class);

            if (obj.get("result").getAsBoolean()) {
                item.setAmount(obj.get("ic").getAsInt());

                player.getInventory().removeItem(item);//new ItemStack(item_material, obj.get("ic").getAsInt()));
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
