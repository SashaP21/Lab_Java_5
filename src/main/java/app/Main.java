package app;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) {
        List<Visitor> visitors;
        try (BufferedReader reader = Files.newBufferedReader(
                Path.of("books.json"), StandardCharsets.UTF_8)) {
            Gson gson = new Gson();
            Type visitorsType = new TypeToken<List<Visitor>>() {}.getType();
            visitors = gson.fromJson(reader, visitorsType);
        } catch (IOException e) {
            System.err.println("Ошибка при чтении файла books.json: " + e.getMessage());
            return;
        }

        System.out.println("Задание 1. Посетители библиотеки");
        visitors.forEach(v -> System.out.println(v.getName() + " " + v.getSurname()));
        System.out.println("Количество посетителей: " + visitors.size());
        System.out.println();

        System.out.println("Задание 2. Книги без повторений");
        List<Book> uniqueBooks = visitors.stream()
                .flatMap(v -> v.getFavoriteBooks().stream())
                .distinct()
                .collect(Collectors.toList());
        uniqueBooks.forEach(b -> System.out.println(b.getName()));
        System.out.println("Количество книг без повторений: " + uniqueBooks.size());
        System.out.println();

        System.out.println("Задание 3. Сортировка книг");
        uniqueBooks.stream()
                .sorted(Comparator.comparingInt(Book::getPublishingYear))
                .forEach(b -> System.out.println(
                        b.getPublishingYear() + " — " + b.getName() + " (" + b.getAuthor() + ")"));
        System.out.println();

        System.out.println("Задание 4. Поиск автора");
        boolean hasAusten = visitors.stream()
                .flatMap(v -> v.getFavoriteBooks().stream())
                .anyMatch(b -> "Jane Austen".equalsIgnoreCase(b.getAuthor()));
        System.out.println(hasAusten
                ? "Книга автора Jane Austen есть в списках избранного"
                : "Книг автора Jane Austen нет в списках избранного");
        System.out.println();

        System.out.println("Задание 5. Максимальное число книг");
        int maxBooks = visitors.stream()
                .mapToInt(v -> v.getFavoriteBooks().size())
                .max()
                .orElse(0);
        System.out.println("Максимальное число избранных книг: " + maxBooks);
        System.out.println();

        System.out.println("Задание 6. Сообщения подписчикам");
        double average = visitors.stream()
                .mapToInt(v -> v.getFavoriteBooks().size())
                .average()
                .orElse(0);
        System.out.println("Среднее число избранных книг по всем посетителям: " + average);

        List<SmsMessage> smsMessages = visitors.stream()
                .filter(Visitor::isSubscribed)
                .map(v -> {
                    int count = v.getFavoriteBooks().size();
                    String text;
                    if (count < average) {
                        text = "read more";
                    } else if (count > average) {
                        text = "you are a bookworm";
                    } else {
                        text = "fine";
                    }
                    return new SmsMessage(v.getPhone(), text);
                })
                .collect(Collectors.toList());

        smsMessages.forEach(m -> System.out.println(m.getPhoneNumber() + ": " + m.getMessage()));
        System.out.println("Всего сообщений: " + smsMessages.size());
    }
}
