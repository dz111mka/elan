package ru.chepikov.elan.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.chepikov.elan.config.BotConfig;
import ru.chepikov.elan.entity.Location;
import ru.chepikov.elan.entity.Person;
import ru.chepikov.elan.repository.PersonRepository;

import java.util.ArrayList;
import java.util.List;


@Component
@Log4j
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class TelegramBot extends TelegramLongPollingBot {

    static final String YES_BUTTON = "YES_BUTTON";
    static final String NO_BUTTON = "NO_BUTTON";

    BotConfig config;

    PersonRepository personRepository;

    public TelegramBot(BotConfig config, PersonRepository personRepository) {
        this.config = config;
        this.personRepository = personRepository;
        List<BotCommand> listOfCommands = new ArrayList<>();
        listOfCommands.add(new BotCommand("/start", "начальное сообщение"));
        listOfCommands.add(new BotCommand("/register", "регистрация"));
        listOfCommands.add(new BotCommand("/checkemployee", "Вывести всех сотрудников"));
        listOfCommands.add(new BotCommand("/edit", "Добавить/изменить данные"));
        listOfCommands.add(new BotCommand("/help", "инфо пользования"));

        try {
            this.execute(new SetMyCommands(listOfCommands, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            log.error("Error setting bot's command list: " + e.getMessage());
        }
    }

    @Override
    public void onUpdateReceived(Update update) {

        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            switch (messageText) {
                case "/start":
                    startCommandReceived(chatId, update.getMessage().getChat().getFirstName());
                    break;
                case "/register":
                    registerUser(update.getMessage());
                    break;
                case "/checkemployee":
                    checkPerson(chatId);
                    break;
                case "/edit":
                    updatePerson(chatId, update.getMessage().getChat().getUserName());
                    break;
                default:
                    sendMessage(chatId, "Sorry, command was not recognized");
            }

        }
    }


    private void startCommandReceived(long chatId, String name) {
        String answer = "Hi " + name + ", nice to meet you!";
        sendMessage(chatId, answer);
    }


    private void sendMessage(long chatId, String textToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.warn("Сообщение не отправлено");
        }
    }

    private void checkPerson(long chatId) {
        StringBuilder stringBuilder = new StringBuilder();
        for (Person person : personRepository.findAll()) {
            /*String input = person.getId() + ") " + person.getFirstname() + " " + person.getLastname() + " находится в " + person.getLocation() + "\n";*/
            stringBuilder.append(person).append("\n");
        }
        sendMessage(chatId, stringBuilder.toString());
    }

    private void updatePerson(long chatId, String username) {
        Person person = personRepository.findByUsername(username);
        String personInfo = "Имя: " + person.getFirstname() + "\n" +
                "Фамилия: " + person.getLastname() + "\n" +
                "телефон: " + person.getPhone() + "\n" +
                "дата рождения: " + "null" + "\n";
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(personInfo + " \n" + "Что ты хочешь изменить");
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
        List<InlineKeyboardButton> rowInLine = new ArrayList<>();

        InlineKeyboardButton firstNameButton = new InlineKeyboardButton();
        InlineKeyboardButton lastNameButton = new InlineKeyboardButton();
        InlineKeyboardButton phoneButton = new InlineKeyboardButton();
        InlineKeyboardButton birthdayButton = new InlineKeyboardButton();

        firstNameButton.setText("Имя");
        firstNameButton.setCallbackData("FIRSTNAME_BUTTON");
        lastNameButton.setText("Фамилия");
        lastNameButton.setCallbackData("LASTNAME_BUTTON");
        phoneButton.setText("Телефон");
        phoneButton.setCallbackData("PHONE_BUTTON");
        birthdayButton.setText("День рождения");
        birthdayButton.setCallbackData("BIRTHDAY_BUTTON");

        rowInLine.add(firstNameButton);
        rowInLine.add(lastNameButton);
        rowInLine.add(phoneButton);
        rowInLine.add(birthdayButton);

        rowsInLine.add(rowInLine);

        keyboardMarkup.setKeyboard(rowsInLine);
        message.setReplyMarkup(keyboardMarkup);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private void register(long chatId) {
        /*SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText("Do you really want to register?");

        InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
        List<InlineKeyboardButton> rowInLine = new ArrayList<>();
        InlineKeyboardButton yesButton = new InlineKeyboardButton();

        yesButton.setText("Yes");
        yesButton.setCallbackData(YES_BUTTON);

        InlineKeyboardButton noButton = new InlineKeyboardButton();

        noButton.setText("No");
        noButton.setCallbackData(NO_BUTTON);

        rowInLine.add(yesButton);
        rowInLine.add(noButton);

        rowsInLine.add(rowInLine);

        markupInLine.setKeyboard(rowsInLine);
        message.setReplyMarkup(markupInLine);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }*/
    }

    private void registerUser(Message message) {
        //TODO доделать условия проверки на наличие зарегестрирован ли уже пользователь
        Long chatId = message.getChatId();
        Chat chat = message.getChat();
        Person person = new Person();
        person.setId(chatId);
        person.setFirstname(chat.getFirstName());
        person.setLastname(chat.getLastName());
        person.setUsername(chat.getUserName());
        person.setLocation(Location.HOME);
        personRepository.save(person);
        log.info("Пользователь: " + person.getUsername() + " был зарегестрирован");
    }

    @Override
    public String getBotUsername() {
        return config.getBotName();
    }

    @Override
    public String getBotToken() {
        return config.getToken();
    }

}
