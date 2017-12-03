package app.agents;


import app.Helper;
import app.RoomEnum;
import jade.core.*;
import jade.core.Runtime;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.wrapper.AgentContainer;
import jade.wrapper.StaleProxyException;
import org.ajbrown.namemachine.NameGenerator;
import org.joda.time.LocalTime;

import java.util.*;

public class WorldAgent extends RoomAgent {

    private int peopleInWorld;

    public static float worldTemp;
    private AgentContainer peopleContainer;

    public WorldAgent(RoomEnum roomEnum) {
        super(roomEnum);
    }

    public WorldAgent() {
        super(null);
    }


    @Override
    protected void setup() {

        Helper.registerInDFService(this, "mr-world-wide");

        //peoplecontainer
        Profile pee = new ProfileImpl();
        pee.setParameter(Profile.CONTAINER_NAME, "Pee container");
        peopleContainer = Runtime.instance().createAgentContainer(pee);

        addBehaviour(new InitBehaviour());
        addBehaviour(new TimeBehaviour(this, 2000));
        //addBehaviour(new OfferRequestServer());
        addBehaviour(new PersonSpawnBehaviour(this, 1000));

        super.setup();
    }

    private LocalTime time = new LocalTime(0, 0);

    public LocalTime getTime() {
        return time;
    }

    class InitBehaviour extends OneShotBehaviour {

        private Map<RoomEnum, RoomAgent> roomMap = new HashMap<>();

        @Override
        public void action() {
            Profile profile = new ProfileImpl();
            profile.setParameter(Profile.CONTAINER_NAME, "Rooms");

            AgentContainer roomContainer = Runtime.instance().createAgentContainer(profile);

            try {
                for (RoomEnum e : RoomEnum.values()) {
                    RoomAgent a = new RoomAgent(e);
                    roomContainer.acceptNewAgent(e.toString(), a)
                            .start();
                    Helper.registerInDFService(a, e.toString());
                    roomMap.put(e, a);
                }
                defineRooms(roomMap);
            } catch (StaleProxyException e) {
                e.printStackTrace();
            }
        }

        private <T extends BaseSensorAgent> void createSensor(Class<T> clazz, AgentContainer container, String name, AID roomAid, int IDX) {

            T sensorAgent;
            try {
                sensorAgent = clazz.newInstance();
                sensorAgent.setRoomAID(roomAid);
                sensorAgent.setIDX(IDX);
                container.acceptNewAgent(name, sensorAgent)
                        .start();
                Helper.registerInDFService(sensorAgent, sensorAgent.toString());

            } catch (InstantiationException | IllegalAccessException | StaleProxyException e) {
                e.printStackTrace();
            }
        }

        private void defineRooms(Map<RoomEnum, RoomAgent> roomMap) {
            Set<AID> aids;

            Profile profile = new ProfileImpl();
            profile.setParameter(Profile.CONTAINER_NAME, "Sensors");
            AgentContainer sensorsContainer = Runtime.instance().createAgentContainer(profile);

            for (Map.Entry<RoomEnum, RoomAgent> room : roomMap.entrySet()) {
                aids = new HashSet<>();
                AID roomAID = room.getValue().getAID();

                switch (room.getKey()) {
                    case LOBBY:
                        aids.add(roomMap.get(RoomEnum.TOILET).getAID());
                        aids.add(roomMap.get(RoomEnum.LIVING).getAID());

                        room.getValue().setHeatingIDX(18);

                        createSensor(MotionSensor.class, sensorsContainer, "MotionLobby", roomAID, 13);
                        createSensor(TempSensorAgent.class, sensorsContainer, "TempLobby", roomAID, 4);

                        break;
                    case BED:
                        aids.add(roomMap.get(RoomEnum.LIVING).getAID());

                        room.getValue().setHeatingIDX(20);

                        createSensor(MotionSensor.class, sensorsContainer, "MotionBed", roomAID, 10);
                        createSensor(TempSensorAgent.class, sensorsContainer, "TempBed", roomAID, 7);

                        break;
                    case LIVING:
                        aids.add(roomMap.get(RoomEnum.BED).getAID());
                        aids.add(roomMap.get(RoomEnum.LOBBY).getAID());

                        room.getValue().setHeatingIDX(19);

                        createSensor(MotionSensor.class, sensorsContainer, "MotionLiving", roomAID, 11);
                        createSensor(TempSensorAgent.class, sensorsContainer, "TempLiving", roomAID, 5);

                        break;
                    case TOILET:
                        aids.add(roomMap.get(RoomEnum.LOBBY).getAID());

                        room.getValue().setHeatingIDX(21);

                        createSensor(MotionSensor.class, sensorsContainer, "MotionToilet", roomAID, 12);
                        createSensor(TempSensorAgent.class, sensorsContainer, "TempToilet", roomAID, 6);
                        break;
                }

                room.getValue().setRoomList(aids);
            }

            createSensor(LuxSensorAgent.class, sensorsContainer, "LuxW", myAgent.getAID(), 1);
            createSensor(TempSensorAgent.class, sensorsContainer, "TempW", myAgent.getAID(), 3);
        }
    }

    class TimeBehaviour extends TickerBehaviour {

        private WorldAgent worldAgent;

        TimeBehaviour(WorldAgent worldAgent, long period) {

            super(worldAgent, period);
            this.worldAgent = worldAgent;
        }

        @Override
        public void onTick() {
            worldAgent.time = time.plusMinutes(28);
        }
    }

    class PersonSpawnBehaviour extends TickerBehaviour {

        public PersonSpawnBehaviour(Agent a, long period) {
            super(a, period);
        }

        @Override
        protected void onTick() {
            if (peopleInWorld < 2 && new Random().nextFloat() > 0.5) {
                try {
                    peopleContainer
                            .createNewAgent(new NameGenerator().generateName().getFirstName() + time.millisOfDay().getAsText(), PersonAgent.class.getCanonicalName(), null)
                            .start();
                    peopleInWorld++;
                } catch (StaleProxyException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
