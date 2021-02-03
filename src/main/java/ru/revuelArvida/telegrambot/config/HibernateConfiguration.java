package ru.revuelArvida.telegrambot.config;

import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.service.ServiceRegistry;
import ru.revuelArvida.telegrambot.entities.AnekdotEntity;
import ru.revuelArvida.telegrambot.entities.UserEntity;

import java.util.Map;
import java.util.Properties;

public class HibernateConfiguration {

    private static final Map<String, String> getenv = System.getenv();

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
                settings.put(Environment.URL, getenv.get("DB_URL"));
                settings.put(Environment.USER, getenv.get("DB_USER"));
                settings.put(Environment.PASS, getenv.get("DB_PASSWORD"));
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

