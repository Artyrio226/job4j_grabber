package ru.job4j.grabber;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.job4j.grabber.utils.DateTimeParser;
import ru.job4j.grabber.utils.HabrCareerDateTimeParser;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class HabrCareerParse implements Parse {
    private int count = 0;
    private static final String SOURCE_LINK = "https://career.habr.com";
    private static final String PAGE_LINK = String.format("%s/vacancies/java_developer", SOURCE_LINK);
    private static final int PAGE_COUNT = 1;
    private final DateTimeParser dateTimeParser;

    public HabrCareerParse(DateTimeParser dateTimeParser) {
        this.dateTimeParser = dateTimeParser;
    }

    private Post equipPost(String title, String link, Element dateElement) {
        String description = retrieveDescription(link);
        LocalDateTime created = dateTimeParser.parse(dateElement.attr("datetime"));
        return new Post(count++, title, link, description, created);
    }

    private String retrieveDescription(String link) {
        final String[] result = new String[1];
        Document document = getDocument(link);
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

    private static Document getDocument(String link) {
        Connection connection = Jsoup.connect(link);
        Document document = null;
        try {
            document = connection.get();
        } catch (IOException e) {
            System.out.printf("Invalid link %s%n", link);
        }
        assert document != null;
        return document;
    }

    @Override
    public List<Post> list(String link) {
        List<Post> result = new ArrayList<>();
        for (int i = 1; i <= PAGE_COUNT; i++) {
            Document document = getDocument(String.format("%s?page=%s", link, i));
            Elements rows = document.select(".vacancy-card__inner");
            rows.forEach(row -> {
                Element titleElement = row.select(".vacancy-card__title").first();
                Element dateElement = row.select(".vacancy-card__date").first().child(0);
                Element linkElement = titleElement.child(0);
                String vacancyName = titleElement.text();
                String href = String.format("%s%s", SOURCE_LINK, linkElement.attr("href"));
                result.add(equipPost(vacancyName, href, dateElement));

            });
        }
        return result;
    }
}
