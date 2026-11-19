package com.arcanum.client;

/** Lets common code (the Grimoire item) open the client-only guide screen safely. */
public final class GuideOpener {
    public static Runnable handler = () -> {
    };

    public static void open() {
        handler.run();
    }

    private GuideOpener() {
    }
}
