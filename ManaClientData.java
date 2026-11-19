package com.arcanum.mana;

/** Client-side copy of the player's mana (filled by the sync packet). */
public final class ManaClientData {
    public static float mana = 0.0F;
    public static float max = 100.0F;

    private ManaClientData() {
    }
}
