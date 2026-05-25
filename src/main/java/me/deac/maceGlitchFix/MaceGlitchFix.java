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
    public long minFallDist;

    public boolean logCancels;
    public boolean cancelFall;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        getConfig().options().copyDefaults(true);
        saveConfig();

        ySubtraction = getConfig().getLong("y-subtraction");
        minFallDist = getConfig().getLong("min-fall-distance");

        logCancels = getConfig().getBoolean("log-cancels");
        cancelFall = getConfig().getBoolean("cancel-fall-dmg");

        boolean devMode = getConfig().getBoolean("developer-mode");

        getLogger().info(
                "Started with config:\n - ySubtraction: %s\n - minFallDist: %s\n - logCancels: %s\n - cancel fall: %s\n - dev mode: %s"
                        .formatted(ySubtraction, minFallDist, logCancels, cancelFall, devMode)
        );

        getServer().getPluginManager().registerEvents(this, this);

        // Creates a ghost block 2 blocks below every player placed piece of bedrock
        // I couldn't replicate the glitch the proper way, but this seems to work lol
        // Included like this for optimisation's sake
        if (devMode) getServer().getPluginManager().registerEvent(
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

        float fallDist = attacker.getFallDistance();
        // If the block below isn't air AND fell from above the minimum
        // Minimum check is to allow for use of mace without falling (ie on ground or just critting)
        if (blockBelow.getType() != Material.AIR && fallDist > minFallDist) {
            event.setCancelled(true); // Cancel event, mace does no damage

            if (cancelFall) attacker.setFallDistance(0); // Cancels fall damage
            attacker.sendBlockChange(blockBelow.getLocation(), blockBelow.getBlockData()); // Update block for player

            if (logCancels) getLogger().info( // Log the cancellation, using formatted now because it kinda looks cleaner
                    "Cancelled attack by %s (uuid %s ) falling from %s / %s blocks"
                            .formatted(attacker.getName(), attacker.getUniqueId(), fallDist, minFallDist)
            );
        }
    }
}