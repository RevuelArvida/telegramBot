package ru.revuelArvida.telegrambot;



import lombok.RequiredArgsConstructor;
import org.hibernate.SessionFactory;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.revuelArvida.telegrambot.config.HibernateConfiguration;
import ru.revuelArvida.telegrambot.entities.AnekdotEntity;
import ru.revuelArvida.telegrambot.repositories.HibernateAnekdotEntityRepository;
import ru.revuelArvida.telegrambot.repositories.HibernateUserEntityRepository;

import javax.persistence.PersistenceException;
import java.util.*;

@RequiredArgsConstructor
public class UpdateProcessor {

    private static final Map<String, String> getenv = System.getenv();
    private final Bot bot;
    private SessionFactory factory = HibernateConfiguration.getFactory();
    HibernateAnekdotEntityRepository anekdotEntityRepository = new HibernateAnekdotEntityRepository(factory);
    HibernateUserEntityRepository userEntityRepository = new HibernateUserEntityRepository(factory);

    private Queue<String> proposal = new PriorityQueue<>();

    void  processMessage(Message message){

        String text = message.getText();
        if(bot.getState() == States.SLEEP) {
            switch (text) {

                case "/start":
                    bot.sendMsg(message, "Привет, " + message.getChat().getFirstName() + "," +
                            " я бот анекдотчик! Знаю все анекдоты про Штирлица! Хочешь расскажу?");
                        try {
                                userEntityRepository.createUserEntity(
                                        message.getChat().getId(),
                                        message.getChatId(),
                                        message.getChat().getFirstName(),
                                        message.getChat().getLastName(),
                                        message.getChat().getUserName());
                            } catch (PersistenceException exc){
                                exc.printStackTrace();
                            }
                    break;

                case "Помощь":
                case "/help":
                    bot.sendMsg(message, "Я умею предоставлять анекдоты по команде: " +
                            "\n - Вкинь анек \n - анек \n - анекдот \n - Анек \n - Анекдот " +
                            "\nА также искать анеки по командам: \n - Найди анек по id " +
                            "\n - Найди анек по словам " +
                            "\nВы также можете предложить анекдот по команде: \n - Предложить анекдот " +
                            "\nДля возврата в главное меню используйте команду: \n - Выход" );
                    break;

                case "Анекдот":
                case "анекдот":
                case "анек":
                case "Анек":
                case "Вкинь анек":
                    Random r = new Random();

                    int id = r.nextInt(anekdotEntityRepository.count());

                    AnekdotEntity anekdot = anekdotEntityRepository.findById(id);
                    String anek = anekdot.getAnek();

                    bot.sendMsg(message,anek,anekdot.getId());
                break;

                case "Найти анек по Id":
                    bot.setState(States.SEARCH_BY_ID);
                    bot.sendMsg(message, "Количество анеков в базе: " + anekdotEntityRepository.count() +
                            "\nВведите id анека:" +  "\nДля возврата в главное меню напишите: Выход ");
                    break;

                case "Найти анек по словам":
                    bot.setState(States.SEARCH_BY_KEYWORDS);
                    bot.sendMsg(message, "Введите ключевые слова через пробел: " +
                            "\nДля возврата в главное меню напишите: Выход ");
                    break;

                case "Предложить анекдот":
                    bot.setState(States.ADD_REQUEST);
                    bot.sendMsg(message, "Отправте свой анекдот про Штирлица:" +
                            "\nДля возврата в главное меню напишите: Выход ");
                    break;

                case "Выход":
                    bot.sendMsg(message, "Вы уже находитесь в главном меню");
                    break;

                case "3765":
                    if (proposal.peek()!= null) {
                        bot.sendMsg(message, "В предложке " + proposal.size() + " анеков");
                        bot.sendWithApproval(message, proposal.peek());
                    } else bot.sendMsg(message, "Предложка пуста");
                    break;

                default:
                    bot.sendMsg(message, "Я такого не умею");
            }

        } else {
            boolean exit = text.equals("Выход") || text.equals("выход") || text.equals("ВЫХОД");
            if (bot.getState() == States.SEARCH_BY_ID){

                    if (exit) {
                        bot.setState(States.SLEEP);

                    } else {
                        try {
                            int id = Integer.parseInt(text);
                            if (id > 0 ) {
                                AnekdotEntity anekdot = anekdotEntityRepository.findById(id);
                                String anek = anekdot.getAnek();

                                bot.setState(States.SLEEP);

                                bot.sendMsg(message,"Вот, что я нашел:"); //Set replyKeyboard
                                bot.sendMsg(message, anek, id);

                            } else throw new NumberFormatException();
                        } catch(NumberFormatException exc){
                            bot.sendMsg(message, "Id должен содержать только цифры и быть в пределах размеров " +
                                    "базы анекдотов. Попробуйте еще раз! " +
                                    "\nДля возврата в главное меню напишите: Выход");
                        }
                }


            } else if (bot.getState() == States.SEARCH_BY_KEYWORDS) {
                if (exit) {

                    bot.setState(States.SLEEP);

                    bot.sendMsg(message,"Возврат в главное меню");

                } else {
                    List<String> keyWords = new ArrayList<>();
                    String[] words = text.split(" ");

                    for (String word : words) {
                        if (word.length() >= 3) keyWords.add(word.toLowerCase(Locale.ROOT));
                    }

                    List<AnekdotEntity> anekdotEntityList = anekdotEntityRepository.findByKeyWords(keyWords);
                    sort(anekdotEntityList, keyWords);

                    if (!anekdotEntityList.isEmpty()) {
                        bot.setState(States.SLEEP);
                        bot.sendMsg(message,"Вот, что я нашел:"); //Set replyKeyboard

                        for (AnekdotEntity anek : anekdotEntityList) {
                            bot.sendMsg(message, anek.getAnek() + "\nId анекдота: " + anek.getId(), anek.getId());
                        }


                    } else
                        bot.sendMsg(message, "Ничего не найдено попробуйте еще раз. " +
                                "\nТакже, пожалуйста не используйте слово Штирлиц " +
                                "\nДля возврата в главное меню напишите: Выход");
                }

            } else if (bot.getState() == States.ADD_REQUEST) {
                bot.setState(States.SLEEP);
                if (!exit) {
                    proposal.add(text);
                    bot.sendMsg(message, "Анекдот отправлен");

                    Message msg = new Message();
                    Chat chat = new Chat();

                    chat.setId(Long.parseLong(getenv.get("ADMIN_CHAT")));
                    msg.setChat(chat);
                    bot.sendMsg(msg, "В предложку закинут анекдот!" +
                            "\nКоличество анеков в предложке: " + proposal.size());
                } else bot.sendMsg(message,"Возврат в главное меню");

            }
        }

    }

    void processQuery(CallbackQuery query){
        String text = query.getData();
        if (text.equals("Approve") || text.equals("Decline")){
            switch (text) {

                case "Approve":
                    anekdotEntityRepository.createAnekdotEntity(proposal.poll());
                    bot.sendMsg(query.getMessage(), "Анекдот принят" +
                            "\nКоличество анеков в предложке: " + proposal.size());
                    break;

                case "Decline":
                    proposal.remove();
                    bot.sendMsg(query.getMessage(), "Анекдот отклонен"+
                            "\nКоличество анеков в предложке: " + proposal.size());
                    break;
            }

        } else {
            int id = Integer.parseInt(text);
            String anek = anekdotEntityRepository.findById(id).getAnek();
            System.out.println(anek);
            bot.sendAud(query, anek);
        }
    }

    private void sort(List<AnekdotEntity> anekdotEntityList, List<String> words){
        Iterator<AnekdotEntity> i = anekdotEntityList.iterator();
        while (i.hasNext()){
            AnekdotEntity anek = i.next();
            int count = 0;

            for (String word: words) {
                if(anek.getAnek().contains(word)) count++;
            }


            if ( (words.size() - count) > 1){
                i.remove();
            }
        }
    }
}
