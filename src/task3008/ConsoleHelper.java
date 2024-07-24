package task3008;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ConsoleHelper {
    private static BufferedReader bis = new BufferedReader(new InputStreamReader(System.in));

    public static void writeMessage(String message) {
        System.out.println(message);
    }

    public static String readString() {
        while (true) {
            try {
                String buf = bis.readLine();
                if (buf != null)
                    return buf;
            } catch (IOException e) {
                writeMessage("Помилка при спробі введення тексту. Спробуйте ще раз.");
            }
        }
    }

    public static int readInt() {
        while (true) {
            try {
                return Integer.parseInt(readString().trim());
            } catch (NumberFormatException e) {
                writeMessage("Виникла помилка при спробі введення числа. Спробуйте ще раз.");
            }
        }
    }
}
