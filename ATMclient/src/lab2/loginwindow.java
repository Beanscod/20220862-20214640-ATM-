package lab2;

import lab2.systemwindow;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.*;
import java.net.Socket;

public class loginwindow extends JFrame {
    private final String serverHostname = "10.242.228.223";
    private final int serverPort = 2525;
    public JTextField usernameField;
    public JPasswordField passwordField;// 声明为类的成员变量

    public loginwindow(){
        this.setTitle("ATM登录界面");
        this.setSize(400, 300); // 设置窗口大小为 400x300
        this.setLocationRelativeTo(null);//设置界面居中
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // 关闭窗口时退出应用

        initComponents(); // 初始化组件
        this.setVisible(true);//显示窗口
    }

    private void initComponents() {
        // 设置布局为null，使用绝对布局
        this.setLayout(null);

        // 初始化标签
        JLabel usernameLabel = new JLabel("用户名:");
        JLabel passwordLabel = new JLabel("密码:");

        // 初始化文本框
        usernameField = new JTextField();
        passwordField = new JPasswordField();

        // 初始化按钮
        JButton loginButton = new JButton("登录");

        // 设置组件位置和大小
        usernameLabel.setBounds(50, 100, 60, 30); // 调整标签的大小
        usernameField.setBounds(120, 100, 150, 30); // 调整文本框的大小
        passwordLabel.setBounds(50, 150, 60, 30); // 调整标签的大小
        passwordField.setBounds(120, 150, 150, 30); // 调整文本框的大小
        loginButton.setBounds(150, 200, 80, 30); // 调整按钮的大小

        // 添加组件到面板
        this.add(usernameLabel);
        this.add(usernameField);
        this.add(passwordLabel);
        this.add(passwordField);
        this.add(loginButton);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 获取密码
                String passMessage = passwordField.getText();
                // 使用 SwingWorker 处理与服务器的通信
                SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                    @Override
                    protected Void doInBackground() throws Exception {
                        sendMessageToServer2(passMessage);
                        return null;
                    }

                    @Override
                    protected void done() {
                        // 在 SwingWorker 执行完毕后创建并显示新的系统窗口
                        systemwindow atmWindow = new systemwindow();
                        atmWindow.setVisible(true); // 显示窗口
                        // 关闭当前登录窗口
                        dispose();
                    }
                };
                worker.execute();
            }
        });



        passwordField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                // 获取用户名
                String heloMessage = usernameField.getText();
                // 使用 SwingWorker 处理与服务器的通信
                SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                    @Override
                    protected Void doInBackground() throws Exception {
                        sendMessageToServer1(heloMessage);
                        return null;
                    }
                };
                worker.execute();
            }
        });
    }

    public void sendMessageToServer1(String heloMessage) throws IOException {
        try (Socket clientSocket = new Socket(serverHostname, serverPort);
             BufferedWriter outToServer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream(), "UTF-8"));
             BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), "UTF-8"))) {

            // 向服务器发送HELO报文
            outToServer.write("HELO " + heloMessage);
            outToServer.newLine();
            outToServer.flush();

            // 接收服务器的响应报文
            String response = inFromServer.readLine();
            System.out.println("服务器响应：" + response);

            // 处理服务器的响应报文
            if (response.equals("402 USER NOT FOUND")) {
                // 弹出窗口要求重新输入userid
                JOptionPane.showMessageDialog(this, "该用户不存在，请重新输入用户名", "用户不存在", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void sendMessageToServer2(String passMessage) throws IOException {
        try (Socket clientSocket = new Socket(serverHostname, serverPort);
             BufferedWriter outToServer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream(), "UTF-8"));
             BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), "UTF-8"))) {

            // 向服务器发送PASS报文
            outToServer.write("PASS " + passMessage);
            outToServer.newLine();
            outToServer.flush();

            // 接收服务器的响应报文
            String response = inFromServer.readLine();
            System.out.println("服务器响应：" + response);

            // 处理服务器的响应报文
            if (response.equals("525 OK!")) {
                // 创建并显示新的系统窗口
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        systemwindow atmWindow = new systemwindow();
                        atmWindow.setVisible(true); // 显示窗口
                    }
                });

            }
            // 处理服务器的响应报文
            if (response.equals("401 ERROR!")) {
                // 弹出窗口要求重新输入密码
                JOptionPane.showMessageDialog(this, "密码错误，请重新输入密码", "密码错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
