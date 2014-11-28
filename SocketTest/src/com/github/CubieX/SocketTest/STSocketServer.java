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

public class STSocketServer // TCP
{
   private SocketTest plugin = null; 
   private ServerSocket server = null;
   private boolean active = false;

   public STSocketServer(SocketTest plugin)
   {
      this.plugin = plugin;      
   }

   public void startListenerService(final CommandSender sender)
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
                  plugin.sendSyncMessage(sender, SocketTest.logPrefix + "Starte Server auf Port: " + SocketTest.port, false);

                  while (active)
                  {
                     Socket socket = null;

                     try
                     {
                        if(SocketTest.debug){SocketTest.log.info(SocketTest.logPrefix + "Warte auf Client-Anfrage...");}
                        // FIXME Wie den Eingangspuffer des Sockets/Ports leeren beim Serverstart?
                        // ansonsten werden ClientRequests die vor Serverstart gesendet wurden nachträglich verarbeitet
                        // was nicht gewollt ist.
                        socket = server.accept(); // server will wait at this point until a client request is received
                        if(SocketTest.debug){SocketTest.log.info(SocketTest.logPrefix + "Client-Anfrage empfangen.");}
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
   
   /* UDP Socket Server
    public static void main(String args[]) throws Exception
      {
         DatagramSocket serverSocket = new DatagramSocket(9876);
            byte[] receiveData = new byte[1024];
            byte[] sendData = new byte[1024];
            while(true)
               {
                  DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                  serverSocket.receive(receivePacket);
                  String sentence = new String( receivePacket.getData());
                  System.out.println("RECEIVED: " + sentence);
                  InetAddress IPAddress = receivePacket.getAddress();
                  int port = receivePacket.getPort();
                  String capitalizedSentence = sentence.toUpperCase();
                  sendData = capitalizedSentence.getBytes();
                  DatagramPacket sendPacket =
                  new DatagramPacket(sendData, sendData.length, IPAddress, port);
                  serverSocket.send(sendPacket);
               }
      } - See more at: http://systembash.com/content/a-simple-java-udp-server-and-udp-client/#sthash.MQ3Tqu49.dpuf
    */

   private void handleReceivedRequestFromClient(Socket socket) throws IOException
   {
      if((socket != null) && !socket.isClosed())
      {
         if(SocketTest.debug){SocketTest.log.info(SocketTest.logPrefix + "Lese Client-Anfrage...");}
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
               printStream.println(ChatColor.WHITE + "[R] " + "Hallo auch!");
               break;
            case "Tschuess":
               if(SocketTest.debug){SocketTest.log.info(SocketTest.logPrefix + "Sende Antwort: " + "Tschuess auch!");}
               printStream.println(ChatColor.WHITE + "[R] " + "Tschuess auch!");
               break;
            case "gc":
               if(SocketTest.debug){SocketTest.log.info(SocketTest.logPrefix + "Sende Antwort: " + "/gc data");}
               
               Runtime runtime = Runtime.getRuntime();
               long maxRAM = runtime.maxMemory() / (1024 * 1024);               
               long reservedRAM = runtime.totalMemory() / (1024 * 1024);
               long freeRAM = runtime.freeMemory() / (1024 * 1024);
               long usedRAM = reservedRAM - freeRAM;               
               
               printStream.println(ChatColor.WHITE + "[R] " + "RAM maximal fuer VM: " + ChatColor.GREEN + String.format("%,d", maxRAM) + ChatColor.WHITE + " MB");
               printStream.println(ChatColor.WHITE + "[R] " + "davon reserviert: " + ChatColor.GREEN + String.format("%,d", reservedRAM) + ChatColor.WHITE + " MB");
               printStream.println(ChatColor.WHITE + "[R] " + "davon genutzt: " + ChatColor.GREEN + String.format("%,d", usedRAM) + ChatColor.WHITE + " MB");
               printStream.println(ChatColor.WHITE + "[R] " + "davon frei: " + ChatColor.GREEN + String.format("%,d", freeRAM) + ChatColor.WHITE + " MB");
               //printStream.println(ChatColor.WHITE + "[R] " + "TPS: " + Bukkit.getServer().get + " MB"); // TODO
               break;
            default:
               if(SocketTest.debug){SocketTest.log.info(SocketTest.logPrefix + "Sende Antwort: '" + req + "' ist eine ungueltige Anfrage!");}
               printStream.println(ChatColor.WHITE + "[R] '" + req + "' ist eine ungueltige Anfrage!");
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
