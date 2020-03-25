package me.davidml16.aparkour.events;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import me.davidml16.aparkour.managers.ColorManager;
import me.davidml16.aparkour.managers.PluginManager;
import me.davidml16.aparkour.utils.LocationUtil;
import me.davidml16.aparkour.utils.Sounds;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

import me.davidml16.aparkour.Main;

import java.util.ArrayList;
import java.util.List;

public class event_InventoryGUI implements Listener {

    @EventHandler
    public void onInventoryClickEvent(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();

        if (e.getCurrentItem() == null) return;

        if (Main.getInstance().getStatsGUI().getOpened().contains(p.getUniqueId())) {
            e.setCancelled(true);
            return;
        } else if (Main.getInstance().getRankingsGUI().getOpened().contains(p.getUniqueId())) {
            e.setCancelled(true);
            return;
        } else if (Main.getInstance().getWalkableBlocksGUI().getOpened().containsKey(p.getUniqueId())) {
            e.setCancelled(true);
            int slot = e.getRawSlot();
            String id = Main.getInstance().getWalkableBlocksGUI().getOpened().get(p.getUniqueId());
            if (slot == 40) {
                p.closeInventory();
                Main.getInstance().getConfigGUI().open(p, id);
            } else if (slot >= 45 && slot <= 80) {
                List<Material> walkable = Main.getInstance().getParkourHandler().getParkourById(id).getWalkableBlocks();
                if (walkable.size() < 21) {

                    if (e.getCurrentItem().getType() == Material.AIR) return;

                    if(!e.getCurrentItem().getType().isSolid() || e.getCurrentItem().getType().name().contains("PLATE")) {
                        p.sendMessage(ColorManager.translate(Main.getInstance().getLanguageHandler().getPrefix()
                                + "&cThe walkable block has to be a solid block!"));
                        Sounds.playSound(p, p.getLocation(), Sounds.MySound.NOTE_PLING, 10, 0);
                    } else {
                        if (!walkable.contains(e.getCurrentItem().getType())) {
                            p.sendMessage(ColorManager.translate(Main.getInstance().getLanguageHandler().getPrefix()
                                    + "&aYou add &e" + e.getCurrentItem().getType().name() + " &ato walkable blocks of parkour &e" + id));
                            walkable.add(e.getCurrentItem().getType());
                            Main.getInstance().getParkourHandler().getParkourById(id).setWalkableBlocks(walkable);
                            Main.getInstance().getWalkableBlocksGUI().reloadGUI(id);
                            Sounds.playSound(p, p.getLocation(), Sounds.MySound.CLICK, 10, 2);
                        } else {
                            p.sendMessage(ColorManager.translate(Main.getInstance().getLanguageHandler().getPrefix()
                                    + "&cThe block &e" + e.getCurrentItem().getType().name() + " &calready exists in walkable blocks of parkour &e" + id));
                            Sounds.playSound(p, p.getLocation(), Sounds.MySound.NOTE_PLING, 10, 0);
                        }
                    }
                }
            } else if ((slot >= 10 && slot <= 16) || (slot >= 19 && slot <= 25) || (slot >= 28 && slot <= 34)) {
                if (e.getCurrentItem().getType() == Material.AIR) return;

                if (Main.getInstance().getParkourHandler().getParkourById(id).getWalkableBlocks().size() == 0) return;

                p.sendMessage(ColorManager.translate(Main.getInstance().getLanguageHandler().getPrefix()
                        + "&aYou remove &e" + e.getCurrentItem().getType().name() + " &afrom walkable blocks of parkour &e" + id));
                List<Material> walkable = Main.getInstance().getParkourHandler().getParkourById(id).getWalkableBlocks();
                walkable.remove(e.getCurrentItem().getType());
                Main.getInstance().getParkourHandler().getParkourById(id).setWalkableBlocks(walkable);
                Main.getInstance().getWalkableBlocksGUI().reloadGUI(id);
                Sounds.playSound(p, p.getLocation(), Sounds.MySound.CLICK, 10, 2);
            }
            return;
        } else if (Main.getInstance().getConfigGUI().getOpened().containsKey(p.getUniqueId())) {
            e.setCancelled(true);
            int slot = e.getRawSlot();
            if ((slot >= 19 && slot <= 23)) {
                changeParkourConfig(p, slot);
            } else if (slot == 25) {
                if (e.getCurrentItem().getType() == Material.PISTON_BASE) {
                    String id = Main.getInstance().getConfigGUI().getOpened().get(p.getUniqueId());
                    p.closeInventory();
                    Main.getInstance().getWalkableBlocksGUI().open(p, id);
                }
            } else if (slot == 40) {
                if (e.getCurrentItem().getType() == Material.BARRIER) {
                    PluginManager.reloadAll();
                    p.sendMessage(Main.getInstance().getLanguageHandler().getMessage("COMMANDS_RELOAD", true));
                }
            }
            return;
        }
    }

    @EventHandler
    public void InventoryCloseEvent(InventoryCloseEvent e) {
        Player p = (Player) e.getPlayer();
        if (Main.getInstance().getStatsGUI().getOpened().contains(p.getUniqueId())) {
            Main.getInstance().getStatsGUI().getOpened().remove(p.getUniqueId());
            return;
        } else if (Main.getInstance().getRankingsGUI().getOpened().contains(p.getUniqueId())) {
            Main.getInstance().getRankingsGUI().getOpened().remove(p.getUniqueId());
            return;
        } else if (Main.getInstance().getConfigGUI().getOpened().containsKey(p.getUniqueId())) {
            Main.getInstance().getConfigGUI().getOpened().remove(p.getUniqueId());
            return;
        } else if (Main.getInstance().getWalkableBlocksGUI().getOpened().containsKey(p.getUniqueId())) {
            Main.getInstance().getWalkableBlocksGUI().getOpened().remove(p.getUniqueId());
            return;
        }
    }

    public void changeParkourConfig(Player p, int slot) {
        String id = Main.getInstance().getConfigGUI().getOpened().get(p.getUniqueId());

        switch (slot) {
            case 19:
                LocationUtil.setPosition(p, id, "spawn");
                break;
            case 20:
                LocationUtil.setPosition(p, id, "start");
                break;
            case 21:
                LocationUtil.setPosition(p, id, "end");
                break;
            case 22:
                LocationUtil.setHologram(p, id, "stats");
                break;
            case 23:
                LocationUtil.setHologram(p, id, "top");
                break;
            default:
                break;
        }

        if (slot != 25) {
            Sounds.playSound(p, p.getLocation(), Sounds.MySound.CLICK, 100, 3);
        }

        Main.getInstance().getConfigGUI().reloadGUI(id);
    }

}
