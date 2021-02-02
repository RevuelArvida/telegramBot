package ru.revuelArvida.telegrambot.config;

import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.service.ServiceRegistry;
import ru.revuelArvida.telegrambot.entities.AnekdotEntity;
import ru.revuelArvida.telegrambot.entities.UserEntity;

import java.util.Properties;

public class HibernateConfiguration {

    private static SessionFactory factory;

//    private static SessionFactory initializeSessionFactory(){
//        Configuration configuration = new Configuration()
//                .configure();
//
//        return  configuration.buildSessionFactory();
//    }

    private HibernateConfiguration(){};

    public static SessionFactory getFactory() {
        if (factory == null){
            try {
                Configuration configuration = new Configuration();

                Properties settings = new Properties();
                settings.put(Environment.DRIVER, "org.postgresql.Driver");
                settings.put(Environment.URL, "jdbc:postgresql://ec2-34-251-118-151.eu-west-1.compute.amazonaws.com:5432/d4hlt96n232fmp");
                settings.put(Environment.USER, "gtfbqynfjmrddk");
                settings.put(Environment.PASS, "51f249586b3bc15763c950d3c992f1de30d3c1c8c40569615fe1de685954387b");
                settings.put(Environment.DIALECT, "org.hibernate.dialect.PostgreSQL10Dialect");

                settings.put(Environment.SHOW_SQL, true);
                settings.put(Environment.FORMAT_SQL, true);

                settings.put(Environment.HBM2DDL_AUTO, "validate");

                configuration.setProperties(settings);

                configuration.addAnnotatedClass(UserEntity.class);
                configuration.addAnnotatedClass(AnekdotEntity.class);


                ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                        .applySettings(configuration.getProperties()).build();

                factory = configuration.buildSessionFactory();
            } catch (Exception exc) {
                exc.printStackTrace();
            }
        }
        return factory;
    }
        }

