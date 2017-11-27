package app.agents;

public enum RoomEnum {
    LOBBY("lobby-room"),
    LIVING("living-room"),
    BED("bed-room"),;


    private final String name;

    RoomEnum(String s) {
        this.name = s;
    }


    @Override
    public String toString() {
        return this.name;
    }
}
