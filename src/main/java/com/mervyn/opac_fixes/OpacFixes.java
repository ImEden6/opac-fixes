package com.mervyn.opac_fixes;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OpacFixes implements ModInitializer {
    public static final String MOD_ID = "opac_fixes";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("Initializing OPAC Fixes!");
    }
}
