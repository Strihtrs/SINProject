package app.agents;

import app.RoomEnum;
import app.SensorEnum;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import org.joda.time.LocalTime;

import java.util.HashSet;
import java.util.Set;

public class RoomAgent extends Agent {

    private final RoomEnum roomEnum;
    private Set<AID> roomList;
    private int peopleCount;
    private boolean isHeatingOn = false;
    private float temperature = 10;
    private String heatingIDX;

    private boolean isInaccessible(String currentRoom) {
        boolean isAccessible = false;
        for (AID r : roomList) {
            if (r.getLocalName().equals(currentRoom)) {
                isAccessible = true;
                break;
            }
        }
        return peopleCount >= roomEnum.getPeopleCapacity() || !isAccessible;
    }

    public RoomEnum getRoomEnum() {
        return roomEnum;
    }

    public RoomAgent(RoomEnum roomEnum) {
        roomList = new HashSet<>();
        this.roomEnum = roomEnum;
    }

    public Set<AID> getNextRooms() {
        return roomList;
    }

    @Override
    protected void setup() {
        super.setup();

        addBehaviour(new PersonEntersRoomBehaviour());
        addBehaviour(new OfferSensorServer());
        addBehaviour(new PersonLeavesRoomBehaviour());
        addBehaviour(new TemperatureBehaviour(this, 800));
        addBehaviour(new HeatingBehaviour(this, 800));
    }

    public void setRoomList(Set<AID> roomList) {
        this.roomList = roomList;
    }

    public float getTemperature() {
        return temperature;
    }

    public void setTemperature(float temperature) {
        this.temperature = temperature;
    }

    class PersonLeavesRoomBehaviour extends CyclicBehaviour {

        @Override
        public void action() {
            MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
            ACLMessage msg = myAgent.receive(mt);

            if (msg != null && msg.getContent().contains("leaving")) {
                peopleCount--;
                System.out.println(myAgent.getLocalName() + " bye bye " + peopleCount);
            } else {
                block();
            }
        }
    }

    class PersonEntersRoomBehaviour extends CyclicBehaviour {

        @Override
        public void action() {

            MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.PROPOSE);
            ACLMessage msg = myAgent.receive(mt);

            if (msg != null && msg.getContent().contains("enter")) {

                String msgContent = msg.getContent();
                ACLMessage reply = msg.createReply();
                String currentRoom = msgContent.substring(msgContent.indexOf("_") + 1);

                if (currentRoom.equals("world") || !isInaccessible(currentRoom)) {
                    reply.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
                    System.out.println(myAgent.getLocalName() + " prijal " + msg.getSender().getLocalName());
                    peopleCount++;
                } else {
                    reply.setPerformative(ACLMessage.REJECT_PROPOSAL);
                    System.out.println(myAgent.getLocalName() + " zamitnul " + msg.getSender().getLocalName());
                }

                myAgent.send(reply);
            } else {
                block();
            }
        }
    }

    class TemperatureBehaviour extends TickerBehaviour {

        TemperatureBehaviour(Agent a, long period) {
            super(a, period);
        }

        @Override
        protected void onTick() {

            if (temperature < WorldAgent.worldTemp) {
                temperature += .5;
            } else if (temperature >= WorldAgent.worldTemp) {
                temperature -= .5;
            }
        }
    }

    class HeatingBehaviour extends TickerBehaviour {

        float heatingStep = 2;

        HeatingBehaviour(Agent a, long period) {
            super(a, period);
        }

        @Override
        protected void onTick() {
            if (isHeatingOn) {
                temperature += heatingStep;
            }
        }
    }

    class HeatingSwitchBehaviour extends CyclicBehaviour{
        @Override
        public void action() {

            String url = "http://localhost:8080/json.htm?type=devices&rid=" + heatingIDX;
            try {
                HttpResponse<JsonNode> json = Unirest.get(url).asJson();
                //if(json.getBody().getObject().getJSONArray("result").get(0))
            } catch (UnirestException e) {
                e.printStackTrace();
            }
        }
    }

    class OfferSensorServer extends CyclicBehaviour {

        private static final float sinStep = (float) (Math.PI * 2. / (24. * 60.));

        private int getMinutesOfDay() {
            WorldAgent world = (WorldAgent) myAgent;
            LocalTime time = world.getTime();
            return time.hourOfDay().get() * 60 + time.minuteOfHour().get();
        }

        @Override
        public void action() {
            MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
            ACLMessage msg = myAgent.receive(mt);
            if (msg != null) {
                String title = msg.getContent();

                ACLMessage reply = msg.createReply();
                reply.setPerformative(ACLMessage.INFORM);

                for (SensorEnum e : SensorEnum.values()) {
                    if (e.toString().equals(title)) {

                        switch (e) {
                            case LUX:
                                int lux = (int) (Math.sin(sinStep * getMinutesOfDay() - (sinStep * 6 * 60)) * 5000) + 5000;

                                reply.setContent(lux + "");
                                myAgent.send(reply);
                                break;
                            case TEMPERATURE:
                                if (myAgent.getLocalName().equals("World")) {

                                    ((WorldAgent) myAgent).setTemperature((float) (Math.sin(sinStep * getMinutesOfDay() - (sinStep * 6 * 60)) * 10) + 20);
                                    WorldAgent.worldTemp = ((WorldAgent) myAgent).getTemperature();

                                    reply.setContent(temperature + "");
                                } else {
                                    reply.setContent(((RoomAgent) myAgent).getTemperature() + "");
                                }
                                myAgent.send(reply);
                                break;
                            case RAIN:

                                break;
                            case MOTION:
                                reply.setContent((((RoomAgent) myAgent).peopleCount > 0) ? "On" : "Off");
                                myAgent.send(reply);
                                break;
                        }
                    }
                }
            } else {
                block();
            }
        }
    }
}
