package mainFesht3.niuMaManager.Vault;

import mainFesht3.niuMaManager.NiuMaManager;
import mainFesht3.niuMaManager.Utils.httpClient;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import net.milkbowl.vault.economy.Economy; // 此时应无报错

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import com.google.gson.*;

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

        if(args.length==3){
            Player player = (Player) sender;
            if(Objects.equals(args[0], "buy")){
                shopProgress sp = new shopProgress(player,args[1],Integer.valueOf(args[2]));
                sp.buy();
            }
        }

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

            }
        }
        if(args.length == 1){
            if(Objects.equals(args[0], "shop")) {

            }
        }

        if(args.length == 0){
            Player player = (Player) sender;
//            Economy econ = NiuMaManager.getEconomy();
//
//            // 获取玩家余额并格式化
//            double balance = econ.getBalance(player);
//
//            String formattedBalance = econ.format(balance); // 自动适配货币单位（如$、€等）
//
//            // 返回结果
//            player.sendMessage("§a你的牛马币余额: §e" + formattedBalance);

            Map<String, String> params = new HashMap<>();
            params.put("type" , "checknmb");
            params.put("name" , NiuMaManager.getPlayerNiuMaServerAccount(player));
//            JsonObject obj = gson.fromJson(hc.get(params) , JsonObject.class);

            player.sendMessage("§a你的牛马币余额: §e" + hc.get(params));
        }

        return  true;
    }
}
