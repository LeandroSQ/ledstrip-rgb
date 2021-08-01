#ifdef FIREBASE_ENABLED
inline void setupFirebase() {
	logLine("Starting firebase connection protocol...");

    firebase_auth.user.email = FIREBASE_AUTH_EMAIL;
    firebase_auth.user.password = FIREBASE_AUTH_PASSWORD;

    firebase_config.api_key = FIREBASE_API_KEY;
    firebase_config.database_url = FIREBASE_HOST;
    firebase_config.token_status_callback = [](TokenInfo info) {
        if (info.status == token_status_error) {
            logLine("Firebase token error!");
            log("Code: ");
            log(info.error.code);
            log(" | ");
            logLine(info.error.message.c_str());
        } else {
            log("Token type: ");
            log(getTokenType(info));
            log(" status: ");
            logLine(getTokenStatus(info));
        }
    };

    Firebase.begin(&firebase_config, &firebase_auth);
    fbdo.setBSSLBufferSize(512, 2048);

    while (!Firebase.ready()) {
        yield();
        delay(100);
    }

	setSettingInt("/test/piranha", getSettingInt("/test/piranha") + 1);

	logLine("Firebase setup done!");

    loadSettings();
}

inline String getTokenType(struct token_info_t info) {
    switch (info.type) {
        case token_type_undefined: return "undefined";
        case token_type_legacy_token: return "legacy token";
        case token_type_id_token: return "id token";
        case token_type_custom_token: return "custom token";
        case token_type_oauth2_access_token: return "OAuth2.0 access token";
        default: return "undefined";
    }

}

inline String getTokenStatus(struct token_info_t info) {
    switch (info.status) {
        case token_status_uninitialized: return "uninitialized";
        case token_status_on_initialize: return "on initializing";
        case token_status_on_signing: return "on signing";
        case token_status_on_request: return "on request";
        case token_status_on_refresh: return "on refreshing";
        case token_status_ready: return "ready";
        case token_status_error: return "error";
        default: return "uninitialized";
    }
}

inline void loadSettings() {
    target_brightness = getSettingUint8("/settings/brightness");
    current_animation = getSettingUint8("/settings/animation");
    color = CRGB(getSettingInt("/settings/color"));
    speed = getSettingFloat("/settings/speed");
}

void updateSettings() {
    setSettingUint8("/settings/brightness", target_brightness);
    setSettingUint8("/settings/animation", current_animation);
    setSettingInt("/settings/color", getColorCode(color));
    setSettingFloat("/settings/speed", speed);
}

inline uint32_t getColorCode(CRGB color) {
    return color.r << 16 | color.g << 8 | color.b;
}

inline char* concat(char* a, char* b) {
    char* c = (char*) malloc(strlen(a) + strlen(b));
    strcpy(c, a);
    strcat(c, b);
    return c;
}

inline uint8_t getSettingUint8(char* path) {
    return (uint8_t) getSettingInt(path);
}

int getSettingInt(char* path) {
    int target;

    if (Firebase.RTDB.getInt(&fbdo, path)) return fbdo.intData();
    else {
        logLine(fbdo.errorReason());

        return -1;
    }
}

float getSettingFloat(char* path) {
    float target;

    if (Firebase.RTDB.getFloat(&fbdo, path)) return fbdo.floatData();
    else {
        logLine(fbdo.errorReason());

        return -1.0f;
    }
}

bool setSettingUint8(char* path, uint8_t value) {
    if (Firebase.RTDB.setIntAsync(&fbdo, path, value)) {
        return true;
    } else {
        logLine(fbdo.errorReason());

        return false;
    }
}

bool setSettingInt(char* path, int value) {
    if (Firebase.RTDB.setIntAsync(&fbdo, path, value)) {
        return true;
    } else {
        logLine(fbdo.errorReason());

        return false;
    }
}

bool setSettingFloat(char* path, float value) {
    if (Firebase.RTDB.setFloatAsync(&fbdo, path, value)) {
        return true;
    } else {
        logLine(fbdo.errorReason());

        return false;
    }
}

#endif