package org.mnikitin.lesson6.service;

import org.mnikitin.lesson6.SimpleProtocolResponseCode;

public interface SimpleProtocolService {

    SimpleProtocolResponseCode processMessage(String message);
}
