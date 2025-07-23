package mainFesht3.niuMaManager.Vault.signs;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class Shop implements InventoryHolder {
    @Override
    public Inventory getInventory() {
        return null; // 无需实际物品栏，仅作为标记
    }
}
