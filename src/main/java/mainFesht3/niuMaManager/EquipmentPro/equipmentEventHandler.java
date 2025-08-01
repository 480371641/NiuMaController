package mainFesht3.niuMaManager.EquipmentPro;

import mainFesht3.niuMaManager.Utils.RandomUtils;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class equipmentEventHandler implements Listener {

    @EventHandler
    public void onHurt(EntityDamageEvent event){
        try {
//            Bukkit.getLogger().info("entity fall");
            Player player = (Player) event.getEntity();
            double fallDamage = 0;
//            Bukkit.getLogger().info("player fall");
            Bukkit.getLogger().info(event.getCause().toString());
            ItemStack[] in = player.getInventory().getArmorContents();
            if(event.getCause() == EntityDamageEvent.DamageCause.FALL || event.getCause() == EntityDamageEvent.DamageCause.FLY_INTO_WALL ) {

                for (ItemStack armor : in) {
                    if(armor!=null) {
                        equipmentAPI ep = new equipmentAPI(armor);
                        fallDamage += ep.getFallDamageResisdent();
                    }
                }

                event.setDamage(event.getDamage() * (1 + fallDamage));
                player.sendMessage("掉落额外减伤率" + fallDamage);
            }
            double fireDamage = 0;
            if(event.getCause() == EntityDamageEvent.DamageCause.FIRE || event.getCause() == EntityDamageEvent.DamageCause.FIRE_TICK) {

                for (ItemStack armor : in) {
                    if(armor!=null) {
                        equipmentAPI ep = new equipmentAPI(armor);
                        fireDamage += ep.getFireDamageResisdent();
                    }
                }

                event.setDamage(event.getDamage() * (1 + fireDamage));
                player.sendMessage("火焰灼烧额外减伤率" + fireDamage);
            }

        }catch (Exception e){
            Bukkit.getLogger().info("event exception : "+ e);
        }
    }

//    @EventHandler
//    public void disAbleFireOnPlay


    @EventHandler
    public void onPlayerFire(EntityCombustEvent event) {
        try {
//            Bukkit.getLogger().info("entity fall");
            double antiFire = 0;//防止着火的概率
            Player player = (Player) event.getEntity();
            ItemStack[] in = player.getInventory().getArmorContents();
            for (ItemStack armor : in) {
                if(armor!=null) {
                    equipmentAPI ep = new equipmentAPI(armor);
                    antiFire += ep.getAntiFirePercentage();
                }
            }
            if(RandomUtils.randomDouble() < antiFire){
                player.sendMessage("免疫一次着火" );
                event.setCancelled(true);
            }

        }catch (Exception e){
//            Bukkit.getLogger().info("event exception : "+ e);
        }
    }

    public void checkArmorChange(Player player){

    }

    // 监听玩家切换手持物品
    @EventHandler
    public void onItemHeld(PlayerItemHeldEvent event) {
        checkArmorChange(event.getPlayer());
    }

    // 监听玩家交互（可能装备物品）
    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        checkArmorChange(event.getPlayer());
    }


}
