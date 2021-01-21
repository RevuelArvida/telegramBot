package ru.revuelArvida.telegrambot;


import com.voicerss.tts.*;
import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendAudio;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import org.telegram.telegrambots.meta.api.methods.send.SendVoice;
import org.telegram.telegrambots.meta.api.objects.*;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;


import java.io.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class Bot extends TelegramLongPollingBot {

    private final String botName;
    private final String token;
    private UpdateProcessor processor = new UpdateProcessor(this);

    private Bot(){
        botName = "botName";
        token = "token";
    }


    @Override
    public String getBotUsername() {
        return botName;
    }

    @Override
    public String getBotToken() {
        return token;
    }

    @Override
    public void onUpdateReceived(Update update) {
//        Message msg = update.getMessage();
//        String txt = msg.getText();
//        if (txt.equals("/start")) {
//            sendMsg(msg, "Привет, " + msg.getChat().getFirstName() + ", я бот анекдотчик! Знаю все анекдоты про Штирлица! Хочешь расскажу?");
//        } else {
//            sendMsg(msg, "Я еще ничего не умею!");
//        }
        if (update.hasMessage() && update.getMessage().hasText()) {
            processor.processMessage(update.getMessage());
        } else if(update.hasCallbackQuery()) {
            processor.processQuery(update.getCallbackQuery());
        }
    }
    void sendMsgWithAudio(Message msg, String text){
        SendMessage s = new SendMessage();
        s.setChatId(msg.getChatId().toString());
        s.setText(text);

        SendVoice v = new SendVoice();
        SendAudio a = new SendAudio();
        v.setChatId(msg.getChatId().toString());
        try {
            v.setVoice(getAudio(text));
        } catch (Exception e) {
            e.printStackTrace();
        }

        setButtons(s);

        try {
            execute(s);
            execute(v);
        } catch (TelegramApiException exc) {
            exc.printStackTrace();
        }
    }

    void sendMsg(Message msg, String text){
        SendMessage s = new SendMessage();
        s.setChatId(msg.getChatId().toString());
        s.setText(text);

        setButtons(s);

        try {
            execute(s);
        } catch (TelegramApiException exc) {
            exc.printStackTrace();
        }
    }

    void setButtons(SendMessage sendMessage) {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        sendMessage.setReplyMarkup(keyboardMarkup);
        keyboardMarkup.setSelective(true);
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setOneTimeKeyboard(false);
        List<KeyboardRow> keyboard = new ArrayList<>();

        KeyboardRow firstRow = new KeyboardRow();
        KeyboardRow secondRow = new KeyboardRow();
        firstRow.add(new KeyboardButton("Вкинь анек"));
        secondRow.add(new KeyboardButton("Найди мне анек"));
        keyboard.add(firstRow);
        keyboard.add(secondRow);

        keyboardMarkup.setKeyboard(keyboard);
    }

    private InputFile getAudio(String text) throws Exception {
        VoiceProvider tts = new VoiceProvider("8bac82206e9544328b43a9328adad894");
        VoiceParameters params = new VoiceParameters(text, Languages.Russian);
        params.setCodec(AudioCodec.MP3);
        params.setVoice("Marina");
        params.setFormat(AudioFormat.Format_44KHZ.AF_44khz_16bit_mono);
        params.setBase64(false);
        params.setRate(0);

        byte[] voice = tts.speech(params);


        FileOutputStream fileOutputStream = new FileOutputStream("src/main/resources/Anek.mp3");
        fileOutputStream.write(voice, 0, voice.length);
        fileOutputStream.flush();
        fileOutputStream.close();
        File fileIO = new File("src/main/resources/Anek.mp3");

        InputFile file = new InputFile();
        file.setMedia(fileIO);
        return file;
    }

}

