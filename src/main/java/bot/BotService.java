package bot;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.request.SendMessage;
import entity.TelegramUser;
import enums.TelegramState;

import java.text.NumberFormat;
import java.util.StringJoiner;

import static bot.MyBot.telegramBot;

public class BotService {

    public static void acceptStartSendGreetingAndCarryOn(TelegramUser telegramUser) {
        SendMessage sendMessage = new SendMessage(telegramUser.getChatId(), """
                Assalomu alaykum, botga xush keblibsiz!
                                 
                Mahsulot narxini kiriting!
                                 
                Misol uchun: 1500000
                       
                       """);
        telegramBot.execute(sendMessage);
        telegramUser.setState(TelegramState.acceptingPriceOfProduct);
    }

    public static void acceptPriceAskForInitialPayment(TelegramUser telegramUser, Message message) {
        try {
            telegramUser.setPriceOfProduct((float) Integer.parseInt(message.text()));
        } catch (Exception e) {
            throw new RuntimeException();
        }
        SendMessage sendMessage = new SendMessage(telegramUser.getChatId(), """
                Boshlang'ich to'lov qilmoqchimisiz?
                             """);
        sendMessage.replyMarkup(BotUtils.generateYesOrNoButton());
        telegramBot.execute(sendMessage);
        telegramUser.setState(TelegramState.acceptingInitialPaymentThenActAccordingly);
    }

    public static void askForInitialPaymentAmount(TelegramUser telegramUser) {
        SendMessage sendMessage = new SendMessage(
                telegramUser.getChatId(),
                "Qancha boshlang'ich to'lov qilishingizni kiriting"
        );
        telegramBot.execute(sendMessage);
        telegramUser.setState(TelegramState.acceptInitialPaymentThenShowReportWithInitialPaymentIncluded);
    }

    public static void skipInitialPaymentShowReport(TelegramUser telegramUser) {
        float priceOfProduct = telegramUser.getPriceOfProduct();

        SendMessage sendMessage = new SendMessage(telegramUser.getChatId(), """
                Kredit kalkulyatori 🧮
                                       
                %s
                                        
                """.formatted(generateReportWithoutInitialPayment(priceOfProduct)));
        sendMessage.replyMarkup(BotUtils.generateBackButton());
        telegramBot.execute(sendMessage);
        telegramUser.setState(TelegramState.BACK_BUTTON);
    }

    private static String generateReportWithoutInitialPayment(float priceOfProduct) {
        StringJoiner stringJoiner = new StringJoiner("\n");

        for (int i = 0; i < 12; i++) {
            stringJoiner.add(i + 1 + " - " + calculateMonthlyWithoutInitialPayment(priceOfProduct, i));
        }
        return stringJoiner.toString();
    }

    private static String calculateMonthlyWithoutInitialPayment(float priceOfProduct, int i) {
        i++;
        double v = priceOfProduct * (1 + i * 0.06) / i;

        int v1 = (int) v;
        return formatPrice(v1);
    }

    public static void acceptingInitialAmountAndSendingReport(TelegramUser telegramUser, String text) {
        Integer initialPayment = Integer.parseInt(text);
        float priceOfProduct = telegramUser.getPriceOfProduct();

        SendMessage sendMessage = new SendMessage(telegramUser.getChatId(), """
                Kredit kalkulyatori 🔢
                                       
                %s
                                        
                """.formatted(generateReportWithInitialPayment(priceOfProduct, initialPayment)));

        sendMessage.replyMarkup(BotUtils.generateBackButton());
        telegramBot.execute(sendMessage);
        telegramUser.setState(TelegramState.BACK_BUTTON);
    }

    private static String generateReportWithInitialPayment(float priceOfProduct, Integer initialPayment) {
        StringJoiner stringJoiner = new StringJoiner("\n");
        for (int i = 0; i < 12; i++) {
            stringJoiner.add(i + 1 + " - " + calculateMonthlyReportWithInitialPayment(priceOfProduct, i, initialPayment));
        }

        return stringJoiner.toString();
    }

    private static String calculateMonthlyReportWithInitialPayment(float priceOfProduct, int i, Integer initialPayment) {
        i++;
        double v = (priceOfProduct - initialPayment) * (1 + i * 0.06) / i;

        int v1 = (int) v;
        return formatPrice(v1);
    }

    private static String formatPrice(Integer price) {
        return NumberFormat.getNumberInstance().format(price);
    }
}
