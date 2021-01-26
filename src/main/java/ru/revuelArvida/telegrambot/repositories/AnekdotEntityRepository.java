package ru.revuelArvida.telegrambot.repositories;

import ru.revuelArvida.telegrambot.entities.AnekdotEntity;

public interface AnekdotEntityRepository {

    AnekdotEntity createAnekdotEntity (String anek);

    AnekdotEntity findById(Integer id);

    Integer count();

}
