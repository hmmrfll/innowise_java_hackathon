package innowise.hackathon.bot1.telegram;
import innowise.hackathon.bot1.configuration.BotConfig;
import innowise.hackathon.bot1.entity.CryptoDto;
import innowise.hackathon.bot1.entity.CryptoLast;
import innowise.hackathon.bot1.entity.CryptoNew;
import innowise.hackathon.bot1.repository.CryptoLastRepository;
import innowise.hackathon.bot1.repository.CryptoNewRepository;
import innowise.hackathon.bot1.service.CryptoChanges;
import innowise.hackathon.bot1.service.CryptoParser;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TelegramBot extends TelegramLongPollingBot {

    private final BotConfig botConfig;
    private final CryptoParser cryptoParser;
    private final CryptoLastRepository lastRepository;
    private final CryptoNewRepository newRepository;
    private final CryptoChanges cryptoChanges;

    private static final Logger LOG = LoggerFactory.getLogger(TelegramBot.class);

    private static final String START = "/start";
    private static final String CHANGES_3 = "/3";
    private static final String CHANGES_5 = "/5";
    private static final String CHANGES_10 = "/10";
    private static final String CHANGES_15 = "/15";
    private static final String CURRENT_RATE = "/currentRate";
    private static final String HELP = "/help";


    @Override
    public String getBotUsername() {
        return botConfig.getBotUsername();
    }
    @Override
    public String getBotToken() {
        return botConfig.getBotToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            return;
        }
        var message = update.getMessage().getText();
        var chatId = update.getMessage().getChatId();
        switch (message) {
            case START:
                String userName = update.getMessage().getChat().getUserName();
                startCommand(chatId, userName);
                break;
            case CURRENT_RATE:
                commandCurrentRate(chatId);
                break;
            case CHANGES_3:
                commandChanges_3(chatId);
                break;
            case CHANGES_5:
                commandChanges_5(chatId);
                break;
            case CHANGES_10:
                commandChanges_10(chatId);
                break;
            case CHANGES_15:
                commandChanges_15(chatId);
                break;
            case HELP:
                helpCommand(chatId);
                break;
            default:
                unknownCommand(chatId);
                break;
        }
    }

    private void startCommand(Long chatId, String userName) {
        String text = "Добро пожаловать в бот, " + userName + "!\n\n" +
                "Здесь Вы сможете узнать официальные курсы криптовалют на сегодня.\n\n" +
                "Для этого воспользуйтесь командами:\n" +
                "/currentRate\n\n" +
                "Текущие изменения криптовалют на:\n" +
                "/3% - подорожает/подешевеет\n" +
                "/5% - подорожает/подешевеет\n" +
                "/10% - подорожает/подешевеет\n" +
                "/15% - подорожает/подешевеет\n\n" +
                "Дополнительные команды:\n" +
                "/help - получение справки";
        sendMessage(chatId, text);
    }



    private void commandCurrentRate(Long chatId) {
        cryptoParser.parseAndStoreData();
        List<CryptoNew> cryptoNews = newRepository.findAll();
        if (!cryptoNews.isEmpty()) { // Проверяем, что список не пустой
            List<CryptoNew> firstTenCryptoNews = cryptoNews.subList(0, Math.min(cryptoNews.size(), 10));

            StringBuilder messageBuilder = new StringBuilder();
            for (CryptoNew cryptoNew : firstTenCryptoNews) {
                messageBuilder.append(cryptoNew.getSymbol()).append(": ").append(cryptoNew.getPrice()).append("\n");
            }

            sendMessage(chatId, messageBuilder.toString());
        } else {
            sendMessage(chatId, "Список криптовалют пуст.");
        }
    }

    private void commandChanges_3(Long chatId) {
        List<String> changes = cryptoChanges.infoChangesOfPercent(0.3);
        String formattedChanges = cryptoChanges.formatChangesForTelegram(changes);
        sendMessage(chatId, formattedChanges);
    }




    private void commandChanges_5(Long chatId) {
        List<String> changes = cryptoChanges.infoChangesOfPercent(0.5);
        String formattedChanges = cryptoChanges.formatChangesForTelegram(changes);
        sendMessage(chatId, formattedChanges);
    }

    private void commandChanges_10(Long chatId) {
        List<String> changes = cryptoChanges.infoChangesOfPercent(1);
        String formattedChanges = cryptoChanges.formatChangesForTelegram(changes);
        sendMessage(chatId, formattedChanges);
    }

    private void commandChanges_15(Long chatId) {
        List<String> changes = cryptoChanges.infoChangesOfPercent(1.5);
        String formattedChanges = cryptoChanges.formatChangesForTelegram(changes);
        sendMessage(chatId, formattedChanges);
    }

    private void helpCommand(Long chatId) {
        var text = """
        Справочная информация по боту

        Текущий курс:
        /currentRate

        Текущие изменения криптовалют на:
        /3% - подорожает/подешевеет
        /5% - подорожает/подешевеет
        /10% - подорожает/подешевеет
        /15% - подорожает/подешевеет
        """;
        sendMessage(chatId, text);
    }

    private void unknownCommand(Long chatId) {
        var text = "Не удалось распознать команду!";
        sendMessage(chatId, text);
    }

    private void sendMessage(Long chatId, String text) {
        var chatIdStr = String.valueOf(chatId);
        var sendMessage = new SendMessage(chatIdStr, text);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            LOG.error("Ошибка отправки сообщения", e);
        }
    }
}
