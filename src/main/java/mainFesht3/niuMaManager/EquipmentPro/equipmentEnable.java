package mainFesht3.niuMaManager.EquipmentPro;



import com.google.gson.JsonElement;
import mainFesht3.niuMaManager.NiuMaManager;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

public class equipmentEnable {
    //本类用于记录所有插件承认的可操作的装备模板
    //如合金胸甲等基础胸甲
//     static final List<String> legalType = Arrays.asList(
//
//    );
     //改用网络数据，，！！！！！！！！！！！！

    /**
     * 用于确认即将被强化的物品是否合法，被插件认可
     * @param item 确认的物品
     * @return
     */
     public static boolean getEquipmentLegal(ItemStack item){
         for(JsonElement element : NiuMaManager.getGemlegal()) {
             if (item.getType().toString().equals(element.getAsString().toUpperCase())){
                 return true;
             }
         }
         return false;
     }
}
