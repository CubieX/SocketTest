package com.github.CubieX.SocketTest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class STSocketClient
{
   private SocketTest plugin = null;
   private final int MAX_ATTEMPS = 20;

   public STSocketClient(SocketTest plugin) // TCP
   {
      this.plugin = plugin;
   }

   public void sendClientRequest(final CommandSender sender, final String request)
   {
      plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable()
      {
         @Override
         public void run()
         {
            Socket socket = null;
            InetAddress hostIP = null;

            try
            {
               hostIP = InetAddress.getByName(SocketTest.server); // can handle strings like "my.server.ip.com" and "123.21.15.67"
               socket = new Socket(hostIP, SocketTest.port);

               OutputStream outStream = socket.getOutputStream();
               PrintStream pStream = new PrintStream(outStream, true);

               pStream.println(request); // send request to server
               //pStream.println("ClientRequest: Hallo Welt!"); // send request to server (more than one is theoretiocally possible at a time)
               //pStream.println("ClientRequest: Hallo Otto!"); // send request to server
               
               InputStream inStream = socket.getInputStream(); // receive servers answer

               int attemps = 0;

               while((inStream.available()) == 0 && (attemps < MAX_ATTEMPS))
               {
                  try
                  {
                     attemps++;
                     Thread.sleep(50); // wait 50 ms until next check of input stream                     
                  }
                  catch (InterruptedException e)
                  {
                     e.printStackTrace();
                  }
               }

               if(SocketTest.debug){SocketTest.log.info(SocketTest.logPrefix + "Empfangsversuche: " + attemps);}

               if(inStream.available() > 0)
               {
                  if(SocketTest.debug){SocketTest.log.info(SocketTest.logPrefix + "verfuegbare Bytes: " + inStream.available());}                  
                  BufferedReader bufInputReader = new BufferedReader(new InputStreamReader(inStream)); // read servers response

                  final ArrayList<String> responseList = new ArrayList<String>();

                  while (bufInputReader.ready()) // server may send multiple lines as response
                  {
                     responseList.add(bufInputReader.readLine());                                                         
                  }

                  plugin.getServer().getScheduler().runTask(plugin, new Runnable()
                  { // use sync task to send message, in case the sender was a player
                     @Override
                     public void run()
                     {
                        for(String res : responseList) // loop through all received response messages and handle them
                        {                          
                           sender.sendMessage(res);   
                        }
                     }
                  });

                  // cleanup                  
                  bufInputReader.close();
                  inStream.close();                  
                  outStream.close();
                  pStream.flush();
                  pStream.close();
               }
               else
               {
                  plugin.sendSyncMessage(sender, SocketTest.logPrefix + "Keine Antwort vom Server erhalten.", true);
               }
            }
            catch (UnknownHostException e)
            {
               sendExceptionMessage(sender, e);               
            }
            catch (IOException e)
            {               
               sendExceptionMessage(sender, e);               
            }
            finally
            {
               if (socket != null)
               {
                  try
                  {
                     if(!socket.isClosed())
                     {
                        socket.close();
                     }
                     if(SocketTest.debug){SocketTest.log.info(SocketTest.logPrefix + "Socket geschlossen.");}
                  }
                  catch (IOException e)
                  {
                     sendExceptionMessage(sender, e);
                     //SocketTest.log.info(SocketTest.logPrefix + "Fehler beim Schliessen des Sockets!");                     
                  }
               }
            }
         }
      });
   }
   
   /* UDP Socket Client
    public static void main(String args[]) throws Exception
   {
      BufferedReader inFromUser =
         new BufferedReader(new InputStreamReader(System.in));
      DatagramSocket clientSocket = new DatagramSocket();
      InetAddress IPAddress = InetAddress.getByName("localhost");
      byte[] sendData = new byte[1024];
      byte[] receiveData = new byte[1024];
      String sentence = inFromUser.readLine();
      sendData = sentence.getBytes();
      DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 9876);
      clientSocket.send(sendPacket);
      DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
      clientSocket.receive(receivePacket);
      String modifiedSentence = new String(receivePacket.getData());
      System.out.println("FROM SERVER:" + modifiedSentence);
      clientSocket.close();
   } - See more at: http://systembash.com/content/a-simple-java-udp-server-and-udp-client/#sthash.MQ3Tqu49.dpuf
    */

   void sendExceptionMessage(final CommandSender sender, final Exception ex)
   {
      plugin.getServer().getScheduler().runTask(plugin, new Runnable()
      { // use sync task to send message, in case the sender was a player
         @Override
         public void run()
         {
            if(ex instanceof UnknownHostException)
            {
               sender.sendMessage(ChatColor.RED + SocketTest.logPrefix + "Unbekannter Server: " + SocketTest.server);
               ex.printStackTrace();
            }
            else if (ex instanceof ConnectException)
            {
               sender.sendMessage(ChatColor.RED + SocketTest.logPrefix + "Verbindungsaufbau zum Server fehlgeschlagen.");
            }
            else if (ex instanceof IOException)
            {
               sender.sendMessage(ChatColor.RED + SocketTest.logPrefix + "IO Probleme.");
               ex.printStackTrace();
            }
            else
            {
               sender.sendMessage(ChatColor.RED + SocketTest.logPrefix + "Fehler bei Bearbeitung der Anfrage! (siehe Logfile)");
               ex.printStackTrace();
            }
         }
      }); 
   }   
}
