package com.github.CubieX.SocketTest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class STSocketServer
{
   private SocketTest plugin = null; 
   //private final ServerSocket server;
   //private boolean active = true;
   private int port = 4444;

   public STSocketServer(SocketTest plugin, int port) throws IOException
   {
      this.plugin = plugin;
      this.port = port;
      //server = new ServerSocket(port);       
   }

   public void startListenerService()
   {
      /*
   }
      plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable()
      {
         @Override
         public void run()
         {
            if(null != server)
            {
               SocketTest.log.info(SocketTest.logPrefix + "Starte Server und verbinde Socket...");

               while (active)
               {
                  Socket socket = null;
                  try
                  {
                     socket = server.accept();                     
                     reinRaus(socket);
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
            else
            {
               SocketTest.log.info(SocketTest.logPrefix + "Server laeuft bereits!");
            }
         }
      });
   }

   public void close()
   {      
      //Bukkit.getServer().getScheduler().cancelTasks(plugin); // cancel async server task
      active = false; // this will end the server task

      SocketTest.log.info(SocketTest.logPrefix + "Server geschlossen.");         
   }

   private void reinRaus(Socket socket) throws IOException
   {
      BufferedReader rein = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      PrintStream raus = new PrintStream(socket.getOutputStream());
      String s;

      while(rein.ready())
      {
         s = rein.readLine();
         raus.println("ServerTXechoFromClient: " + s);
      }*/

      plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable()
      {
         @Override
         public void run()
         {
            ServerSocket serverSocket = null;
            try
            {
               serverSocket = new ServerSocket(port);
            }
            catch (IOException e)
            {
               SocketTest.log.warning(SocketTest.logPrefix + "Could not listen on port: " + port + ".");
               stopServer(null, null, null, serverSocket);
               return;
            }

            Socket clientSocket = null;
            PrintWriter out = null;
            BufferedReader in = null;

            try
            {
               clientSocket = serverSocket.accept();
               SocketTest.log.info(SocketTest.logPrefix + "Server open. Listening on port: " + port);
            }
            catch (IOException e)
            {
               SocketTest.log.warning(SocketTest.logPrefix + "Accept failed. Stopping Server...");
               stopServer(out, in, clientSocket, serverSocket);
               return;
            }

            try
            {
               out = new PrintWriter(clientSocket.getOutputStream(), true);
               in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
               String inputLine, outputLine;
               STSocketProtocol kkp = new STSocketProtocol();

               outputLine = kkp.processInput(null);
               out.println(outputLine);

               while ((inputLine = in.readLine()) != null)
               {
                  outputLine = kkp.processInput(inputLine);
                  out.println(outputLine);
                  
                  if (outputLine.equals("Bye."))
                  {
                     break;
                  }
               }

               stopServer(out, in, clientSocket, serverSocket);               
            }
            catch(IOException ex)
            {
               ex.printStackTrace();
            }    
         }
      });
   }
   
   private void stopServer(PrintWriter out, BufferedReader in, Socket clientSocket, ServerSocket serverSocket)
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
         
         if (null != clientSocket)
         {
            clientSocket.close();
         }
         
         if (null != serverSocket)
         {
            serverSocket.close();
         }
         
         SocketTest.log.info(SocketTest.logPrefix + "Server closed.");
      }
      catch (IOException ex)
      {
         ex.printStackTrace();
      }
   }
}
