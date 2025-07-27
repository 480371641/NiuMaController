package mainFesht3.niuMaManager;

//
//import com.tacz.guns.api.event.common.EntityHurtByGunEvent;
//import com.tacz.guns.api.event.common.EntityKillByGunEvent;
//import mainFesht3.niuMaManager.Utils.ShootLine;
//
//import net.minecraftforge.eventbus.api.SubscribeEvent;
//import org.bukkit.Bukkit;
//import org.bukkit.Location;
//import org.bukkit.Particle;
//import org.bukkit.World;
//
//import org.bukkit.entity.Entity;
//import org.bukkit.entity.Player;
//import com.tacz.guns.api.event.common.GunShootEvent;
//
//import java.util.List;
//import java.util.UUID;

public class ForgeEvent {
//
//
//    @SubscribeEvent
//    public void onShoot(GunShootEvent event){
////        LivingEntity entity = event.getShooter();
////        Player player = getPlayerFromUUID(entity.getBukkitLivingEntity().getUniqueId());
//////        NiuMaManager.runStaticCmd("say" , new String[]{p.getName() + "has just shoot"});
////
////
//////        Bukkit.getLogger().info(player.getName() + "has just shoot");
////
////        World wd = player.getLocation().getWorld();
////        ShootLine st = new ShootLine(player.getEyeLocation());
//////        wd.spawnParticle(Particle.FLAME,
//////                    st.getPhasePos(0.4 , 0 ,1.5),
//////                    1 ,
//////                    0.1,
//////                    0.1,s
//////                    0.1,
//////                    0.001);
////
////        List<Location> sl = st.getShootLine(st.getPhasePos(0.4 , 0 ,1.5) , st.getCenterPos(25) , 30);
////        for(Location pos : sl) {
//////            wd.spawnParticle(Particle.FLAME, pos, 1);
////            wd.spawnParticle(Particle.SONIC_BOOM,
////                    pos,//new Location(wd , footpos.getX() , footpos.getY()+1.6, footpos.getZ()) ,
////                    1 ,
////                    0.1,
////                    0.1,
////                    0.1,
////                    0.001);
////        }
//    }
//    @SubscribeEvent
//    public void onHitEntity(EntityHurtByGunEvent event){
//        Player player = getPlayerFromUUID(event.getAttacker().getBukkitLivingEntity().getUniqueId());
//        if(!NiuMaManager.isVIP(player)){return;}
//
//        Entity entity = Bukkit.getEntity(event.getHurtEntity().getBukkitEntity().getUniqueId());
////        player.getLocation().getDirection();
//        World wd = player.getLocation().getWorld();
//        ShootLine st = new ShootLine(player.getEyeLocation());
////        wd.spawnParticle(Particle.FLAME,
////                    st.getPhasePos(0.4 , 0 ,1.5),
////                    1 ,
////                    0.1,
////                    0.1,
////                    0.1,
////                    0.001);
//        Location epos = entity.getLocation();
//        epos.setY( epos.getY() + entity.getHeight() - 0.15);
//
////        player.sendMessage(event.getBaseAmount()+" BaseDamage?");
////        player.sendMessage(event.getAmount()+" damage2?");
//
//        List<Location> sl = st.getShootLine(st.getPhasePos(0.4 , 0 ,2) , epos , 12);
//        for(Location pos : sl) {
////            wd.spawnParticle(Particle.FLAME, pos, 1);
//            wd.spawnParticle(Particle.SOUL_FIRE_FLAME,
//                    pos,//new Location(wd , footpos.getX() , footpos.getY()+1.6, footpos.getZ()) ,
//                    1 ,
//                    0,
//                    0,
//                    0,
//                    0.0001);
//        }
//    }
//
//
//    @SubscribeEvent
//    public void onKillEntity(EntityKillByGunEvent event){
//        Player player = getPlayerFromUUID(event.getAttacker().getBukkitLivingEntity().getUniqueId());
//        if(!NiuMaManager.isVIP(player)){return;}
//
//        Entity entity = Bukkit.getEntity(event.getKilledEntity().getBukkitEntity().getUniqueId());
////        player.getLocation().getDirection();
//        World wd = player.getLocation().getWorld();
//        ShootLine st = new ShootLine(player.getEyeLocation());
////        wd.spawnParticle(Particle.FLAME,
////                    st.getPhasePos(0.4 , 0 ,1.5),
////                    1 ,
////                    0.1,
////                    0.1,
////                    0.1,
////                    0.001);
//        Location epos = entity.getLocation();
//        epos.setY( epos.getY() + entity.getHeight() - 0.15);
//        List<Location> sl = st.getShootLine(st.getPhasePos(0.4 , 0 ,5.5) , epos  , 30);
//        for(Location pos : sl) {
////            wd.spawnParticle(Particle.FLAME, pos, 1);
//            wd.spawnParticle(Particle.SONIC_BOOM,
//                    pos,//new Location(wd , footpos.getX() , footpos.getY()+1.6, footpos.getZ()) ,
//                    1 ,
//                    0.1,
//                    0.1,
//                    0.1,
//                    0.001);
//        }
//        wd.spawnParticle(Particle.EXPLOSION_LARGE ,entity.getLocation() ,1);
//    }
//
//    public Player getPlayerFromUUID(UUID uuid){return Bukkit.getPlayer(uuid);}
}
