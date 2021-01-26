package ru.revuelArvida.telegrambot.repositories;

import org.hibernate.SessionFactory;
import ru.revuelArvida.telegrambot.entities.UserEntity;

public class HibernateUserEntityRepository extends AbstractRepository implements UserEntityRepository {

    public HibernateUserEntityRepository(SessionFactory factory){
        super(factory);
    }

    @Override
    public UserEntity createUserEntity(Long userId, Long chatId, String firstName, String lastName, String userName) {
       return runWithTransaction(session -> {
            UserEntity user = new UserEntity(userId, chatId, firstName, lastName, userName);
            session.persist(user);
            return user;
        });
    }

    @Override
    public UserEntity findByChatId(Long chatId) {
        return run(session -> session.createQuery("from UserEntity where chatid = :chatId", UserEntity.class)
        .setParameter("chatid", chatId)
        .getSingleResult());
    }

}
