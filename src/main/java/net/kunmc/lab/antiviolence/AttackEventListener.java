package net.kunmc.lab.antiviolence;

import net.kunmc.lab.antiviolence.config.ConfigManager;
import net.minecraft.server.v1_16_R3.EnchantmentManager;
import net.minecraft.server.v1_16_R3.EntityPlayer;
import net.minecraft.server.v1_16_R3.MathHelper;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class AttackEventListener implements Listener {
    @EventHandler
    public void onAttack(PlayerMoveEvent event) {
    }

    @EventHandler
    public void onAttack(EntityDamageByEntityEvent event) {
        ConfigManager configManager = AntiViolencePlugin.getInstance().getConfigManager();
        if (!configManager.isEnabled()) {
            return;
        }
        if (event.getEntity().getUniqueId().equals(event.getDamager().getUniqueId())) {
            return;
        }
        if (!(event.getEntity() instanceof LivingEntity)) {
            return;
        }
        double damageMultiplier = configManager.getDamageMultiplier();
        double returnedAmount = event.getDamage() * damageMultiplier;
        boolean immediatelyKill = configManager.isImmediatelyKill();
        if (event.getDamager() instanceof CraftPlayer) {
            revenge((CraftPlayer)event.getDamager(), returnedAmount, immediatelyKill);
        } else if (event.getDamager() instanceof Projectile) {
            Projectile damagerProjectile = (Projectile)event.getDamager();
            if (damagerProjectile.getShooter() instanceof CraftPlayer) {
                revenge((CraftPlayer)damagerProjectile.getShooter(), returnedAmount, immediatelyKill);
            }
        }
    }

    private void revenge(CraftPlayer damager, double returnedAmount, boolean absolutelyKill) {
        if (absolutelyKill) {
            damager.setHealth(0);
        } else {
            damager.damage(returnedAmount);
        }
    }
}
