package com.github.ynverxe.mmts.core;

import com.github.ynverxe.mmts.core.message.MessagingResource;
import org.junit.Test;

import java.util.Arrays;

import static com.github.ynverxe.mmts.core.MessengerTestContext.*;

public class MessengerTest {

    @Test
    public void sendCustomMessage() {
        MMTS_HANDLER.dispatchMessage(
                new MessagePack(
                        Arrays.asList("These are the languages i understand: ", "- Spanish", "- English")
                ), null, ONE);

        printSeparator();
    }

    @Test
    public void sendMessage() {
        MMTS_HANDLER.dispatchMessage(MessagingResource.pathOfText("Hi %player_name%!"),
                null, Arrays.asList(ONE, TWO));

        printSeparator();
    }

    @Test
    public void findAndSendTest() {
        MMTS_HANDLER.dispatchMessage(MessagingResource.pathOfText("hi-message"), null, Arrays.asList(ONE, TWO));

        printSeparator();
    }

    @Test
    public void testIncorrectMessagePath() {
        MMTS_HANDLER.dispatchMessage(MessagingResource.pathOfText("warn-message"), null, Arrays.asList(ONE, TWO));
    }

    private void printSeparator() {
        System.out.println("---------------------------------");
    }
}