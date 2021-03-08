/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package legit_chat_server;

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
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;




/**
 *
 * @author aniketbasu
 */
public class server_frame extends javax.swing.JFrame {

    /**
     * Creates new form server_frame
     */
    
     LinkedHashMap<String, String> usr_id_map = new LinkedHashMap<String, String>();
    
    public class StartServer implements Runnable{
        public void run(){
            int port = 8283;
            ServerS server = new ServerS(port);
            server.start();
        }
    }
    
    public class ServerS extends Thread{
        // Main reason for creating this class is to havce a clooection for this workers for every Client that joins the Server
        private final int serverPort;
        
        private ArrayList<ServerWorker> workerList = new ArrayList<>();
        
        public ServerS (int serverPort){
            this.serverPort = serverPort;          
        }
        
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
                    console_text.append("\n About to accept connections....\n");
                    //While loop to keep looking or accepting from clients
                    Socket clientSocket = serverSocket.accept();
                    console_text.append("\n Accepting Connections from \n" + clientSocket);
                    ServerWorker worker = new ServerWorker(this, clientSocket);
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

        private void handleClientSocket() throws InterruptedException, IOException {
           // Main function to handel all the Users and in case the Moderators action in the Server
           InputStream inputStream = clientSocket.getInputStream();
           this.outputStream = clientSocket.getOutputStream();
           
           BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
           String line;
           while ((line = reader.readLine()) != null){
                String tokens[] = line.split(" ");
                if (tokens!= null && tokens.length>0){
                    String cmd = tokens[0];
                    if ("quit".equalsIgnoreCase(cmd)){
                        //quit();
                        disconnectHandler();
                    } else if ("login".equalsIgnoreCase(cmd)) {
                        // Clalling the loginHandler function to store the login information for further use
                        loginHandler(outputStream, tokens);
                        } else if("kick".equalsIgnoreCase(cmd)){
                            // Call a function or method to Kick someone out of the server
                            String[] tokensMsg = line.split(" ");
                                kickUser(tokensMsg);
                            } else if("msg".equalsIgnoreCase(cmd)){
                                // Call a function or method to send a message to other user
                                String[] tokensMsg = line.split(" ", 3);
                                messageHandler(tokensMsg);
                                } else if ("join".equalsIgnoreCase(cmd)){
                                    // Call a function to join a User to a Group for Group Conversation 
                                    joinHandler(tokens);
                                    } else if ("leave".equalsIgnoreCase(cmd)){
                                        // Call a function or method to leave a Group Conversation
                                        leaveHandler(tokens);
                                        } else if ("sleep".equalsIgnoreCase(cmd)){
                                            // Callsa function to ban member from messaging for a certain amount of time
                                            String[] tokensMsg = line.split(" ");
                                            sleep(tokensMsg);
                                                // Call a function or method to Sleep an user for certain period of time
                                                } else if ("announcement".equalsIgnoreCase(cmd)){
                                                    String[] tokensMsg = line.split(" ", 2);
                                                    sendAll(tokensMsg);
                                                    // Call a function or method to announce a message to all active users
                                                    } else if ("ListUser".equalsIgnoreCase(cmd)){
                                                        ListUser();
                                                        // Call a function or method to list all active users in the Server
                                                        } else if ("NUKE".equalsIgnoreCase(cmd)) { 
                                                            // Spam world domination on all GUIs
                                                            String[] tokensMsg = line.split(" ");
                                                            NUKE(tokensMsg);
                                                            } else {
                                                                String msg = " Unknown " + cmd + "\n";
                                                                outputStream.write(msg.getBytes());
                                                            }
                }
           }
           clientSocket.close();
       }   
        
        public String getLogin(){
            return username;
        }

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
        
        private  void idRemover(){
            Set<String> keys = usr_id_map.keySet();
                for (String k : keys){
                    System.out.println(k);
                    // Removing disconnected Users from the list
                    if (username.equals(usr_id_map.get(k))){
                        usr_id_map.remove(k);
                    }
                }
        }

        private void disconnectHandler() throws IOException {
            // Function to handle all User Disconnection from the Server 
            List<ServerWorker> workerList = server.getworkerList();
            for (ServerWorker worker : workerList){
                String msg = "User Disconnected " +  username + "\n";
                worker.sendMsg(msg);
                idRemover();
                break;
//                Set<String> keys = usr_id_map.keySet();
//                for (String k : keys){
//                    System.out.println(k);
//                    // Removing disconnected Users from the list
//                    if (username.equals(usr_id_map.get(k))){
//                        usr_id_map.remove(k);
//                    }
//                }
            }
            // Append approipriate response to the Server 
            console_text.append(" User Disconnected: " +  username +"\n");
        }
        
        private void quit() throws IOException {
            // At this moment does nothing but i hope to make it wortk in the near future
            clientSocket.close();
        }

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
        
        private void ListUser() throws IOException {
           List<ServerWorker> workerList = server.getworkerList();
           for (ServerWorker worker : workerList){
               Set<String> keys = usr_id_map.keySet();
                for (String k : keys){
                    System.out.println(k);
                    // Removing disconnected Users from the list
                    if (username.equals(usr_id_map.get(k))){
                        if (username.equals(worker.getLogin())){
                            String  data = username + usr_id_map;
                            worker.sendMsg("Online Users");
                            worker.sendMsg(data);
                        }
                    }
                }
               
           }
        }
        
        private void sendMsg(String msg) throws IOException {
            // Function to send Message to the Client Side in this case its the Command Prompt
            if(username != null){
            outputStream.write(msg.getBytes());
           //console_text.append(msg);
            }
            // Printing same messsages to the Server
            console_text.append(msg);
        }
        
        private void kickUser(String[] tokens) throws IOException, InterruptedException {
            String receiver = tokens[1];
            
            List<ServerWorker> workerList = server.getworkerList();
            for (ServerWorker worker : workerList){
                 if (receiver.equalsIgnoreCase(worker.getLogin())){
                        String MsgOut = "Msg : " + username + " " + "YOU are KICKED from the Server " +"\n";
                        //outputStream.write(message.getBytes());
                        worker.sendMsg(MsgOut);
                        try{
                            Thread.sleep(5000);
                            worker.clientSocket.close();
                        }catch (IOException ex){
                            ex.printStackTrace();
                        }
                    }
                 idRemover();
            }            
        }
        
         private void sendAll(String[] tokens) throws IOException {
            String msg = tokens[1];
             
            List<ServerWorker> workerList  = server.getworkerList();
            for (ServerWorker worker : workerList){
                if(worker.getLogin()!= null){
                    worker.sendMsg(msg);
                }
            }
        }
         
        private void NUKE( String[] tokens) throws IOException {
            String timer = tokens[1];
            
            List<ServerWorker> workerList = server.getworkerList();
            long aTime = System.currentTimeMillis();
            while(false||(System.currentTimeMillis()-aTime) < 10000 ){
                    for (ServerWorker worker : workerList){
                     String msg = "WORLD DOMINATION" + "\n";
                    worker.sendMsg(msg);   
                }
            }
        }
        
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
        
        public boolean Membership(String topic){
            return SetTopic.contains(topic);
        }
        
        private void joinHandler(String[] tokens) {
            if (tokens.length > 1){
                String topic = tokens[1];
                SetTopic.add(topic);
            }
        }

        private void leaveHandler(String[] tokens) {
            if (tokens.length > 1){
                String topic = tokens[1];
                SetTopic.remove(topic);
            }
        }

        
    }

//    public void userDisconnect(String username){        
//        usr_id_map.remove(username);
//    }
  
    public void stopServer(){
       // Doers nothing  
    }
    
    public void serverStartButton(){
       // Starts the Server by calling in the StratServer Thread that in turn calls the ServerWorker Thread and the clientHandler
      Thread starter = new Thread(new StartServer());
      starter.start();
      console_text.append("Server started...\n");
    }           
    
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
    
    public void idGenerator(String username) {   
        // Creates random number for the ID of eash User
        int gen_id =(int)(Math.random() * (10000000 - 1000000 + 1) + 100000);
        usr_id_map.put(String.valueOf(gen_id),username);
    } 
    
    public void idRetriever(String username) {
        // Prints in the console window all the online users or running ServerWorker
        console_text.append(usr_id_map+"\n");
    }
    
    public boolean ArrayIteratorCompare(String usr,LinkedHashMap map){
        // Goes Through the array and checks for duplicates
        Boolean duplicate_usr = false;
        Set<String> keys = map.keySet();
        for (String k : keys){
            System.out.println(k);
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

        console_text.setColumns(20);
        console_text.setRows(5);
        jScrollPane1.setViewportView(console_text);

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
                                .addComponent(start_button)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(stop_button))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(listusr_button)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(clear_button))))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 440, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(start_button)
                    .addComponent(stop_button))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 320, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(clear_button)
                    .addComponent(listusr_button))
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
    private javax.swing.JButton start_button;
    private javax.swing.JButton stop_button;
    // End of variables declaration//GEN-END:variables
}
