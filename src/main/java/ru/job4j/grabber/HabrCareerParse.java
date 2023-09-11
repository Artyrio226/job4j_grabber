package ru.job4j.grabber;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class HabrCareerParse {

    private static final String SOURCE_LINK = "https://career.habr.com";

    private static final String PAGE_LINK = String.format("%s/vacancies/java_developer", SOURCE_LINK);

    public static void main(String[] args) throws IOException {
        for (int i = 1; i < 6; i++) {
            Connection connection = Jsoup.connect(String.format("%s?page=%s", PAGE_LINK, i));
            Document document = connection.get();
            Elements rows = document.select(".vacancy-card__inner");
            rows.forEach(row -> {
                Element titleElement = row.select(".vacancy-card__title").first();
                Element dateElement = row.select(".vacancy-card__date").first().child(0);
                Element linkElement = titleElement.child(0);
                String vacancyName = titleElement.text();
                String link = String.format("%s%s", SOURCE_LINK, linkElement.attr("href"));
                LocalDate date = LocalDate.parse(dateElement.attr("datetime"), DateTimeFormatter.ISO_DATE_TIME);
                System.out.printf("%s %s %s%n", vacancyName, link, date);
            });
        }
    }
}
