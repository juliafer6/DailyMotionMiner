package aiss.dailymotionminer.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ChannelNotFoundException extends RuntimeException {
    public ChannelNotFoundException(String channelId) {
        super("Channel not found: " + channelId);
    }
}
