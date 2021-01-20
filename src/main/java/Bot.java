import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class Bot extends TelegramLongPollingBot {

    private final String botName;
    private final String token;

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
        Message msg = update.getMessage();
        String txt = msg.getText();
        if (txt.equals("/start")) {
            sendMsg(msg, "Привет, " + msg.getChat().getFirstName() + ", я бот анекдотчик! Знаю все анекдоты про Штирлица! Хочешь расскажу?");
        } else {
            sendMsg(msg, "Я еще ничего не умею!");
        }
    }

    private void sendMsg(Message msg, String text){
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

    private void setButtons(SendMessage sendMessage) {
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
}

