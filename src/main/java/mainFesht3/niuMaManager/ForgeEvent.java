package mainFesht3.niuMaManager;


import com.tacz.guns.api.event.common.EntityHurtByGunEvent;
import mainFesht3.niuMaManager.Utils.ShootLine;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftLivingEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import com.tacz.guns.api.event.common.GunShootEvent;

import java.util.List;
import java.util.UUID;

public class ForgeEvent {


    @SubscribeEvent
    public void onShoot(GunShootEvent event){
        LivingEntity entity = event.getShooter();
        Player player = getPlayerFromUUID(entity.getBukkitLivingEntity().getUniqueId());
//        NiuMaManager.runStaticCmd("say" , new String[]{p.getName() + "has just shoot"});


        Bukkit.getLogger().info(player.getName() + "has just shoot");

        World wd = player.getLocation().getWorld();
        ShootLine st = new ShootLine(player.getEyeLocation());
        wd.spawnParticle(Particle.FLAME,
                    st.getPhasePos(1 , 0 ,1),
                    1 ,
                    0.1,
                    0.1,
                    0.1,
                    0.001);

//        List<Location> sl = st.getShootLine(st.getPhasePos(1 , 0 ,1) , st.getCenterPos(10) , 20);
//        for(Location pos : sl) {
////            wd.spawnParticle(Particle.FLAME, pos, 1);
//            wd.spawnParticle(Particle.FLAME,
//                    pos,//new Location(wd , footpos.getX() , footpos.getY()+1.6, footpos.getZ()) ,
//                    1 ,
//                    0.1,
//                    0.1,
//                    0.1,
//                    0.001);
//        }
    }

    @SubscribeEvent
    public void onHitEntity(EntityHurtByGunEvent event){
        Player player = getPlayerFromUUID(event.getAttacker().getBukkitLivingEntity().getUniqueId());
        Entity bullet = Bukkit.getEntity(event.getBullet().getBukkitEntity().getUniqueId());
//        player.getLocation().getDirection();
    }

    public Player getPlayerFromUUID(UUID uuid){return Bukkit.getPlayer(uuid);}
}
