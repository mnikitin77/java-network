package org.mnikitin.lesson6.service;

import org.mnikitin.lesson6.SimpleProtocolResponseCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Pattern;

public class SimpleProtocolServiceImpl implements SimpleProtocolService {

    private static final String MESSAGE_DELIMITER = ":";
    private static final Logger log = LoggerFactory.getLogger(SimpleProtocolServiceImpl.class);
    private final Pattern positiveWholeNumberPattern = Pattern.compile("\\d+");

    @Override
    public SimpleProtocolResponseCode processMessage(String message) {
        var messageElements = message.split(MESSAGE_DELIMITER);
        if (messageElements.length == 2 &&
                validateMessage(messageElements[0], messageElements[1])) {
            log.info("Message [{}] is valid", message);
            return SimpleProtocolResponseCode.OK;
        }
        log.warn("Message [{}] is invalid", message);
        return SimpleProtocolResponseCode.ERR;
    }

    private boolean validateMessage(String sizePart, String payload) {
        if (sizePart != null && payload != null &&
                positiveWholeNumberPattern.matcher(sizePart).matches()) {
            return Integer.parseInt(sizePart) == payload.length();
        } else {
            return false;
        }
    }
}
