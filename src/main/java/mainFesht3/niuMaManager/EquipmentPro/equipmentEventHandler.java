package mainFesht3.niuMaManager.EquipmentPro;

import de.tr7zw.nbtapi.NBTEntity;
import de.tr7zw.nbtapi.NBTItem;
import mainFesht3.niuMaManager.EquipmentPro.signs.gemInstaller;
import mainFesht3.niuMaManager.NiuMaManager;
import mainFesht3.niuMaManager.Utils.RandomUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NetherWartsState;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public class equipmentEventHandler implements Listener {

    /**
     * 从玩家的盔甲栏获取特定的宝石加成数据
     * @param dataKey 特殊加成数据名，如fallDamage
     * @param armors 玩家盔甲栏物品数组
     * @return 此数值的返回
     */
    public double getSpecialValueFromArmors(String dataKey ,ItemStack[] armors ){
        double dataSpecialValue = 0;
        for (ItemStack armor : armors) {
            if(armor!=null) {
                equipmentAPI ep = new equipmentAPI(armor);
                dataSpecialValue += ep.getSpecialValue(dataKey);
            }
        }
        return dataSpecialValue;
    }



    @EventHandler
    public void onHurt(EntityDamageEvent event){
        try {
//            Bukkit.getLogger().info("entity fall");

            Player player = (Player) event.getEntity();

//            Bukkit.getLogger().info("player fall");

//            Bukkit.getLogger().info(event.getCause().toString());
            ItemStack[] in = player.getInventory().getArmorContents();
            if(event.getCause() == EntityDamageEvent.DamageCause.FALL || event.getCause() == EntityDamageEvent.DamageCause.FLY_INTO_WALL ) {
                double fallDamage = 0;
                fallDamage = getSpecialValueFromArmors("fallDamage" ,in);
                if(fallDamage!=0){
                    double finalDamage = event.getDamage() * (1 + fallDamage);
                    if(finalDamage<=0){
                        finalDamage = 0;
                    }
                    event.setDamage(finalDamage);
                    player.sendMessage("掉落减伤: " + (fallDamage*100) +"%");
                }
            }

            if(event.getCause() == EntityDamageEvent.DamageCause.FIRE || event.getCause() == EntityDamageEvent.DamageCause.FIRE_TICK || event.getCause() == EntityDamageEvent.DamageCause.LAVA) {
                double fireDamage = 0;
                fireDamage = getSpecialValueFromArmors("fireDamage" , in);
                if(fireDamage!=0) {
                    double finalDamage = event.getDamage() * (1 + fireDamage);
                    if(finalDamage<=0){
                        finalDamage = 0;
                    }
                    event.setDamage(finalDamage);
                    player.sendMessage("§6火焰灼烧额外减伤率" + (fireDamage*100) + "%" );
                }
            }

            if(event.getCause() == EntityDamageEvent.DamageCause.FIRE || event.getCause() == EntityDamageEvent.DamageCause.LAVA || event.getCause() == EntityDamageEvent.DamageCause.HOT_FLOOR) {
                double fireRecover = 0;
                fireRecover = getSpecialValueFromArmors("fireRecover" , in);
                if(fireRecover!=0) {
                    //上面会有减伤机制，此处不再减伤，仅加血
                    double newHealth =  player.getHealth() + ( player.getMaxHealth()-player.getHealth() )*fireRecover;
                    if(newHealth > player.getMaxHealth()){
                        newHealth = player.getMaxHealth();
                    }
                    player.setHealth( newHealth );

                    player.sendMessage("§6§l欲火重生, 恢复已损生命值" + (fireRecover*100)+"%");
//                    event.setCancelled(true);
                }
            }

        }catch (Exception e){
//            Bukkit.getLogger().info("event exception : "+ e);
        }
    }

//    @EventHandler
//    public void disAbleFireOnPlay


    double lastAntiFirePost = 0;
    @EventHandler
    public void onPlayerFire(EntityCombustEvent event) {
        try {
//            Bukkit.getLogger().info("entity fall");
            double antiFire = 0;//防止着火的概率
            Player player = (Player) event.getEntity();
            ItemStack[] in = player.getInventory().getArmorContents();
            antiFire = getSpecialValueFromArmors("antiFire" ,in);
            if(RandomUtils.randomDouble() < antiFire){
                if(NiuMaManager.getTime() - lastAntiFirePost > 5) {
                    //cd 5s
                    player.sendMessage("免疫一次着火");
                    event.setCancelled(true);
                    lastAntiFirePost = NiuMaManager.getTime();
                }
            }

        }catch (Exception e){
//            Bukkit.getLogger().info("event exception : "+ e);
        }
    }

    @EventHandler
    public void onBulletHitHurt__(EntityDamageByEntityEvent event){
        try {
            Entity bullet = event.getDamager();
            Entity entity = event.getEntity();
//            Player player = (Player) attacker;

//            Bukkit.getLogger().info(bullet.getName() + " cause "+event.getFinalDamage() + " to "+ entity.getName());
//            player.sendMessage(attacker.getName() + " cause "+event.getFinalDamage() + " to "+ entity.getName());
//            Bukkit.getLogger().info(event.getFinalDamage()+" ");
            NBTEntity bulletnbt = new NBTEntity(bullet);

            if((bullet.getType()+"").equals("TACZ_BULLET") ){
                Player shooter = Bukkit.getPlayer(bulletnbt.getUUID("Owner"));

                if(shooter.isInvisible()&&!shooter.isOp()){
                    //隐身者禁止枪击其他实体！！！
                    event.setCancelled(true);
                    shooter.kickPlayer("服务器明令禁止使用隐身，你不仅不解除还攻击了其他实体！已被踢出服务器");
                }

                ItemStack[] in = shooter.getInventory().getArmorContents();
                double bulletDamage = 0;
                bulletDamage = getSpecialValueFromArmors("bulletDamage" , in);

                bulletDamage = getSpecialValueFromArmors("fireRecover" , in);
                if(bulletDamage!=0) {
                    double finalDamage =  event.getDamage()*(1+bulletDamage);
                    if(finalDamage <= 0){
                        finalDamage = 0;
                    }
                    event.setDamage( finalDamage );

                    shooter.sendMessage("§7§l子弹伤害减免" + ((int)bulletDamage*100) + "%");
//                    event.setCancelled(true);
                }





//                shooter.sendMessage("You shoot at "+entity.getName());



            }

//            Bukkit.getLogger().info(nbt.getUUID("Owner").toString());
//            Bukkit.getLogger().info(Bukkit.getPlayer("feSHt3").getUniqueId().toString());
//            Bukkit.getPlayer("feSHt3").sendMessage(bullet.getType()+"");
//            Bukkit.getLogger().info("  !!!!!!!!!!!!!!!!  ");
        }catch (Exception e){
            //pass
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

    /**
     * 只处理epGUI事件
     * @param event
     */
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        Inventory inventory = event.getInventory();
        ItemStack clickedItem = event.getCurrentItem();
        if(! (inventory.getHolder() instanceof gemInstaller) ){
            return;
        }

        NBTItem nbt = new NBTItem(clickedItem);


        if(event.isLeftClick()){
            //左键，安装宝石
            player.performCommand("nmb addGem "+nbt.getInteger("special_id"));
            player.closeInventory();
        } else if (event.isRightClick()) {
            //右键 卸载宝石
            player.performCommand("nmb removeGem "+nbt.getInteger("special_id"));
            player.closeInventory();
        }



    }


    }
