# Chat-Server-Client-Application

It is a basic Server based chat application in java using Sockets. 

** How to Run the Chat application **

Run the server_frame file  first, start the server and then the the client_frame  
Enter the details in IP Addredd as "localhost" and Port as what you set in the server side Interface since its the port ID that is initialised in the code

If the client code is still incompete then you can use cmd prompt or terminal depending on your OS to run the chat client
Open terminal or cmd prompt and type in 
telnet localhost <PortID>     // Basically replecating the client
                              // You can do this as mnay times as you want
                        
 ** Here are some commands to interact to the server and with other clients **
 // Enter the commands without the "( )" sign to iunteract with the Server 
 
 login  (username)   // logs you into the server 
  
 msg (receiver) (message_body)  // send a message to your targeted receiver
  
 kick (username)    // kicks the specified user from the server
  
 join (group_name)  // join a group chat
 
 leave (group_name) // leave the group chat
 
 msg (group_name) (message_body)    // msg all the participants in the specified group chat
  
 announcemnet (message_body)    // send a message to all the active users in the server 
 
 quit   // disconnects you from the server
 
 ListUser  // lists all the current active users and their server ID in the server
 
 NUKE   // spams "WORLD DOMONATION" all throughout the server and all active client window for 10 seconds
 
 

** Currently Under Developement **

**It is incomplete and still under developement
**Currently working on the Chat Client API to recieve the messages to the Client UI



** TO DO List **

Add functionality for a Server Admin to look after the server
