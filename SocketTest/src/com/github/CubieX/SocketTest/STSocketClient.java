package com.github.CubieX.SocketTest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class STSocketClient
{
   private SocketTest plugin = null;

   public STSocketClient(SocketTest plugin) throws IOException
   {
      this.plugin = plugin;
   }

   public void stringClient_send()
   {
      plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable()
      {
         @Override
         public void run()
         {
            /* #################################################################
            Socket clientSocket = null;
            PrintWriter out = null;
            BufferedReader in = null;
            InetAddress hostIP = null;

            try
            {
               hostIP = InetAddress.getByName(host);
               clientSocket = new Socket(hostIP, port);
               out = new PrintWriter(clientSocket.getOutputStream(), true);
               in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            }
            catch (UnknownHostException e)
            {
               SocketTest.log.severe(SocketTest.logPrefix + "Don't know about host: " + host);
               stopClient(out, in, null, clientSocket);
               return;
            }
            catch (IOException e)
            {
               SocketTest.log.severe(SocketTest.logPrefix + "Couldn't get I/O for the connection to: " + host + ". Stopping Client.");
               stopClient(out, in, null, clientSocket);
               return;
            }

            try
            {
               BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
               String fromServer;
               String fromUser;

               SocketTest.log.info(SocketTest.logPrefix + "Client running.");

               while ((fromServer = in.readLine()) != null)
               {
                  SocketTest.log.info(SocketTest.logPrefix + "Server: " + fromServer);

                  if (fromServer.equals("Bye."))
                  {
                     break;                     
                  }

                  fromUser = stdIn.readLine();

                  if (fromUser != null)
                  {
                     SocketTest.log.info(SocketTest.logPrefix + "Client: " + fromUser);
                     out.println(fromUser);
                  }
               }

               stopClient(out, in, stdIn, clientSocket);               
            }
            catch (IOException ex)
            {
               ex.printStackTrace();
            }

             */ //###########################################################

            Socket socket = null;
            InetAddress hostIP = null;
            
            try
            {
               hostIP = InetAddress.getByName(SocketTest.server); // can handle strings like "my.server.ip.com" and "123.21.15.67"
               socket = new Socket(hostIP, SocketTest.port);

               OutputStream outStream = socket.getOutputStream();
               PrintStream pStream = new PrintStream(outStream, true);
               
               pStream.println("ClientRequest: Hallo Welt!"); // send request to server
               pStream.println("ClientRequest: Hallo Otto!"); // send request to server

               InputStream inStream = socket.getInputStream(); // receive servers answer
               SocketTest.log.info(SocketTest.logPrefix + "verfuegbare Bytes: " + inStream.available()); // FIXME Wieso immer 0 Bytes??
               // der Server antwortet definitiv. Also scheitert das Lesen im Client hier.
               BufferedReader buffReader = new BufferedReader(new InputStreamReader(inStream)); // read servers answer

               while (buffReader.ready())
               {
                  SocketTest.log.info(SocketTest.logPrefix + "ServerAnswer: " + buffReader.readLine());
               }

            }
            catch (UnknownHostException e)
            {
               SocketTest.log.info(SocketTest.logPrefix + "Unbekannter Host!");
               e.printStackTrace();
            }
            catch (IOException e)
            {
               SocketTest.log.info(SocketTest.logPrefix + "IO Probleme!");
               e.printStackTrace();
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
                     SocketTest.log.info(SocketTest.logPrefix + "Socket geschlossen.");
                  }
                  catch (IOException e)
                  {
                     SocketTest.log.info(SocketTest.logPrefix + "Fehler beim Schliessen des Sockets!");
                     e.printStackTrace();
                  }
               }
            }
         }
      });
   }
   
   //###################################################
   /*private void stopClient(PrintWriter out, BufferedReader in, BufferedReader stdIn, Socket kkSocket)
   {
      try
      {
         if (null != out)
         {
            out.close();
         }

         if (null != in)
         {
            in.close();
         }

         if (null != stdIn)
         {
            stdIn.close();
         }

         if (null != kkSocket)
         {
            kkSocket.close();
         }

         SocketTest.log.info(SocketTest.logPrefix + "Client stopped.");
      }
      catch (IOException ex)
      {
         ex.printStackTrace();
      }
   }*/
}
