package mainFesht3.niuMaManager.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FastChestGUI {

    private String title;  // 商店标题
    private  int size;      // 商店界面大小 (9的倍数)
    private  Map<Integer, ItemStack> items;  // 商店物品列表
    private  Inventory gui ;

//    public FastChestGUI(String title, int rows) {
//        this.title = title;
//        this.size = rows * 9;
//        this.items = new ConcurrentHashMap<>();
//        createGUI();
//    }
    public FastChestGUI(String title , int allitems) {
        this.title = title;
        int rows;
        if(allitems%9==0){
            rows = allitems/9;
        }else{
            rows = allitems/9 + 1;
        }
        if(rows > 6 ){//size max 54 rows max 6
            rows = 6;
        }
        this.size = rows * 9;
        this.items = new ConcurrentHashMap<>();
//        createGUI();
    }

    public void setTtile(String title){
        this.title = title;
    }
    public int getMaxSize(){return 54;}

    // 添加物品到商店
    public void addItem(int slot, ItemStack item) {
        items.put(slot, item);
    }

    //create and save gui object
    public void createGUI(InventoryHolder owner){
        Inventory inventory = Bukkit.createInventory(owner, size, title);

        // 填充商店物品
        // 使用 for-each 循环遍历 entrySet
        for (Map.Entry<Integer, ItemStack> entry : items.entrySet() ) {
            int key = entry.getKey();      // 获取键
            ItemStack item = entry.getValue(); // 获取值
            inventory.setItem(entry.getKey(), item);
        }
        gui = inventory;
    }

    //get the gui object
    public Inventory getGui(){
        return gui;
    }

    // 打开商店界面给玩家
    public void openTo(Player player) {
        player.openInventory(gui);
    }
}

