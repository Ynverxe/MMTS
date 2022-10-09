package com.github.ynverxe.mmts.core;

import com.github.ynverxe.mmts.core.entity.MessageSender;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class TestMessageSender implements MessageSender<Identity> {
    @Override
    public void sendMessage(@NotNull Identity receiver, @NotNull String mode, @NotNull Object message) {
        if (message instanceof MessagePack) {
            ((MessagePack) message).forEach(receiver::handleReceivedMessage);
        } else {
            receiver.handleReceivedMessage(Objects.toString(message));
        }
    }

    @Override
    public boolean offer(@NotNull Class<?> messageClass) {
        return String.class == messageClass || messageClass == MessagePack.class;
    }
}