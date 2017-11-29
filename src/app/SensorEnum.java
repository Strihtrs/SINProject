package app;

public enum SensorEnum {
    LUX("lux-sensor"),
    TEMPERATURE("temp-sensor"),
    MOTION("motion-sensor"),
    RAIN("rain-sensor");


    private final String name;

    SensorEnum(String s) {
        this.name = s;
    }

    public static SensorEnum getByName(String name) {
        if (LUX.toString().equals(name)) {
            return LUX;
        }
        if (TEMPERATURE.toString().equals(name)) {
            return TEMPERATURE;
        }
        if (MOTION.toString().equals(name)) {
            return MOTION;
        }
        if (RAIN.toString().equals(name)) {
            return RAIN;
        }
        return null;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
