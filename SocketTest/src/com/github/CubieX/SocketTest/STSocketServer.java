package com.github.CubieX.SocketTest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

import org.bukkit.command.CommandSender;

public class STSocketServer
{
   private SocketTest plugin = null; 
   private ServerSocket server = null;
   private boolean active = false;

   public STSocketServer(SocketTest plugin) throws IOException
   {
      this.plugin = plugin;
      server = new ServerSocket(SocketTest.port);
   }

   public void startListenerService(CommandSender sender)
   {
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
                        socket = server.accept();
                        SocketTest.log.info(SocketTest.logPrefix + "Client-Anfrage empfangen.");
                        handleReceivedMessageFromClient(socket);
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

                  // listener loop canceled. So prepare for new start and create new server instance with latest port number from config (for reloads).
                  try
                  {
                     server.close();
                     server= null;
                     SocketTest.log.info(SocketTest.logPrefix + "Server geschlossen.");
                     
                     server = new ServerSocket(SocketTest.port); // new instance to apply the new port in preparation for next start
                  }
                  catch (IOException e)
                  {
                     e.printStackTrace();
                  }
               }
            });
         }
         else
         {
            sender.sendMessage(SocketTest.logPrefix + "Server laeuft bereits!");
         }
      }
      else
      {
         try
         {
            server = new ServerSocket(SocketTest.port);  // try to create new instance again to get the server back on
         }
         catch (IOException e)
         {            
            e.printStackTrace();
         }

         sender.sendMessage(SocketTest.logPrefix + "Fehler bei Socket-Init. Bitte nochmals versuchen.");
      }
   }


   private void handleReceivedMessageFromClient(Socket socket) throws IOException
   {
      BufferedReader rein = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      PrintStream raus = new PrintStream(socket.getOutputStream());
      String s;

      while(rein.ready())
      {
         s = rein.readLine();
         SocketTest.log.info(SocketTest.logPrefix + "Sende Antwort: " + s);
         raus.println("ServerTXechoFromClient: " + s);
      }

      /* ##########################################################################
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
      });*/
   }

   public void stopListenerService(CommandSender sender)
   {
      if(active)
      {
         active = false; // this will end the server task after current run of listener loop
         sender.sendMessage(SocketTest.logPrefix + "Server wird nach naechster Client-Anfrage geschlossen...");
      }
      else
      {
         sender.sendMessage(SocketTest.logPrefix + "Server ist bereits geschlossen.");
      }
   }

   /*private void stopServer(PrintWriter out, BufferedReader in, Socket clientSocket, ServerSocket serverSocket)
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
   }*/ //#########################################################################################
}
