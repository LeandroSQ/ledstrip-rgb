<h1 align="center">LED Strip RGB</h1>

<p align="center">
  <img src="./misc/banner blur.png"/>
</p>

<p align="center">Controlling WS8211 LED strip with ESP8266 and Android</p>

---

## The project

This project was made for controlling a WS8211 LED string using an ESP8266 MCU.
And to control the MCU an Android app (It can also be controlled by a simple web interface at port 80)

The project currently is under development, the goal being to add more screenshots, better documentation, unit testing, async requests on the firmware and so on...


### ESP8266

The code is availbale inside the [ESP8266 source code directory](./ESP8266)

Features:
- Uses Rx pin to interface with the LED strip, so no need for pullup resistors at GP0 or GP2
- Makes usage of the wonderful [FastLED](http://fastled.io/) library
- Automatically syncs configurations with [Firebase](https://firebase.google.com/) and on restart restore the last state
- Exposes a REST API, of which allows control over:
  - color
  - effect
  - brightness
  - power
  - speed
- Web interface at port 80
<img src="./misc/web interface.png" width="128"/>

- Remote debbuging via telnet
- OTA (Over The Air) firmware updates
- Configurable with define flags (On configuration.h, template to be released...)
- Multiple effects
  - Rainbow cycle
  - Rainbow
  - Solid color
  - Knight rider
  - Sparkle
  - Pulse
  - Fire

---

### Android

Features:
- Architecture
  - Kotlin
  - View state
  - Live data (To be replaced by flow)
  - View model
  - MVVM
  - Koin
  - Material components
  - Navigation component
  - Android Workers
  - Android App Shortcuts
- Light and Dark theming

<img src="./misc/app light.png" width="128"/> <img src="./misc/app dark.png" width="128"/>

- Color picker
<img src="./misc/app dark color picker.png" width="128"/>

- [Custom slider button](./Android/app/src/main/kotlin/quevedo/soares/leandro/ledstriprgb/view/component/SliderButtonComponent.kt)
  - A custom view designed for me to this project, it's used on the Home screen for controlling Brightness and Speed values
  - Supports view outlines
  - Supports finger index
  - Handle saved and restored instances
  - Auto animate changes
  - Drawing made from scratch using Canvas API
  - Touch handling with drag and tap detection
  - Antialiased
  - Optmized for performance
  - Customizable by code and xml properties (Styles also supported)



## Example configuration.h
```cpp
/* Web server config */
#define WIFI_SSID "..."
#define WIFI_PASS "..."
#define WIFI_DHCP_IP IPAddress(192, 168, 25, 1)
#define WIFI_GATEWAY_IP IPAddress(192, 168, 25, 1)
#define WIFI_SUBNET_MASK IPAddress(255, 255, 255, 0)
#define WIFI_DNS_IP IPAddress(192, 168, 25, 1)
#define WEBSERVER_PORT 80 /* Default http port */

/* Firebase config */
#define FIREBASE_ENABLED
#define FIREBASE_HOST "..."
#define FIREBASE_SSL_FINGERPRINT "..."
#define FIREBASE_API_KEY "..."
#define FIREBASE_AUTH_EMAIL "..."
#define FIREBASE_AUTH_PASSWORD "..."

/* LED config */
#define LED_COUNT 48
#define TX_PIN 1
#define RX_PIN 3
#define DATA_PIN 3
#define TABLE_START 3 /* Table meaning the LEDS to be controlled, if all of the LEDS are to be controlled, then simply TABLE_START = 0 and TABLE_END = LED_COUNT*/
#define TABLE_END 15
#define TABLE_LENGTH (TABLE_END - TABLE_START)

/* Animation config */
#define TICKS_TO_UPDATE 10 /* Define the amount of ticks to update OTA and the WebServer */
#define INITAL_BRIGHTNESS 170 /* Ranging 0 to 255 */

/* Debugging (optional) */
#define DEBUG_SERIAL
#define DEBUG_WEB
```