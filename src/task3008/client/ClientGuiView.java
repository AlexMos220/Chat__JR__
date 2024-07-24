package task3008.client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ClientGuiView {
    private final ClientGuiController controller;

    private JFrame frame = new JFrame("Чат");
    private JTextField textField = new JTextField(50);
    private JTextArea messages = new JTextArea(10, 50);
    private JTextArea users = new JTextArea(10, 10);

    public ClientGuiView(ClientGuiController controller) {
        this.controller = controller;
        initView();
    }

    private void initView() {
        textField.setEditable(false);
        messages.setEditable(false);
        users.setEditable(false);

       // добавление элементов в окно
        frame.getContentPane().add(textField, BorderLayout.NORTH);
        frame.getContentPane().add(new JScrollPane(messages), BorderLayout.WEST);
        frame.getContentPane().add(new JScrollPane(users), BorderLayout.EAST);


        frame.pack();  //авто установка размера окна
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);// при закрытии окна программа завершается
        frame.setVisible(true);


        //считывает сообщение в поле textField. Поле активно, если textField.setEditable(true);
        textField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                controller.sendTextMessage(textField.getText());
                textField.setText("");
            }
        });
    }

    // выводится окошко c запросом сервера
    public String getServerAddress() {
        return JOptionPane.showInputDialog(
                frame,
                "Введіть адресу сервера:",
                "Конфігурація клієнта",
                JOptionPane.QUESTION_MESSAGE);
    }

    public int getServerPort() {
        while (true) {
            String port = JOptionPane.showInputDialog(
                    frame,
                    "Введіть адресу сервера:",
                    "Конфігурація клієнта",
                    JOptionPane.QUESTION_MESSAGE);
            try {
                return Integer.parseInt(port.trim());
            } catch (Exception e) {
                JOptionPane.showMessageDialog(
                        frame,
                        "Введено некоректний порт сервера. Спробуйте ще раз.",
                        "Конфігурація клієнта",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public String getUserName() {
        return JOptionPane.showInputDialog(
                frame,
                "Введіть ваше ім'я:",
                "Конфігурація клієнта",
                JOptionPane.QUESTION_MESSAGE);
    }


    // при подключению к серверу активируется поле textField или выводится сообщение про ошибку
    public void notifyConnectionStatusChanged(boolean clientConnected) {
        textField.setEditable(clientConnected);
        if (clientConnected) {
            JOptionPane.showMessageDialog(
                    frame,
                    "З'єднання з сервером установлено",
                    "Чат",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(
                    frame,
                    "Клієнт не підключено до сервера",
                    "Чат",
                    JOptionPane.ERROR_MESSAGE);
        }

    }

    public void refreshMessages() {
        messages.append(controller.getModel().getNewMessage() + "\n");
    }

    public void refreshUsers() {
        ClientGuiModel model = controller.getModel();
        StringBuilder sb = new StringBuilder();
        for (String userName : model.getAllUserNames()) {
            sb.append(userName).append("\n");
        }
        users.setText(sb.toString());
    }
}
