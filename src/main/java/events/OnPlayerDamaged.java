package events;

import com.happiergore.deathexceptions.EventListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author HappierGore
 */
public class OnPlayerDamaged {

    public static Map<Player, Set<ItemStack>> playerData = new HashMap<>();

    public static void onPlayerDamaged(EntityDamageByEntityEvent e) {
        if (e.getEntity() instanceof Player) {
            Player player = (Player) e.getEntity();

            List<String> entities = EventListener.configYML.getStringList("EntitiesExceptions");

            for (String entity : entities) {
                if (e.getDamager().getName().equalsIgnoreCase(entity)) {
                    registerUser(player);
                    break;
                }
            }

        }
    }

    public static void OnDamageEvent(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player) {

            List<String> exCauses = EventListener.configYML.getStringList("DamageEventExceptions");

            if (!exCauses.contains(e.getCause().toString())) {
                return;
            }

            Player player = (Player) e.getEntity();
            registerUser(player);
        }
    }

    public static void onItemDamaged(PlayerItemDamageEvent e) {
        if (playerData.containsKey(e.getPlayer())) {
            Set<ItemStack> data = playerData.get(e.getPlayer());

            if (data.contains(e.getItem())) {
                data.remove(e.getItem());
                e.setCancelled(true);
            }
            if (data.isEmpty()) {
                playerData.remove(e.getPlayer());
            }

        }
    }

    private static void registerUser(Player player) {
        if (player.getEquipment() != null) {

            List<String> armorMaterial = EventListener.configYML.getStringList("ArmorTypes");

            Set data = new HashSet<>();

            for (ItemStack item : player.getEquipment().getArmorContents()) {
                if (item == null || item.getType() == Material.AIR) {
                    continue;
                }

                for (String armor : armorMaterial) {
                    if (item.getType().toString().contains(armor.toUpperCase())) {
                        data.add(item);
                        break;
                    }
                }
            }

            playerData.put(player, data);
        }
    }
}
