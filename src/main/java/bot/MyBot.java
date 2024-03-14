package bot;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import db.DB;
import entity.TelegramUser;
import enums.TelegramState;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MyBot {
    public static TelegramBot telegramBot = new TelegramBot("6587666490:AAFeLsc6g3PSAwpim0bJxERBjobOJD5nV7Q");
    public static ExecutorService executorService = Executors.newFixedThreadPool(20);

    public void start() {
        telegramBot.setUpdatesListener((updates) -> {
            executorService.submit(() -> updates.forEach(this::handleUpdate));
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        }, Throwable::printStackTrace);
    }

    private void handleUpdate(Update update) {
        if (update.message() != null) {
            Message message = update.message();
            Long chatId = message.chat().id();
            TelegramUser telegramUser = getUser(chatId);

            if (message.text() != null) {
                String text = message.text();

                if (text.equals("/start")) {
                    BotService.acceptStartSendGreetingAndCarryOn(telegramUser);
                } else if (telegramUser.checkState(TelegramState.acceptingPriceOfProduct)) {
                    BotService.acceptPriceAskForInitialPayment(telegramUser, message);
                } else if (telegramUser.checkState(TelegramState.acceptInitialPaymentThenShowReportWithInitialPaymentIncluded)) {
                    BotService.acceptingInitialAmountAndSendingReport(telegramUser, text);
                }
            }
        } else if (update.callbackQuery() != null) {
            CallbackQuery callbackQuery = update.callbackQuery();
            Long chatId = callbackQuery.from().id();
            TelegramUser telegramUser = getUser(chatId);
            if (callbackQuery.data() != null) {
                String data = callbackQuery.data();

                if (telegramUser.checkState(TelegramState.acceptingInitialPaymentThenActAccordingly)) {
                    if (data.equals(BotConstants.YES)) {
                        BotService.askForInitialPaymentAmount(telegramUser);
                    } else {
                        BotService.skipInitialPaymentShowReport(telegramUser);
                    }
                } else if (telegramUser.checkState(TelegramState.BACK_BUTTON)) {
                    if (data.equals(BotConstants.BACK)) {
                        telegramUser.setState(TelegramState.start);
                        BotService.acceptStartSendGreetingAndCarryOn(telegramUser);
                    }
                }
            }
        }
    }

    private TelegramUser getUser(Long chatId) {
        List<TelegramUser> list = DB.TELEGRAM_USERS.stream()
                .filter(telegramUser -> telegramUser.getChatId().equals(chatId))
                .toList();

        if (list.isEmpty()) {
            TelegramUser newUser = TelegramUser.builder()
                    .chatId(chatId)
                    .state(TelegramState.start)
                    .build();
            DB.TELEGRAM_USERS.add(newUser);
            return newUser;
        }
        return list.get(0);
    }
}
