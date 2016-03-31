/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jdroidremote;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MouseInfo;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JTextField;
import org.apache.commons.codec.binary.Base64;

/**
 *
 * @author om4rezz
 */
public class ServerFrame extends javax.swing.JFrame {

    ServerSocket serverSocket;
    Socket clientSocket;
    DataInputStream dis;
    DataOutputStream dos;

    Thread thReceiveMouseCoords;
    Thread thStartMonitoring;

    Robot robot;

    /**
     * Creates new form ServerFrame
     */
    public ServerFrame() {
        initComponents();

        initObjects();
        initUI();
        initEventDriven();

        startMonitoring();
    }

    public void initEventDriven() {
        jbtRunServer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            serverSocket = new ServerSocket(5005);
                            System.out.println("Server is running...");
                            clientSocket = serverSocket.accept();
                            dis = new DataInputStream(clientSocket.getInputStream());
                            dos = new DataOutputStream(clientSocket.getOutputStream());
                            System.out.println("some device connected us from address: " + clientSocket.getInetAddress());

                            thReceiveMouseCoords.start();
                            thStartMonitoring.start();

                        } catch (IOException ex) {
                            Logger.getLogger(ServerFrame.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }).start();

                thReceiveMouseCoords = new Thread(new Runnable() {
                    @Override
                    public void run() {

                        System.out.println("START RECEIVING COORDS.............");

                        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
                        double width = screenSize.getWidth();
                        double height = screenSize.getHeight();

                        while (1 == 1) {
                            try {
                                String receivedStr = dis.readUTF();

                                if (receivedStr.contains("left_click")) {
                                    robot.mousePress(KeyEvent.BUTTON1_DOWN_MASK);
                                    robot.mouseRelease(KeyEvent.BUTTON1_DOWN_MASK);
                                } else if (receivedStr.contains("right_click")) {
                                    robot.mousePress(KeyEvent.BUTTON3_DOWN_MASK);
                                    robot.mouseRelease(KeyEvent.BUTTON3_DOWN_MASK);
                                } else if (receivedStr.contains("coords")) {
                                    System.out.println(receivedStr);
                                    String[] mouseCoords = receivedStr.split(":");

                                    int x = (int) (Integer.parseInt(mouseCoords[0]) * width / 100);
                                    int y = (int) (Integer.parseInt(mouseCoords[1]) * height / 100);

                                    robot.mouseMove(x, y);
                                } else {
                                    String[] dataArr = receivedStr.split("-");

                                    typeCharacter(dataArr[1]);
                                }
                            } catch (IOException ex) {
                                Logger.getLogger(ServerFrame.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    }
                });
            }
        });
    }

    public static void typeCharacter(String letter) {
        System.out.println(letter);
        Robot robot = null;
        try {
            robot = new Robot();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        if (Character.isLetter(letter.charAt(0))) {
            try {
                boolean upperCase = Character.isUpperCase(letter.charAt(0));
                String variableName = "VK_" + letter.toUpperCase();

                KeyEvent ke = new KeyEvent(new JTextField(), 0, 0, 0, 0, ' ');
                Class clazz = ke.getClass();
                Field field = clazz.getField(variableName);
                int keyCode = field.getInt(ke);

                //System.out.println(keyCode + " = keyCode");
                robot.delay(80);

                if (upperCase) {
                    robot.keyPress(KeyEvent.VK_SHIFT);
                }

                robot.keyPress(keyCode);
                robot.keyRelease(keyCode);

                if (upperCase) {
                    robot.keyRelease(KeyEvent.VK_SHIFT);
                }
            } catch (Exception e) {
                System.out.println(e);
            }
        } else if (letter.equals(".")) {
            robot.keyPress(KeyEvent.VK_PERIOD); //keyCode 46
            robot.keyRelease(KeyEvent.VK_PERIOD);
        } else if (letter.equals("!")) {
            robot.keyPress(KeyEvent.VK_SHIFT); //keyCode 16
            robot.keyPress(KeyEvent.VK_1); //keycode 49
            robot.keyRelease(KeyEvent.VK_1);
            robot.keyRelease(KeyEvent.VK_SHIFT);
        } else if (letter.equals(" ")) {
            robot.keyPress(KeyEvent.VK_SPACE);
            robot.keyRelease(KeyEvent.VK_SPACE);
        } else if (letter.equals("?")) {
            robot.keyPress(KeyEvent.VK_SHIFT); //keyCode 16
            robot.keyPress(KeyEvent.VK_SLASH); //keyCode 47
            robot.keyRelease(KeyEvent.VK_SLASH);
            robot.keyRelease(KeyEvent.VK_SHIFT);
        } else if (letter.equals(",")) {
            robot.keyPress(KeyEvent.VK_COMMA);
            robot.keyRelease(KeyEvent.VK_COMMA);
        } else if (letter.equals("@enter")) {
            robot.keyPress(KeyEvent.VK_ENTER);
            robot.keyRelease(KeyEvent.VK_ENTER);
        } else if (letter.equals("@backspace")) {
            robot.keyPress(KeyEvent.VK_BACK_SPACE);
            robot.keyRelease(KeyEvent.VK_BACK_SPACE);
        }
    }

    public void initUI() {
        setLocationRelativeTo(null);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jbtRunServer = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("JDroidRemote Server");

        jbtRunServer.setFont(new java.awt.Font("Cantarell", 0, 15)); // NOI18N
        jbtRunServer.setText("Run Server");

        jLabel1.setText("Please, press run server to start.!");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(jbtRunServer, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(154, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jbtRunServer, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(277, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

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
            java.util.logging.Logger.getLogger(ServerFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ServerFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ServerFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ServerFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ServerFrame().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JButton jbtRunServer;
    // End of variables declaration//GEN-END:variables

    private void initObjects() {
        try {
            robot = new Robot();
        } catch (AWTException ex) {
            Logger.getLogger(ServerFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void startMonitoring() {
        thStartMonitoring = new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    System.out.println("START MONITORING..........");

//                    while (1 == 1) {
                    BufferedImage screenCapture = robot.createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));

                    Image cursor = ImageIO.read(new File("cursor.png"));
                    int x = MouseInfo.getPointerInfo().getLocation().x;
                    int y = MouseInfo.getPointerInfo().getLocation().y;
                    Graphics2D graphics2D = screenCapture.createGraphics();
                    graphics2D.drawImage(cursor, x, y, 13, 25, null); // cursor.gif is 16x16 size.
                    ImageIO.write(screenCapture, "JPG", new File("2.jpg"));

                    Thread.sleep(200);

                    File file = new File("2.jpg");

                    // Reading a Image file from file system
                    FileInputStream imageInFile = new FileInputStream(file);
                    byte imageData[] = new byte[(int) file.length()];
                    imageInFile.read(imageData);

                    // Converting Image byte array into Base64 String
                    String imageDataString = ImageManipulation.encodeImage(imageData);

                    System.out.println(imageDataString.length());

//                        System.out.println(imageDataString);
//                        // Converting a Base64 String into Image byte array
//                        byte[] imageByteArray = ImageManipulation.decodeImage(imageDataString);
//
//                        // Write a image byte array into file system
//                        FileOutputStream imageOutFile = new FileOutputStream(
//                                "_avatar_.jpg");
//
//                        imageOutFile.write(imageByteArray);
                    imageInFile.close();
//                        imageOutFile.close();

                    System.out.println("Image Successfully Manipulated!");

                    // Split the five sandwiches.!
                    StringBuilder sbImageDataString = new StringBuilder(imageDataString);

                    for (int i = 0; i < sbImageDataString.toString().length(); i += 30000) {
                        if (i + 30000 <= sbImageDataString.toString().length()) {
                            dos.writeUTF(sbImageDataString.substring(i, i + 30000));
                            dos.flush();
                        } else {
                            dos.writeUTF(sbImageDataString.substring(i, sbImageDataString.toString().length()));
                            dos.flush();
                        }

                    }
                    dos.writeUTF("...");
                    dos.flush();

//                    }
                } catch (IOException ex) {
                    Logger.getLogger(ServerFrame.class.getName()).log(Level.SEVERE, null, ex);
                } catch (InterruptedException ex) {
                    Logger.getLogger(ServerFrame.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }
}

class ImageManipulation {

    /**
     * Encodes the byte array into base64 string
     *
     * @param imageByteArray - byte array
     * @return String a {@link java.lang.String}
     */
    public static String encodeImage(byte[] imageByteArray) {
        return Base64.encodeBase64URLSafeString(imageByteArray);
    }

    /**
     * Decodes the base64 string into byte array
     *
     * @param imageDataString - a {@link java.lang.String}
     * @return byte array
     */
    public static byte[] decodeImage(String imageDataString) {
        return Base64.decodeBase64(imageDataString);
    }
}
