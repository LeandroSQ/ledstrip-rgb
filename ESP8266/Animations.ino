/* Cycle between the rainbow colors, all LEDs at once */
void animateRainbowCycle() {
    hue += 0.15f * speed;
    uint8_t pixel = (uint8_t) hue;
    if (pixel >= 256) {
        hue = 0;
        pixel = 0;
    }

    for (uint8_t i = 0; i < TABLE_LENGTH; i++) leds[i + TABLE_START].setHSV(pixel, 255, brightness);

    FastLED.show();
    sleepFor = 33;
}

/* Fill every LED with a color in a rainbow pattern, only TABLE_START to TABLE_END*/
void animateRainbow() {
    hue += 0.15f * speed * 3;

    float offset = 234 / (TABLE_LENGTH);

    for (uint8_t i = 0; i < TABLE_LENGTH; i++) {
        uint8_t pixel = ((uint16_t) (hue + i * offset)) % 255;
        pixel = (uint8_t) (powf(pixel / 255.0f, 1.5f) * 255.0f);

        leds[i + TABLE_START].setHSV(pixel, 255, brightness);
    }

    FastLED.show();
    sleepFor = 25;
}

/* Fills all the LEDs with only one color */
void animateSolidColor() {
    hue ++;

    if (hue >= 20) {
        for (uint8_t i = 0; i < TABLE_LENGTH; i++) leds[i + TABLE_START] = color;

        FastLED.setBrightness(brightness);

        FastLED.show();

        sleepFor = 17;
    } else {
        sleepFor = 33;
    }
}

void animateSparkle() {
    hue += 0.15f * speed * 2;
    uint8_t count = 0;
    uint8_t threshold = 33;
    uint8_t scale_down_factor = (uint8_t) (170.0f + 84.0f * (1 - speed / 10.0f));

    for (uint8_t i = 0; i <= TABLE_LENGTH; i++) {
        uint8_t index = i + TABLE_START;

        // Fade out all the LEDS
        leds[index].nscale8(scale_down_factor);

        // Count black LEDs
        if (leds[index].r <= threshold && leds[index].g <= threshold && leds[index].b <= threshold) count ++;
    }

    // If by a chance of 5%
    // Or less than half of the LEDs are lit, spawn a new sparkle
    if (random8() <= 5 || (TABLE_LENGTH - count) < TABLE_LENGTH / 2) {
        uint8_t index = random8(0, TABLE_LENGTH + 1);

        if (leds[index].r <= threshold && leds[index].g <= threshold && leds[index].b <= threshold) {
            leds[index + TABLE_START] = applyBrightness(color, brightness);
        }
    }

    FastLED.show();
    sleepFor = 33;
}

void animateKnightRider() {
    hue += 0.15f * speed;
    uint8_t index = (uint8_t) hue;

    // Resets when went forward and backwards
    if (index > TABLE_LENGTH * 2) {
        hue = 0;
        index = 0;
    }

    // Fade to black by 10 scales
    for (uint8_t i = 0; i <= TABLE_LENGTH; i++) {
        leds[i + TABLE_START].nscale8(170);
    }

    if (index < TABLE_LENGTH) {
        // LEFT TO RIGHT
        leds[index + TABLE_START] = applyBrightness(color, brightness);
    } else {
        // RIGHT TO LEFT
        leds[(TABLE_LENGTH) - (index - TABLE_LENGTH) + TABLE_START] = applyBrightness(color, brightness);
    }

    // Set the overall brightness
    FastLED.setBrightness(brightness);

    FastLED.show();
    sleepFor = 33;
}

void animatePulse() {
    hue += 0.15f * speed;
    uint8_t index = (uint8_t) hue;
    uint8_t center = TABLE_LENGTH / 2;

    // Fade to black by 10 scales
    for (uint8_t i = 0; i <= TABLE_LENGTH; i++) leds[i + TABLE_START].nscale8(170);

    if (index >= TABLE_LENGTH) {
        hue = 0;
        index = 0;
    }

    if (index >= center) {
        /* index = center - (index - center);
        sleepFor = (uint8_t) ((((float) center - index) / ((float) center)) * 22.0f + 11.0f); */
        sleepFor = 17 * speed;
    } else {
        uint8_t a = (center + index) + TABLE_START;
        leds[a] = applyBrightness(color, brightness);

        uint8_t b = TABLE_END - (center + index);
        leds[b] = applyBrightness(color, brightness);
        sleepFor = (uint8_t) (22.0f * ((hue / (TABLE_LENGTH / 2.0f)))) + 11;
    }




    FastLED.show();
}

void animateFire() {
    // Outlines RED 0
    // Middle ORANGE 32
    // Inner YELLOW 64
    if (hue == 0) {
        buffer = new float[TABLE_LENGTH];
        for (uint8_t i = 0; i < TABLE_LENGTH; i++) buffer[i] = random8() / 255.0f;
        hue = 1;
    }

    for (uint8_t i = 0; i < TABLE_LENGTH; i++) {
        uint8_t index = TABLE_START + i;
        float value = buffer[i];
        uint8_t h = (uint8_t) (value * 50);
        uint8_t b = (uint8_t) ((value < 0.5f ? 0.5f : value) * brightness);

        leds[index].setHSV(h, 255, b);

        buffer[i] += (random8() / 255.0f - 0.5f) * 0.15f * speed;
        if (buffer[i] > 1.0f) buffer[i] = 1.0f;
        else if (buffer[i] < 0.0f) buffer[i] = 0.0f;

    }

    FastLED.show();
    sleepFor = 33;
}

CRGB applyBrightness(CRGB color, uint8_t brightness) {
    // Manually apply brightness, otherwise it just does not respect the brightness overall value
    float brightness_factor = (float)brightness / 255.0f;
    return CRGB(
        (uint8_t)(color.r * brightness_factor),
        (uint8_t)(color.g * brightness_factor),
        (uint8_t)(color.b * brightness_factor)
    );
}

/***
 Approximation of the SIN function

 @param hue 0 to 255
 @returns A RGB color corresponding to the Color wheel at the given position
***/
CRGB wheel(uint8_t hue) {
    if (hue < 85) {
        return CRGB(255 - hue * 3, 0, hue * 3);
    } else if (hue < 170) {
        hue -= 85;
        return CRGB(0, hue * 3, 255 - hue * 3);
    } else {
        hue -= 170;
        return CRGB(hue * 3, 255 - hue * 3, 0);
    }
}