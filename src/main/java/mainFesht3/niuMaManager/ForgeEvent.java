package mainFesht3.niuMaManager;


import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftLivingEntity;
import org.bukkit.entity.Player;
import com.tacz.guns.api.event.common.GunShootEvent;
import java.util.UUID;

public class ForgeEvent {
    @SubscribeEvent
    public void onShoot(GunShootEvent event){
        LivingEntity entity = event.getShooter();
        UUID uuid = entity.getBukkitLivingEntity().getUniqueId();
        Player p = Bukkit.getPlayer(uuid);
//        NiuMaManager.runStaticCmd("say" , new String[]{p.getName() + "has just shoot"});
        Bukkit.getLogger().info(p.getName() + "has just shoot");
    }
}
