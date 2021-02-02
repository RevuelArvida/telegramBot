package ru.revuelArvida.telegrambot.config;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateConfiguration {

    private static SessionFactory factory = initializeSessionFactory();

    private static SessionFactory initializeSessionFactory(){
        Configuration configuration = new Configuration()
                .configure("src/main/resources/Hibernate.cfg.xml");

        return  configuration.buildSessionFactory();
    }

    private HibernateConfiguration(){};

    public static SessionFactory getFactory() {return factory;}
}
