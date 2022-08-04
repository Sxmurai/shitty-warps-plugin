package shit.warps;

import net.minecraft.server.v1_8_R3.BlockPosition;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import shit.warps.util.FileUtil;
import shit.warps.util.GuiUtil;

import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * I made this plugin in 20 minutes.
 *
 * @author aesthetical
 * @since 08/08/2022
 */
public class ShitWarps extends JavaPlugin implements CommandExecutor {
    public static ShitWarps INSTANCE;

    public static final Map<String, BlockPosition> WARPS = new ConcurrentHashMap<>();
    public static final Map<UUID, GuiUtil> GUIS = new ConcurrentHashMap<>();

    @Override
    public void onEnable() {
        getLogger().info("God is dead.");
        FileUtil.loadWarps();
        getLogger().info("Loaded " + WARPS.size() + " warps.");

        INSTANCE = this;
    }

    @Override
    public void onDisable() {
        GUIS.forEach((uuid, gui) -> {
            Player player = getServer().getPlayer(uuid);
            if (player != null && player.getInventory() != null && player.getInventory().equals(gui.getInv())) {
                player.closeInventory();
            }

            GUIS.remove(uuid);
        });
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = getServer().getPlayer(sender.getName());

        switch (label.toLowerCase()) {
            case "warps": {
                if (player != null) {
                    GUIS.put(player.getUniqueId(), new GuiUtil(player));
                }
                return true;
            }

            case "warpto":
            case "goto":
            case "warp": {
                if (args.length == 0) {
                    sender.sendMessage(ChatColor.RED + "You must provide a name.");
                    return true;
                }

                teleport(player, args[0]);
                return true;
            }

            case "createwarp":
            case "newwarp":
            case "setwarp": {
                if (!sender.isOp() || player == null) {
                    return false;
                }

                if (args.length == 0) {
                    sender.sendMessage(ChatColor.RED + "You must provide a name.");
                    return true;
                }

                String name = args[0];

                Location location = player.getLocation();
                WARPS.put(name, new BlockPosition(location.getX(), location.getY(), location.getZ()));
                FileUtil.save();

                sender.sendMessage(ChatColor.GREEN + "Placed warp " + name + " at your current position.");

                return true;
            }

            case "delwarp":
            case "deletewarp": {
                if (!sender.isOp() || player == null) {
                    return false;
                }

                if (args.length == 0) {
                    sender.sendMessage(ChatColor.RED + "You must provide a name.");
                    return true;
                }

                String name = getClosestTo(args[0]);
                if (WARPS.containsKey(name)) {
                    WARPS.remove(name);
                    FileUtil.save();
                    sender.sendMessage(ChatColor.GREEN + "Removed warp " + name + ".");
                } else {
                    player.sendRawMessage(ChatColor.RED + "That warp does not exist.");
                }
                return true;
            }
        }

        return super.onCommand(sender, command, label, args);
    }

    public static String getClosestTo(String name) {
        String stripped = ChatColor.stripColor(name);
        for (Entry<String, BlockPosition> entry : WARPS.entrySet()) {
            if (entry.getKey().equalsIgnoreCase(stripped)) {
                return entry.getKey();
            }
        }

        return null;
    }

    public static void teleport(Player player, String name) {
        BlockPosition pos = WARPS.getOrDefault(getClosestTo(name), null);
        if (pos == null) {
            player.sendRawMessage(ChatColor.RED + "That warp does not exist.");
        } else {
            player.teleport(new Location(player.getWorld(), pos.getX(), pos.getY(), pos.getZ()));
            player.sendRawMessage(ChatColor.GREEN + "Sent to warp.");
        }
    }
}
