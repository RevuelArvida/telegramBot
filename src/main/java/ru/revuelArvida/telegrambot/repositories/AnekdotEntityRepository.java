package ru.revuelArvida.telegrambot.repositories;

import ru.revuelArvida.telegrambot.entities.AnekdotEntity;

import java.util.List;

public interface AnekdotEntityRepository {

    AnekdotEntity createAnekdotEntity (String anek);

    AnekdotEntity findById(Integer id);

    List<AnekdotEntity> findByKeyWords(List<String> words);

    Integer count();



}
