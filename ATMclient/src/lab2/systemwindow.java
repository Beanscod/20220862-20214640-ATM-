package lab2;

import javax.swing.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.*;
import java.net.Socket;

public class systemwindow extends JFrame {
    private final String serverHostname = "10.242.228.223";
    private final int serverPort = 2525;


    public systemwindow() {
        init();//设置窗口
        initComponents(); // 初始化组件
    }

    private void init() {
        this.setTitle("ATM使用界面");
        this.setAlwaysOnTop(true);//设计界面置顶
        this.setSize(800, 600); // 设置窗口大小
        this.setLocationRelativeTo(null);//设置界面居中
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // 关闭窗口时退出应用
        this.setLayout(null);//取消默认的居中位置
    }

    private void initComponents() {
        // 在这里初始化组件，例如按钮等
        // 设置布局为null，使用绝对布局
        this.setLayout(null);

        // 初始化标签和文本框
        JLabel withdrawLabel = new JLabel("取款金额：");
        JTextField withdrawField = new JTextField();

        // 初始化按钮
        JButton searchButton = new JButton("查询");
        JButton subButton = new JButton("取款");
        JButton quitButton = new JButton("退出");

        // 设置组件位置和大小
        int labelWidth = 80;
        int labelHeight = 30;
        int fieldWidth = 150;
        int fieldHeight = 30;
        int buttonWidth = 100;
        int buttonHeight = 30;
        int x1 = 50; // 标签的 x 坐标
        int x2 = x1 + labelWidth + 10; // 文本框的 x 坐标，标签后面留空10像素
        int x3 = x2 + fieldWidth + 20; // 按钮的 x 坐标，文本框后面留空20像素
        int y = 100; // 初始 y 坐标

        // 设置标签和文本框位置和大小
        withdrawLabel.setBounds(x1, y, labelWidth, labelHeight);
        withdrawField.setBounds(x2, y, fieldWidth, fieldHeight);

        // 设置按钮位置和大小
        int xSpacing = 120; // 按钮之间的水平间距
        searchButton.setBounds(x3, y, buttonWidth, buttonHeight);
        subButton.setBounds(x3 + xSpacing, y, buttonWidth, buttonHeight);
        quitButton.setBounds(x3 + 2 * xSpacing, y, buttonWidth, buttonHeight);

        // 将标签、文本框和按钮添加到窗口中
        this.add(withdrawLabel);
        this.add(withdrawField);
        this.add(searchButton);
        this.add(subButton);
        this.add(quitButton);

        // 请求焦点在窗口上
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                requestFocusInWindow();
            }
        });

        searchButton.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                //发送 BALA 报文
                String balaMessage = "BALA";
                // 使用 SwingWorker 处理与服务器的通信
                SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                    @Override
                    protected Void doInBackground() throws Exception {
                        sendMessageToServer3(balaMessage);
                        return null;
                    }
                };
                worker.execute();

            }
        });
// 给取款按钮添加焦点监听器
        subButton.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                //发送 WDRA 报文
                String wdraMessage = withdrawField.getText();
                // 使用 SwingWorker 处理与服务器的通信
                SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                    @Override
                    protected Void doInBackground() throws Exception {
                        sendMessageToServer4(wdraMessage);
                        return null;
                    }
                };
                worker.execute();

            }
        });
        // 给退出按钮添加焦点监听器
        quitButton.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                //发送 BYE 报文
                String byeMessage = "BYE";
                // 使用 SwingWorker 处理与服务器的通信
                SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                    @Override
                    protected Void doInBackground() throws Exception {
                        sendMessageToServer5(byeMessage);
                        return null;
                    }
                };
                worker.execute();

            }
        });
    }

    public void sendMessageToServer3(String balaMessage) throws IOException {
        try (Socket clientSocket = new Socket(serverHostname, serverPort);
             BufferedWriter outToServer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream(), "UTF-8"));
             BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), "UTF-8"))) {

            // 向服务器发送 BALA 报文
            outToServer.write("BALA");
            outToServer.newLine();
            outToServer.flush();

            // 接收服务器的响应报文
            String response = inFromServer.readLine();
            System.out.println("服务器响应：" + response);

        }
    }
    public void sendMessageToServer4(String wdraMessage) throws IOException {
        try (Socket clientSocket = new Socket(serverHostname, serverPort);
             BufferedWriter outToServer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream(), "UTF-8"));
             BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), "UTF-8"))) {

            // 向服务器发送 WDRA 报文
            outToServer.write("WDRA "+wdraMessage);
            outToServer.newLine();
            outToServer.flush();

            // 接收服务器的响应报文
            String response = inFromServer.readLine();
            System.out.println("服务器响应：" + response);
// 处理服务器的响应报文
            if (response.equals("403 INSUFFICIENT FUNDS")) {
                // 创建一个提示框来提示用户重新输入取款金额
                JOptionPane.showMessageDialog(this, "余额不足，请重新输入取款金额", "取款失败", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    public void sendMessageToServer5(String byeMessage) throws IOException {
        try (Socket clientSocket = new Socket(serverHostname, serverPort);
             BufferedWriter outToServer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream(), "UTF-8"));
             BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), "UTF-8"))) {

            // 向服务器发送 BYE 报文
            outToServer.write("BYE");
            outToServer.newLine();
            outToServer.flush();

            // 接收服务器的响应报文
            String response = inFromServer.readLine();
            System.out.println("服务器响应：" + response);
// 处理服务器的响应报文
            if (response.equals("BYE")) {
                dispose();
            }
        }
    }
}
