package bot;

import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.Keyboard;

public class BotUtils {
    public static Keyboard generateYesOrNoButton() {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.addRow(
                new InlineKeyboardButton("YO'Q ❎").callbackData(BotConstants.NO),
                new InlineKeyboardButton("HA ✅").callbackData(BotConstants.YES)
        );
        return inlineKeyboardMarkup;
    }

    public static Keyboard generateBackButton() {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.addRow(new InlineKeyboardButton("⬅️ Orqaga").callbackData(BotConstants.BACK));
        return inlineKeyboardMarkup;
    }
}
