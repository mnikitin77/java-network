package lesson2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

public class HttpResourceInfoApp {

    public static void main(String[] args) throws IOException {

        if (args.length == 0) {
            System.out.println("Введите адрес HTTP-ресурса");
            System.exit(1);
        }

        BufferedReader reader = null;

        try {
            var url = new URL(args[0]);
            var protocol = url.getProtocol();
            if (!protocol.equals("http") && !protocol.equals("https")) {
                System.out.println("Согласно условию ДЗ допускается ресурс только по протоколу http(s)");
                System.exit(1);
            }

            var connection = url.openConnection();

            System.out.println("Размер ресурса (байт):\t" + connection.getContentLength());
            System.out.println("Тип ресурса:\t\t\t" + connection.getContentType());
            System.out.println();
            System.out.println("Содержимое ресурса:\n");

            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            reader.lines().forEach(System.out::println);

        } catch (MalformedURLException e) {
            System.out.println("Формат введённого адреса некорректен. Попробуйте ещё раз.");
        } catch (Exception e) {
            System.out.println("В процессе работы возникло исключение.\n Подробная информация:");
            e.printStackTrace();
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }
}
