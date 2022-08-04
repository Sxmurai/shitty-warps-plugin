package shit.warps.util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import shit.warps.ShitWarps;

/**
 * @author aesthetical
 * @since 08/08/2022
 */
public class GuiUtil implements Listener {
    private final Inventory inv;
    private final Player player;

    public GuiUtil(Player player) {
        this.player = player;

        this.inv = Bukkit.createInventory(null, 45, "Warps");
        populateInventory();

        Bukkit.getServer().getPluginManager().registerEvents(this, ShitWarps.INSTANCE);
        player.openInventory(inv);
    }

    private void populateInventory() {
        // todo: pagination

        int i = 0;
        for (String name : ShitWarps.WARPS.keySet()) {
            ItemStack stack = new ItemStack(Material.BOOK);

            ItemMeta meta = stack.getItemMeta();
            meta.setDisplayName(ChatColor.BLUE + name);

            stack.setItemMeta(meta);

            inv.setItem(i, stack);
            ++i;
        }
    }

    @EventHandler
    public void clickWindow(InventoryClickEvent event) {
        if (event.getWhoClicked().equals(player) && event.getInventory().equals(inv)) {
            ItemStack stack = inv.getItem(event.getSlot());
            if (stack == null || stack.getType().equals(Material.AIR)) {
                return;
            }

            if (stack.getType().equals(Material.BOOK)) {
                event.setCancelled(true);
                ShitWarps.teleport(player, stack.getItemMeta().getDisplayName());
            }
        }
    }

    public Inventory getInv() {
        return inv;
    }
}
