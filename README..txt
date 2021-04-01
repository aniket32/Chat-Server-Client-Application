# Chat-Server-Client-Application

It is a basic Server based chat application in java using Sockets. 

** How to Run the Chat application **

Run the server_frame file  first, start the server and then the the client_frame  
Enter the details in IP Addredd as "localhost" and Port as what you set in the server side Interface since its the port ID that is initialised in the code
                        
 ** Here are some commands to interact to the server and with other clients **
 // Enter the commands without the "( )" sign to iunteract with the Server 
 
 login  (username)   // Logs you into the server 
  
 msg (receiver) (message_body)  // Send a message to your targeted receiver
  
 kick (username)    // Kicks the specified user from the server
  
 join (group_name)  // Join a group chat
 
 leave (group_name) // Leave the group chat
 
 msg (group_name) (message_body)    // Msg all the participants in the specified group chat
  
 sendall (message_body)    // Send a message to all the active users in the server 
 
 quit   // Disconnects you from the server
 
 ListUser    // Lists all the current active users along with their Port ID, IP Address
 
 NUKE   // Initialize WORLD DOMINATION

 checkStatus 	// Sends a message to every client asking if they are online and according to answer, the admin will get feedback from server
 
 InactiveUser // Returns a list of all users that did not respond to the checkStatus command.
 
 promote (username) // If run by admin, will give username entered admin priviledges.
 
 demote (username) // Removes admin privilidges from given username.
 
 pass-ownership (username) // Resets all admins to clients and gives admin to specified username.
 
 stop-server (time in seconds)	// closes the server and closes all open Client Connections 
