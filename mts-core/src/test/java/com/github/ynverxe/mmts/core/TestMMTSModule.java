package com.github.ynverxe.mmts.core;

import com.github.ynverxe.mmts.translation.TranslationSource;

public class TestMMTSModule extends AbstractMMTSModule {
    @Override
    public void configure() {
        addTranslationSource("en", TranslationSource.newMemorySource()
                .addMessageData("hi-message", "Hi %user_name%, im a linguist!"));

        addTranslationSource("es", TranslationSource.newMemorySource()
                .addMessageData("hi-message", "Hola %user_name%, soy un linguista!"));

        getEntityContainer(Identity.class, false)
                .bindSender(new TestMessageSender())
                .bindLinguist(Identity::getLang)
                .bindStrategy((mmtsHandler, entity, path) -> entity.handleReceivedMessage(path));

        installPlaceholderApplier("player", MessengerTestContext.PLACEHOLDER_APPLIER);
    }
}