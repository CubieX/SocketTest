package com.github.CubieX.SocketTest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class STSocketServer
{
   private SocketTest plugin = null; 
   private ServerSocket server = null;
   private boolean active = false;

   public STSocketServer(SocketTest plugin)
   {
      this.plugin = plugin;      
   }

   public void startListenerService(CommandSender sender)
   {
      try
      {
         if (null == server)
         {
            server = new ServerSocket(SocketTest.port);
         }         
      }
      catch (IOException ex)
      {         
         ex.printStackTrace();
      }

      if(null != server)
      {
         if(!active)
         {
            active = true; // unlock listener WHILE loop

            plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable()
            {
               @Override
               public void run()
               {
                  SocketTest.log.info(SocketTest.logPrefix + "Starte Server...");

                  while (active)
                  {
                     Socket socket = null;

                     try
                     {
                        SocketTest.log.info(SocketTest.logPrefix + "Warte auf Client-Anfrage...");
                        // FIXME Wie den Eingangspuffer des Sockets/Ports leeren beim Serverstart?
                        // ansonsten werden ClientRequests die vor Serverstart gesendet wurden nachträglich verarbeitet
                        // was nicht gewollt ist.
                        socket = server.accept(); // server will wait at this point until a client request is received
                        SocketTest.log.info(SocketTest.logPrefix + "Client-Anfrage empfangen.");
                        handleReceivedRequestFromClient(socket);
                     }
                     catch (SocketException e)
                     {
                        // this will be called on closing the server while its in server.accept() mode. Ignore this.
                        //e.printStackTrace();
                     }
                     catch (IOException e)
                     {
                        e.printStackTrace();
                     }                     
                     finally
                     {
                        if (socket != null)
                        {
                           try
                           {
                              socket.close();
                           }
                           catch (IOException e)
                           {
                              e.printStackTrace();
                           }
                        }
                     }
                  }                  
               }
            });
         }
         else
         {
            sender.sendMessage(ChatColor.GOLD + SocketTest.logPrefix + "Server laeuft bereits!");
         }
      }
      else
      {
         sender.sendMessage(ChatColor.RED + SocketTest.logPrefix + "Fehler! Port scheint belegt zu sein. Bitte nochmals versuchen.");
      }
   }

   private void handleReceivedRequestFromClient(Socket socket) throws IOException
   {
      if((socket != null) && !socket.isClosed())
      {
         SocketTest.log.info(SocketTest.logPrefix + "Lese Client-Anfrage...");
         BufferedReader bufInputReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
         PrintStream printStream = new PrintStream(socket.getOutputStream());
         final ArrayList<String> requestList = new ArrayList<String>();

         while(bufInputReader.ready()) // only one request expected
         {
            requestList.add(bufInputReader.readLine());            
         }

         // handle requests and send responses back to client
         for(String req : requestList) // TODO STSocketProtocol Klasse hierfuer verwenden? Befehle definieren u.s.w.?
         {
            switch(req)
            {
            case "Hallo":
               if(SocketTest.debug){SocketTest.log.info(SocketTest.logPrefix + "Sende Antwort: " + "Hallo auch!");}
               printStream.println("ServerResponse: " + "Hallo auch!");
               break;
            case "Tschuess":
               if(SocketTest.debug){SocketTest.log.info(SocketTest.logPrefix + "Sende Antwort: " + "Tschuess auch!");}
               printStream.println("ServerResponse: " + "Tschuess auch!");
               break;
            default:
               if(SocketTest.debug){SocketTest.log.info(SocketTest.logPrefix + "Sende Antwort: '" + req + "' ist eine ungueltige Anfrage!");}
               printStream.println("ServerResponse: '" + req + "' ist eine ungueltige Anfrage!");
            }
         }

         // cleanup      
         bufInputReader.close();
         printStream.flush();
         printStream.close();
      }      
   }

   public void stopListenerService(CommandSender sender)
   {
      if(active)
      {
         try
         {
            active = false; // this will end the server task
            server.close();
            server= null;

            sender.sendMessage(SocketTest.logPrefix + "Server geschlossen.");
         }
         catch (IOException e)
         {
            sender.sendMessage(SocketTest.logPrefix + "Fehler beim Schliessen des Servers!.");
            e.printStackTrace();
         }
      }
      else
      {
         sender.sendMessage(SocketTest.logPrefix + "Server ist bereits geschlossen.");
      }
   }
}
