package legit_chat_server;

//Importing all the Packages 
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.LinkedList;
import java.io.File;
import java.util.Scanner;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayDeque;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/*
TODO LIST:


Need help calling the stopServer() function from outside the ServerWorker class




*/

/*
Added Functionality 
Added AFK but need help with removing users when they are AFK

*/

public class server_frame extends javax.swing.JFrame {
    // List of lists to store ArrayList object containing username,userID,Port,IP Address,Role 
    ArrayList<ArrayList> usr_id_list = new ArrayList<ArrayList>();
    // Declairing global variables
    // Forgot where they are used, just leave them will do semothing about them
    // later
    PrintWriter printerOut;
    ArrayDeque<String> role_queue = new ArrayDeque<String>();
    Socket clientWindow;
    public class StartServer implements Runnable {
        @Override
        public void run() {
            String portID = portId.getText();
            int port = Integer.parseInt(portID);
            ServerS server = new ServerS(port);
      
             server.start();
        }
    }

    // Works
    public class ServerS extends Thread {
        // Main reason for creating this class is to havce a clooection for this workers
        // for every Client that joins the Server
        private final int serverPort;

        private ArrayList<ServerWorker> workerList = new ArrayList<>();

        public ServerS(int serverPort) {
            this.serverPort = serverPort;
        }

        // Works
        public List<ServerWorker> getworkerList() {
            return workerList;
        }

        @Override
        public void run() {
            try {
                ServerSocket serverSocket = new ServerSocket(serverPort);
                // Accepting Client Sockets and creates the connection between them
                // clientSocket used to identify Clients
                while (true) {
                    console_text.append( new Date() + " About to accept connections....\n ");
                    // While loop to keep looking or accepting from clients
                    Socket clientSocket = serverSocket.accept();
                    console_text.append( new Date() + " Accepting Connections from \n " + clientSocket);
                    ServerWorker worker = new ServerWorker(this, clientSocket);
                    InetAddress Ip = clientSocket.getLocalAddress();
                    // System.out.print(Ip);
                    workerList.add(worker);
                    console_text.append(String.valueOf(workerList));
                    worker.start();
                }
            } catch (IOException e) {
                // print in server console if error in Creating Server~
                console_text.append( new Date() + " Server broke \n");
            }
        }
    }

    // Best Code I ==> AB has written in my entire life
    public class ServerWorker extends Thread {
        public final Socket clientSocket;
        public ServerS server;
        public String username = null;
        private OutputStream outputStream;
        //private InputStream inputStream;
        private HashSet<String> SetTopic = new HashSet<>();

        // Passing a Server instance to each ServerWorker
        public ServerWorker(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        public ServerWorker(ServerS server, Socket clientSocket) {
            this.server = server;
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {
            try {
                handleClientSocket();
            } catch (IOException | InterruptedException ex) {
            }
        }

        // Best Code I ==> AB has writtem in my entire life
        private void handleClientSocket() throws InterruptedException, IOException {
            // Main function to handel all the Users and in case the Moderators action in the Server
            try (clientSocket) {
                // Main function to handel all the Users and in case the Moderators action in the Server
                InputStream inputStream = clientSocket.getInputStream();
                this.outputStream = clientSocket.getOutputStream();
                //this.inputStream = clientSocket.getInputStream();
                printerOut = new PrintWriter(this.outputStream, true);
                
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null){
                    String tokens[] = line.split(" ");
                    if (tokens!= null && tokens.length>0){
                        
                        // Declairing the first token or the first word as the Command
                        String cmd = tokens[0];
                        // Switch case to choose from different types of commands
                        switch(cmd){
                            case "login":
                                // calls the loginHandler function to handle login 
                                loginHandler(outputStream, tokens);
                                adminStatus();
                                break;
                            case "join":
                                // calls the joinHandler function to join a group
                                joinHandler(tokens);
                                break;
                            case "leave":
                                 // calls the leaveHandler function to leave a group
                                leaveHandler(tokens);
                                break;
                            case "msg":
                                // Message a receiver
                                String[] tokensMsg = line.split(" ", 3);
                                messageHandler(tokensMsg);
                                break;
                            case "privatemsg":
                                // privately message a user
                                privateMsg();
                            case "sendall":
                                // calls the sendAll function to send a message to all the clients
                                String[] token = line.split(" ", 2);
                                announcements(token);
                                break;
                            case "ListUser":
                                // calls the ListUser function to lists all the activ users in the serve
                                getAll();
                                break;
                            case "NUKE":
                                // calls the NUKE function and Spams WORLD DOMINATION all over the server and client
                                //String[] tokenMsg = line.split(" ");
                                Boolean v1 = getStatus();
                                if(v1 == true){
                                    NUKE();
                                }else{
                                    wrongPermission();
                                }
                                break;
                            case "sleep":
                                // calls the sleep fucntion and bans a client for a certain time
                                 Boolean v8 = getStatus();
                                if(v8 == true){
                                    String[] Msg = line.split(" ");
                                    sleep(Msg);
                                }else{
                                    wrongPermission();
                                }
                                break;
                            case "kick":
                                // calls the kickuser functin and removes a client from the Server
                                Boolean v2 = getStatus();
                                if(v2 == true){
                                    String[] msg = line.split(" ");
                                    kickUser(msg);
                                }else{
                                    wrongPermission();
                                }
                                break;
                            case "help":
                                //calls the help function and show all commands for the admin and the clients
                                help();
                                break;
                            case "quit":
                                // calls the disconnectHandler functin and removes a user from the 
                                disconnectHandler();
                                //adminStatus();
                                break;
                            case "stop-server":
                                //calls function to stop the Server
                                Boolean v3 = getStatus();
                                if(v3 == true){
                                    String[] msg = line.split(" ", 2);
                                    stopServer(msg);
                                }else{
                                    wrongPermission();
                                }
                                break;
                            case "sendStatus":
                                //calls a function to send the satatus of all online users to all the Clients
                                //sendStatus();
                                getStatus();
                                break;
                            case "checkStatus":
                                //calls a function to check if the Clients are online or not
                                Boolean v4 = getStatus();
                                if(v4 == true){
                                    checkStatus();
                                }else{
                                    wrongPermission();
                                }
                                break;
                            case "changeStatus":
                                //call a function to keep the Clinet in the Server
                                //changeStatus();
                                break;
                            case "no":
                                //calls a function to kick the Clinet out of the server
                                break;
                            case "promote":
                                Boolean v5 = getStatus();
                                if(v5 == true){
                                    String[] usr = line.split(" ");
                                    promote(usr);
                                    adminStatus();
                                }else{
                                    wrongPermission();
                                }
                                break;
                            case "demote":
                                Boolean v6 = getStatus();
                                if(v6 == true){
                                    String[] usr = line.split(" ");
                                    demote(usr);
                                    adminStatus();
                                }else{
                                    wrongPermission();
                                }
                                break;
                            case "pass-ownership":
                                Boolean v7 = getStatus();
                                if(v7 == true){
                                    String[] usr = line.split(" ");
                                    passOwnership(usr);
                                    adminStatus();
                                }else{
                                    wrongPermission();
                                }
                                break;
                            default:
                                // sends a default message for unknown commands
                                String defMsg = " Unknown " + cmd + "\n";
                                outputStream.write(defMsg.getBytes());       
                        }
                    }
                }
            }
       }

        // Functions
        // gets the Login name ==> String
        public String getLogin() {
            return username;
        }
        

        // gets the PortID ==> Int
        public int getPort() {
            return clientSocket.getPort();
        }
        

        // gets the IP address ==> InetAddress
        // in this case all the IP Address will be ==> 127.0.0.1(GUI) or
        // 0.0.0.0.0.1(Command Line)
        public InetAddress getIP() {
            return clientSocket.getInetAddress();
        }
        
        // gets the Status of the Clients ==> Boolean
        public Boolean getStatus(){
            String status = null;
            Boolean states = null;
            for (int i = 0; i < usr_id_list.size(); i++) { 
                ArrayList info_list = (ArrayList) usr_id_list.get(i);
                    if(username.equals(info_list.get(0))){
                        status = (String) info_list.get(4);
                        if (status.equals("Admin")){
                            states = true;
                        }else if (status.equals("Client")){
                        states = false;
                    }
                }
            }
            return states;
        }

            
        // Changes the status of all the Clients to Client except the first one
        public String changeStatus(String usr){
            String role = null;
            //String status = null;
            if (usr.equals(role_queue.peek())){
                role = "Admin";
            } else {
                role = "Client";
            }
           
            return role;  
        }
        
        
        // Lets all the user knows the active admin of the server
        public void StatusUpdater() throws IOException{
            String usr = role_queue.peek();
            List<ServerWorker> workerList = server.getworkerList();
            for (int i = 0; i < usr_id_list.size(); i++) {
             ArrayList list = (ArrayList) usr_id_list.get(i);
             if (usr.equals(list.get(0))) {
                 System.out.println(list);
                 list.set(4, "Admin");
                 usr_id_list.set(i, list);
                }
            } 
            String Msg = new Date() + usr + " is now the admin of the Server \n";
                for (ServerWorker worker : workerList) {
                    if (!username.equals(worker.getLogin())) {
                        worker.sendMsg(Msg);
                    }
                }
        }
                
        // 
        private void loginHandler(OutputStream outputStream, String[] tokens) throws IOException {
            if (tokens.length == 2) {
                String username = tokens[1];
                List<ServerWorker> workerList = server.getworkerList();
                //int i = 0;
                if (!(ArrayIteratorCompare(username, usr_id_list))) {
                    String msg = new Date() + " Logged in " + username + "\n";
                    outputStream.write(msg.getBytes());
                    this.username = username;
                    console_text.append( new Date() + " Logged in User: " + username + "\n");
                    int port = getPort();
                    InetAddress address = getIP();
                    role_queue.addLast(username);
                    String role = changeStatus(username);
                    idGenerator(username, port, address.toString(), role);
                    console_text.append(usr_id_list + "\n");
                    // Send the current user other online Login
                    for (ServerWorker worker : workerList) {
                        if (!username.equals(worker.getLogin())) {
                            if (worker.getLogin() != null) {
                                String onlineMsg = new Date() + " User online: " + worker.getLogin() + "\n";
                                sendMsg(onlineMsg);
                            }
                        }
                    }

                    // Send other online users current users Status
                    String Msg =new Date() +  " User Online: " + username + "\n";
                    for (ServerWorker worker : workerList) {
                        if (!username.equals(worker.getLogin())) {
                            worker.sendMsg(Msg);
                        }
                    }
                } else {
                    //handle login failure
                    String msg = new Date() + "login failed \n";
                    outputStream.write(msg.getBytes());
                    console_text.append( new Date() + " Logged in Failed for User: " + username + "\n");
                    
                    //clientSocket.close();
                }
            }
        }

        // Changes the ID and the related data of the Client
        private void idRemover() {
            for (int i = 0; i < usr_id_list.size(); i++) {
                // Removing disconnected Users from the list
                ArrayList list = (ArrayList) usr_id_list.get(i);
                if (username.equals(list.get(0))) {
                    usr_id_list.remove(i);
                }
            }
        }
        
        
        // Checks if the User have the correct permission to send certain commands
        private void wrongPermission() throws IOException{
            String msg = new Date() +  " You dont the correct Credentials for this command \n ";
            outputStream.write(msg.getBytes());
        }

        
        // Gathers and displays all the active users in the server
        private LinkedList<String> getAll() throws IOException {
            // Declairing all the variables
            String usr_list = null;
            // Linked Hash ,map to store the Users name, POrt ID and IP Address
            // THis will be used to show the other users who is online
            LinkedList<String> listUser = new LinkedList<String>();
            List<ServerWorker> workerList = server.getworkerList(); // will need this to get the IP , Port and Name
            // Going through the list of workers in the LinkedList workerList
            for (int i = 0; i < usr_id_list.size(); i++) { // will need this to get the Port, IP and Name
                ArrayList info_list = (ArrayList) usr_id_list.get(i);
                //System.out.println(usr_id_map);
                // Storing all the values in the variable
                String info = " Username: " + info_list.get(0) +","+ "User ID: " + info_list.get(1) + "," 
                        + " Port ID: " + info_list.get(2) +","+ " IP Address: " 
                        + info_list.get(3) +","+ " Status: " + info_list.get(4) + "\n";
                listUser.add(info);
                usr_list = listUser.toString();
                System.out.println(usr_list);
                // System.out.print(listUser);
                // worker.sendMsg();

            }
            sendMsg(usr_list);
            listUser.clear();
            return null;
        }

        // Works but sometimes breaks
        private void disconnectHandler() throws IOException {
            // Function to handle all User Disconnection from the Server
            ArrayList<ServerWorker> workerList = (ArrayList<ServerWorker>) server.getworkerList();
            for (int i = 0; i < workerList.size(); i++) {
                String msg = new Date() + " User Disconnected: " + username + "\n";
                ServerWorker worker = workerList.get(i);
                worker.sendMsg(msg);
                idRemover();
                role_queue.remove(username);
                StatusUpdater();
                workerList.remove(worker);
                //worker.clientSocket.close();
                //break;

            }
            // Append approipriate response to the Server
            console_text.append( new Date() + " User Disconnected: " + username + "\n");
        }


        // Works
        private void messageHandler(String[] tokens) throws IOException {
            String receiver = tokens[1];
            String message = tokens[2];

            boolean isTopic = receiver.charAt(0) == '#';

            List<ServerWorker> workerList = server.getworkerList();
            for (ServerWorker worker : workerList) {
                if (isTopic) {
                    if (worker.Membership(receiver)) {
                        String MsgOut = new Date() + "Msg: " + receiver + " : " + username + " " + message + "\n";
                        worker.sendMsg(MsgOut);
                    }
                } else {
                    if (receiver.equalsIgnoreCase(worker.getLogin())) {
                        String MsgOut = new Date() +  "Msg: " + username + " " + message + "\n";
                        worker.sendMsg(MsgOut);
                    }
                }
            }
        }

        private String findPath(String file) {
            File f = new File(file);
            String path = f.getAbsolutePath();

            return path;
        }

        // displays the current commands for the client and admin
        private void help() throws IOException {
            String help = null;
            String dataFile = null;
            Boolean status = getStatus();
            List<ServerWorker> workerList = server.getworkerList();
             //for (ServerWorker worker : workerList){
            // This line needs to be universal, currently only works on Leo's machine. Path
            // must be changed.
            Scanner adminFile = new Scanner(new File(
                    "/Users/aniketbasu/Programming_codes/Java/Net Beans/Chat-Server-Client-Application/legit_chat_server/src/legit_chat_server/adminCommand.txt"));
            Scanner clientFile = new Scanner(new File(
                    "/Users/aniketbasu/Programming_codes/Java/Net Beans/Chat-Server-Client-Application/legit_chat_server/src/legit_chat_server/clientCommand.txt"));
          if(status == true){
                while (adminFile.hasNextLine()) {
                    dataFile = adminFile.nextLine() + "\n";
                    outputStream.write(dataFile.getBytes());
                }
            }else{
                while (adminFile.hasNextLine()) {
                    dataFile = clientFile.nextLine() + "\n";
                    outputStream.write(dataFile.getBytes());
                }
            }
        }
        
        
        
        // a bit broken need to fix it
        // Need to add the functionality that when a person leaves the next becomes the
        // admin
        private void adminStatus() throws IOException {
//            String ID = tokens[1];
//            String dataFile = null;

            // This line needs to be universal, currently only works on Leo's machine. Path
            // must be changed.

            // Gets the file path for the file
            //String adminFilePath = findPath("adminCommands.txt");
            //String clientFilePath = findPath("clientCommands.txt");

            // Path is half right so I <AB> moved the files up a folder, it should work by
            // doesnt
            // Ned to try this with Buffered Reader
//            Scanner adminFile = new Scanner(new File(
//                    "/home/jesus/Nextcloud/JAVA/src/git/Chat-Server-Client-Application/legit_chat_server/src/legit_chat_server/adminCommand.txt"));
//            Scanner clientFile = new Scanner(new File(
//                    "/home/jesus/Nextcloud/JAVA/src/git/Chat-Server-Client-Application/legit_chat_server/src/legit_chat_server/clientCommand.txt"));

            // Scanner adminFile = new Scanner(new File("\""+adminFilePath+"\""));
            // Scanner clientFile = new Scanner(new File("\""+clientFilePath+"\""));

            List<ServerWorker> workerList = server.getworkerList();
            // while(true){
            for (ServerWorker worker : workerList) {
                if (role_queue.contains(worker.getLogin()) == true) {
                    //System.out.println();
                } else {
                    role_queue.add(worker.getLogin());
                    System.out.println(role_queue);
                }
                
                if(worker.getLogin().equals(role_queue)){
                    role_queue.remove();
                }else{
                    //System.out.println("No");
                }
                // System.out.println(queue.peek());
                String first = role_queue.peek();
                //System.out.println(first);
                if(!first.equals(worker.getLogin())){
                    //System.out.println();
                    if (worker.getLogin() != null) {
                        String msg = new Date() + first + " is now the admin of the Server \n";
                        worker.outputStream.write(msg.getBytes()); 
                    }
                
            }
//                if (first.equals(worker.getLogin())) {
//                    String msg = worker.getLogin() + " the admin of this server " + "\n";
//                    adminjoinHandler();
//                    boolean isTopic = first.charAt(0) == '#';
//                    if (isTopic) {
//                        if (worker.Membership(first))
//                            ;
//                    }
//                    worker.sendMsg(msg);
//                    if (username != null && username == first) {
//                        while (adminFile.hasNextLine()) {
//                            dataFile = adminFile.nextLine() + "\n";
//                            outputStream.write(dataFile.getBytes());
//                        }
//                    } else {
//                        while (clientFile.hasNextLine()) {
//                            dataFile = clientFile.nextLine() + "\n";
//                            outputStream.write(dataFile.getBytes());
//                        }
//                    }
//                }
            }
        }


        // sends and displays mesages to other clients and the server
        private void sendMsg(String msg) throws IOException {
            // Function to send Message to the Client Side in this case its the Command
            // Prompt
            if (username != null) {
                outputStream.write(msg.getBytes());
            }
            // Printing same messsages to the Server
            console_text.append(msg);
        }

        // function to send a message to all the active users
        private void sendAll(String msg) throws IOException {
            List<ServerWorker> workerList = server.getworkerList();
            for (ServerWorker worker : workerList) {
                worker.outputStream.write(msg.getBytes());
            }
            console_text.append(msg);
        }

        // kicks a specified user from the sever
        private void kickUser(String[] tokens) throws IOException, InterruptedException {
            String receiver = tokens[1];

            List<ServerWorker> workerList = server.getworkerList();
            for (ServerWorker worker : workerList) {
                if (receiver.equalsIgnoreCase(worker.getLogin())) {
                    String MsgOut = "kick \n";
                    worker.sendMsg(MsgOut);
                    TimeUnit.SECONDS.sleep(5);
                    worker.clientSocket.close();
                    worker.idRemover();
                    workerList.remove(worker);
                }
            }
        }

        // makes a announcemnet to all the clients through the server
        private void announcements(String[] token) throws IOException {
            String msg = token[1];

            console_text.append(msg);
            List<ServerWorker> workerList = server.getworkerList();
            for (ServerWorker worker : workerList) {
                if (username != null) {
                    worker.sendMsg(msg);
                }
            }
        }

        // Spams WORLD DOMINATION to all the clients and server for 5 seconds
        private void NUKE() throws IOException {

            List<ServerWorker> workerList = server.getworkerList();
            long aTime = System.currentTimeMillis();
            while (false || (System.currentTimeMillis() - aTime) < 5000) {
                for (ServerWorker worker : workerList) {
                    String msg = "WORLD DOMINATION \n";
                    worker.sendMsg(msg);
                }
            }
        }

        // Bans the specified user for a certain amount of time
        private void sleep(String[] tokens) throws InterruptedException, IOException {
            String receiver = tokens[1];
            String time = tokens[2];
            int sec = Integer.parseInt(time);
            List<ServerWorker> workerList = server.getworkerList();
            for (ServerWorker worker : workerList) {
                if (receiver.equalsIgnoreCase(worker.getLogin())) {
                    String MsgOut = new Date() +  "Msg : " + username + " You are banned from the server for " + sec + " sec \n";
                    worker.sendMsg(MsgOut);
                    int msec = sec * 1000;
                    //clientSocket.setSoTimeout(10000);
                    worker.sleep(msec);
                }
            }
        }

        // Handles the topic of group messaging 
        public boolean Membership(String topic) {
            return SetTopic.contains(topic);
        }

        // Hadles the Clients joining a group messaging
        private void joinHandler(String[] tokens) {
            if (tokens.length > 1) {
                String topic = tokens[1];
                SetTopic.add(topic);
            }
        }

        // Handles the Clients Leavinf the group messaging
        private void leaveHandler(String[] tokens) {
            if (tokens.length > 1) {
                String topic = tokens[1];
                SetTopic.remove(topic);
            }
        }

        // no functionality Yet
        private void privateMsg() {
            List<ServerWorker> workerList = server.getworkerList();
            for (ServerWorker worker : workerList) {
                String name = worker.getLogin();
                int port = worker.getPort();
                System.out.print(port);
                System.out.print(name);
            }
        }
        
        // Kicks all active users and closes the server after a specified time
        public void stopServer( String[] tokens) throws IOException, InterruptedException {
            String time = tokens[1];
            int sec = Integer.parseInt(time);
            List<ServerWorker> workerList = server.getworkerList();
            for (int i = 0; i < usr_id_list.size(); i++) {
            ArrayList list = (ArrayList) usr_id_list.get(i);
                for (ServerWorker worker : workerList) {
                    if(worker.getLogin().equals(list.get(0)) && worker.getLogin() != null){
                        String msg = "\n Server Closing in " + sec +  " seconds \n";
                        sendAll(msg);
                        TimeUnit.SECONDS.sleep(sec);
                    }
                    clientSocket.close();
                    worker.idRemover();
                }
            }
        }
        
        // change the status of a specific user to Admin and change all other status to Client
        private void passOwnership(String[] user){
            String name = user[1];
            List<ServerWorker> workerList = server.getworkerList();
            for (int i = 0; i < usr_id_list.size(); i++) {
                ArrayList info_list = (ArrayList) usr_id_list.get(i);
                info_list.set(4, "Client");
                for(ServerWorker worker : workerList){
                    if(name.equals(worker.getLogin()) && name.equals(info_list.get(0))){
                            info_list.set(4, "Admin");
                    }
                }
            }
        }
         
        // promotes a specified Client's status to Admin
        private void promote(String[] user){
            String name = user[1];
            List<ServerWorker> workerList = server.getworkerList();
            for (int i = 0; i < usr_id_list.size(); i++) {
                ArrayList info_list = (ArrayList) usr_id_list.get(i);
                for(ServerWorker worker : workerList){
                    if(name.equals(worker.getLogin()) && name.equals(info_list.get(0))){
                        info_list.set(4, "Admin");
                    }
                }
            }
        }
        
        // demotes a specified Client's status back from Admin to a Client
        private void demote(String[] user){
            String name = user[1];
            List<ServerWorker> workerList = server.getworkerList();
            for (int i = 0; i < usr_id_list.size(); i++) {
                ArrayList info_list = (ArrayList) usr_id_list.get(i);
                for(ServerWorker worker : workerList){
                    if(name.equals(worker.getLogin()) && name.equals(info_list.get(0))){
                        info_list.set(4, "Client");
                    }
                }
            }
        }

        
        // Check for Afk Users
        private void checkStatus() throws IOException {
                
            List<ServerWorker> workerList = server.getworkerList();
            for (ServerWorker worker : workerList){
                if (!username.equals(worker.getLogin())) {
                    if (worker.getLogin() != null) {
                        String msg = new Date() +  " Are you online? \n Type anything to cancel \n Inactive users will be disconnected after 5 mins \n";
                        worker.outputStream.write(msg.getBytes());  
                        worker.clientSocket.setSoTimeout(5000);
                        worker.clientSocket.setKeepAlive(false);
                        console_text.append(worker.getLogin());
                    }
                }
           }
        }

    }

    // Stps the server 
    public void serverStartButton() {
        // Starts the Server by calling in the StratServer Thread that in turn calls the
        // ServerWorker Thread and the clientHandler
        Thread starter = new Thread(new StartServer());
        starter.start();
        console_text.append(new Date() + " Server started...\n");
    }

    // Needs to Fix it ==> Find a way to call the function on line 579 to be called
    // here to close the Server
    // With appropriate messages to all the active clients
    public void serverStopButton() throws InterruptedException {
        // Closes the Server without any warnings, ned to find a way to send message to
        // everyone when Server is closing
        Thread starter = new Thread(new StartServer());
        // try
        // {
        // console_text.append("Closing Server.\n");
        // The Thread will wait for 5 seconds before closing the server
        // Thread.sleep(5000);
        System.exit(0);
        // }
        // catch(InterruptedException ex) {
        // ex.printStackTrace();
        // }
    }

    
    // Generates a ramdon ID for each user the joind the server
    public void idGenerator(String username, int port, String ipaddress, String status) {
        // Creates random number for the ID of eash User
        ArrayList<String> port_usr_list = new ArrayList<String>();
        String gen_id_str = String.valueOf((int) (Math.random() * (10000000 - 1000000 + 1) + 100000));
        port_usr_list.add(username);
        port_usr_list.add(gen_id_str);
        port_usr_list.add(String.valueOf(port));
        port_usr_list.add(ipaddress);
        port_usr_list.add(status);
        
        usr_id_list.add(port_usr_list);
        // keys + all values
        System.out.println(usr_id_list);
    }

    // Prints in the console window all the online users or running ServerWorker
    public void idRetriever(String username) {
        console_text.append(usr_id_list + "\n");
    }

    // Checks for duplicates in the array
    public boolean ArrayIteratorCompare(String usr, ArrayList list) {
        Boolean duplicate_usr = false;
        for (int i = 0; i < usr_id_list.size(); i++) {
            // System.out.println(k);
            ArrayList info_list = (ArrayList) list.get(i);
            if (usr.equals(info_list.get(0))) {
                duplicate_usr = true;
            }
        }
        if (duplicate_usr == true) {
            return true;
        } else {
            return false;
        }
    }

    public server_frame() {
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        start_button = new javax.swing.JButton();
        stop_button = new javax.swing.JButton();
        clear_button = new javax.swing.JButton();
        listusr_button = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        console_text = new javax.swing.JTextArea();
        portId = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Chat Server");

        start_button.setText("Start");
        start_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                start_buttonActionPerformed(evt);
            }
        });

        stop_button.setText("Stop");
        stop_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stop_buttonActionPerformed(evt);
            }
        });

        clear_button.setText("Clear");
        clear_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clear_buttonActionPerformed(evt);
            }
        });

        listusr_button.setText("List Active Users");
        listusr_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                listusr_buttonActionPerformed(evt);
            }
        });

        console_text.setEditable(false);
        console_text.setColumns(20);
        console_text.setRows(5);
        jScrollPane1.setViewportView(console_text);

        portId.setText("Port ID");
        portId.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                portIdMouseClicked(evt);
            }
        });
        portId.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                portIdActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 415, Short.MAX_VALUE)
                                .addComponent(stop_button, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(listusr_button, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 280, Short.MAX_VALUE)
                                .addComponent(clear_button, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(portId, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(start_button, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 292, Short.MAX_VALUE))
                            .addComponent(jScrollPane1))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(start_button)
                    .addComponent(stop_button)
                    .addComponent(portId))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 367, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(clear_button)
                    .addComponent(listusr_button))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void stop_buttonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_stop_buttonActionPerformed
        try {
            // TODO add your handling code here:
            serverStopButton();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }// GEN-LAST:event_stop_buttonActionPerformed

    private void start_buttonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_start_buttonActionPerformed
        // TODO add your handling code here:
        serverStartButton();
    }// GEN-LAST:event_start_buttonActionPerformed

    private void listusr_buttonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_listusr_buttonActionPerformed
        // TODO add your handling code here:
        idRetriever("");
    }// GEN-LAST:event_listusr_buttonActionPerformed

    private void clear_buttonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_clear_buttonActionPerformed
        // TODO add your handling code here:
        console_text.setText("");
    }// GEN-LAST:event_clear_buttonActionPerformed

    private void portIdActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_portIdActionPerformed
        // TODO add your handling code here:
    }// GEN-LAST:event_portIdActionPerformed

    private void portIdMouseClicked(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_portIdMouseClicked
        // TODO add your handling code here:
    }// GEN-LAST:event_portIdMouseClicked

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        // <editor-fold defaultstate="collapsed" desc=" Look and feel setting code
        // (optional) ">
        /*
         * If Nimbus (introduced in Java SE 6) is not available, stay with the default
         * look and feel. For details see
         * http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(server_frame.class.getName()).log(java.util.logging.Level.SEVERE, null,
                    ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(server_frame.class.getName()).log(java.util.logging.Level.SEVERE, null,
                    ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(server_frame.class.getName()).log(java.util.logging.Level.SEVERE, null,
                    ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(server_frame.class.getName()).log(java.util.logging.Level.SEVERE, null,
                    ex);
        }
        // </editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new server_frame().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton clear_button;
    private javax.swing.JTextArea console_text;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton listusr_button;
    private javax.swing.JTextField portId;
    private javax.swing.JButton start_button;
    private javax.swing.JButton stop_button;
    // End of variables declaration//GEN-END:variables
}
