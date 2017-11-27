package app;

public enum RoomEnum {
    LOBBY("lobby-room", 6, (float) 18.0),
    LIVING("living-room", 10, (float) 23.0),
    BED("bed-room", 3, (float) 21.0),
    TOILET("toilet-room", 1, (float) 26.0);


    private final String name;
    private int peopleCapacity;
    private float temperature;

    RoomEnum(String s, int capacity, float temp) {
        this.name = s;
        this.peopleCapacity = capacity;
        this.temperature = temp;
    }

    @Override
    public String toString() {
        return this.name;
    }

    public int getPeopleCapacity() {
        return peopleCapacity;
    }

    public float getTemperature() {
        return temperature;
    }
}
