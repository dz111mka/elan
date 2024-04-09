package ru.chepikov.elan.service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
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
import java.util.Arrays;
import java.util.List;


@Component
@Log4j
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class TelegramBot extends TelegramLongPollingBot {
    BotConfig config;

    PersonRepository personRepository;

    public TelegramBot(BotConfig config, PersonRepository personRepository) {
        this.config = config;
        this.personRepository = personRepository;
        List<BotCommand> listOfCommands = new ArrayList<>();
        listOfCommands.add(new BotCommand("/start", "Начальное сообщение"));
        listOfCommands.add(new BotCommand("/register", "Регистрация"));
        listOfCommands.add(new BotCommand("/editstatus", "Изменение статуса"));
        listOfCommands.add(new BotCommand("/checkemployee", "Вывести всех сотрудников"));
        listOfCommands.add(new BotCommand("/edit", "Добавить/изменить данные"));
        listOfCommands.add(new BotCommand("/help", "Помощь"));

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
                case "/start" -> startCommandReceived(chatId, update.getMessage().getChat().getFirstName());
                case "/register" -> registerUser(update.getMessage());
                case "/checkemployee" -> checkPerson(chatId);
                case "/edit" -> updatePerson(chatId, update.getMessage().getChat().getUserName());
                case "/editstatus" -> editStatus(chatId);
                default -> sendMessage(chatId, "Sorry, command was not recognized");
            }
        } else if (update.hasCallbackQuery()) {
            String callBackData = update.getCallbackQuery().getData();
            long messageId = update.getCallbackQuery().getMessage().getMessageId();
            long chatId = update.getCallbackQuery().getMessage().getChatId();

            switch (callBackData) {
                //TODO не сделан метод от слова совсем
                /*case "FIRSTNAME_BUTTON" -> {
                    Person person = personRepository.findByUsername(update.getCallbackQuery().getId());
                    EditMessageText message = new EditMessageText();
                    message.setChatId(String.valueOf(chatId));
                    message.setText("Введите новое имя");
                    message.setMessageId((int) messageId);
                    try {
                        execute(message);
                    } catch (TelegramApiException e) {
                        log.warn("GG");
                    }
                    boolean trigger = true;
                    while (trigger) {
                        Update newUpdate = new Update(); // Функция, которая ждет новое сообщение
                        if (newUpdate.hasMessage() && newUpdate.getMessage().hasText()) {
                            person.setFirstname(newUpdate.getMessage().getText());
                            personRepository.save(person);
                            trigger = false;
                        }
                    }
                }*/

                case "HOME_BUTTON" -> {
                    Person person = personRepository.findByUsername(update.getCallbackQuery().getMessage().getChat().getUserName());
                    person.setLocation(Location.HOME);
                    personRepository.save(person);
                }
                case "ILL_BUTTON" -> {
                    Person person = personRepository.findByUsername(update.getCallbackQuery().getMessage().getChat().getUserName());
                    person.setLocation(Location.ILL);
                    personRepository.save(person);
                }
                case "DINNER_BUTTON" -> {
                    Person person = personRepository.findByUsername(update.getCallbackQuery().getMessage().getChat().getUserName());
                    person.setLocation(Location.DINNER);
                    personRepository.save(person);
                }
                case "OFFICE_BUTTON" -> {
                    Person person = personRepository.findByUsername(update.getCallbackQuery().getMessage().getChat().getUserName());
                    person.setLocation(Location.OFFICE);
                    personRepository.save(person);
                }
                case "BLANK_WORKSHOP_BUTTON" -> {
                    Person person = personRepository.findByUsername(update.getCallbackQuery().getMessage().getChat().getUserName());
                    person.setLocation(Location.BLANK_WORKSHOP);
                    personRepository.save(person);
                }
                case "AUTO_WORKSHOP_BUTTON" -> {
                    Person person = personRepository.findByUsername(update.getCallbackQuery().getMessage().getChat().getUserName());
                    person.setLocation(Location.AUTO_WORKSHOP);
                    personRepository.save(person);
                }
                case "VACUUM_WORKSHOP_BUTTON" -> {
                    Person person = personRepository.findByUsername(update.getCallbackQuery().getMessage().getChat().getUserName());
                    person.setLocation(Location.VACUUM_WORKSHOP);
                    personRepository.save(person);
                }
                case "SHPAT_WORKSHOP_BUTTON" -> {
                    Person person = personRepository.findByUsername(update.getCallbackQuery().getMessage().getChat().getUserName());
                    person.setLocation(Location.SHPAT_WORKSHOP);
                    personRepository.save(person);
                }
                case "QUALITY_CONTROL_DEPARTMENT_BUTTON" -> {
                    Person person = personRepository.findByUsername(update.getCallbackQuery().getMessage().getChat().getUserName());
                    person.setLocation(Location.QUALITY_CONTROL_DEPARTMENT);
                    personRepository.save(person);
                }
                case "FLUSHING_BUTTON" -> {
                    Person person = personRepository.findByUsername(update.getCallbackQuery().getMessage().getChat().getUserName());
                    person.setLocation(Location.FLUSHING);
                    personRepository.save(person);
                }
                case "LABORATORY_BUTTON" -> {
                    Person person = personRepository.findByUsername(update.getCallbackQuery().getMessage().getChat().getUserName());
                    person.setLocation(Location.LABORATORY);
                    personRepository.save(person);
                }
                case "LEO_KHUDYAKOV_BUTTON" -> {
                    Person person = personRepository.findByUsername(update.getCallbackQuery().getMessage().getChat().getUserName());
                    person.setLocation(Location.LEO_KHUDYAKOV);
                    personRepository.save(person);
                }
                case "TIMONIN_BUTTON" -> {
                    Person person = personRepository.findByUsername(update.getCallbackQuery().getMessage().getChat().getUserName());
                    person.setLocation(Location.TIMONIN);
                    personRepository.save(person);
                }
                case "VACATION_BUTTON" -> {
                    Person person = personRepository.findByUsername(update.getCallbackQuery().getMessage().getChat().getUserName());
                    person.setLocation(Location.VACATION);
                    personRepository.save(person);
                }
            }
        }
    }

    private void editStatus(long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Выберите свой статус");
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();

        InlineKeyboardButton[] buttonsRow1 = {
                createInlineKeyboardButton("Дом \uD83C\uDFE0", "HOME_BUTTON"),
                createInlineKeyboardButton("Больничный \uD83E\uDD12", "ILL_BUTTON"),
                createInlineKeyboardButton("Обед \uD83C\uDF7D\uFE0F", "DINNER_BUTTON"),
                createInlineKeyboardButton("Офис \uD83D\uDCBC", "OFFICE_BUTTON")
        };

        InlineKeyboardButton[] buttonsRow2 = {
                createInlineKeyboardButton("Заготовка \uD83D\uDEE0\uFE0F", "BLANK_WORKSHOP_BUTTON"),
                createInlineKeyboardButton("Автоматный участок \uD83D\uDEE0\uFE0F", "AUTO_WORKSHOP_BUTTON"),
                createInlineKeyboardButton("Вакуумный участок \uD83D\uDEE0\uFE0F", "VACUUM_WORKSHOP_BUTTON"),
                createInlineKeyboardButton("Шпат \uD83D\uDEE0\uFE0F", "SHPAT_WORKSHOP_BUTTON"),
                createInlineKeyboardButton("ОТК \uD83D\uDC67", "QUALITY_CONTROL_DEPARTMENT_BUTTON")
        };

        InlineKeyboardButton[] buttonsRow3 = {
                createInlineKeyboardButton("Промывочная \uD83D\uDC67", "FLUSHING_BUTTON"),
                createInlineKeyboardButton("Лаборатория \uD83E\uDD7C", "LABORATORY_BUTTON"),
                createInlineKeyboardButton("Лаборатория Лени Худякова \uD83E\uDD7C", "LEO_KHUDYAKOV_BUTTON"),
                createInlineKeyboardButton("У Тимонина \uD83D\uDEAC", "TIMONIN_BUTTON"),
                createInlineKeyboardButton("Отпуск \uD83D\uDEAC", "VACATION_BUTTON")
        };

        List<InlineKeyboardButton> rowInLine1 = Arrays.asList(buttonsRow1);
        List<InlineKeyboardButton> rowInLine2 = Arrays.asList(buttonsRow2);
        List<InlineKeyboardButton> rowInLine3 = Arrays.asList(buttonsRow3);

        rowsInLine.add(rowInLine1);
        rowsInLine.add(rowInLine2);
        rowsInLine.add(rowInLine3);

        keyboardMarkup.setKeyboard(rowsInLine);
        message.setReplyMarkup(keyboardMarkup);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private InlineKeyboardButton createInlineKeyboardButton(String text, String callbackData) {
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(text);
        button.setCallbackData(callbackData);
        return button;
    }


    private void startCommandReceived(long chatId, String name) {
        String answer = "Hi " + name + ", nice to meet you!";
        sendMessage(chatId, answer);
    }


    private void sendMessage(long chatId, String textToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
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
        message.setChatId(chatId);
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

    private void changeFirstName(long chatId, String username) {
        Person person = personRepository.findByUsername(username);
        EditMessageText messageText = new EditMessageText();

        person.setFirstname(username);
        personRepository.save(person);
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
