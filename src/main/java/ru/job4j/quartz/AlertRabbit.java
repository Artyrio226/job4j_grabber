package ru.job4j.quartz;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.Properties;

import static org.quartz.JobBuilder.*;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.*;

public class AlertRabbit {
    Properties prop;

    public AlertRabbit() {
        prop = getSettings();
    }

    private Properties getSettings() {
        Properties properties = new Properties();
        try (InputStream in = AlertRabbit.class.getClassLoader().getResourceAsStream("rabbit.properties")) {
            properties.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties;
    }

    public static void main(String[] args) throws ClassNotFoundException {
         AlertRabbit ar = new AlertRabbit();
        Class.forName(ar.prop.getProperty("driver-class-name"));
        try (Connection cnt = DriverManager.getConnection(
                ar.prop.getProperty("url"),
                ar.prop.getProperty("username"),
                ar.prop.getProperty("password")
        ); Statement statement = cnt.createStatement()) {
                statement.execute("DROP TABLE IF EXISTS rabbit;"
                        + "CREATE TABLE IF NOT EXISTS rabbit(created_date timestamp);");

            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.start();
            JobDataMap data = new JobDataMap();
            data.put("connect", cnt);
            JobDetail job = newJob(Rabbit.class)
                    .usingJobData(data)
                    .build();
            SimpleScheduleBuilder times = simpleSchedule()
                    .withIntervalInSeconds(5)
                    .repeatForever();
            Trigger trigger = newTrigger()
                    .startNow()
                    .withSchedule(times)
                    .build();
            scheduler.scheduleJob(job, trigger);
            Thread.sleep(10000);
            scheduler.shutdown();
        } catch (Exception se) {
            se.printStackTrace();
        }
    }

    public static class Rabbit implements Job {
        @Override
        public void execute(JobExecutionContext context) {
            Connection cnt = (Connection) context.getJobDetail().getJobDataMap().get("connect");
            try (PreparedStatement ps = cnt.prepareStatement("INSERT INTO rabbit(created_date) VALUES (?)")) {
                ps.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
                ps.execute();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            System.out.println("Rabbit runs here ...");
        }
    }
}