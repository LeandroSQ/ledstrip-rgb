inline void setupOTA() {
    logLine("Setting up OTA updates...");

    ArduinoOTA.onStart([]() {
        running = false;
        logLine("Starting OTA...");
    });

    ArduinoOTA.onEnd([]() {
        logLine("OTA update finished!");
    });

    ArduinoOTA.onProgress([](unsigned int progress, unsigned int total) {
        logf("OTA progress: %u%%r", (progress / (total / 100)));
    });

    ArduinoOTA.onError([](ota_error_t error) {
        running = true;

        logf("-- OTA ERROR: [%u]", error);

        if (error == OTA_AUTH_ERROR)
            logLine("Authentication error");
        else if (error == OTA_BEGIN_ERROR)
            logLine("OTA begin error");
        else if (error == OTA_CONNECT_ERROR)
            logLine("Connect error");
        else if (error == OTA_RECEIVE_ERROR)
            logLine("Receive error");
        else if (error == OTA_END_ERROR)
            logLine("OTA end error");
    });

    ArduinoOTA.begin();
}
