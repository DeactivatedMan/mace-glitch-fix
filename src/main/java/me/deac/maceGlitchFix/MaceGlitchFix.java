package me.deac.maceGlitchFix;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.plugin.java.JavaPlugin;

public final class MaceGlitchFix extends JavaPlugin implements Listener {

    public long ySubtraction;
    public boolean logCancels;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        ySubtraction = getConfig().getLong("y-subtraction");
        logCancels = getConfig().getBoolean("log-cancels");

        getServer().getPluginManager().registerEvents(this, this);

        // Creates a ghost block 2 blocks below every player placed piece of bedrock
        // I couldn't replicate the glitch the proper way, but this seems to work lol
        // Included like this for optimisation's sake
        if (getConfig().getBoolean("developer-mode")) getServer().getPluginManager().registerEvent(
                BlockPlaceEvent.class,
                this,
                EventPriority.NORMAL,
                (listener, event) -> {
                    BlockPlaceEvent placeEvent = (BlockPlaceEvent) event;

                    Block block = placeEvent.getBlock();
                    if (block.getType() != Material.BEDROCK) return;
                    placeEvent.getPlayer().sendBlockChange(block.getLocation().subtract(0, 2, 0), Bukkit.createBlockData(Material.AIR));
                    getLogger().info("Sent change for player" + placeEvent.getPlayer().name() + " at " + block.getLocation());
                },
                this
        );
    }

    @EventHandler
    public void playerKillEvent(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player attacker)) return; // Not a player
        else if (attacker.getInventory().getItemInMainHand().getType() != Material.MACE) return; // Not a mace

        Block blockBelow = attacker.getLocation().clone().subtract(0, ySubtraction,0).getBlock(); // Get block below

        if (blockBelow.getType() != Material.AIR) { // If block below isn't air serverside
            event.setCancelled(true); // Cancel event, mace does no damage
            attacker.sendBlockChange(blockBelow.getLocation(), blockBelow.getBlockData()); // Update block for player, added benefit of them dying to fall damage too!
            if (logCancels) getLogger().info("Cancelled attack by " + attacker.getName() + " (uuid "+attacker.getUniqueId()+" )"); // Log the cancellation
        }
    }
}
