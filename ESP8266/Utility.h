/* Utility */
#define ConvertToRad(degres) (degres) * DEG_TO_RAD
#define sign(a) (a > 0 ? 1 : (a < -1 ? -1 : 0))

/* Debug logs */
#if defined(DEBUG_SERIAL)
    #define logLine(...) Serial.println(__VA_ARGS__)
    #define log(...) Serial.print(__VA_ARGS__)
    #define logf(...) Serial.printf(__VA_ARGS__)
#elif defined(WEB_DEBUG)
    RemoteDebug Debug;
    #define logLine(...) Debug.println(__VA_ARGS__)
    #define log(...) Debug.print(__VA_ARGS__)
    #define logf(...) Debug.printf(__VA_ARGS__)
#else
    #define logLine(...) /* x */
    #define log(...)     /* x */
    #define logf(...)    /* x */
#endif