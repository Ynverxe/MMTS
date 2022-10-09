package com.github.ynverxe.mmts.core;

public class Identity {

    private final String identifier;
    private final String lang;

    public Identity(String identifier, String lang) {
        this.identifier = identifier;
        this.lang = lang;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getLang() {
        return lang;
    }

    public void handleReceivedMessage(String message) {
        System.out.println("(" + identifier + "'s POV) " + message);
    }
}