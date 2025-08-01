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
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.DoubleAccumulator;

public class ShopGUI implements CommandExecutor {

    FastChestGUI gui ;

    /**
     *
     * @param array 原始JsonArray
     * @param start 开始截取的下标
     * @param end 结束截取的下标
     * @return 返回处理后的新数组
     */
    private JsonArray cutJsonArrayAt(JsonArray array , int start , int end){
        JsonArray newarray = new JsonArray();
        for(int i = start; i<end + 1 ;i++){
            try {
                newarray.add(array.get(i));
            }catch (Exception e){
                break;
            }
        }
        return newarray;
    }


    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Player player = (Player) sender;
        JsonArray data = NiuMaManager.getData();
        int item_space_num = 52;//少2是因为中间页的话需要附带转上页或转下页的按钮,最后一页
        int maxPage = (int) Math.ceil(data.size() / item_space_num +0.5);

        int page = 1;
        int index = 0;
        List<ItemStack> itemList = new ArrayList<>();
//        Bukkit.getLogger().info("arglength "+ args.length);
        if(args.length==1){
            //choose page.!
//            Bukkit.getLogger().info("Page choose");
            try{
                page = Integer.parseInt(args[0]);
                if(page<1 || page > maxPage){
                    page = 1;
                }
//                data = cutJsonArrayAt(data , (page-1) * item_space_num , page * item_space_num);
                index = (page-1) * item_space_num;
            }catch (Exception e){}
        }
//        Bukkit.getLogger().info("Page ::"+page);

//        Bukkit.getLogger().info("maxpage : "+maxPage+" jsonarr:\n"+ data.size());
        ItemStack toLastPage = new ItemStack(Material.COMPASS);
        ItemMeta sign_meta = toLastPage.getItemMeta();

        sign_meta.addEnchant(Enchantment.ARROW_FIRE,1,true);
        sign_meta.setLore(Arrays.asList("§e§l翻页按钮，点击返回上一页",""+page));
        toLastPage.setItemMeta(sign_meta);

        ItemStack toNextPage = new ItemStack(Material.COMPASS);
        sign_meta.setLore(Arrays.asList("§b§l翻页按钮，点击返回下一页",""+page));
        toNextPage.setItemMeta(sign_meta);

        gui = new FastChestGUI("shopGUI",54);

        gui.addItem(0,toLastPage);
        gui.addItem(53,toNextPage);


        if(cmd.getName().equals("shop")) {

            gui.setTtile("§b§l牛马商店 (点击物品即可购买)");



            for (int i = 0; i < data.size(); i++) {
                JsonObject itemdata = data.get(i).getAsJsonObject();
                ItemStack item;
                try {
                    if(itemdata.has("onSell")&&itemdata.get("onSell").getAsBoolean()) {
                        if (itemdata.get("special").getAsBoolean()) {
                            ItemStack raw_item;
                            raw_item = new ItemStack(Material.valueOf(itemdata.get("item_type").getAsString().toUpperCase()));
                            item = shopProgress.createSpecialItem(raw_item, itemdata.getAsJsonObject("meta"));
                        } else if (itemdata.get("item_name").getAsString().equals("nmc")) {//牛马积分特殊处理
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
//                        Bukkit.getLogger().info("set "+ " "+ item.getType());
//                        gui.addItem(shunxu, item);
                        itemList.add(item);

//                        shunxu++;
                    }
                } catch (Exception e) {
//                pass
                    Bukkit.getLogger().info("wrong!!" + e);
                }
            }

            for(int i = 1;i<52+1 ; i++){
                if(index < itemList.size()) {
                    gui.addItem(i, itemList.get(index));
                    index++;
                }else{
                    break;
                }
            }

            gui.createGUI(new Shop());
        }else{
            //复制粘贴魅力时刻
            gui.setTtile("§g§l牛马回收商店 (点击物品即可回收)");
            double tax = NiuMaManager.getTax();
//            int shunxu = 0;
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
                        if (itemdata.get("special").getAsBoolean()) {
                            ItemStack raw_item;
                            raw_item = new ItemStack(Material.valueOf(itemdata.get("item_type").getAsString().toUpperCase()));
                            item = shopProgress.createSpecialItem(raw_item, itemdata.getAsJsonObject("meta"));
                        }else {
                            item = new ItemStack(Material.valueOf(itemdata.get("item_name").getAsString().toUpperCase()));
                        }

                        ItemMeta meta = item.getItemMeta();
                        List<String> newlist = new ArrayList<>(Arrays.asList(ChatColor.GOLD+"§l理论回收价格：" + itemdata.get("cost").getAsInt() * tax ));
                        if (meta.getLore() != null) {
                            newlist.addAll(meta.getLore());
                        }
                        newlist.add("" + i);
                        meta.setLore(newlist);
                        meta.setDisplayName(itemdata.get("name").getAsString());

                        item.setItemMeta(meta);
//                        gui.addItem(shunxu, item);
                        itemList.add(item);
//                        shunxu++;
                    }
                } catch (Exception e) {
//                pass
                    Bukkit.getLogger().info("wrong!!" + e);
                }
            }

            for(int i = 1;i<52+1 ; i++){
                if(index < itemList.size()) {
                    gui.addItem(i, itemList.get(index));
                    index++;
                }else{
                    break;
                }
            }

            gui.createGUI(new Exui());
        }


        gui.openTo(player);

        return true;
    }







}
