package com.github.CubieX.SocketTest;

import java.util.UUID;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class STEntityListener implements Listener
{
   private SocketTest plugin = null;

   UUID leashedEntity;

   public STEntityListener(SocketTest plugin)
   {        
      this.plugin = plugin;

      plugin.getServer().getPluginManager().registerEvents(this, plugin);
   }

   //================================================================================================
   @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
   public void onPlayerInteract(PlayerInteractEvent e)
   {
      
   }
}
