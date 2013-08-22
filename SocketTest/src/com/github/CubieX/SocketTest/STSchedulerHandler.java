package com.github.CubieX.SocketTest;

public class STSchedulerHandler
{
   private SocketTest plugin = null;

   public STSchedulerHandler(SocketTest plugin)
   {
      this.plugin = plugin;
   }

   public void startPlayerInWaterCheckScheduler_SynchRepeating()
   {
      plugin.getServer().getScheduler().runTaskTimer(plugin, new Runnable()
      {
         public void run()
         {
                    
         }
      }, 10 * 20L, 1 * 20L); // 10 seconds delay, 1 second cycle
   }
}
