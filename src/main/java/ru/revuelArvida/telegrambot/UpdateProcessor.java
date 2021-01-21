package ru.revuelArvida.telegrambot;



import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.concurrent.locks.ReentrantLock;

@RequiredArgsConstructor
public class UpdateProcessor {

    private final Bot bot;


    void  processMessage(Message message){
        String text = message.getText();
        switch (text) {
            case "/start":
                bot.sendMsg(message, "Привет, " + message.getChat().getFirstName() + ", я бот анекдотчик! Знаю все анекдоты про Штирлица! Хочешь расскажу?");
                break;

            case "Вкинь анек":
                String anek = "Штирлиц и Мюллер ездили по очереди на танке. Очередь редела, но не расходилсь...";
                bot.sendMsgWithAudio(message,anek);
            break;

            case "Найди мне анек":
                bot.sendMsg(message, "А я пока их не знаю");
                break;

            default:
                bot.sendMsg(message, "Я такого не умею");
        }
    }

    void processQuery(CallbackQuery query){
        String text = query.getMessage().getText();
    }

}
