package ru.revuelArvida.telegrambot.repositories;

import org.hibernate.SessionFactory;
import ru.revuelArvida.telegrambot.config.HibernateConfiguration;
import ru.revuelArvida.telegrambot.entities.AnekdotEntity;

public class TestApp {

    public static void main(String[] args) {
        SessionFactory factory = HibernateConfiguration.getFactory();

        HibernateAnekdotEntityRepository anekdotEntityRepository = new HibernateAnekdotEntityRepository(factory);

        AnekdotEntity anekdotEntity = anekdotEntityRepository.findById(9);

        String count = anekdotEntity.getAnek();

        System.out.println(count);
        System.out.println( anekdotEntityRepository.count());
    }

}
