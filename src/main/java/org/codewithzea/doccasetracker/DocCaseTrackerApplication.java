package org.codewithzea.doccasetracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
@EnableCaching
public class DocCaseTrackerApplication {

    public static void main(String[] args) {
        SpringApplication.run(DocCaseTrackerApplication.class, args);
    }

}
