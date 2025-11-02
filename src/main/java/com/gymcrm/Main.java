package com.gymcrm;

import com.gymcrm.config.AppConfig;
import com.gymcrm.facade.GymFacade;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Main {

    public static void main(String[] args) {
        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        System.out.println("Spring Context Loaded Successfully!");

        GymFacade facade = context.getBean(GymFacade.class);

        System.out.println("All trainees: " + facade.getAllTrainees());
    }
}