package com.gymcrm;

import com.gymcrm.config.AppConfig;
import com.gymcrm.facade.GymFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Main {

    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        // Load Spring context
        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        log.info("Spring Context Loaded Successfully!");

        // Get the GymFacade bean
        GymFacade facade = context.getBean(GymFacade.class);

        // Example usage
        log.debug("All trainees: {}", facade.getAllTrainees());
        log.debug("All trainers: {}", facade.getAllTrainers());
        log.debug("All trainings: {}", facade.getAllTrainings());
    }
}
