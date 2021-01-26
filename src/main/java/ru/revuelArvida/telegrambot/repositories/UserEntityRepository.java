package ru.revuelArvida.telegrambot.repositories;

import ru.revuelArvida.telegrambot.entities.UserEntity;

public interface UserEntityRepository {

    UserEntity createUserEntity(Long userId, Long chatId, String firstName, String lastName, String usetName);
    UserEntity findByChatId(Long chatId);
}
