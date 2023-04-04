package org.bahmni.client.router;


import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bahmni.client.router.atomfeed.jobs.FeedProcessor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;


@Slf4j
@SpringBootApplication
@AllArgsConstructor
@EnableTransactionManagement
@EnableScheduling
public class RouterApplication {
    private final FeedProcessor feedProcessor;

    public static void main(String[] args) {
        SpringApplication.run(RouterApplication.class, args);
    }


//    @Override
//    public void run(String... args) {
//        log.info("Hello World");
//        feedProcessor.processPatientFeed();
//    }
}
