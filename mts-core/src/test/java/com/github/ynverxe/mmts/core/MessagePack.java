package com.github.ynverxe.mmts.core;

import org.jetbrains.annotations.NotNull;

import java.util.*;

public class MessagePack implements Iterable<String> {

    private final List<String> messages;

    public MessagePack(List<String> messages) {
        this.messages = messages;
    }

    @NotNull
    @Override
    public Iterator<String> iterator() {
        return messages.iterator();
    }

}