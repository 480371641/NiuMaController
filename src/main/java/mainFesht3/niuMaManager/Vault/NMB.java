package mainFesht3.niuMaManager.Vault;

import mainFesht3.niuMaManager.NiuMaManager;
import mainFesht3.niuMaManager.EquipmentPro.*;
import mainFesht3.niuMaManager.Utils.httpClient;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import net.milkbowl.vault.economy.Economy; // 此时应无报错

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import com.google.gson.*;
import org.bukkit.inventory.ItemStack;
//import org.bukkit.inventory.ItemStack;

//专门查询牛马币等操作
public class NMB implements CommandExecutor {

        NiuMaManager nmm;
        public NMB(NiuMaManager nmm){
            this.nmm = nmm;
        }
        Gson gson = new Gson();
        httpClient hc = new httpClient("http://139.224.250.35:666/mc/nm.php");

        @Override
        public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("该命令仅玩家可使用");
                return true;
            }
            //buy xxxx 1
            //0   1    2


            if(args.length==3){
                Player player = (Player) sender;
                if(Objects.equals(args[0], "buy")){
                    shopProgress sp = new shopProgress(player,args[1],Integer.parseInt(args[2]));
                    sp.buy();

                }else if("re".equals(args[0])){
                    int num;
                    try {
                        if (args[2].equals("all")) {
                            num = shopProgress.countItemsInInventory(player, Material.valueOf(args[1].toUpperCase()));
                        }
//                        else if(args[2].equals("主手物品")) {
//                            ItemStack mainhand = player.getInventory().getItemInMainHand();
//                            if(mainhand.getType() == Material.AIR){
//                                num = 0;
//                            }else {
//                                num = shopProgress.countItemsInInventory(player, mainhand.getType());
//                            }
//                        }
                        else{
                            num = Integer.parseInt(args[2]);
                        }
                        shopProgress sp = new shopProgress(player, args[1], num);
                        sp.re();
                    }catch (Exception e){
                        player.sendMessage("§4执行命令出现错误，请检查输入！！！！");
                    }
                }
            }

            //addGem gem_id //手持需要装配的装备
            if(args.length == 2){
                Player player = (Player) sender;
                if(Objects.equals(args[0], "bind")){
                    //bind qq on account through command

                    Map<String, String> params = new HashMap<>();
                    params.put("type" , "set_account");
                    params.put("name" , player.getName());
                    params.put("type2",args[1]);
                    params.put("type3" , NiuMaManager.getPlayerNiuMaServerAccount(player));//ACCOUNT
                    JsonObject obj = gson.fromJson(hc.get(params) , JsonObject.class);
                    if(obj.get("result").getAsBoolean()){
                        player.sendMessage("§a已成功绑定您的账号" + args[1]);
                    }else{
                        player.sendMessage("§4绑定您的账号时出现问题");
                    }

                }else if (args[0].equals("addGem")) {
                    //addGem ....
                    try {
                        ItemStack mainHandItem = player.getInventory().getItemInMainHand();
                        int gem_id = Integer.parseInt(args[1]);
                        equipmentAPI ep = new equipmentAPI(mainHandItem);

                        JsonObject gemShopData = equipmentAPI.getGemShopData(gem_id);
                        ItemStack gem = shopProgress.createSpecialItem(new ItemStack(Material.valueOf(gemShopData.get("item_type").getAsString().toUpperCase())) ,gemShopData.getAsJsonObject("meta") );

                        if(shopProgress.getItemNum(player,gem)>0){
                            gem.setAmount(1);
                            player.getInventory().removeItem(gem);
                            Bukkit.getLogger().info("add Gem result" + ep.addGem(gem_id) );
                            shopProgress.replaceItemsInInventory(player.getInventory() , mainHandItem , ep.getFinalItem() );
                            player.updateInventory();
                        }

//                        Bukkit.getLogger().info("add Gem result" + ep.addGem(gem_id) );


                        player.performCommand("test");


                    }catch (Exception e){
                        Bukkit.getLogger().info("Exception : "+e);
                    }
                }else if (args[0].equals("removeGem")) {
                    //addGem ....
                    try {
                        ItemStack mainHandItem = player.getInventory().getItemInMainHand();
                        int gem_id = Integer.parseInt(args[1]);
                        equipmentAPI ep = new equipmentAPI(mainHandItem);

                        JsonObject gemShopData = equipmentAPI.getGemShopData(gem_id);
                        ItemStack gem = shopProgress.createSpecialItem(new ItemStack(Material.valueOf(gemShopData.get("item_type").getAsString().toUpperCase())) ,gemShopData.getAsJsonObject("meta") );

                        if(shopProgress.getItemNum(player,gem)>0){
                            if(shopProgress.getRemainingCapacity(player , gem) > 0) {
                                gem.setAmount(1);
                                player.getInventory().addItem(gem);
                                Bukkit.getLogger().info("remove Gem result" + ep.removeGem(gem_id));

                                shopProgress.replaceItemsInInventory(player.getInventory(), mainHandItem, ep.getFinalItem());
                                player.updateInventory();
                            }else {
                                player.sendMessage(ChatColor.DARK_RED+"你的背包空间不够！不足以容纳褪下的强化宝石");
                            }
                        }


                    }catch (Exception e){
                        Bukkit.getLogger().info("Exception : "+e);
                    }
                }
            }
//            if(args.length == 1){
//                if(Objects.equals(args[0], "shop")) {
//
//                }
//            }

            if(args.length == 0){
                Player player = (Player) sender;
                Economy econ = NiuMaManager.getEconomy();

                // 获取玩家余额并格式化
                double balance = econ.getBalance(player);

                String formattedBalance = econ.format(balance); // 自动适配货币单位（如$、€等）

    //            // 返回结果
    //            player.sendMessage("§a你的牛马币余额: §e"  formattedBalance);

                Map<String, String> params = new HashMap<>();
                params.put("type" , "checknmb");
                params.put("name" , NiuMaManager.getPlayerNiuMaServerAccount(player));
    //            JsonObject obj = gson.fromJson(hc.get(params) , JsonObject.class);

                player.sendMessage("§a你的牛马币余额: §e" + hc.get(params));
                player.sendMessage("§b你的 \"牛马积分\" 余额: §e" + formattedBalance);
            }

            return  true;
        }
}
