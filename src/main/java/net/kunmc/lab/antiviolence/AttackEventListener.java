package net.kunmc.lab.antiviolence;

import net.kunmc.lab.antiviolence.config.ConfigManager;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_16_R3.util.CraftVector;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityCombustByEntityEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExhaustionEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerVelocityEvent;
import org.bukkit.util.Vector;

import java.util.Iterator;
import java.util.List;

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
            return;
        }
        damager.damage(returnedAmount);
        float yaw = damager.getEyeLocation().getYaw();
        int knockback = EnchantmentManager.b(damager.getHandle());
        if (damager.isSprinting() && damager.getAttackCooldown() > 0.9F) {
            knockback++;
        }
        damager.getHandle().a(knockback * 0.5F, -MathHelper.sin(yaw * 0.017453292F), MathHelper.cos(yaw * 0.017453292F));
    }
}
