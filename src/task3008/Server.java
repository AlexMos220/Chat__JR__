package task3008;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Server {
    private static Map<String, Connection> connectionMap = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        ConsoleHelper.writeMessage("Введіть порт сервера:");
        int port = ConsoleHelper.readInt();

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            ConsoleHelper.writeMessage("Чат сервер запущено.");
            while (true) {
                // Очікуємо вхідне з'єднання і запускаємо окремий потік при його прийнятті
                Socket socket = serverSocket.accept();
                new Handler(socket).start();
            }
        } catch (Exception e) {
            ConsoleHelper.writeMessage("Помилка при запуску або роботі сервера.");
        }
    }

    private static class Handler extends Thread {
        private Socket socket;

        public Handler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            ConsoleHelper.writeMessage("Встановлено нове з'єднання з " + socket.getRemoteSocketAddress());

            String userName = null;

            try (Connection connection = new Connection(socket)) {
                userName = serverHandshake(connection);

                // Повідомляємо всіх учасників, що приєднався новий учасник
                sendBroadcastMessage(new Message(MessageType.USER_ADDED, userName));

                // Повідомляємо нового учасника про існуючих учасників
                notifyUsers(connection, userName);

                // Обробляємо повідомлення користувачів
                serverMainLoop(connection, userName);

            } catch (IOException | ClassNotFoundException e) {
                ConsoleHelper.writeMessage("Помилка при обміні даними з " + socket.getRemoteSocketAddress());
            }

            if (userName != null) {
                connectionMap.remove(userName);
                sendBroadcastMessage(new Message(MessageType.USER_REMOVED, userName));
            }

            ConsoleHelper.writeMessage("З'єднання з " + socket.getRemoteSocketAddress() + " закрито.");
        }

        private String serverHandshake(Connection connection) throws IOException, ClassNotFoundException {
            while (true) {
                connection.send(new Message(MessageType.NAME_REQUEST));

                Message message = connection.receive();
                if (message.getType() != MessageType.USER_NAME) {
                    ConsoleHelper.writeMessage("Отримано повідомлення від " + socket.getRemoteSocketAddress() + ". Тип повідомлення не відповідає протоколу.");
                    continue;
                }

                String userName = message.getData();

                if (userName.isEmpty()) {
                    ConsoleHelper.writeMessage("Спроба підключення до сервера з пустим ім'ям від " + socket.getRemoteSocketAddress());
                    continue;
                }

                if (connectionMap.containsKey(userName)) {
                    ConsoleHelper.writeMessage("Спроба підключення до сервера з уже використовуваним ім'ям від " + socket.getRemoteSocketAddress());
                    continue;
                }
                connectionMap.put(userName, connection);

                connection.send(new Message(MessageType.NAME_ACCEPTED));
                return userName;
            }
        }

        private void notifyUsers(Connection connection, String userName) throws IOException {
            for (String name : connectionMap.keySet()) {
                if (name.equals(userName))
                    continue;
                connection.send(new Message(MessageType.USER_ADDED, name));
            }
        }

        private void serverMainLoop(Connection connection, String userName) throws IOException, ClassNotFoundException {
            while (true) {
                Message message = connection.receive();

                if (message.getType() == MessageType.TEXT) {
                    String data = message.getData();
                    sendBroadcastMessage(new Message(MessageType.TEXT, userName + ": " + data));
                } else {
                    ConsoleHelper.writeMessage("Отримано повідомлення від " + socket.getRemoteSocketAddress() + ". Тип повідомлення не відповідає протоколу.");
                }
            }
        }
    }

    public static void sendBroadcastMessage(Message message) {
        // Розсилаємо повідомлення по всіх з'єднаннях
        for (Connection connection : connectionMap.values()) {
            try {
                connection.send(message);
            } catch (IOException e) {
                ConsoleHelper.writeMessage("Не змогли надіслати повідомлення " + connection.getRemoteSocketAddress());
            }
        }
    }
}
