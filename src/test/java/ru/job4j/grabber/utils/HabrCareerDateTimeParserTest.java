package ru.job4j.grabber.utils;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.*;

class HabrCareerDateTimeParserTest {

    @Test
    public void parseDateTime() {
        DateTimeParser dtp = new HabrCareerDateTimeParser();
        String time = "2023-09-10T14:27:21+03:00";
        LocalDateTime res = dtp.parse(time);
        String expected = "2023-09-10T14:27:21";
        assertThat(res).isEqualTo(expected);
    }
}