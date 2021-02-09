package ru.revuelArvida.telegrambot.repositories;

import org.hibernate.SessionFactory;
import ru.revuelArvida.telegrambot.entities.AnekdotEntity;

import javax.persistence.NoResultException;
import java.util.ArrayList;
import java.util.List;

public class HibernateAnekdotEntityRepository
        extends AbstractRepository
        implements AnekdotEntityRepository{

    public HibernateAnekdotEntityRepository(SessionFactory factory){
        super(factory);
    }


    @Override
    public AnekdotEntity createAnekdotEntity(String anek) {
        return runWithTransaction(s -> {
            AnekdotEntity anekdot = new AnekdotEntity(anek);
            s.persist(anekdot);
            return anekdot;
        });
    }

    @Override
    public AnekdotEntity findById(Integer id) {
        try{
            return run(session -> session.createQuery("from AnekdotEntity where id = :id", AnekdotEntity.class)
                    .setParameter("id", id)
                    .getSingleResult());
        } catch (NoResultException exc){
            throw new NoResultException(exc.getMessage());
        }
    }

    @Override
    public List<AnekdotEntity> findByKeyWords(List<String> words) {
        List<AnekdotEntity> anekdotEntityList = new ArrayList<>();

        try{
            for (String word: words) {

                if (word.equals("штирлиц")) { } else {

                    List<AnekdotEntity> aneks = run(session -> session.createQuery(
                            "from AnekdotEntity where lower(anek) like lower(:word)", AnekdotEntity.class)
                            .setParameter("word", "%" + word + "%")
                            .getResultList());

                    for (AnekdotEntity anek: aneks) {
                        if (!anekdotEntityList.contains(anek)) anekdotEntityList.add(anek);
                    }

                }
            }
        } catch (NoResultException exc) {
            throw new NoResultException(exc.getMessage());
        }

        return anekdotEntityList;
    }

    @Override
    public Integer count() {
        return run(session -> session.createQuery("select count(*) from AnekdotEntity", Long.class)
        .getSingleResult().intValue());
    }
}
