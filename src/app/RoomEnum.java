package app;

public enum RoomEnum {
    LOBBY("lobby-room", 6),
    LIVING("living-room", 10),
    BED("bed-room", 3),
    TOILET("toilet-room", 1);


    private final String name;
    private int peopleCapacity;

    RoomEnum(String s, int capacity) {
        this.name = s;
        this.peopleCapacity = capacity;
    }



    @Override
    public String toString() {
        return this.name;
    }

    public int getPeopleCapacity() {
        return peopleCapacity;
    }
}
