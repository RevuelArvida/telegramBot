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
import java.util.Collection;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Random;

@RequiredArgsConstructor
public class UpdateProcessor {

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
                    bot.sendMsg(message, "Привет, " + message.getChat().getFirstName() + ", я бот анекдотчик! Знаю все анекдоты про Штирлица! Хочешь расскажу?");
                        try {
                                userEntityRepository.createUserEntity(message.getChat().getId(),message.getChatId(),message.getChat().getFirstName(), message.getChat().getLastName(), message.getChat().getUserName());
                            } catch (PersistenceException exc){
                                exc.printStackTrace();
                            }

                    break;

                case "Помощь":
                case "/help":
                    bot.sendMsg(message, "Я умею предоставлять анекдоты по команде: \n - Вкинь анек \n - анек \n - анекдот \n - Анек \n - Анекдот \nА также искать анеки по команде: \n - Найди мне анек \nВы также можете предложить анекдот по команде: \n - Предложить анекдот \nДля возврата в главное меню используйте команду: \n - Выход" );
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

                case "Найди мне анек":
                    bot.sendMsg(message, "Количество анеков в базе: " + anekdotEntityRepository.count() + "\n Введите id анека:" +  "\n Для возврата в главное меню напишите: Выход ");
                    bot.setState(States.FINDING);
                    break;

                case "Предложить анекдот":
                    bot.sendMsg(message, "Отправте свой анекдот про Штирлица:" + "\n Для возврата в главное меню напишите: Выход ");
                    bot.setState(States.DBPENDING);
                    break;

                case "3765":

                    if (proposal.peek()!= null) {
                        bot.sendMsg(message, "В предложке " + proposal.size() + " анеков");
                        bot.sendAdmin(message, proposal.peek());
                    } else bot.sendMsg(message, "Предложка пуста");
                    break;

                default:
                    bot.sendMsg(message, "Я такого не умею");
            }

        } else if (bot.getState() == States.FINDING){

                if (text.equals("Выход") || text.equals("выход")|| text.equals("ВЫХОД")) {
                    bot.setState(States.SLEEP);
                    bot.sendMsg(message,"Возврат в главное меню");
                } else {
                    try {
                        int id = Integer.parseInt(text);
                        if (id > 0) {
                            AnekdotEntity anekdot = anekdotEntityRepository.findById(id);
                            String anek = anekdot.getAnek();

                            bot.sendMsg(message, anek, id);
                            bot.setState(States.SLEEP);
                        } else throw new NumberFormatException();
                    } catch(NumberFormatException exc){
                        bot.sendMsg(message, "Id должен содержать только цифры и быть в пределах размеров базы анекдотов. Попробуйте еще раз! \n Для возврата в главное меню напишите: Выход");
                    }
            }

        } else if (bot.getState() == States.DBPENDING) {
            if (!text.equals("Выход")) {
                proposal.add(text);
                bot.sendMsg(message, "Анекдот отправлен");
                Message msg = new Message();
                Chat chat = new Chat();
                chat.setId(297075285L);
                msg.setChat(chat);
                bot.sendMsg(msg, "В предложку закинут анекдот!" + "\n Количество анеков в предложке: " + proposal.size());
            } else bot.sendMsg(message,"Возврат в главное меню");
            bot.setState(States.SLEEP);

        }

    }

    void processQuery(CallbackQuery query){
        String text = query.getData();
        if (text.equals("Approve") || text.equals("Decline")){
            switch (text) {
                case "Approve":
                    anekdotEntityRepository.createAnekdotEntity(proposal.poll());
                    bot.sendMsg(query.getMessage(), "Анекдот принят" + "/n Количество анеков в предложке: " + proposal.size());
                    break;
                case "Decline":
                    bot.sendMsg(query.getMessage(), "Анекдот отклонен"+ "/n Количество анеков в предложке: " + proposal.size());
                    proposal.remove();
                    break;
            }

        } else {
            int id = Integer.parseInt(text);
            String anek = anekdotEntityRepository.findById(id).getAnek();
            System.out.println(anek);
            bot.sendAud(query, anek);
        }
    }

}
