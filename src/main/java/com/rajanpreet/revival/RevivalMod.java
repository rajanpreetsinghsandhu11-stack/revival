package com.rajanpreet.revival;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RevivalMod implements ModInitializer {
    public static final String MOD_ID = "revival";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("Revival initialized.");
    }
}
