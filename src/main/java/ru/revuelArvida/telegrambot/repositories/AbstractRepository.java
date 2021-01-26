package ru.revuelArvida.telegrambot.repositories;

import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.function.Function;

@RequiredArgsConstructor
public class AbstractRepository {

    protected final SessionFactory factory;

    protected <T> T run(Function<Session, T> function) {
        try(Session s = factory.openSession()) {
            return function.apply(s);
        }
    }

    protected <T> T runWithTransaction(Function<Session, T> function) {
        Transaction tx = null;

        try (Session s = factory.openSession()){
            tx = s.beginTransaction();

            T result = function.apply(s);

            tx.commit();

            return result;
        } catch (Exception exc) {
            if (tx != null) {
                tx.rollback();
            }
            throw new RuntimeException(exc);
        }
    }
}
