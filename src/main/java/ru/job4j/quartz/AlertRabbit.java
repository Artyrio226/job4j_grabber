package ru.job4j.quartz;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.Properties;

import static org.quartz.JobBuilder.*;
import static org.quartz.TriggerBuilder.*;

public class AlertRabbit {

    public Properties getSettings() {
        Properties properties = new Properties();
        try (InputStream in = AlertRabbit.class.getClassLoader().getResourceAsStream("rabbit.properties")) {
            properties.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties;
    }

    public static void main(String[] args) throws ClassNotFoundException {
        Properties prop = new AlertRabbit().getSettings();
        Class.forName(prop.getProperty("driver-class-name"));
        try (Connection cnt = DriverManager.getConnection(
                prop.getProperty("url"),
                prop.getProperty("username"),
                prop.getProperty("password")
        ); Statement statement = cnt.createStatement()) {
                statement.execute("CREATE TABLE IF NOT EXISTS rabbit(created_date timestamp);");

            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.start();
            JobDataMap data = new JobDataMap();
            data.put("connect", cnt);
            JobDetail job = newJob(Rabbit.class)
                    .usingJobData(data)
                    .build();
            Trigger trigger = newTrigger()
                    .startNow()
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