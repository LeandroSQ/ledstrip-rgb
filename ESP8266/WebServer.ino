/* Setup method */
inline void setupWiFi() {
    logLine("Setting up WiFi...");

    // Configures the connection
    WiFi.config(WIFI_DHCP_IP, WIFI_GATEWAY_IP, WIFI_SUBNET_MASK, WIFI_DNS_IP);

    // Starts the wifi connection
    WiFi.mode(WIFI_STA);
    wifi_set_sleep_type(LIGHT_SLEEP_T);
    WiFi.begin(WIFI_SSID, WIFI_PASS);

    uint8_t timer = 0;
    hue = 0;

    while (WiFi.status() != WL_CONNECTED) {
        timer ++;

        for (uint8_t i = 0; i < 58; i++) {
            animateConnectionEstablished();
            delay(17);
            yield();
        }

        if (timer >= 20) {
            log("WiFi status:");
            logLine(WiFi.status());
            logLine("Unable to establish connection!");

            // Clear the LEDs
	        for (int i = 0; i < LED_COUNT; i++) leds[i] = CRGB::Red;
            FastLED.show();

            delay(25);

            ESP.restart();
        }
    }

    hue = 0;

    log("Connected to ");
    logLine(WiFi.SSID());
    log("Local address: ");
    logLine(WiFi.localIP());
    log("MAC Address: ");
    logLine(WiFi.macAddress());
}

inline void setupWebServer() {
    logLine("Starting web server...");
    if (MDNS.begin("esp8266")) {
        logLine("mDNS responder started!");
    } else {
        logLine("Unable to start mDNS responder!");
    }

    server.on("/", HTTP_GET, onRequestRoot);
    server.on("/brightness", HTTP_POST, onRequestSetBrightness);
    server.on("/brightness", HTTP_GET, onRequestGetBrightness);
    server.on("/power_toggle", HTTP_POST, onRequestPowerToggle);
    server.on("/speed", HTTP_POST, onRequestSetSpeed);
    server.on("/speed", HTTP_GET, onRequestGetSpeed);
    server.on("/color", HTTP_POST, onRequestSetColor);
    server.on("/animation", HTTP_POST, onRequestSetAnimation);
    server.begin();

    log("WebServer started at port ");
    logLine(WEBSERVER_PORT);
}

void animateConnectionEstablished() {
    hue += 0.15f * 2.1f;
    uint8_t b = 100;

    if (hue < 25.0f) {
        b += hue;
    } else if (hue < 50.0f) {
        b -= (hue - 25.0f);
    } else {
        hue = 0;
    }

    for (uint8_t i = 0; i < TABLE_LENGTH; i++) {
        leds[i + TABLE_START].setHSV(150, 255, b);
    }

    FastLED.show();
}

/* WebServer resquest handling */
void onRequestRoot() {
    server.send(200, "text/html", ROOT_HTML);
}

inline String handleValueSetRequest() {
    if (server.hasArg("value")) {
        server.send(200, "text/html", "OK");
        return server.arg("value");
    } else {
        server.send(422, "text/html", "Missing 'value'");
    }
}

// POST /brightness
void onRequestSetBrightness() {
    log("POST /brightness = ");

    String value = handleValueSetRequest();
    logLine(value);
    if (value) {
        target_brightness = (uint8_t) value.toInt();

#ifdef FIREBASE_ENABLED
        setSettingUint8("/settings/brightness", target_brightness);
#endif
    }
}

// GET /brightness
void onRequestGetBrightness() {
    log("GET /brightness = ");

    server.send(200, "text/html", String((int) target_brightness).c_str());
}

// POST /power_toggle
void onRequestPowerToggle() {
    log("POST /power_toggle");

    if (target_brightness <= 10) {
        target_brightness = 0;
    } else {
        target_brightness = 128;
    }

#ifdef FIREBASE_ENABLED
    setSettingUint8("/settings/brightness", target_brightness);
#endif
}

// POST /speed
void onRequestSetSpeed() {
    log("POST /speed = ");

    String value = handleValueSetRequest();
    logLine(value);
    if (value) {
        speed = value.toFloat();

#ifdef FIREBASE_ENABLED
        setSettingFloat("/settings/speed", speed);
#endif
    }
}

// GET /speed
void onRequestGetSpeed() {
    log("GET /speed = ");

    server.send(200, "text/html", String(speed).c_str());
}

// POST /color
void onRequestSetColor() {
    log("POST /color = ");

    String value = handleValueSetRequest();
    logLine(value);
    if (value) {
        // Convert hex string to CRGB
        color = CRGB((uint32_t) strtol(value.c_str(), NULL, 16));

        // Hue is used as an internal counter, setting as 100 will engage it to change the color
        if (currentAnimation == 2) hue = 100;

#ifdef FIREBASE_ENABLED
        setSettingInt("/settings/color", getColorCode(color));
#endif
    }
}

// POST /animation
void onRequestSetAnimation() {
    log("POST /animation = ");

    String value = handleValueSetRequest();
    logLine(value);
    if (value) {
        // Clears the LED strip before changing animations
        clearLED();

        currentAnimation = (uint8_t) value.toInt();
        brightness = 0;

        // Hue is used as an internal counter on animateSolidColor(), setting as 100 will engange it to change the color
        if (currentAnimation == 2) hue = 100;
        else hue = 0;

#ifdef FIREBASE_ENABLED
        setSettingUint8("/settings/animation", currentAnimation);
#endif
    }
}