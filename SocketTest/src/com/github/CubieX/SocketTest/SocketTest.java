/*
 * SocketTest - A CraftBukkit plugin utilizing sockets to connect 2 servers
 * Copyright (C) 2013  CubieX
 * 
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with this program; if not,
 * see <http://www.gnu.org/licenses/>.
 */
package com.github.CubieX.SocketTest;

import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class SocketTest extends JavaPlugin
{
   public static final Logger log = Bukkit.getServer().getLogger();
   static final String logPrefix = "[SocketTest] "; // Prefix to go in front of all log entries

   private STCommandHandler comHandler = null;
   private STConfigHandler cHandler = null;
   private STEntityListener eListener = null;
   //private STSchedulerHandler schedHandler = null;
   private STSocketServer socketServer = null;
   private STSocketClient socketClient = null;
   
   public static final String ENCODING = "UTF-8";

   // config values
   static boolean debug = false;
   static boolean isServer = true;
   static String server = "localhost"; // address or IP of the server (only relevant for the client)
   static int port = 4444;             // port for communication between server ans client (must match on both sides!)

   //*************************************************
   static String usedConfigVersion = "1"; // Update this every time the config file version changes, so the plugin knows, if there is a suiting config present
   //*************************************************

   @Override
   public void onEnable()
   {
      cHandler = new STConfigHandler(this);

      if(!checkConfigFileVersion())
      {
         log.severe(logPrefix + "Outdated or corrupted config file(s). Please delete your config files."); 
         log.severe(logPrefix + "will generate a new config for you.");
         log.severe(logPrefix + "will be disabled now. Config file is outdated or corrupted.");
         getServer().getPluginManager().disablePlugin(this);
         return;
      }

      readConfigValues();

      eListener = new STEntityListener(this);

      if(isServer)
      {
         socketServer = new STSocketServer(this);
      }
      else
      {
         socketClient = new STSocketClient(this);
      }

      comHandler = new STCommandHandler(this, cHandler, socketServer, socketClient);      
      getCommand("st").setExecutor(comHandler);

      //schedHandler = new LWSchedulerHandler(this);

      if(!SocketTest.isServer)
      {
         log.info(this.getDescription().getName() + " SERVER version " + getDescription().getVersion() + " is enabled!");
      }
      else
      {
         log.info(this.getDescription().getName() + " CLIENT version " + getDescription().getVersion() + " is enabled!");
      }      

      //schedHandler.startPlayerInWaterCheckScheduler_SynchRepeating();
   }   

   private boolean checkConfigFileVersion()
   {      
      boolean configOK = false;     

      if(cHandler.getConfig().isSet("config_version"))
      {
         String configVersion = getConfig().getString("config_version");

         if(configVersion.equals(usedConfigVersion))
         {
            configOK = true;
         }
      }

      return (configOK);
   }  

   public void readConfigValues()
   {
      boolean exceed = false;
      boolean invalid = false;

      if(getConfig().contains("debug")){debug = getConfig().getBoolean("debug");}else{invalid = true;}
      if(getConfig().contains("isServer")){isServer = getConfig().getBoolean("isServer");}else{invalid = true;}
      if(getConfig().contains("server")){server = getConfig().getString("server");}else{invalid = true;}
      if(getConfig().contains("port")){port = getConfig().getInt("port");}else{invalid = true;}

      if(exceed)
      {
         log.warning(logPrefix + "One or more config values are exceeding their allowed range. Please check your config file!");
      }

      if(invalid)
      {
         log.warning(logPrefix + "One or more config values are invalid. Please check your config file!");
      }
   }

   @Override
   public void onDisable()
   {
      socketServer.stopListenerService(Bukkit.getConsoleSender());
      socketClient = null;
      socketServer = null;      
      this.getServer().getScheduler().cancelTasks(this);
      cHandler = null;
      eListener = null;
      comHandler = null;
      //schedHandler = null; // TODO ACTIVATE THIS AGAIN IF USED!
      log.info(this.getDescription().getName() + " version " + getDescription().getVersion() + " is disabled!");
   }

   // #########################################################

   public void restartListenerService(CommandSender sender)
   {
      if(isServer)
      {
         socketServer.stopListenerService(sender);
         socketServer.startListenerService(sender);
      }
   }
   
   public void sendSyncMessage(final CommandSender sender, final String msg, final boolean isErrorMsg)
   {
      getServer().getScheduler().runTask(this, new Runnable()
      { // use sync task to send message, in case the sender was a player
         @Override
         public void run()
         {
            if(null != sender)
            {
               if(isErrorMsg)
               {
                  sender.sendMessage(ChatColor.RED + msg);
               }
               else
               {
                  sender.sendMessage(ChatColor.WHITE + msg);  
               }               
            }
         }
      }); 
   }
}


