import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Main {
    //Создаём блокирующие очереди для анализирующих потоков
    public static BlockingQueue<String> quantityA = new ArrayBlockingQueue<>(100);
    public static BlockingQueue<String> quantityB = new ArrayBlockingQueue<>(100);
    public static BlockingQueue<String> quantityC = new ArrayBlockingQueue<>(100);

    //Задаём количество генерируемых текстов
    public static final int TEXT_COUNT = 10_000;

    public static void main(String[] args) throws InterruptedException {
        //Задаём длину текста
        int textLength = 100_000;
        //Создаём список потоков
        List<Thread> threads = new ArrayList<>();

        //Инициализируем генерирующий поток
        Thread textGen =
                new Thread(() -> {
                    //Запускаем цикл для генерации нужного количества строк:
                    for (int i = 0; i < TEXT_COUNT; i++) {
                        //Генерируем строку
                        String toPut = generateText("abc", textLength);
                        try {
                            //Добавляем сгенерированную строку во все очереди
                            quantityA.put(toPut);
                            quantityB.put(toPut);
                            quantityC.put(toPut);
                        } catch (InterruptedException e) {
                            return;
                        }
                    }
                });

        //добавляем поток в список
        threads.add(textGen);

        //Инициализируем поток для подсчёта символов "а"
        Thread countA =
                new Thread(() -> threadLogic("a", quantityA));
        //добавляем созданный поток в список
        threads.add(countA);

        //Инициализируем поток для анализа на символы "b"
        Thread countB =
                new Thread(() -> threadLogic("b", quantityB));
        //добавляем созданный поток в список
        threads.add(countB);

        //Инициализируем поток для анализа на символы "c"
        Thread countC = new Thread(() -> threadLogic("c", quantityC));
        //добавляем созданный поток в список
        threads.add(countC);

        //запускаем созданные потоки
        for (Thread thread : threads) {
            thread.start();
        }

        //ожидаем завершения всех запущенных потоков потоком main:
        for (Thread thread : threads) {
            thread.join();
        }


    }

    //логика создания потока принимает символ и очередь:
    public static void threadLogic(String symbol, BlockingQueue<String> queue) {
        //Создаём строку для хранения строки с максимальным количеством заданных символов
        String maxStr = "";
        //Переменная для хранения максимального количества заданного символа в строке
        int maxLen = 0;
        //Запускаем цикл по всем строкам:
        for (int i = 0; i < TEXT_COUNT; i++) {
            try {
                //Пытаемся получить строку из очереди:
                String tempStr = queue.take();
                //Считаем количество появлений заданного символа
                int tempLen = countLetters(tempStr, symbol);
                //Если текущее количество больше рассчитанных на предществующих итерациях
                //меняем строку и количество повторений
                if (tempLen > maxLen) {
                    maxLen = tempLen;
                    maxStr = tempStr;
                }
            } catch (InterruptedException e) {
                return;
            }
        }
        //Выводим полученные значения на экран
        System.out.printf("Пример строки с самым большим количеством символа %s: %s %n" +
                "Общее количество символов: %d%n", symbol, maxStr.substring(0, 100), maxLen);
    }

    //Функция подсчёта вхождений символа в строку, рпинимающая на вход строку и символ
    public static int countLetters(String analyze, String symbol) {
        return analyze.length() - analyze.replace(symbol, "").length();
    }

    //Функция генерации текста нужной длины из заданных символов
    public static String generateText(String letters, int length) {
        //Создаём рандомайзер
        Random random = new Random();
        //Создаём переменную для добавления очередного псевдослучайного символа
        StringBuilder text = new StringBuilder();
        //Запускаем цикл для генерации псевдослучайной последовательности символов
        for (int i = 0; i < length; i++) {
            //Добавляем сгенерированный символ в переменную
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        //Возвращаем псевдослучайную последовательность заданных символов
        return text.toString();
    }
}