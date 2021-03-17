package actual_chat_client;

// Importing the Packages
import java.awt.Color;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Date;


/*
TODO LIST:

Client must handle login failed

Client must not get stuck in an loop when disconnected.

Client gets stuck in ConnectionReset Loop when server is closed sometimes.


*/

/*
Added Functionality 

Added a Refresh functionality in the Client GUI
*/        


public class client_frame extends javax.swing.JFrame 
{
    Boolean connection_state = false;
    String username;
    ChatClient clientWindow;
    BufferedReader packetIn;
    Socket socket;
    
    // Works
    public class ChatClient
    {
        private final String serverName;
        private final int serverPort;
        
        // Reading and writing to the Server
        private OutputStream serverOut;
        private InputStream serverIn;
        private PrintWriter packetOut;
       
        
       
        public ChatClient (String serverName, int serverPort) throws IOException 
        {
          
          this.serverName = serverName;
          this.serverPort = serverPort;
        
        }
        
        // Works
        public void server_connector()
        {
            try
            {
                socket = new Socket(serverName, serverPort);
                serverOut = socket.getOutputStream();
                serverIn = socket.getInputStream();
                clientconsoleText.append( new Date() + " Connection Sucessful\n ");
                //Using the login command in the server_code and passing in the entire username as a command
                String login_command = ("login "+username);
                packetIn = new BufferedReader(new InputStreamReader(serverIn));
                packetOut = new PrintWriter(serverOut, true);
                // Printing the login command in this case its login <username>
                //System.out.println(login_command);
                packetOut.println(login_command);
                String message = packetIn.readLine();
                //statement is only active until it receives the login message from server.
                clientconsoleText.append(message+"\n");
                connection_state = true;
                
                
            }
            catch(IOException ex)
            {
                ex.printStackTrace();
                clientconsoleText.append(new Date() + " Connection Failed\n ");
                connection_state = false;
            } 
        }
        
        // BullShit Code
        // Need to Delete this
//        private void login(String address, int port ,String username) throws IOException { 
//            this.socket = new Socket(serverName, serverPort);
//            //this.bufferedIn = new BufferedReader(new InputStreamReader(serverIn));
//            this.serverOut = socket.getOutputStream();
//            if (address.equalsIgnoreCase(serverName) && port == serverPort){
//                username = usernametext.getText();
////                String cmd = "login " + username + "\n";
////                serverOut.write(cmd.getBytes());
//                
////                String response = bufferedIn.readLine();
////                System.out.println(response);
////                clientconsoleText.append(response);
////            
//                
//                PrintWriter out = new PrintWriter(serverOut, true);
//                String login_command = ("login "+username);
//                out.println(login_command);
//            }
//       }
        
        // Works
        public void sendMsg(String msg_text_str) throws IOException{
            //this.serverOut.write(msg_text_str.getBytes());
            packetOut.println(msg_text_str);  
        }
        
        // Works
        public void server_disconnect() throws IOException{
            if (connection_state == true){
                packetOut.println("quit");
                socket.close();
                connection_state = false;
            }
        }
    }
    
    // Works Beautifully
    // Leo is the BEST
    public class SocketListener implements Runnable {
    //function to receive messages
        public void run() {
            //Will run until client has disconnected.
            while (connection_state == true && socket.isClosed() == false){
                try {
                    String message = packetIn.readLine();
                    if (message != null){
                    clientconsoleText.append(message+"\n");
                    }
                } catch (IOException ex) {
                    Logger.getLogger(client_frame.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
    
    public client_frame() throws IOException
    {
        initComponents();
        clientWindow = null;
        
    }
    
    // Works
    //Fuction that creates new thread dedicated to listening for inputs.
    public void ListenThread() {
            if (socket.isClosed() == false) {
		Thread SocketListener = new Thread(new SocketListener());
		SocketListener.start();
            }
    }
   
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jPanel1 = new javax.swing.JPanel();
        discBtn = new javax.swing.JButton();
        usernametext = new javax.swing.JTextField();
        addressStr = new javax.swing.JTextField();
        portID = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        clientconsoleText = new javax.swing.JTextArea();
        chatinputBox = new javax.swing.JTextField();
        sendBtn = new javax.swing.JButton();
        refreshbutton = new javax.swing.JButton();
        connectBtn = new javax.swing.JButton();

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jScrollPane1.setViewportView(jTextArea1);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Chat Client");

        discBtn.setText("Disconnect");
        discBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                discBtnActionPerformed(evt);
            }
        });

        usernametext.setText("Username");
        usernametext.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                usernametextMouseClicked(evt);
            }
        });
        usernametext.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                usernametextActionPerformed(evt);
            }
        });

        addressStr.setText("IP Address");
        addressStr.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                addressStrMouseClicked(evt);
            }
        });
        addressStr.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addressStrActionPerformed(evt);
            }
        });

        portID.setText("Port");
        portID.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                portIDMouseClicked(evt);
            }
        });
        portID.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                portIDActionPerformed(evt);
            }
        });

        clientconsoleText.setEditable(false);
        clientconsoleText.setColumns(20);
        clientconsoleText.setRows(5);
        jScrollPane2.setViewportView(clientconsoleText);

        chatinputBox.setText("Type something nice...");
        chatinputBox.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                chatinputBoxMouseClicked(evt);
            }
        });
        chatinputBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chatinputBoxActionPerformed(evt);
            }
        });

        sendBtn.setText("Send");
        sendBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sendBtnActionPerformed(evt);
            }
        });

        refreshbutton.setText("Refresh");
        refreshbutton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                refreshbuttonActionPerformed(evt);
            }
        });

        connectBtn.setText("Connect");
        connectBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                connectBtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jScrollPane2)
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                        .addComponent(chatinputBox, javax.swing.GroupLayout.PREFERRED_SIZE, 414, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(sendBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(29, 29, 29))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(usernametext, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(addressStr, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(portID, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addComponent(refreshbutton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(connectBtn)
                                .addGap(22, 22, 22))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addComponent(discBtn)
                                .addGap(24, 24, 24))))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(addressStr, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(portID, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(refreshbutton)
                        .addComponent(connectBtn)))
                .addGap(21, 21, 21)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(discBtn)
                    .addComponent(usernametext, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 268, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chatinputBox, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(sendBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(17, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Works
    private void connectBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_connectBtnActionPerformed
        // TODO add your handling code here:
        username = usernametext.getText();
        if (addressStr.getText().equals("IP Address") || portID.getText().equals("Port") || addressStr.getText().equals("") || portID.equals("")){
            clientconsoleText.append("Please enter a valid address and port.\n");
        }
        else {
            if (connection_state == true) {
            clientconsoleText.append("Alredy connected.\n");
            }
            else {
                int portnum = Integer.parseInt(portID.getText());
                clientconsoleText.append("Connecting...\n");
                try {
                    clientWindow = new ChatClient(addressStr.getText(), portnum);
                } catch (IOException ex) {
                    Logger.getLogger(client_frame.class.getName()).log(Level.SEVERE, null, ex);
                }
                clientWindow.server_connector();
                if (connection_state == true){
                    addressStr.setEditable(false);
                    portID.setEditable(false);
                    usernametext.setEditable(false);
                    addressStr.setBackground(Color.gray);
                    portID.setBackground(Color.gray);
                    usernametext.setBackground(Color.gray);
                    ListenThread();
                    
                }
            }
        }
        
        
      
        
    }//GEN-LAST:event_connectBtnActionPerformed

    // Works
    private void discBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_discBtnActionPerformed
        // TODO add your handling code here:
        if (connection_state == false) 
        {
            clientconsoleText.append(new Date() + " No Connection has been made.\n ");
        }
       else {
            try {
                clientWindow.server_disconnect();
                clientWindow = null;
                addressStr.setEditable(true);
                portID.setEditable(true);
                usernametext.setEditable(true);
                addressStr.setBackground(Color.white);
                portID.setBackground(Color.white);
                usernametext.setBackground(Color.white);
            } catch (IOException ex) {
                Logger.getLogger(client_frame.class.getName()).log(Level.SEVERE, null, ex);
            }
         }
    }//GEN-LAST:event_discBtnActionPerformed

    private void usernametextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_usernametextActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_usernametextActionPerformed

    private void usernametextMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_usernametextMouseClicked
        // TODO add your handling code here:
       if (connection_state == false && usernametext.getText().equals("Username")){
            usernametext.setText("");
       }
    }//GEN-LAST:event_usernametextMouseClicked

    private void addressStrActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addressStrActionPerformed
        // TODO add your handling code here:
        
    }//GEN-LAST:event_addressStrActionPerformed

    private void portIDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_portIDActionPerformed
        // TODO add your handling code here:
        
    }//GEN-LAST:event_portIDActionPerformed

    private void portIDMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_portIDMouseClicked
        // TODO add your handling code here:
        if (connection_state == false){
            portID.setText("");
        }
    }//GEN-LAST:event_portIDMouseClicked

    private void chatinputBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chatinputBoxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chatinputBoxActionPerformed

    private void chatinputBoxMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_chatinputBoxMouseClicked
        if (connection_state == false){
            chatinputBox.setText("");
        }
    }//GEN-LAST:event_chatinputBoxMouseClicked

    private void sendBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sendBtnActionPerformed
        // TODO add your handling code here:
        String msgText = chatinputBox.getText();
        //int portnum = Integer.parseInt(portID.getText());
        System.out.print(connection_state);
        if (connection_state == true) {
            clientconsoleText.append(username+":"+msgText+"\n");
            try {
                clientWindow.sendMsg(msgText);
            } catch (IOException ex) {
                Logger.getLogger(client_frame.class.getName()).log(Level.SEVERE, null, ex);
            }
             
        }else{
            clientconsoleText.append(new Date() + " Connection not found. Please Connect to a Server\n ");
        }
    }//GEN-LAST:event_sendBtnActionPerformed

    private void addressStrMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_addressStrMouseClicked
        // TODO add your handling code here:
        if (connection_state == false){
            addressStr.setText("");
        }
    }//GEN-LAST:event_addressStrMouseClicked

    private void refreshbuttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_refreshbuttonActionPerformed
        // TODO add your handling code here:
        revalidate();
        clientconsoleText.setText("");
        addressStr.setText("IP Address");
        chatinputBox.setText("Type something nice...");
        portID.setText("Port");
        usernametext.setText("Username");
        clientWindow = null;
        
        connection_state = false;{
                    addressStr.setEditable(true);
                    portID.setEditable(true);
                    usernametext.setEditable(true);
                    addressStr.setBackground(Color.white);
                    portID.setBackground(Color.white);
                    usernametext.setBackground(Color.white);
        }
    }//GEN-LAST:event_refreshbuttonActionPerformed

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
            java.util.logging.Logger.getLogger(client_frame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(client_frame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(client_frame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(client_frame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    new client_frame().setVisible(true);
                } catch (IOException ex) {
                    Logger.getLogger(client_frame.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField addressStr;
    private javax.swing.JTextField chatinputBox;
    private javax.swing.JTextArea clientconsoleText;
    private javax.swing.JButton connectBtn;
    private javax.swing.JButton discBtn;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextField portID;
    private javax.swing.JButton refreshbutton;
    private javax.swing.JButton sendBtn;
    private javax.swing.JTextField usernametext;
    // End of variables declaration//GEN-END:variables
}
