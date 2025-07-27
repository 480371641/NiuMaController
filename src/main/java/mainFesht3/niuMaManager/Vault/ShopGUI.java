package mainFesht3.niuMaManager.Vault;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.tr7zw.nbtapi.NBTItem;
import mainFesht3.niuMaManager.NiuMaManager;
import mainFesht3.niuMaManager.Utils.FastChestGUI;
import mainFesht3.niuMaManager.Vault.signs.Exui;
import mainFesht3.niuMaManager.Vault.signs.Shop;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ShopGUI implements CommandExecutor {

    FastChestGUI gui ;
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        Player player = (Player) commandSender;
        JsonArray data = NiuMaManager.getData();
        if(command.getName().equals("shop")) {
            gui = new FastChestGUI("§b§l牛马商店", data.size());
            int shunxu = 0;
            for (int i = 0; i < data.size(); i++) {
                JsonObject itemdata = data.get(i).getAsJsonObject();
                ItemStack item;
                try {
                    if(itemdata.has("onSell")&&itemdata.get("onSell").getAsBoolean()) {
                        if (itemdata.get("special").getAsBoolean()) {
                            ItemStack raw_item;
                            raw_item = new ItemStack(Material.valueOf(itemdata.get("item_type").getAsString().toUpperCase()));
                            item = shopProgress.createSpecialItem(raw_item, itemdata.getAsJsonObject("meta"));
                        } else if (itemdata.get("item_name").getAsString().equals("nmc")) {
                            item = new ItemStack(Material.valueOf(itemdata.get("item_type").getAsString().toUpperCase()));
                        } else {
                            item = new ItemStack(Material.valueOf(itemdata.get("item_name").getAsString().toUpperCase()));
                        }
                        ItemMeta meta = item.getItemMeta();
                        List<String> newlist = new ArrayList<>(Arrays.asList("§g§l售价：" + itemdata.get("cost").getAsInt()));
                        if (meta.getLore() != null) {
                            newlist.addAll(meta.getLore());
                        }
                        newlist.add("" + i);
                        meta.setLore(newlist);
                        meta.setDisplayName(itemdata.get("name").getAsString());

                        item.setItemMeta(meta);
                        gui.addItem(shunxu, item);
                        shunxu++;
                    }
                } catch (Exception e) {
//                pass
                    Bukkit.getLogger().info("wrong!!" + e);
                }
            }
            gui.createGUI(new Shop());
        }else{
            //复制粘贴魅力时刻
            gui = new FastChestGUI("§g§l牛马回收商店 (点击物品即可购买)", data.size());
            double tax = NiuMaManager.getTax();
            int shunxu = 0;
            for (int i = 0; i < data.size(); i++) {
                JsonObject itemdata = data.get(i).getAsJsonObject();
                ItemStack item;
                try {
//                    if (itemdata.get("special").getAsBoolean()) {
//                        item = shopProgress.createSpecialItem(Material.valueOf(itemdata.get("item_type").getAsString().toUpperCase()), itemdata.getAsJsonObject("meta"));
//                    } else if (itemdata.get("item_name").getAsString().equals("nmc")) {
//                        item = new ItemStack(Material.valueOf(itemdata.get("item_type").getAsString().toUpperCase()));
//                    } else {
//                        item = new ItemStack(Material.valueOf(itemdata.get("item_name").getAsString().toUpperCase()));
//                    }
                    if(itemdata.get("canre").getAsBoolean()) {
                        item = new ItemStack(Material.valueOf(itemdata.get("item_name").getAsString().toUpperCase()));
                        ItemMeta meta = item.getItemMeta();
                        List<String> newlist = new ArrayList<>(Arrays.asList(ChatColor.GOLD+"§l理论回收价格：" + itemdata.get("cost").getAsInt() * tax ));
                        if (meta.getLore() != null) {
                            newlist.addAll(meta.getLore());
                        }
                        newlist.add("" + i);
                        meta.setLore(newlist);
                        meta.setDisplayName(itemdata.get("name").getAsString());

                        item.setItemMeta(meta);
                        gui.addItem(shunxu, item);
                        shunxu++;
                    }
                } catch (Exception e) {
//                pass
                    Bukkit.getLogger().info("wrong!!" + e);
                }
            }
            gui.createGUI(new Exui());
        }


        gui.openTo(player);

        return true;
    }







}
