package com.arcanum.client;

import com.arcanum.mana.ManaClientData;
import com.arcanum.mana.ManaNetworking;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;

@Environment(EnvType.CLIENT)
public class ArcanumClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(ManaNetworking.SYNC_ID, (client, handler, buf, responseSender) -> {
            float mana = buf.readFloat();
            float max = buf.readFloat();
            client.execute(() -> {
                ManaClientData.mana = mana;
                ManaClientData.max = max;
            });
        });

        ManaHud.register();
        GuideOpener.handler = () -> MinecraftClient.getInstance().setScreen(new GuideScreen());
    }
}
