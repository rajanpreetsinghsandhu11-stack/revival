package com.rajanpreet.revival.client;

import com.rajanpreet.revival.ModRegistry;
import com.rajanpreet.revival.client.render.TheRevivedRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

public class RevivalClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.register(ModRegistry.THE_REVIVED, TheRevivedRenderer::new);
    }
}
