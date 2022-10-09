package com.github.ynverxe.mmts.core;

import com.github.ynverxe.mmts.core.format.FormattingContextNamespaces;
import com.github.ynverxe.mmts.core.remittent.Remittent;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static com.github.ynverxe.mmts.core.MessengerTestContext.*;

public class MessengerTest {

    @Test
    public void sendCustomMessage() {
        MMTS_HANDLER.prepareMessage(
                new MessagePack(
                        Arrays.asList("These are the languages i understand: ", "- Spanish", "- English")
                )).send(Collections.singletonList(ONE));

        printSeparator();
    }

    @Test
    public void sendMessage() {
        MMTS_HANDLER.prepareMessage("Hi %player_name%!")
                .send(Arrays.asList(ONE, TWO));

        printSeparator();
    }

    @Test
    public void customizeContextAndSend() {
        MMTS_HANDLER.prepareMessage("Hi %player_name%!, i see that your lang is %lang%!")
                .configureContext(context -> {
                    String lang = Optional.ofNullable(context.getData(FormattingContextNamespaces.REMITTENT_NAMESPACE))
                                    .map(object -> ((Remittent) object).getEntity())
                                    .map(entity -> (Identity) entity).get()
                                    .getLang();

                    context.setReplacement("%lang%", lang);
                })
                .send(Arrays.asList(ONE, TWO));

        printSeparator();
    }

    @Test
    public void findAndSendTest() {
        MMTS_HANDLER.translating("hi-message", String.class)
                .send(Arrays.asList(ONE, TWO));

        printSeparator();
    }

    @Test
    public void testIncorrectMessagePath() {
        MMTS_HANDLER.translating("warn-message", String.class)
                .send(ONE);
    }

    private void printSeparator() {
        System.out.println("---------------------------------");
    }

}