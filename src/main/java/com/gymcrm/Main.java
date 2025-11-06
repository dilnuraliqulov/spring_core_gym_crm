package com.gymcrm;

import com.gymcrm.config.AppConfig;
import com.gymcrm.service.impl.TraineeServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Main {

    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        log.info("Spring Context Loaded Successfully!");

        TraineeServiceImpl traineeService = context.getBean("traineeServiceImpl", TraineeServiceImpl.class);
        log.info("Trainee Service Bean Loaded: {}", traineeService);
    }
}
