package task3008.client;

import task3008.Connection;
import task3008.ConsoleHelper;
import task3008.Message;
import task3008.MessageType;

import java.io.IOException;
import java.net.Socket;

// если на локальном компе - localhost/////////////////////////////


public class Client {

    protected Connection connection;
    private volatile boolean clientConnected = false;

    public static void main(String[] args) {

        Client client = new Client();
        client.run();
    }


    /////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////////

    protected String getServerAddress() {

        ConsoleHelper.writeMessage("Введіть адресу сервера");

        return ConsoleHelper.readString();
    }

    protected int getServerPort() {

        ConsoleHelper.writeMessage("Введіть номер порту");

        return ConsoleHelper.readInt();
    }

    protected String getUserName() {

        ConsoleHelper.writeMessage("Введіть Ім'я користувача");

        return ConsoleHelper.readString();
    }

    protected boolean shouldSendTextFromConsole() {

        return true;
    }

    protected SocketThread getSocketThread() {

        return new SocketThread();
    }

    protected void sendTextMessage(String text) {

        try {
            connection.send(new Message(MessageType.TEXT, text));
        } catch (IOException e) {
            System.out.println("Виникла помилка при відправленні повідомлення");
            clientConnected = false;
        }
    }
////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////

    public void run() {

        SocketThread socketThread = getSocketThread();
        socketThread.setDaemon(true);
        socketThread.start();
        try {
            synchronized (this) {
                wait();
            }
        } catch (InterruptedException e) {
            System.out.println("Сталася системна помилка, програму буде закрито");
            return;
        }

        if (clientConnected) {
            System.out.println("З'єднання встановлено. Для виходу набери команду 'exit'.");
        } else {
            System.out.println("Сталася помилка під час роботи клієнта.");
        }

        while (clientConnected) {
            String text = ConsoleHelper.readString();
            if (text.equalsIgnoreCase("exit")) break;
            if (shouldSendTextFromConsole()) sendTextMessage(text);
        }
    }

    public class SocketThread extends Thread {

        protected void processIncomingMessage(String message) {

            ConsoleHelper.writeMessage(message);
        }

        protected void informAboutAddingNewUser(String userName) {

            ConsoleHelper.writeMessage(String.format("Користувач %s приєднався до чата", userName));
        }

        protected void informAboutDeletingNewUser(String userName) {

            ConsoleHelper.writeMessage(String.format("Користувач %s залишив чат", userName));
        }

        protected void notifyConnectionStatusChanged(boolean clientConnected) {

            Client.this.clientConnected = clientConnected;
            synchronized (Client.this) {
                Client.this.notify();
            }
        }
        ///////////////////////////////////////////////////////////////////////////////////////
        ///////////////////////////////////////////////////////////////////////////////////////

        protected void clientHandshake() throws IOException, ClassNotFoundException {

            while (true) {

                Message message = connection.receive();                   //a
                if (message.getType() == MessageType.NAME_REQUEST) {      //б
                    String userName = getUserName();
                    connection.send(new Message(MessageType.USER_NAME,userName));
                    continue;
                }

                if(message.getType() == MessageType.NAME_ACCEPTED){
                    notifyConnectionStatusChanged(true);
                    break;
                }
                else throw new IOException("Unexpected MessageType");
            }
        }
        protected void clientMainLoop() throws IOException, ClassNotFoundException {


            while (true) {

                Message message = connection.receive();

                if (message.getType() == MessageType.TEXT) {
                    processIncomingMessage(message.getData());
                    continue;

                } if (message.getType() == MessageType.USER_ADDED) {
                    informAboutAddingNewUser(message.getData());
                    continue;

                } if (message.getType() == MessageType.USER_REMOVED) {
                    informAboutDeletingNewUser(message.getData());

                } else throw new IOException("Unexpected MessageType");
            }
        }

       public void run(){

            String serverAddress =  getServerAddress();
            int serverPort = getServerPort();


           try {
               Socket socket = new Socket(serverAddress,serverPort);
               connection = new Connection(socket);
               clientHandshake();
               clientMainLoop();
           } catch (IOException |ClassNotFoundException e) {
               notifyConnectionStatusChanged(false);
           }
       }
    }
}
