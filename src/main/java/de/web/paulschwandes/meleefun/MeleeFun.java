package de.web.paulschwandes.meleefun;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.plugin.java.JavaPlugin;

@SuppressWarnings("WeakerAccess")
public class MeleeFun extends JavaPlugin implements Listener {

    private boolean isEnabled = false;
    private double damageFactor;

    @Override
    public void onLoad() {
        damageFactor = getConfig().getDouble("damageFactor");
    }

    @Override
    public void onEnable() {
        getCommand("meleefun").setExecutor(new CommandExecutor() {
            public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
                isEnabled = !isEnabled;
                if (isEnabled) {
                    Bukkit.getPluginManager().registerEvents(MeleeFun.this, MeleeFun.this);
                } else {
                    HandlerList.unregisterAll((Listener)MeleeFun.this);
                }

                Bukkit.broadcastMessage(ChatColor.RED + "MeleeFun is now " +
                        (isEnabled ?
                                "enabled (" + damageFactor + "x damage)" :
                                "disabled"
                        )
                );
                return true;
            }
        });
    }

    @EventHandler
    @SuppressWarnings("unused")
    public void onDamage(EntityDamageByEntityEvent event) {
        if (event.getEntity().getType() != EntityType.PLAYER) {
            return;
        }

        if (event.getDamager().getType() != EntityType.PLAYER) {
            return;
        }

        if (event.getCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
            return;
        }

        event.setDamage(event.getDamage() * damageFactor);

        final Player player = (Player) event.getEntity();
        Bukkit.getScheduler().runTaskLater(this, new Runnable() {
            public void run() {
                player.setNoDamageTicks(0);
            }
        }, 1L);
    }
}
