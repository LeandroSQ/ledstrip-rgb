/* Library configurations */
#define FASTLED_ESP8266_RAW_PIN_ORDER
#define FASTLED_INTERRUPT_RETRY_COUNT 1
#define FASTLED_ALLOW_INTERRUPTS 0

/* Include */
#include <FastLED.h>
#include <Arduino.h>
#include <ESP8266WebServer.h>
#include <ESP8266mDNS.h>
#include <ESP8266WiFi.h>
#include <ESP8266HTTPClient.h>
#include <WiFiClient.h>
#include <WiFiUdp.h>
#include <ArduinoOTA.h>
#include <RemoteDebug.h>

#define DEBUG_SERIAL
//#define WEB_DEBUG

#include "Configuration.h"

/* WebServer configuration */
ESP8266WebServer server(WEBSERVER_PORT);

/* Brightness */
uint8_t brightness = 0;
float raw_brightness = 0;
uint8_t target_brightness = 127;

/* LED strip configuration */
CRGB leds[LED_COUNT];
float* buffer = nullptr;
float hue = 0.0f;
float speed = 2.1f;
CRGB color = CRGB::Red;//CRGB::Black;
uint8_t currentAnimation = 0;

/* Global variables */
uint8_t sleepFor = 0;
uint8_t timer = 0;
unsigned long lastAnimationUpdate = 0;
unsigned long lastServerUpdate = 0;
boolean running = true;

/* Setup methods */
void setup() {
	// Starts the serial communication
#ifdef DEBUG_SERIAL
	Serial.begin(115200);
#else
	pinMode(1, FUNCTION_3);// TX as GPIO
	pinMode(3, FUNCTION_3);// RX as GPIO
#endif

	logLine("Starting up...");

	// Setup the LED strip
	setupLedStrip();

	// Setup the wifi communication
	setupWiFi();
	// Setup the OTA protocol
	setupOTA();

#ifdef WEB_DEBUG
	setupWebDebug();
#endif

	// Setup the webserver
	setupWebServer();

#ifdef FIREBASE_ENABLED
	// Setup firebase
	setupFirebase();
#endif

	logLine("Ready!");
}

#ifdef WEB_DEBUG
inline void setupWebDebug() {
	Debug.begin(String("0.0.0.0"));
	Debug.setSerialEnabled(true);
	logLine("Remote debug initialized!");
}
#endif

inline void setupLedStrip() {
	logLine("Setting up LED strip...");

	FastLED.addLeds<WS2811, DATA_PIN, BRG>(leds, LED_COUNT).setCorrection(TypicalLEDStrip);
	FastLED.setMaxPowerInVoltsAndMilliamps(3.3f, 1000);

	// Clear the LEDs
	clearLED();
}

void clearLED() {
	// Clear the LEDs
	for (int i = 0; i < LED_COUNT; i++) leds[i] = CRGB::Black;
	FastLED.show();
}

/* Loop methods */
void loop() {
	unsigned long now = millis();
	uint8_t invalidated = false;

	if (now - lastAnimationUpdate >= sleepFor) {
		runAnimations();
		lastAnimationUpdate = millis();
		invalidated = true;
	} else delay(1);

	if (now - lastServerUpdate >= 500) {
		lastServerUpdate = millis();
		updateServer();
		invalidated = true;
	}

	yield();
}

inline void updateServer() {
	if (WiFi.status() != WL_CONNECTED) {
		digitalWrite(1, HIGH);
		logLine("Reconnecting to WiFi!");

		WiFi.disconnect();
		WiFi.reconnect();

		lastServerUpdate = millis() + 200000;
		return;
	} else {
		digitalWrite(0, HIGH);
	}

#ifdef WEB_DEBUG
	Debug.handle();
#endif

	// Handle any incoming OTA request
	ArduinoOTA.handle();

	// Handle any incoming HTTP request
	server.handleClient();
}

inline void runAnimations() {
	// Doesn't run animations when the LED strip is off
	if (target_brightness > 0 || (uint8_t) raw_brightness != target_brightness) {
		// Animate brightness changes
		animateBrightness();

		// Animate the LED strip
		switch(currentAnimation) {
			case 0: animateRainbowCycle(); 	break;
			case 1: animateRainbow(); 		break;
			case 2: animateSolidColor(); 	break;
			case 3: animateKnightRider(); 	break;
			case 4: animateSparkle(); 		break;
			case 5: animatePulse(); 		break;
			case 6: animateFire(); 			break;
		}
	} else {
		clearLED();
		sleepFor = 64;
	}
}

void animateBrightness() {
	// Calculate the difference between the two
	float diff = target_brightness - raw_brightness;
	// Define the linear step to be applied on to
	float threshold = 0.5f * speed;

	// Apply the linear tranformation
	if (diff < -threshold) raw_brightness += -threshold;
	else if (diff > threshold) raw_brightness += threshold;
	else raw_brightness = target_brightness;

	// Flattens the brightness, because the control of it is not linear
	// More likely to be a Ascending f(x) = x^2
	// Therefore by adding a square root of it, it flattens it making the brightness curve of the LED linear
	brightness = (uint8_t) (sqrt(raw_brightness / 255.0) * 255.0f);
}
