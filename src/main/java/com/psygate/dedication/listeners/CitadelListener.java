/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.psygate.dedication.listeners;

import com.psygate.dedication.data.PlayerData;
import java.util.Map;
import java.util.UUID;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import vg.civcraft.mc.citadel.Citadel;
import vg.civcraft.mc.citadel.events.ReinforcementDamageEvent;
import vg.civcraft.mc.citadel.reinforcement.PlayerReinforcement;
import vg.civcraft.mc.citadel.reinforcement.Reinforcement;
import com.psygate.dedication.Dedication;

/**
 *
 * @author florian
 */
public class CitadelListener implements Listener {

    private final Map<UUID, PlayerData> dedication;
    private final PvPListener pvp;

    public CitadelListener(Map<UUID, PlayerData> dedication, PvPListener pvp) {
        this.dedication = dedication;
        this.pvp = pvp;
    }

    @EventHandler
    public void citadelPrevention(ReinforcementDamageEvent ev) {
        if (ev.getPlayer() == null) {
            return;
        }
        Reinforcement reinf = ev.getReinforcement();
        if (!(reinf instanceof PlayerReinforcement)) return;
        PlayerReinforcement playerReinf = (PlayerReinforcement) reinf;
        
        UUID playerId = ev.getPlayer().getUniqueId();
        
        if (!dedication.get(playerId).isDedicated()) {
        	if (!canBreakCitadel(playerReinf, ev.getPlayer())) {
        		ev.setCancelled(true);
                Dedication.sendMessage(ev.getPlayer(), ChatColor.BOLD + "" + ChatColor.RED + Dedication.PREFIX + "You cannot break this reinforcement; check www.reddit.com/r/Devoted for more information.");
        	}
        }/* else {
        	if (!playerReinf.canBypass(ev.getPlayer())) {
                for (UUID uuid : playerReinf.getGroup().getAllMembers()) {
                    pvp.putInCombat(ev.getPlayer(), uuid);
                }
        	}
        }*/
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent ev) {
        if (ev.getPlayer() == null || Dedication.initPlayer(ev.getPlayer().getUniqueId()).isDedicated()) {
            return;
        }
        if (ev.getClickedBlock() == null || !(ev.getAction() == Action.RIGHT_CLICK_BLOCK)) {
            // Skip this. Player didnt click a block.
            return;
        }

        if (Dedication.getConfiguration().getLockedBlocks().contains(ev.getClickedBlock().getType())
                && Citadel.getReinforcementManager().isReinforced(ev.getClickedBlock())) {

            Reinforcement rf = Citadel.getReinforcementManager().getReinforcement(ev.getClickedBlock());

            if (rf instanceof PlayerReinforcement) {
                PlayerReinforcement prf = (PlayerReinforcement) rf;
                if (!canIgnoreCitadel(prf, ev.getPlayer())) {
                    ev.setCancelled(true);
                    Dedication.sendMessage(ev.getPlayer(), ChatColor.BOLD + "" + ChatColor.RED + Dedication.PREFIX + "You cannot open this container; check www.reddit.com/r/Devoted for more information.");
                }
            }
        }
    }

    private boolean canIgnoreCitadel(PlayerReinforcement prf, Player player) {
        return prf.canAccessChests(player);
    }

    private boolean canBreakCitadel(PlayerReinforcement prf, Player player) {
        return prf.canBypass(player);
    }
}
