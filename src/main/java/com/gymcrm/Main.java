package com.gymcrm;

import com.gymcrm.config.AppConfig;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Main {

    public static void main(String[] args) {
        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        System.out.println("Spring Context Loaded Successfully!");

        var traineeService = context.getBean("traineeServiceImpl", com.gymcrm.service.impl.TraineeServiceImpl.class);
        System.out.println("Trainee Service Bean Loaded: " + traineeService);

    }
}