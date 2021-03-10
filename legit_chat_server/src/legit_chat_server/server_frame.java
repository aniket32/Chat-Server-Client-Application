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
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.io.File;  
import java.util.Scanner;
import java.io.PrintWriter;
import java.net.InetAddress;


/*
TODO LIST:
Check Line 275 and afterwards to know what to do ==> Leo
Will try and fix the admin thingi ==> AB
*/

//Branching test Commit and Push
// More test
// More test last to see that ot works properly






public class server_frame extends javax.swing.JFrame {
    
    
    
    
    // Linked Hash Map to store the user name and their ID
    LinkedHashMap<String, String> usr_id_map = new LinkedHashMap<String, String>();
    
    // Linked Hash ,map to store the Users name, POrt ID and IP Address
    // THis will be used to show the other users who is online
    LinkedList<String> listUser = new LinkedList<String>();
     
    // Declairing global variables
    // Forgot where they are used, just leave them will do semothing about them later
    PrintWriter printerOut;
    Queue<String> queue = new LinkedList<>();
    
    public class StartServer implements Runnable{
        @Override
        public void run(){
            String portID = portId.getText();
            int port = Integer.parseInt(portID);
            ServerS server = new ServerS(port);
            server.start();
        }
    }
    
    // Works
    public class ServerS extends Thread{
        // Main reason for creating this class is to havce a clooection for this workers for every Client that joins the Server
        private final int serverPort;
        
        private ArrayList<ServerWorker> workerList = new ArrayList<>();
        
        public ServerS (int serverPort){
            this.serverPort = serverPort;          
        }
        
        // Works
        public List<ServerWorker> getworkerList(){
            return workerList;
        }
        
        @Override
        public void run()
        {
           try {
                ServerSocket serverSocket = new ServerSocket(serverPort);
                //Accepting Client Sockets and creates the connection between them
                //clientSocket used to identify Clients
                while (true){
                    console_text.append("About to accept connections....\n");
                    //While loop to keep looking or accepting from clients
                    Socket clientSocket = serverSocket.accept();
                    console_text.append("Accepting Connections from \n" + clientSocket);
                    ServerWorker worker = new ServerWorker(this, clientSocket);
                    InetAddress Ip = clientSocket.getLocalAddress();
                    //System.out.print(Ip);
                    workerList.add(worker);
                    console_text.append(String.valueOf(workerList));
                    worker.start();  
                }    
            }catch(IOException e){
               //print in server console if error in Creating Server~
                console_text.append("server broke");
            }  
        }
    }
    
    // Works
    public class ServerWorker extends Thread {
        public final Socket clientSocket;
        public ServerS server;
        public String username = null;
        private OutputStream outputStream;
        private HashSet<String> SetTopic = new HashSet<>();
        
        // Passing a Server instance to each ServerWorker
        public ServerWorker (Socket clientSocket){
            this.clientSocket = clientSocket;
        }

        public ServerWorker(ServerS server, Socket clientSocket) {
            this.server = server;
            this.clientSocket = clientSocket;
        }

        @Override
        public void run (){
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
                                adminStatus(tokens);
                                //getAll();
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
                                sendAll(token);
                                break;
                            case "ListUser":
                                // calls the ListUser function to lists all the activ users in the server
                                //ListUser();
                                getAll();
                                break;
                            case "NUKE":
                                // calls the NUKE function and Spams WORLD DOMINATION all over the server and client
                                String[] tokenMsg = line.split(" ");
                                NUKE(tokenMsg);
                                break;
                            case "sleep":
                                // calls the sleep fucntion and bans a client for a certain time
                                String[] Msg = line.split(" ");
                                sleep(Msg);
                                break;
                            case "kick":
                                // calls the kickuser functin and removes a client from the Server
                                String[] msg = line.split(" ");
                                kickUser(msg);
                                break;
                            case "help":
                                //calls the help function and show all commands for the admin and the clients
                                help();
                                break;
                            case "quit":
                                // calls the disconnectHandler functin and removes a user from the 
                                disconnectHandler();
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
        
        
        //Fnctions
        // gets the Login name ==> String
        public String getLogin(){
            return username;
        }
        // gets the PortID ==> Int
        public int getPort(){
            return clientSocket.getPort();
        }
        // gets the IP address ==> InetAddress
        // in this case all the IP Address will be ==> 127.0.0.1
        public InetAddress getIP(){
            return clientSocket.getInetAddress();
        }
        
        // Works
        private void loginHandler(OutputStream outputStream, String[] tokens) throws IOException {
            if (tokens.length == 2){
                String username = tokens[1];
                List<ServerWorker> workerList = server.getworkerList();
                    int i = 0;
                    if (!(ArrayIteratorCompare(username,usr_id_map))){
                        String msg = " Logged in " + username + "\n";
                        outputStream.write(msg.getBytes()); 
                        this.username = username;
                        console_text.append(" Logged in User: " + username + "\n");
                        idGenerator(username);
                        console_text.append(usr_id_map+"\n");
                        
                        // Send the current user other online Login 
                        for(ServerWorker worker :workerList){
                            if (!username.equals(worker.getLogin())){
                                if (worker.getLogin() != null){
                                    String onlineMsg = " User online: " + worker.getLogin() + "\n";
                                    sendMsg(onlineMsg);
                                }  
                            }
                        }
                        
                        // Send other online users current users Status
                        String Msg  = " User Online: " + username + "\n";
                        for(ServerWorker worker : workerList){
                            if (!username.equals(worker.getLogin())){
                            worker.sendMsg(Msg);
                            }
                        }    
                    } else {
                        String msg = "login failed";
                        
                        outputStream.write(msg.getBytes());
                        console_text.append(" Logged in Failed for User: " + username + "\n");
                    }
            }
        }
        
        //Works
        // Need to make someting similar for the getAll() function
        private  void idRemover(){
            Set<String> keys = usr_id_map.keySet();
                for (String k : keys){
                    //System.out.println(k);
                    // Removing disconnected Users from the list
                    if (username.equals(usr_id_map.get(k))){
                        usr_id_map.remove(k);
                    }
                }
        }
        
        
        // Broken ===> Leo Fix this to display the online users list the client that types ListUser
        // and also add a remove function to remove a Client when a Client leaves
        // OR make something of your own
        
        private LinkedList<String> getAll() throws IOException{
            //Declairing all the variables
            String usr = null;
            int port = 0;
            InetAddress ip = null;
            String info = null;
            String list = null, first, slip;
            

            List<ServerWorker> workerList = server.getworkerList(); // will need this to get the IP , Port and Name
            Set<String> keys = usr_id_map.keySet(); // Ueless line
                // Going through the list of workers in the LinkedList workerList
                
                for(ServerWorker worker : workerList){ // will need this to get the Port, IP and Name
                    
                    // calling the functions to get the Username, Port Id and IP
                    usr = worker.getLogin();
                    port = worker.getPort();
                    ip = worker.getIP();
                
                // Storing all the values in the variable
                info = " Username: " + usr + " Port ID: " + port + " IP Address: " + ip;
                listUser.add(info);
                
                //System.out.print(listUser);
                //worker.sendMsg();
                list = listUser.toString();
                }
                // Useless Code to do show up the list on the Client's Interface
                for (ServerWorker worker : workerList){
                    if(username.equals(worker.getLogin())){
                        worker.sendMsg(list);
                    }
                }
                //console_text.append(listUser);
            return null;
        }
        
        // Works but sometimes breaks
        private void disconnectHandler() throws IOException {
            // Function to handle all User Disconnection from the Server 
            List<ServerWorker> workerList = server.getworkerList();
            for (ServerWorker worker : workerList){
                String msg = "User Disconnected " +  username + "\n";
                worker.sendMsg(msg);
                idRemover();
                break;
            }
            // Append approipriate response to the Server 
            console_text.append(" User Disconnected: " +  username +"\n");
        }
        
        //Broken
        private void quit() throws IOException {
            // At this moment does nothing but i hope to make it wortk in the near future
            clientSocket.close();
        }
        
        // Works
        private void messageHandler(String[] tokens) throws IOException {
            String receiver = tokens[1];
            String message = tokens[2];
            
            boolean isTopic = receiver.charAt(0) == '#';
            
            List<ServerWorker> workerList = server.getworkerList();
            for (ServerWorker worker : workerList){
                if (isTopic){
                    if (worker.Membership(receiver)){
                        String MsgOut = "Msg : " + receiver + " : " + username + " " + message +"\n";
                        //outputStream.write(message.getBytes());
                        worker.sendMsg(MsgOut);
                    }
                } else {
                    if (receiver.equalsIgnoreCase(worker.getLogin())){
                        String MsgOut = "Msg : " + username + " " + message +"\n";
                        //outputStream.write(message.getBytes());
                        worker.sendMsg(MsgOut);
                    }
                }
            }
        }
        
        // Broken code ===> Dont use this 
        // Use getAll() instead
//        private void ListUser() throws IOException {
//           List<ServerWorker> workerList = server.getworkerList();
//           for (ServerWorker worker : workerList){
//               Set<String> keys = usr_id_map.keySet();
//                for (String k : keys){
//                    // System.out.println(k);
//                    if (username.equals(usr_id_map.get(k))){
//                        if (username.equals(worker.getLogin())){
//                            worker.sendMsg("Online Users  : " + getAll() + "\n");
//                            //worker.sendMsg(getAll);
//                        }
//                    }
//                } 
//           }
//        }
        
        
        // Works for the clinet
        // Need to make a thing where it works for the Admin as well
        private void help() throws IOException {
            String help = null;
            Scanner clientFile = new Scanner(new File("/Users/aniketbasu/Programming_codes/Java/Net Beans/Chat-Server-Client-Application/legit_chat_server/src/legit_chat_server/clientCommand.txt"));            
            while (clientFile.hasNextLine()){
                help = clientFile.nextLine();
                outputStream.write(help.getBytes());
            }
        }
        
        //a bit broken need to fix it 
        // Need to add the functionality that when a person leaves the next becomes the admin
        private void adminStatus (String[] tokens) throws IOException{
            String ID = tokens[1];
            String dataFile = null;
            
            Scanner adminFile = new Scanner(new File("/Users/aniketbasu/Programming_codes/Java/Net Beans/Chat-Server-Client-Application/legit_chat_server/src/legit_chat_server/adminCommand.txt"));
            Scanner clientFile = new Scanner(new File("/Users/aniketbasu/Programming_codes/Java/Net Beans/Chat-Server-Client-Application/legit_chat_server/src/legit_chat_server/clientCommand.txt"));            
                       
            List<ServerWorker> workerList = server.getworkerList();
   //         while(true){
                for(ServerWorker worker : workerList){  
                    if (queue.contains(worker.getLogin()) == true){
                    } else{
                        queue.add(worker.getLogin());
                    }
                    //System.out.println(queue.peek());
                    String first = queue.peek();
                    if(first.equals(worker.getLogin())){
                        String msg = worker.getLogin() + " the admin of this server " + "\n";
                        worker.sendMsg(msg);
                        if(username!= null && username == first){
                            while (adminFile.hasNextLine()){
                                dataFile = adminFile.nextLine() + "\n";
                                outputStream.write(dataFile.getBytes());
                            }
                        }else{
                            while (clientFile.hasNextLine()){
                                dataFile = clientFile.nextLine() + "\n";
                                outputStream.write(dataFile.getBytes());
                            }
                        }
                    }
                }
            //}
        }
        
        
        //Works
        private void sendMsg(String msg) throws IOException {
            // Function to send Message to the Client Side in this case its the Command Prompt
            if(username != null){
            outputStream.write(msg.getBytes());
            //printerOut.println(msg+"\n");
            //console_text.append(msg);
            }
            // Printing same messsages to the Server
            console_text.append(msg);
        }
        
        
        // Broken ==> Kicks the client that sends the message and not the other persen
        private void kickUser(String[] tokens) throws IOException, InterruptedException {
            String receiver = tokens[1];
            
            List<ServerWorker> workerList = server.getworkerList();
            for (ServerWorker worker : workerList){
                 if (receiver.equalsIgnoreCase(worker.getLogin())){
                        String MsgOut = "Msg : " + username + " " + "YOU are KICKED from the Server " +"\n";
                        //outputStream.write(message.getBytes());
                        worker.sendMsg(MsgOut);
                        //try{
                            Thread.sleep(5000);
                            worker.clientSocket.close();
//                        }catch (IOException ex){
//                            ex.printStackTrace();
//                        }
                    }
                 idRemover();
            }            
        }
        
        ///Broken Code ===> Funcking breaks the entire thing
        private void sendAll(String[] token) throws IOException {
            
            String msg = token[0]; // This line is broken don't know why
            
            console_text.append(msg);
//            List<ServerWorker> workerList  = server.getworkerList();
//            for (ServerWorker worker : workerList){
//                if(username != null){
//                    worker.sendMsg(msg);
//                }
//            }
        }
         
        // Works
        private void NUKE( String[] tokens) throws IOException {
            //String timer = tokens[1];
            
            List<ServerWorker> workerList = server.getworkerList();
            long aTime = System.currentTimeMillis();
            while(false||(System.currentTimeMillis()-aTime) < 10000 ){
                    for (ServerWorker worker : workerList){
                     String msg = "WORLD DOMINATION" + "\n";
                    worker.sendMsg(msg);   
                }
            }
        }
        
        
        // A bit Broken
        private void sleep(String[] tokens) throws InterruptedException, IOException {
            String receiver = tokens[1];
            List<ServerWorker> workerList = server.getworkerList();
            for(ServerWorker worker : workerList){
                if (receiver.equalsIgnoreCase(worker.getLogin())){
                        String MsgOut = "Msg : " + username + " " + "YOU are banned from the server for 20 sec " +"\n";
                        //outputStream.write(message.getBytes());
                        worker.sendMsg(MsgOut);
                        Thread.sleep(50000);
                    }
            }
        }
        
        // Works
        public boolean Membership(String topic){
            return SetTopic.contains(topic);
        }
        
        // Works
        private void joinHandler(String[] tokens) {
            if (tokens.length > 1){
                String topic = tokens[1];
                SetTopic.add(topic);
            }
        }

        // Works
        private void leaveHandler(String[] tokens) {
            if (tokens.length > 1){
                String topic = tokens[1];
                SetTopic.remove(topic);
            }
        }

        
        // no functionality Yet
        private void privateMsg() {
            List<ServerWorker> workerList = server.getworkerList();
            for(ServerWorker worker : workerList){
                String name = worker.getLogin();
                int port = worker.getPort();
                System.out.print(port);
                System.out.print(name);
            }
        }

        
    }

  
    public void stopServer(){
       // Doers nothing  
    }
    
    // Works
    public void serverStartButton(){
       // Starts the Server by calling in the StratServer Thread that in turn calls the ServerWorker Thread and the clientHandler
      Thread starter = new Thread(new StartServer());
      starter.start();
      console_text.append("Server started...\n");
    }           
    
    
    // Needs to Fix it ==> Right now just force quits the entire program
    public void serverStopButton() throws InterruptedException {
       // Closes the Server without any warnings, ned to find a way to send message to everyone when Server is closing
//       Thread starter = new Thread(new StartServer());
////       starter.stop();
//        try 
//        {
//            console_text.append("Closing Server.\n");
//            // The Thread will wait for 5 seconds before closing the server
//            Thread.sleep(5000);  
            System.exit(0);
//        } 
//        catch(InterruptedException ex) {Thread.currentThread().interrupt();}
//        //System.exit(0);
//        //dispose();
    }
    
    // Works
    public void idGenerator(String username) {   
        // Creates random number for the ID of eash User    
        int gen_id =(int)(Math.random() * (10000000 - 1000000 + 1) + 100000);
        usr_id_map.put(String.valueOf(gen_id),username);
    } 
    
    
    // Works but is of no use at the moment
    public void idRetriever(String username) {
        // Prints in the console window all the online users or running ServerWorker
        console_text.append(usr_id_map + "\n"); 
    }
    
    // Works
    public boolean ArrayIteratorCompare(String usr,LinkedHashMap map){
        // Goes Through the array and checks for duplicates
        Boolean duplicate_usr = false;
        Set<String> keys = map.keySet();
        for (String k : keys){
            //System.out.println(k);
            if (usr.equals(map.get(k))){
                duplicate_usr = true;
            }
        }
        if (duplicate_usr == true){
            return true;
        }else{
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
                                .addGap(374, 374, 374)
                                .addComponent(stop_button, javax.swing.GroupLayout.DEFAULT_SIZE, 82, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(listusr_button, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(239, 239, 239)
                                .addComponent(clear_button, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(portId)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(start_button, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(286, 286, 286))
                            .addComponent(jScrollPane1))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(start_button, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(stop_button, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(portId))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 320, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(clear_button, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(listusr_button, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void stop_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stop_buttonActionPerformed
         try {
             // TODO add your handling code here:
             serverStopButton();
         } catch (InterruptedException ex) {
             ex.printStackTrace();
         }
    }//GEN-LAST:event_stop_buttonActionPerformed

    private void start_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_start_buttonActionPerformed
        // TODO add your handling code here:
        serverStartButton();
    }//GEN-LAST:event_start_buttonActionPerformed

    private void listusr_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_listusr_buttonActionPerformed
        // TODO add your handling code here:
        idRetriever("");
    }//GEN-LAST:event_listusr_buttonActionPerformed

    private void clear_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clear_buttonActionPerformed
        // TODO add your handling code here:
        console_text.setText("");
    }//GEN-LAST:event_clear_buttonActionPerformed

    private void portIdActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_portIdActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_portIdActionPerformed

    private void portIdMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_portIdMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_portIdMouseClicked

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(server_frame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(server_frame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(server_frame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(server_frame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

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