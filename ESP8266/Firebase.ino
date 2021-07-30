#ifdef FIREBASE_ENABLED
inline void setupFirebase() {
	logLine("Starting firebase connection protocol...");

	setSettingInt("/test/piranha", getSettingInt("/test/piranha") + 1);

	logLine("Firebase setup done!");

    loadSettings();
}

inline void loadSettings() {
    target_brightness = getSettingUint8("/settings/brightness");
    currentAnimation = getSettingUint8("/settings/animation");
    color = CRGB(getSettingInt("/settings/color"));
    speed = getSettingFloat("/settings/speed");
}

void updateSettings() {
    setSettingUint8("/settings/brightness", target_brightness);
    setSettingUint8("/settings/animation", currentAnimation);
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

String doFirebaseRequest(char* path, char* method, const char* payload) {
    HTTPClient http;

    char* url = concat(concat(FIREBASE_HOST, path), "/.json");

    http.begin(url, FIREBASE_SSL_FINGERPRINT);

    int statusCode = 0;

    if (method == "GET") {
        statusCode = http.GET();
    } else if (method == "PUT") {
        statusCode = http.PUT(payload);
        http.addHeader("Content-Type", "text/plain");
    }

    if (statusCode > 0) {
        log(method);
        log(" - ");
        log(url);
        log(" -> ");
        logLine(statusCode);

        String response = http.getString();

        return response;
    } else {
        log(method);
        log(" - ");
        log(url);
        log(" -> ERROR: ");
        logLine(http.errorToString(statusCode).c_str());
    }

    http.end();
}

inline uint8_t getSettingUint8(char* path) {
    return (int) getSettingInt(path);
}

int getSettingInt(char* path) {
    String response = doFirebaseRequest(path, "GET", NULL);
    if (response) return response.toInt();

    return -1;
}

float getSettingFloat(char* path) {
    String response = doFirebaseRequest(path, "GET", NULL);
    if (response) return response.toFloat();

    return -1;
}

bool setSettingUint8(char* path, uint8_t value) {
    String response = doFirebaseRequest(path, "PUT", String(value).c_str());
    return response != NULL;
}

bool setSettingInt(char* path, int value) {
    String response = doFirebaseRequest(path, "PUT", String(value).c_str());
    return response != NULL;
}

bool setSettingFloat(char* path, float value) {
    String response = doFirebaseRequest(path, "PUT", String(value).c_str());
    return response != NULL;
}

#endif