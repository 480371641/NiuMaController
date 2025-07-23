package mainFesht3.niuMaManager.Vault;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import de.tr7zw.nbtapi.utils.metrics.json.JsonObjectBuilder;
import mainFesht3.niuMaManager.NiuMaManager;
import mainFesht3.niuMaManager.Vault.signs.Exui;
import mainFesht3.niuMaManager.Vault.signs.Shop;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class GUIEvent implements Listener {

    List<Map<String, Object>> input_player = new ArrayList<>();
    //{
    // "player" : player ,
    // "index" : dataindex
    //}

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        Inventory inventory = event.getInventory();
        ItemStack clickedItem = event.getCurrentItem();

        // 检查是否是商店界面
        if (!isAdditionInventory(inventory)) {
            return;
        }

        // 阻止玩家拿走商店物品
        event.setCancelled(true);

        // 处理点击事件
        if (clickedItem == null || !clickedItem.hasItemMeta()) {
            return;
        }

        // 获取物品在商店中的位置
        int slot = event.getRawSlot();
        if (slot >= inventory.getSize()) {
            return;
        }
        List<String> l = clickedItem.getItemMeta().getLore();
        // 处理购买逻辑（这里需要集成经济系统）
        int dataindex = Integer.parseInt(l.get(l.size()-1));
//        player.sendMessage("你点击了商店中的物品！位置: " + slot+"\n此物品在全局data文件中的位置是"+l.get(l.size()-1));

        String displayname = clickedItem.getItemMeta().getDisplayName();
        Map<String, Object> ipe = new ConcurrentHashMap<>();
        ipe.put("player",player);
        ipe.put("index" , dataindex);
        ipe.put("mode" , getMode(inventory));
        int ipi = getHasInputPlayer(player);
        if(ipi == -1) {
            input_player.add(ipe);
        }else {
            input_player.set(ipi , ipe);
        }
        player.sendMessage("§a你选择了§r"+displayname+"§r§a,请输入数字来选择你想要的数量，取消购买可以输入“C”");
        player.closeInventory();
    }

    @EventHandler
    public void onCHat(AsyncPlayerChatEvent event){
        Iterator<Map<String, Object>> iterator = input_player.iterator();
        JsonArray data = NiuMaManager.getData();
        while (iterator.hasNext()) {
            Map<String, Object> ds = iterator.next();
            Player player = (Player) ds.get("player");
            int index = (int) ds.get("index");
            if(player.getUniqueId() == event.getPlayer().getUniqueId()) {
                try {
                    String item_name = data.get(index).getAsJsonObject().get("item_name").getAsString();
                    String mode = ds.get("mode").toString();

                    if(mode.equals("buy")){
                        shopProgress sp = new shopProgress(player, item_name, Integer.parseInt(event.getMessage()) );
                        sp.buy();
                    }else if(mode.equals("re")){
                        int num ;
                        if(event.getMessage().equals("all")){
                            num = shopProgress.countItemsInInventory(player , Material.valueOf(item_name.toUpperCase()));
                        }else {
                            num = Integer.parseInt(event.getMessage());
                        }
                        shopProgress sp = new shopProgress(player, item_name,  num);
                        sp.re();
                    }
                } catch (Exception e) {
                    player.sendMessage("§4§l你已取消此次输入");
                }
                iterator.remove(); // 安全删除当前元素
                event.setCancelled(true);
            }
        }
    }
    public String getMode(Inventory in){
        InventoryHolder hd = in.getHolder();
        if(hd instanceof Shop ){
            return "buy";
        }else if(hd instanceof Exui){
            return "re";
        }
        return null;
    }

    public boolean isAdditionInventory(Inventory in){
        InventoryHolder hd = in.getHolder();
        if(hd instanceof Shop || hd instanceof Exui){
            return true;
        }else {
            return false;
        }
    }

    public int getHasInputPlayer(Player player){
        for(int i = 0;i<input_player.size();i++){
            Map<String, Object> ip = input_player.get(i);
            Player player1 = (Player) ip.get("player");
            if(player1.getUniqueId() == player.getUniqueId()){
                return i;
            }
        }
        return -1;
    }
}
