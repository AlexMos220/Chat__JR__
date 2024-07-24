package task3008.client;

import task3008.ConsoleHelper;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class BotClient extends Client {

    public static void main(String[] args) {

        Client bot = new BotClient();
        bot.run();
    }

    //////////////////////////////////////методы ///////////////////////////////////////////
    @Override
    protected SocketThread getSocketThread() {
        return new BotSocketThread();
    }

    @Override
    protected boolean shouldSendTextFromConsole() {
        return false;
    }

    @Override
    protected String getUserName() {

        return String.format("date_bot_%d", (int) (Math.random() * 100));
    }

    //////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////

    public class BotSocketThread extends SocketThread {

        @Override
        protected void clientMainLoop() throws IOException, ClassNotFoundException {

           BotClient.this.sendTextMessage("Привіт чатику. Я робот. " +
                    "Розумію команди: дата, день, місяць, рік, час, година, хвилини, секунди.");

            super.clientMainLoop();
        }

        @Override
        protected void processIncomingMessage(String message) {

            ConsoleHelper.writeMessage(message);

            String[] split = message.split(": ");
            if (split.length != 2) return;
            String wish = split[1];
            String format = "";

            if (wish.equals("дата")) {
                format = "d.MM.YYYY";
            } else if (wish.equals("день")) {
                format = "d";
            } else if (wish.equals("місяць")) {
                format = "MMMM";
            } else if (wish.equals("рік")) {
                format = "YYYY";
            } else if (wish.equals("час")) {
                format = "H:mm:ss";
            } else if (wish.equals("година")) {
                format = "H";
            } else if (wish.equals("хвилини")) {
                format = "m";
            } else if (wish.equals("секунди")) {
                format = "s";
            }


            if(!format.isEmpty()) {
                SimpleDateFormat formatter = new SimpleDateFormat(format);
                Calendar calendar = new GregorianCalendar();
                String formattedDate = formatter.format(calendar.getTime());

                sendTextMessage(String.format("Інформація для %s: %s", split[0], formattedDate));
            }
        }

    }

}
