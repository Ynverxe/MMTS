package com.github.ynverxe.mmts.core;

import com.github.ynverxe.mmts.core.placeholder.PlaceholderApplier;

public final class MessengerTestContext {

    public static final Identity ONE = new Identity("ImTheNico_", "es");
    public static final Identity TWO = new Identity("Idk123", "en");

    public static final PlaceholderApplier PLACEHOLDER_APPLIER = new TestPlaceholderApplier();
    public static final MMTSHandler MMTS_HANDLER = MMTSHandler.create(null);

    static {
        MMTS_HANDLER.installModule(new TestMMTSModule());
    }
}