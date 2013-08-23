package com.github.CubieX.SocketTest;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class STCommandHandler implements CommandExecutor
{
   private SocketTest plugin = null;
   private STConfigHandler cHandler = null;
   private STSocketServer socketServer = null;
   private STSocketClient socketClient = null;

   public STCommandHandler(SocketTest plugin, STConfigHandler cHandler, STSocketServer socketServer, STSocketClient socketClient) 
   {
      this.plugin = plugin;
      this.cHandler = cHandler;
      this.socketServer = socketServer;
      this.socketClient = socketClient;
   }

   @Override
   public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
   {
      Player player = null;

      if (sender instanceof Player) 
      {
         player = (Player) sender;
      }

      if (cmd.getName().equalsIgnoreCase("st"))
      {
         if (args.length == 0)
         { //no arguments, so help will be displayed
            return false;
         }

         if (args.length==1)
         {
            if (args[0].equalsIgnoreCase("version") || args[0].equalsIgnoreCase("status"))
            {               
               if(SocketTest.isServer)
               {
                  sender.sendMessage(ChatColor.GREEN + "This server is running " + plugin.getDescription().getName() + ChatColor.WHITE + " SERVER " + ChatColor.GREEN + "version " + plugin.getDescription().getVersion());
                  sender.sendMessage(ChatColor.GREEN + "Using port: " + ChatColor.WHITE + SocketTest.port);
               }
               else
               {
                  sender.sendMessage(ChatColor.GREEN + "This server is running " + plugin.getDescription().getName() + ChatColor.WHITE + " CLIENT " + ChatColor.GREEN + "version " + plugin.getDescription().getVersion());
                  sender.sendMessage(ChatColor.GREEN + "Using port: " + ChatColor.WHITE + SocketTest.port);
               }

               return true;
            }

            if (args[0].equalsIgnoreCase("reload"))
            {
               if(sender.isOp() || sender.hasPermission("sockettest.admin"))
               {                        
                  cHandler.reloadConfig(sender);
                  return true;
               }
               else
               {
                  sender.sendMessage(ChatColor.RED + "You do not have sufficient permission to reload " + plugin.getDescription().getName() + "!");
               }
            }

            if (args[0].equalsIgnoreCase("serverstart"))
            {
               if(sender.isOp() || sender.hasPermission("sockettest.admin"))
               {
                  if(SocketTest.isServer)
                  {
                     socketServer.startListenerService(sender);
                  }
                  else
                  {
                     SocketTest.log.severe(SocketTest.logPrefix + "Dies ist der Client!");
                  }                  
               }
               else
               {
                  sender.sendMessage(ChatColor.RED + "You do not have sufficient permission to start the server!");
               }
               
               return true;
            }

            if (args[0].equalsIgnoreCase("serverstop"))
            {
               if(sender.isOp() || sender.hasPermission("sockettest.admin"))
               {                        
                  if(SocketTest.isServer)
                  {
                     socketServer.stopListenerService(sender);
                  }
                  else
                  {
                     SocketTest.log.severe(SocketTest.logPrefix + "Dies ist der Client!");
                  }
               }
               else
               {
                  sender.sendMessage(ChatColor.RED + "You do not have sufficient permission to start the server!");
               }
                              
               return true;
            }

            if (args[0].equalsIgnoreCase("clientsend"))
            {
               if(sender.isOp() || sender.hasPermission("sockettest.admin"))
               {
                  if(!SocketTest.isServer)
                  {
                     socketClient.stringClient_send();
                  }
                  else
                  {
                     SocketTest.log.severe(SocketTest.logPrefix + "Dies ist der Server!");
                  }                  
               }
               else
               {
                  sender.sendMessage(ChatColor.RED + "You do not have sufficient permission to start the client!");
               }
               
               return true;
            }

            if (args[0].equalsIgnoreCase("send"))
            {
               if(sender.isOp() || sender.hasPermission("sockettest.admin"))
               {                        

                  
               }
               else
               {
                  sender.sendMessage(ChatColor.RED + "You do not have sufficient permission to reload " + plugin.getDescription().getName() + "!");
               }
               
               return true;
            }
         }
         else
         {
            sender.sendMessage(ChatColor.YELLOW + "Falsche Parameteranzahl.");
         }                

      }         
      return false; // if false is returned, the help for the command stated in the plugin.yml will be displayed to the player
   }
}
