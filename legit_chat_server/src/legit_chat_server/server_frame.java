package legit_chat_server;

//Importing all the Packages 
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.io.*;
import java.net.*;
import java.awt.Color;
import java.text.SimpleDateFormat;
import javax.swing.JOptionPane;

public class server_frame extends javax.swing.JFrame {
    // Time stamps for the server
    Calendar cal = Calendar.getInstance();
    SimpleDateFormat timeOnly = new SimpleDateFormat("HH:mm:ss");
    String timeStamp = timeOnly.format(cal.getTime());
    // List of lists to store ArrayList object containing username,userID,Port,IP Address,Role 
    ArrayList<ArrayList> usr_id_list = new ArrayList<ArrayList>();
    ArrayList <String> active_usr_list = new ArrayList<String>();
    ArrayList <String> inactive_usr_list = new ArrayList<String>();
    
    // Declairing global variables
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

    public class ServerS extends Thread {
        // Main reason for creating this class is to havce a clooection for this workers
        // for every Client that joins the Server
        private final int serverPort;

        private ArrayList<ServerWorker> workerList = new ArrayList<>();

        public ServerS(int serverPort) {
            this.serverPort = serverPort;
        }

   
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
                    // While loop to keep looking or accepting from clients
                    Socket clientSocket = serverSocket.accept();
                    ServerWorker worker = new ServerWorker(this, clientSocket);
                    InetAddress Ip = clientSocket.getLocalAddress();
                    workerList.add(worker);
                    //console_text.append(String.valueOf(workerList));
                    worker.start();
                }
            } catch (IOException e) {
                // print in server console if error in Creating Server~
                console_text.append( timeStamp + " Server broke \n");
            }
        }
    }

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
                            case "sendall":
                                // calls the sendAll function to send a message to all the clients
                                String[] token = line.split(" ", 2);
                                announcements(token);
                                break;
                            case "ListUser":
                                // calls the ListUser function to lists all the activ users in the serve
                                getAll();
                                break;
                            case "InactiveUser":
                                // calls the ListUser function to lists all the activ users in the serve
                                Boolean v10 = getStatus();
                                if(v10 == true){
                                    inactive_usr_list.clear();
                                    getInactiveUser();
                                }else{
                                    wrongPermission();
                                }
                                break;    
                            case "NUKE":
                                // calls the NUKE function and Spams WORLD DOMINATION all over the server and client
                                Boolean v1 = getStatus();
                                if(v1 == true){
                                    NUKE();
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
                                adminStatus();
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
                            case "checkStatus":
                                //calls a function to check if the Clients are online or not
                                Boolean v4 = getStatus();
                                if(v4 == true){
                                    active_usr_list.clear();
                                    checkStatus();
                                }else{
                                    wrongPermission();
                                }
                                break;
                            case "yes":
                                //calls a function to let the admin know that the user is active/online
                                serverStay();
                                break;
                            case "Y":
                                //calls a function to let the admin know that the user is active/online
                                serverStay();
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
                        if (status.equals("Client")){
                            states = false;
                        }else if (status.equals("Admin")){
                        states = true;
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
        
        
        // Updates the Clients about the admin of the server
        public void StatusUpdater() throws IOException{
            String usr = role_queue.peek();
            List<ServerWorker> workerList = server.getworkerList();
            for (int i = 0; i < usr_id_list.size(); i++) {
             ArrayList list = (ArrayList) usr_id_list.get(i);
             if (usr.equals(list.get(0))) {
                 list.set(4, "Admin");
                 usr_id_list.set(i, list);
                }
            } 
            String Msg = timeStamp + " " + usr + " is now the admin of the Server \n";
                for (ServerWorker worker : workerList) {
                    if (!username.equals(worker.getLogin())) {
                        worker.sendMsg(Msg);
                    }
                }
        }
                
        // Handles login of all the Clients
        private void loginHandler(OutputStream outputStream, String[] tokens) throws IOException {
            if (tokens.length == 2) {
                String username = tokens[1];
                List<ServerWorker> workerList = server.getworkerList();
                if (!(ArrayIteratorCompare(username, usr_id_list))) {
                    String msg = timeStamp + " Logged in " + username + "\n";
                    outputStream.write(msg.getBytes());
                    this.username = username;
                    console_text.append( timeStamp + " Logged in User: " + username + "\n");
                    int port = getPort();
                    InetAddress address = getIP();
                    role_queue.addLast(username);
                    String role = changeStatus(username);
                    idGenerator(username, port, address.toString(), role);
                    // Send the current user other online Login
                    for (ServerWorker worker : workerList) {
                        if (!username.equals(worker.getLogin())) {
                            if (worker.getLogin() != null) {
                                String onlineMsg = timeStamp + " User online: " + worker.getLogin() + "\n";
                                sendMsg(onlineMsg);
                            }
                        }
                    }

                    // Send other online users current users Status
                    String Msg =timeStamp +  " User Online: " + username + "\n";
                    for (ServerWorker worker : workerList) {
                        if (!username.equals(worker.getLogin())) {
                            worker.sendMsg(Msg);
                        }
                    }
                } else {
                    //handle login failure
                    String msg = timeStamp + "login failed \n";
                    outputStream.write(msg.getBytes());
                    console_text.append( timeStamp + " Logged in Failed for User: " + username + "\n");
                }
            }
        }

        // Deletes the ID and the related data of the Client
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
            String msg = timeStamp +  " Permission Denied \n";
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
                // Storing all the values in the variable
                String info = " Username: " + info_list.get(0) +","+ "User ID: " + info_list.get(1) + "," 
                        + " Port ID: " + info_list.get(2) +","+ " IP Address: " 
                        + info_list.get(3) +","+ " Status: " + info_list.get(4) + "\n";
                listUser.add(info);
                usr_list = listUser.toString();
            }
            sendMsg(usr_list);
            listUser.clear();
            return null;
        }

        // Disconnects Clients from the server
        private void disconnectHandler() throws IOException {
            // Function to handle all User Disconnection from the Server
            ArrayList<ServerWorker> workerList = (ArrayList<ServerWorker>) server.getworkerList();
            ServerWorker worker = workerList.get(workerFinder());
            String msg = timeStamp + " User Disconnected: " + username + "\n";
            worker.sendMsg(msg);
            idRemover();
            role_queue.remove(username);
            StatusUpdater();
            workerList.remove(worker);
                
            // Append appropriate response to the Server
            console_text.append( timeStamp + " User Disconnected: " + username + "\n");
        }


        // Handles messages from all the Clients
        private void messageHandler(String[] tokens) throws IOException {
            String receiver = tokens[1];
            String message = tokens[2];

            boolean isTopic = receiver.charAt(0) == '#';

            List<ServerWorker> workerList = server.getworkerList();
            for (ServerWorker worker : workerList) {
                // Checking if the worker or Clinet belong in any group messaging
                if (isTopic) {
                    if (worker.Membership(receiver)) {
                        String MsgOut = timeStamp + " Msg: " + receiver + " : " + username + " " + message + "\n";
                        worker.sendMsg(MsgOut);
                    }
                } else {
                    if (receiver.equalsIgnoreCase(worker.getLogin())) {
                        String MsgOut = timeStamp +  " Msg: " + username + " " + message + "\n";
                        worker.sendMsg(MsgOut);
                    }
                }
            }
        }


        // displays the current commands for the client and admin
        private void help() throws IOException {
            String help = null;
            String dataFile = null;
            Boolean status = getStatus();
            List<ServerWorker> workerList = server.getworkerList();
            // Path must be changed for the help command to work 
            Scanner adminFile = new Scanner(new File(
                    "C:\\Users\\Pacific\\Desktop\\AB\\Chat-Server-Client-Application\\legit_chat_server\\src\\legit_chat_server\\adminCommand.txt"));
            Scanner clientFile = new Scanner(new File(
                    "C:\\Users\\Pacific\\Desktop\\AB\\Chat-Server-Client-Application\\legit_chat_server\\src\\legit_chat_server\\clientCommand.txt"));
          if(status == true){
                while (adminFile.hasNextLine()) {
                    dataFile = adminFile.nextLine() + "\n";
                    outputStream.write(dataFile.getBytes());
                }
            }if (status == false){
                while (clientFile.hasNextLine()) {
                    dataFile = clientFile.nextLine();
                    outputStream.write(dataFile.getBytes());
                }
            }
        }
        
        
        
        // Lets the Clients know about the admin of the server
        private void adminStatus() throws IOException {
            List<ServerWorker> workerList = server.getworkerList();
            for (int i = 0; i < usr_id_list.size(); i++) {
                ArrayList info_list = (ArrayList) usr_id_list.get(i);
                for(ServerWorker worker : workerList){
                    if(info_list.get(4).equals("Admin")){
                        if(!info_list.get(0).equals(worker.getLogin())){
                            if (worker.getLogin() != null) {
                                String msg = timeStamp + " " + (String) info_list.get(0)+ " is now the admin of the Server \n";
                                worker.outputStream.write(msg.getBytes()); 
                            }
                        }
                    }
                }  
            }
        }

        // Sends admin the status of the active users when checkStatus Command is used
        public void serverStay() throws IOException, InterruptedException{
            List<ServerWorker> workerList = server.getworkerList();
            for (int i = 0; i < usr_id_list.size(); i++) {
                ArrayList info_list = (ArrayList) usr_id_list.get(i);
                for (ServerWorker worker : workerList){
                    if (info_list.get(4).equals("Admin") && info_list.get(0).equals(worker.getLogin())){
                        String msg = timeStamp + " " + username  + " is still active \n" ;
                        sendAdmin(msg);
                        active_usr_list.add(username);
                    }
                }   
            }         
        }
        
        // Admin command to see the inactive clients in the server  
        public void getInactiveUser() throws IOException{
            for (int j = 0; j < usr_id_list.size(); j++) {
                ArrayList user_list = (ArrayList) usr_id_list.get(j);
                if (user_list.get(4).equals("Client") && !active_usr_list.contains(user_list.get(0))){
                    inactive_usr_list.add((String) user_list.get(0));
                }  
            }
            String msg =timeStamp+ " " + "Inactive Clients: \n"+ inactive_usr_list + "\n" ;
            sendAdmin(msg);
        }

        // Sends and displays mesages to other clients and the server
        private void sendMsg(String msg) throws IOException {
            // Function to send Message to the Client Side in this case its the Command
            // Prompt
            if (username != null) {
                outputStream.write(msg.getBytes());
            }
            // Printing same messsages to the Server
            console_text.append(msg);
        }

        // Function to send a message to all the active users
        private void sendAll(String msg) throws IOException {
            List<ServerWorker> workerList = server.getworkerList();
                for (ServerWorker worker : workerList) {
                    worker.outputStream.write(msg.getBytes());
                }
            console_text.append(msg);
        }
        
        // Function to send certain messages to the Admin of the server
        private void sendAdmin(String msg) throws IOException{
            List<ServerWorker> workerList = server.getworkerList();
            for (int i = 0; i < usr_id_list.size(); i++) {
                ArrayList info_list = (ArrayList) usr_id_list.get(i);
                for (ServerWorker worker : workerList){
                    if (info_list.get(4).equals("Admin") && info_list.get(0).equals(worker.getLogin())){
                        worker.outputStream.write(msg.getBytes());
                        active_usr_list.add(username);
                    }
                }   
            }  
        }

        // Function to kick a specified user from the server
        private void kickUser(String[] tokens) throws IOException, InterruptedException {
            String receiver = tokens[1];
            List<ServerWorker> workerList = server.getworkerList();
            ServerWorker worker = workerList.get(workerFinder());
            if (receiver.equalsIgnoreCase(worker.getLogin())) {
                String MsgOut = "kick \n";
                worker.sendMsg(MsgOut);
                TimeUnit.SECONDS.sleep(5);
                worker.idRemover();
                workerList.remove(worker);
            }
            
        }

        // Function to makes a announcement to all the clients through the server
        private void announcements(String[] token) throws IOException {
            String msg = token[1];

            List<ServerWorker> workerList = server.getworkerList();
            for (ServerWorker worker : workerList) {
                if (username != null) {
                    worker.sendMsg(timeStamp + "Announcement: \n" + msg + "\n");
                }
            }
            console_text.append(msg + "\n");
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

        
        // Disconnects all active users and closes the server after a specified time
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
                        workerList.remove(worker);
                        worker.idRemover();
                    }
                }
            }
        }
        
        // Function to change the status of a specific user to Admin and change all other user status to Client
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
         
        // Function to promotes a specified Client's status to Admin
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
        
        // Function to demotes a specified Client's status back from Admin to a Client
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

        
        // Sends a message to all the Clients and checks for their online Status
        private void checkStatus() throws IOException {
            List<ServerWorker> workerList = server.getworkerList();
            for (ServerWorker worker : workerList){
                if (!username.equals(worker.getLogin())) {
                    if (worker.getLogin() != null) {
                        String msg = timeStamp +  " Are you online? \n Type 'yes' or 'Y' to confirm \n";
                        worker.outputStream.write(msg.getBytes());  
                    }
                }
            }
        }
        
        //Returns the index(int) for the worker in workerList
        public int workerFinder(){
            int index_int = 0;
            ArrayList<ServerWorker> workerList = (ArrayList<ServerWorker>) server.getworkerList();
            for (ServerWorker worker: workerList) {
                index_int = workerList.indexOf(worker);
            }
            return index_int;
        }
    }
    // Calls the ServerStaretr to start the server 
    public void serverStartButton() {
        // Starts the Server by calling in the StratServer Thread that in turn calls the
        // ServerWorker Thread and the clientHandler
        Thread starter = new Thread(new StartServer());
        starter.start();
        console_text.append(timeStamp + " Server started...\n");
        portId.setEditable(false);
        portId.setBackground(Color.gray);
    }

    // Forcefully closes the server
    public void serverStopButton() throws InterruptedException {
        System.exit(0);
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
    }

    // Prints in the console window all the online users or running ServerWorker
    public void idRetriever(String username) {
        String usr_list = null;
        // Linked Hash ,map to store the Users name, POrt ID and IP Address
        // THis will be used to show the other users who is online
        LinkedList<String> listUser = new LinkedList<String>();
        for (int i = 0; i < usr_id_list.size(); i++) { // will need this to get the Port, IP and Name
            ArrayList info_list = (ArrayList) usr_id_list.get(i);
            // Storing all the values in the variable
            String info = " Username: " + info_list.get(0) +","+ "User ID: " + info_list.get(1) + "," 
                    + " Port ID: " + info_list.get(2) +","+ " IP Address: " 
                    + info_list.get(3) +","+ " Status: " + info_list.get(4) + "\n";
            listUser.add(info);
            usr_list = listUser.toString();
        }
        JOptionPane.showInternalMessageDialog(null, "Active Users \n"+String.valueOf(usr_list) + "\n");
    }

    
    // Checks for duplicates in the array
    public boolean ArrayIteratorCompare(String usr, ArrayList list) {
        Boolean duplicate_usr = false;
        for (int i = 0; i < usr_id_list.size(); i++) {
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
