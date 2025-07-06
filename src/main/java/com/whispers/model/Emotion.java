package com.whispers.model;

public enum Emotion {
    MELANCHOLY("melancholy"),
    JOY("joy"),
    ANXIETY("anxiety"),
    PEACE("peace");

    private final String value;

    Emotion(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static Emotion fromValue(String value) {
        for (Emotion emotion : Emotion.values()) {
            if (emotion.value.equalsIgnoreCase(value)) {
                return emotion;
            }
        }
        throw new IllegalArgumentException("Invalid emotion: " + value);
    }
}