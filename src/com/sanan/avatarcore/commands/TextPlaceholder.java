package com.sanan.avatarcore.commands;

class TextPlaceholder {
    private final String oldValue;
    private final String newValue;

    private TextPlaceholder(String oldValue, String newValue) {
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    public static TextPlaceholder of(String oldValue, String newValue) {
        return new TextPlaceholder(oldValue, newValue);
    }

    protected String getOldValue() {
        return this.oldValue;
    }

    protected String getReplacement() {
        return this.newValue;
    }
}
