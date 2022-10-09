package com.github.ynverxe.mmts.core;

import com.github.ynverxe.mmts.core.placeholder.PlaceholderReplacer;
import org.junit.Test;

import static com.github.ynverxe.mmts.core.MessengerTestContext.*;
import static org.junit.Assert.*;

public class PlaceholderTest {

    private final static String TEXT_WITH_CORRECTLY_FORMED_PLACEHOLDER = "Welcome! %player_name% :)";
    private final static String TEXT_WITH_MALFORMED_PLACEHOLDER = "Welcome! %player_name, this placeholder is malformed";
    private final static String TEXT_WITH_DOUBLE_CORRECTLY_FORMED_PLACEHOLDERS = "Welcome %player_name%! I see that your lang is '%player_lang%'";

    @Test
    public void testCorrectlyFormedPlaceholder() {
        PlaceholderReplacer replacer = MMTS_HANDLER.createPlaceholderReplacer('%', '%');

        String formatted = replacer
                .replace(ONE, TEXT_WITH_CORRECTLY_FORMED_PLACEHOLDER);

        System.out.println(formatted);

        assertEquals(formatted.replace("%player_name%", ONE.getIdentifier()), formatted);

        printSeparator();
    }

    @Test
    public void testMalformedPlaceholder() {
        PlaceholderReplacer replacer = MMTS_HANDLER.createPlaceholderReplacer('%', '%');

        String text = TEXT_WITH_MALFORMED_PLACEHOLDER;
        String formatted = replacer
                .replace(ONE, text);

        System.out.println(formatted);

        assertEquals(formatted, text);

        printSeparator();
    }

    @Test
    public void testDoubleCorrectlyFormedPlaceholders() {
        PlaceholderReplacer replacer = MMTS_HANDLER.createPlaceholderReplacer('%', '%');

        String formatted = replacer
                .replace(ONE, TEXT_WITH_DOUBLE_CORRECTLY_FORMED_PLACEHOLDERS);

        System.out.println(formatted);

        assertEquals(formatted.replace("%player_name%", ONE.getIdentifier())
                .replace("%player_lang%", ONE.getLang()), formatted);

        printSeparator();
    }

    private void printSeparator() {
        System.out.println("---------------------------------");
    }
}