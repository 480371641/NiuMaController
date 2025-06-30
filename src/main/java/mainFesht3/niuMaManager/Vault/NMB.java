package mainFesht3.niuMaManager.Vault;

import mainFesht3.niuMaManager.NiuMaManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import net.milkbowl.vault.economy.Economy; // 此时应无报错

//专门查询牛马币等操作
public class NMB implements CommandExecutor {
    NiuMaManager nmm;
    public NMB(NiuMaManager nmm){
        this.nmm = nmm;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("该命令仅玩家可使用");
            return true;
        }
        if(args.length == 0){
            Player player = (Player) sender;
            Economy econ = NiuMaManager.getEconomy();

            // 获取玩家余额并格式化
            double balance = econ.getBalance(player);
            String formattedBalance = econ.format(balance); // 自动适配货币单位（如$、€等）

            // 返回结果
            player.sendMessage("§a你的牛马币余额: §e" + formattedBalance);
        }

        return  true;
    }
}
