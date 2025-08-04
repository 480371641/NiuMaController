package mainFesht3.niuMaManager.EquipmentPro;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import mainFesht3.niuMaManager.EquipmentPro.signs.gemInstaller;
import mainFesht3.niuMaManager.NiuMaManager;
import mainFesht3.niuMaManager.Utils.FastChestGUI;
import mainFesht3.niuMaManager.Vault.shopProgress;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;


public class equipmentGUI {
    /**
     * 此类用于快速创建装配宝石的gui
     *
     * 结构为
     *        title
     * 0          1      2     3   4    5   6 7 8 9
     * 手持装备
     *
     * 10开始为宝石栏，遍历玩家拥有的宝石
     *
     */
    FastChestGUI gui = new FastChestGUI("宝石装配界面（左键装上，右键卸下）" , 54);

    equipmentAPI ep;

    ItemStack item ;

    public equipmentGUI(ItemStack equipment){
        ep = new equipmentAPI(equipment);
        item = equipment;
    }

    /**
     * 判断传入的物品是否合法，也就是说不能是宝石本身传入gui
     * @return
     */
    public boolean isAccess(){
        if (ep != null){
            if(!ep.isGem()){
                return true;
            }
        }
        return false;
    }


    public void createGUI(){
        gui.addItem(0 , item);
        JsonArray array = NiuMaManager.getGemData();
        int gem_id = 0;
        for (int i = 9; i < 9 + array.size(); i++) {
            //i为gui位置！！！！
            JsonObject gemShopData = equipmentAPI.getGemShopData(gem_id);
            gui.addItem(i , shopProgress.createSpecialItem( new ItemStack(Material.valueOf(gemShopData.get("item_type").getAsString().toUpperCase())),gemShopData.getAsJsonObject("meta")));

                    gem_id ++;
        }

        gui.createGUI(new gemInstaller());
    }

    public void openTo(Player player){
        gui.openTo(player);
    }


}