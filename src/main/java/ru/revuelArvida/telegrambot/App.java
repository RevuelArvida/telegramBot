package ru.revuelArvida.telegrambot;

import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;;

public class App {

    public static void main(String[] args) {
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(new Bot("StirlitzBot","1503255891:AAGuqSScDzlpdWHOkUASwjUfUVBFFMs5x_o"));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

    }

}
