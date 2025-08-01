package mainFesht3.niuMaManager.Vault;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import mainFesht3.niuMaManager.NiuMaManager;
import mainFesht3.niuMaManager.Utils.httpClient;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.*;

public class NMBCommandTab implements TabCompleter {
        Gson gson = new Gson();
        httpClient hc = new httpClient("http://139.224.250.35:666/mc/nm.php");

        @Override
        public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
            List<String> completions = new ArrayList<>();

            // 当输入第一个参数时，提供补全选项
            if (args.length == 1) {
                completions.addAll(Arrays.asList("bind", "shop","buy","re","addGem","removeGem"));
            }
            // 当输入第二个参数时，根据情况提供补全选项
            else if (args.length == 2) {
                if ("bind".equalsIgnoreCase(args[0])) {
                    completions.addAll(Arrays.asList("请填入你的QQ号（此提示请勿补全）"));
//                    此处由用户填写自己的qq号,补全提示

                } else if ("shop".equalsIgnoreCase(args[0])) {
                    // 商店指令，无需后续指令
//                    completions.addAll(Arrays.asList("item1", "item2", "item3"));
                }

                if("buy".equalsIgnoreCase(args[0])){
                    Map<String, String> params = new HashMap<>();
                    params.put("type" , "data");
//                    params.put("name" , NiuMaManager.getPlayerNiuMaServerAccount((Player) sender));//ACCOUNT
                    JsonArray jarr = gson.fromJson(hc.get(params) , JsonArray.class);
                    List<String> item_list = new ArrayList<>();
                    for(JsonElement ele : jarr){
                        JsonObject obj = ele.getAsJsonObject();

                        item_list.add(obj.get("item_name").getAsString());
                    }

                    completions.addAll(item_list);
                }else if("re".equalsIgnoreCase(args[0])){
                    Map<String, String> params = new HashMap<>();
                    params.put("type" , "data");
//                    params.put("name" , NiuMaManager.getPlayerNiuMaServerAccount((Player) sender));//ACCOUNT
                    JsonArray jarr = gson.fromJson(hc.get(params) , JsonArray.class);
                    List<String> item_list = new ArrayList<>();
//                    item_list.add("主手物品");
                    for(JsonElement ele : jarr){
                        JsonObject obj = ele.getAsJsonObject();
                        if(obj.get("canre").getAsBoolean()) {
                            item_list.add(obj.get("item_name").getAsString());
                        }
                    }

                    completions.addAll(item_list);
                }


            }
            if(args.length==3){//buy or re
                if("buy".equalsIgnoreCase(args[0])){
                    completions.addAll(Arrays.asList("1","64"));
                }else if("re".equalsIgnoreCase(args[0])){
                    completions.addAll(Arrays.asList("all"));
                }

            }
            // 根据用户已输入的内容筛选补全结果
            List<String> finalCompletions = new ArrayList<>();
            for (String completion : completions) {
                if (completion.toLowerCase().startsWith(args[args.length - 1].toLowerCase())) {
                    finalCompletions.add(completion);
                }
            }

            return finalCompletions;
        }
}
