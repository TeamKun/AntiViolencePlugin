package net.kunmc.lab.antiviolence;

import net.kunmc.lab.antiviolence.config.ConfigManager;
import net.minecraft.server.v1_16_R3.DamageSource;
import net.minecraft.server.v1_16_R3.EnchantmentManager;
import net.minecraft.server.v1_16_R3.EntityHuman;
import net.minecraft.server.v1_16_R3.MathHelper;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftArrow;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class AttackEventListener implements Listener {
    private boolean isRevenge;

    @EventHandler
    public void onAttack(EntityDamageByEntityEvent event) {
        ConfigManager configManager = AntiViolencePlugin.getInstance().getConfigManager();
        if (!configManager.isEnabled()) {
            return;
        }
        if (event.getEntity().getUniqueId().equals(event.getDamager().getUniqueId())) {
            return;
        }
        if (!(event.getEntity() instanceof CraftLivingEntity)) {
            return;
        }
        if (isRevenge) {
            return;
        }
        CraftLivingEntity revenger = (CraftLivingEntity)event.getEntity();
        double damageMultiplier = configManager.getDamageMultiplier();
        double returnedAmount = event.getFinalDamage() * damageMultiplier;
        boolean immediatelyKill = configManager.isImmediatelyKill();
        isRevenge = true;
        if (event.getDamager() instanceof CraftPlayer) {
            CraftPlayer damager = (CraftPlayer)event.getDamager();
            if (immediatelyKill) {
                damager.setHealth(0);
            } else {
                revenge(damager, revenger, (float)returnedAmount);
            }
        } else if (event.getDamager() instanceof Projectile) {
            Projectile damagerProjectile = (Projectile)event.getDamager();
            if (damagerProjectile.getShooter() instanceof CraftPlayer) {
                CraftPlayer damager = (CraftPlayer)damagerProjectile.getShooter();
                if (immediatelyKill) {
                    damager.setHealth(0);
                } else {
                    revengeByProjectile(damager, damagerProjectile, revenger, (float)returnedAmount);
                }
            }
        }
        isRevenge = false;
    }

    private void revenge(CraftPlayer damager, CraftLivingEntity revenger, float returnedAmount) {
        DamageSource damageSource;
        if (revenger.getHandle() instanceof EntityHuman) {
            damageSource = DamageSource.playerAttack((EntityHuman)revenger.getHandle());
        } else {
            damageSource = DamageSource.mobAttack(revenger.getHandle());
        }
        damager.getHandle().damageEntity(damageSource, returnedAmount);
        int knockback = EnchantmentManager.b(damager.getHandle());
        if (damager.isSprinting() && damager.getAttackCooldown() > 0.9F) {
            knockback++;
        }
        float yaw = damager.getEyeLocation().getYaw();
        damager.getHandle().a(knockback * 0.5F, -MathHelper.sin(yaw * 0.017453292F), MathHelper.cos(yaw * 0.017453292F));
    }

    private void revengeByProjectile(CraftPlayer damager, Projectile damagerProjectile, CraftLivingEntity revenger, float returnedAmount) {
        returnedAmount += 1e-9;
        DamageSource damageSource;
        if (revenger.getHandle() instanceof EntityHuman) {
            damageSource = DamageSource.playerAttack((EntityHuman)revenger.getHandle());
        } else {
            damageSource = DamageSource.mobAttack(revenger.getHandle());
        }
        damager.getHandle().damageEntity(damageSource, returnedAmount);
        float yaw = damager.getEyeLocation().getYaw();
        int knockbackStrength = 0;
        if (damagerProjectile instanceof CraftArrow) {
            knockbackStrength = ((CraftArrow)damagerProjectile).getKnockbackStrength();
        }
        damager.getHandle().a(knockbackStrength * 0.6F, -MathHelper.sin(yaw * 0.017453292F), MathHelper.cos(yaw * 0.017453292F));
    }
}
