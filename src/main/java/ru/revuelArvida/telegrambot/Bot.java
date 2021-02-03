package ru.revuelArvida.telegrambot;



import com.voicerss.tts.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendAudio;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import org.telegram.telegrambots.meta.api.objects.*;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.validation.constraints.NotNull;
import java.io.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class Bot extends TelegramLongPollingBot {

    private final String botName;
    private final String token;

    @Setter
    @Getter
    private States state = States.SLEEP;

    private UpdateProcessor processor = new UpdateProcessor(this);

    @Getter
    private int amount;

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

        if (update.hasMessage() && update.getMessage().hasText()) {

            processor.processMessage(update.getMessage());

        } else if(update.hasCallbackQuery()) {

            processor.processQuery(update.getCallbackQuery());

        };
    }

    void sendAud(CallbackQuery query, String text){
//        SendVoice v = new SendVoice();
//        v.setChatId(query.getMessage().getChatId().toString());
//
//        try {
//            v.setVoice(getAudio(text));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        try{
//            execute(v);
//        } catch (TelegramApiException exc){
//            exc.printStackTrace();
//        }
        SendAudio a = new SendAudio();
        a.setChatId(query.getMessage().getChatId().toString());

        try {
            a.setAudio(getAudio(text));
        } catch (Exception exc){
            exc.printStackTrace();
        }

        try {
            execute(a);
        } catch (TelegramApiException exc){
            exc.printStackTrace();
        }

    }

    void sendMsg(Message msg, String text){
        SendMessage s = new SendMessage();
        s.setChatId(msg.getChatId().toString());
        s.setText(text);

        if(state == States.SLEEP){
            setButtons(s);
        } else setExit(s);

        try {
            execute(s);
        } catch (TelegramApiException exc) {
            exc.printStackTrace();
        }
    }

    void sendMsg(Message msg, String text, int anekId){
        SendMessage s = new SendMessage();
        s.setChatId(msg.getChatId().toString());
        s.setText(text);

        setInline(s,anekId);
        setButtons(s);


        try {
            execute(s);
        } catch (TelegramApiException exc) {
            exc.printStackTrace();
        }
    }

    void sendAdmin(Message msg, String text){
        SendMessage s = new SendMessage();
        s.setChatId(msg.getChatId().toString());
        s.setText("Предложка: " + text);

        setInline(s);
        try {
            execute(s);
        } catch (TelegramApiException exc) {
            exc.printStackTrace();
        }
    }


    void setButtons(SendMessage sendMessage) {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setSelective(true);
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setOneTimeKeyboard(false);
        List<KeyboardRow> keyboard = new ArrayList<>();

        KeyboardRow firstRow = new KeyboardRow();
        KeyboardRow secondRow = new KeyboardRow();
        KeyboardRow thirdRow = new KeyboardRow();
        firstRow.add(new KeyboardButton("Вкинь анек"));
        secondRow.add(new KeyboardButton("Найти анек по Id"));
        secondRow.add(new KeyboardButton("Найти анек по словам"));
        thirdRow.add(new KeyboardButton("Предложить анекдот"));
        thirdRow.add(new KeyboardButton("Помощь"));
        keyboard.add(firstRow);
        keyboard.add(secondRow);
        keyboard.add(thirdRow);

        keyboardMarkup.setKeyboard(keyboard);
        sendMessage.setReplyMarkup(keyboardMarkup);
    }

    void setExit(SendMessage sendMessage){
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setSelective(true);
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setOneTimeKeyboard(false);
        List<KeyboardRow> keyboard = new ArrayList<>();

        KeyboardRow firstRow = new KeyboardRow();
        firstRow.add(new KeyboardButton("Выход"));
        keyboard.add(firstRow);

        keyboardMarkup.setKeyboard(keyboard);
        sendMessage.setReplyMarkup(keyboardMarkup);
    }


    void setInline(SendMessage sendMessage, int anekId) {
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
        List<InlineKeyboardButton> buttons1 = new ArrayList<>();
        InlineKeyboardButton button = new InlineKeyboardButton();
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        button.setText("Аудиоверсия");
        button.setCallbackData(Integer.toString(anekId));
        buttons1.add(button);
        buttons.add(buttons1);

        inlineKeyboardMarkup.setKeyboard(buttons);
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
    }

    void setInline(SendMessage sendMessage){
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        List<InlineKeyboardButton> buttons = new ArrayList<>();
        InlineKeyboardButton buttonApprove = new InlineKeyboardButton();
        InlineKeyboardButton buttonDecline = new InlineKeyboardButton();
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        buttonApprove.setText("Approve");
        buttonApprove.setCallbackData("Approve");
        buttonDecline.setText("Decline");
        buttonDecline.setCallbackData("Decline");


        buttons.add(buttonApprove);
        buttons.add(buttonDecline);
        keyboard.add(buttons);


        inlineKeyboardMarkup.
                setKeyboard(keyboard);

        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
    }

    private InputFile getAudio(String text) throws Exception {
        VoiceProvider tts = new VoiceProvider("8bac82206e9544328b43a9328adad894");
        VoiceParameters params = new VoiceParameters(text, Languages.Russian);
        FileOutputStream fileOutputStream = new FileOutputStream("src/main/resources/Anek.mp3");
        File createdAudioFile = new File("src/main/resources/Anek.mp3");
        InputFile inputFile = new InputFile();

        params.setCodec(AudioCodec.MP3);
        params.setVoice("Marina");
        params.setFormat(AudioFormat.Format_44KHZ.AF_44khz_16bit_mono);
        params.setBase64(false);
        params.setRate(0);

        byte[] voice = tts.speech(params);


        fileOutputStream.write(voice, 0, voice.length);
        fileOutputStream.flush();
        fileOutputStream.close();



        inputFile.setMedia(createdAudioFile);
        return inputFile;
    }

}

