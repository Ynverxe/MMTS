package com.github.ynverxe.mmts.core;

public abstract class AbstractMMTSModule implements MMTSModule {

    private volatile MMTSHandler mmtsHandler;

    @Override
    public void setMMTSHandler(MMTSHandler mmtsHandler) {
        this.mmtsHandler = mmtsHandler;
    }

    @Override
    public MMTSHandler getMMTSHandler() {
        return mmtsHandler;
    }
}