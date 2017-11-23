package app.agents;

import jade.core.Agent;

import java.util.ArrayList;
import java.util.List;

public class RoomAgent extends Agent {

    private List<BaseSensorAgent> sensorList;
    private List<PersonAgent> personList;
    private List<RoomAgent> roomList;

    public RoomAgent() {
        sensorList = new ArrayList<>();
        personList = new ArrayList<>();
        roomList = new ArrayList<>();
    }


}
