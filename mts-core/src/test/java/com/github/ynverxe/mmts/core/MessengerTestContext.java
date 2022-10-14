package com.github.ynverxe.mmts.core;

import com.github.ynverxe.mmts.core.placeholder.PlaceholderValueProvider;

public final class MessengerTestContext {

    public static final Identity ONE = new Identity("ImTheNico_", "es");
    public static final Identity TWO = new Identity("Idk123", "en");

    public static final PlaceholderValueProvider PLACEHOLDER_APPLIER = new TestPlaceholderValueProvider();
    public static final MMTSHandler MMTS_HANDLER = MMTSHandler.create(null);

    static {
        MMTS_HANDLER.installModule(new TestMMTSModule());
    }
}