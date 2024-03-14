package entity;

import enums.TelegramState;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class TelegramUser {
    private String firstName;
    private String lastName;
    private Long chatId;
    private TelegramState state;
    private float priceOfProduct;

    public boolean checkState(TelegramState telegramState) {
        return this.state.equals(telegramState);
    }
}
