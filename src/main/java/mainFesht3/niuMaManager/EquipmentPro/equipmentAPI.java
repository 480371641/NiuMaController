package mainFesht3.niuMaManager.EquipmentPro;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.NBTItem;
import mainFesht3.niuMaManager.NiuMaManager;
import org.bukkit.Bukkit;
import org.bukkit.Tag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

public class equipmentAPI {
    //equipment 分为两部分主要物品
    /**
     *type 1 :
     * 强化宝石  单独的物品，nbt绑定数据为
     * special_type = gem
     * special_id = 0,1,2,3,4,5.............
     * （属性云端定义，通过主线程TTask循环刷新，便于修改数值）
     * <p>
     * type 2:
     * 强化后的装备,绑定物品的nbt标签(Tag)为:
     * gem_list:[0,1,2,3....] //gem_id
     */

    /**
     * Lore描述机制
     * 以“::”双冒号为GemLore专属开头，每一行都附带Gem的名称
     * 例如
     * ::圣羽之心
     */

    ItemStack item;
    NBTItem nbt;

    int maxGem = 3;//每件装备最多安装的Gem数量

    public equipmentAPI(ItemStack item){

        this.item = item;
        nbt = new NBTItem(item);
    }


    /**
     * 0 为无Gem附加的特殊或普通装备 <p>
     * 1 为强化宝石的物品对象 <p>
     * 2 为强化后附带Gem_List的装备 <p>
     */
    public int getEquipmentType(){
        if(nbt.hasTag("special_type")&&nbt.hasTag("special_id")){
            if( (nbt.getString("special_type")).equals("gem") ){
                //Gem item
                //Type = 1
                return 1;
            }
        } else if (nbt.hasTag("gem_list")) {
            //Special item with gem
            //Type = 2
            if(nbt.getIntArray("gem_list").length>0){
                return 2;
            }
        }
        return 0;
    }



    public boolean isGem(){
        return getEquipmentType() == 1;
    }
    public boolean isGemSpecial(){
        return getEquipmentType() == 2;
    }
    public boolean isNormal(){
        return getEquipmentType() == 0;
    }

//    public boolean NormalToGemSpecial(){
//        try{
//            if(isNormal()){
//                NBTItem nbt = new NBTItem(item);
//                int[] gemList = new int[0];
//                nbt.setIntArray("gem_list", gemList);
//                item = nbt.getItem();
//                return true;
//            }else {
//                return false;
//            }
//        }catch (Exception e){
//            Bukkit.getLogger().info("NormalToGemSpecial wrong : "+e);
//            return false;
//        }
//    }

    /**
     * 获取宝石在商店的数据
     * @param gem_id
     * @return
     */
    public static JsonObject getGemShopData(int gem_id){
        JsonArray data = NiuMaManager.getData();
        for(JsonElement element : data){
            if(element.getAsJsonObject().has("special")){
                if(element.getAsJsonObject().has("special_id")){
                    if(element.getAsJsonObject().get("special_id").getAsInt() == gem_id){
                        return element.getAsJsonObject();
                    }
                }
            }
        }
        return null;
    }

    /**
     * 获取宝石的核心数据
     * @param gem_id
     * @return
     */
    public JsonObject getGemData(int gem_id){
        return NiuMaManager.getGemData().get(gem_id).getAsJsonObject();
    }

    /**
     * 用于检查和修复GemSpecial物品的Gem展示lore
     */
    //TODO:
    public void checkAndFixLoreHealth(){
        if(isGem()){return;}////only when the item is GemSpecial.......!!!!
        Bukkit.getLogger().info("start checking........");

        ItemMeta meta = item.getItemMeta();
//        JsonArray gem_data = NiuMaManager.getGemData();

        Bukkit.getLogger().info("normal add");
        if(!meta.hasLore()){
            //don't have lore, start fixing....
            List<String> lore = new ArrayList<>();
            int[] gem_list = nbt.getIntArray("gem_list");

            for(int gem_id : gem_list){
                //working......
                JsonObject raw_data = getGemData(gem_id);
                JsonArray Lores = raw_data.getAsJsonArray("description");
                for(JsonElement element : Lores){
                    lore.add( "->" + element.getAsString());
//                    Bukkit.getLogger().info("add "+element.getAsString());
                }
                //..........
            }

            Bukkit.getLogger().info(lore.toString());
            meta.setLore(lore);
            item.setItemMeta(meta);
        }else{
            //has Lore!
            //check has GemLore?
            List<String> oldlore = meta.getLore();
            List<String> lore = new ArrayList<>();

            for(String l : oldlore){
                if(!l.startsWith("->")){
                    //属于宝石描述类别，先清空再重新生成
                    lore.add(l);
                }
            }
//            lore = newlore;
            Bukkit.getLogger().info("over add");
            int[] gem_list = nbt.getIntArray("gem_list");
            for(int gem_id : gem_list){
                //working......
                JsonObject raw_data = getGemData(gem_id);
                JsonArray Lores = raw_data.getAsJsonArray("description");
                for(JsonElement element : Lores){
                    lore.add( "->" + element.getAsString());

                }
                //..........
            }
            Bukkit.getLogger().info(lore.toString());
            meta.setLore(lore);
            item.setItemMeta(meta);
        }

    }





    public List<Integer> getGemList(){
        if(isGem()){return null;}
        int[] gem_list = nbt.getIntArray("gem_list");
        List<Integer> gemList = new ArrayList<>();
        Bukkit.getLogger().info("gem list get length : " + gem_list.length);

        for(int gem_id : gem_list){
            gemList.add(gem_id);
        }

        return gemList;
    }

    public Object ListToArray(List<Object> list ){
        Object[] arr = new Object[list.size()];
        for (int i = 0; i < list.size() ; i++) {
            arr[i] = list.get(i);
        }
        return arr;
    }


    /**
     *只有GemSpecial物品可以使用此函数！
     * 并不参与Lore的添加与检测，请使用checkAndFixLoreHealth()
     * @param gem_id 要安装的宝石id
     * @return 是否成功安装
     */
    public boolean addGem(int gem_id){
        if(isGem()){return false;}
        if(!equipmentEnable.getEquipmentLegal(item)){return false;}

        List<Integer> gemList = getGemList();
        Bukkit.getLogger().info("gem list length : "+gemList.size());
        if(gemList.size() < maxGem){
            gemList.add(gem_id);
            int[] gem_list = new int[gemList.size()];
//            gem_list = ListToArray(gemList);
            for (int i = 0; i < gemList.size() ; i++) {
                gem_list[i] = gemList.get(i);
            }
            nbt.setIntArray("gem_list" , gem_list);

            return true;
        }

        //do
        return false;
    }


    public boolean removeGem(int gem_id){
        if(isGem()){return false;}
        if(!equipmentEnable.getEquipmentLegal(item)){return false;}
        List<Integer> gemList = getGemList();
        Bukkit.getLogger().info("gem list length : "+gemList.size());
        if(gemList.size() > 0){
            gemList.remove(Integer.valueOf(gem_id));//remove 只会删除一次
            int[] gem_list = new int[gemList.size()];
            for (int i = 0; i < gemList.size() ; i++) {
                gem_list[i] = gemList.get(i);
            }

            if(gem_list.length == 0){
                nbt.removeKey("gem_list");
            }else {
                nbt.setIntArray("gem_list", gem_list);
            }
            return true;
        }

        //do
        return false;
    }


    /**
     * 用于返回最终处理后的物品对象
     * @return 物品对象
     */
    public ItemStack getFinalItem(){
        item = nbt.getItem();
        checkAndFixLoreHealth();



        return item;
    }

}
