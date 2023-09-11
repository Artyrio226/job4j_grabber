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

    private String retrieveDescription(String link) {
        final String[] result = new String[1];
        Connection connection = Jsoup.connect(link);
        Document document = null;
        try {
            document = connection.get();
        } catch (IOException e) {
            System.out.printf("Invalid link %s%n", link);
        }
        assert document != null;
        Elements rows = document.select(".basic-section--appearance-vacancy-description");
        rows.forEach(row -> {
            Element titleElement = row.select(".basic-section--appearance-vacancy-description > h2:nth-child(1)").first();
            Element discrElement = row.select(".vacancy-description__text").first();
            String titleName = titleElement.text();
            String discrName = discrElement.text();
            result[0] = String.format("%s%n%s%n", titleName, discrName);
        });
        return result[0];
    }

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
                String rd = new HabrCareerParse().retrieveDescription(link);
                System.out.printf("%s%n%s%n%s%n%n", date, vacancyName, rd);
            });
        }
    }
}
