package com.github.ynverxe.mmts.core.format.def;

import com.github.ynverxe.mmts.core.MMTSHandler;
import com.github.ynverxe.mmts.core.format.FormattingInterceptor;
import com.github.ynverxe.mmts.core.format.FormattingMetricsHolder;
import com.github.ynverxe.mmts.core.resource.FindableResource;
import com.github.ynverxe.mmts.core.remittent.Remittent;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Function;

import static com.github.ynverxe.mmts.core.format.FormattingContextNamespaces.*;

@SuppressWarnings("unchecked")
public class ReplacementValuesApplier implements FormattingInterceptor<String> {

    private final MMTSHandler mmtsHandler;

    public ReplacementValuesApplier(MMTSHandler mmtsHandler) {
        this.mmtsHandler = mmtsHandler;
    }

    @Override
    public @NotNull String visit(@NotNull String value, @NotNull FormattingMetricsHolder formattingMetricsHolder) {
        Map<String, String> replacementValues = formattingMetricsHolder
                .optionalDataGet(REPLACEMENT_VALUES, Map.class)
                .orElse(Collections.emptyMap());

        Remittent remittent = formattingMetricsHolder.findData(REMITTENT_NAMESPACE, Remittent.class);

        value = applyReplacements(value, replacementValues, str -> {
            if (remittent != null && remittent.getLang() != null && str.startsWith("<@") && str.endsWith(">")) {
                str = str.substring(2, str.length() - 1);
                Object[] values = mmtsHandler.processModel(FindableResource.withType(str, String.class))
                        .values().toArray();

                if (values.length != 0) {
                    String found = Objects.toString(values[0]);

                    return found != null ? found : str;
                }
            }
            return str;
        });

        return value;
    }

    private String applyReplacements(String value, Map<String, String> replacements, Function<String, String> replacementMapper) {
        for (Map.Entry<String, String> entry : replacements.entrySet()) {
            String target = entry.getKey();
            String replacement = entry.getValue();

            replacement = replacementMapper.apply(replacement);

            value = value.replace(target, replacement);
        }

        return value;
    }
}