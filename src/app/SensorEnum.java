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


    @Override
    public String toString() {
        return this.name;
    }
}
