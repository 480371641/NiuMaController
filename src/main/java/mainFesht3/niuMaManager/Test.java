package mainFesht3.niuMaManager;

import de.tr7zw.nbtapi.NBTItem;
import mainFesht3.niuMaManager.Vault.shopProgress;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;

public class Test implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        Player player = (Player) sender;
        ItemStack hd = player.getInventory().getItemInMainHand();

        player.getInventory().addItem(shopProgress.getGunItem("tacz:vector45",new HashMap<>()));
        NBTItem nbt = new NBTItem(hd);
        player.sendMessage(String.valueOf(hd.getType()));
        player.sendMessage(nbt.toString());
        Bukkit.getLogger().info(ChatColor.GOLD+nbt.toString());
        Bukkit.getLogger().info(ChatColor.GREEN+hd.getType().toString());

        return true;
    }
}
