package app.agents;


import app.Helper;
import app.RoomEnum;
import jade.core.*;
import jade.core.Runtime;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.wrapper.AgentContainer;
import jade.wrapper.StaleProxyException;
import org.ajbrown.namemachine.NameGenerator;
import org.joda.time.LocalTime;

import java.util.*;

public class WorldAgent extends Agent {

    private int peopleInWorld;
    private AgentContainer peopleContainer;


    @Override
    protected void setup() {

        Helper.registerInDFService(this, "mr-world-wide");

        //peoplecontainer
        Profile pee = new ProfileImpl();
        pee.setParameter(Profile.CONTAINER_NAME, "Pee container");
        peopleContainer = Runtime.instance().createAgentContainer(pee);

        addBehaviour(new InitBehaviour());
        addBehaviour(new TimeBehaviour(this, 500));
        addBehaviour(new OfferRequestServer());
        addBehaviour(new PersonSpawnBehaviour(this, 1000));

        super.setup();
    }

    private LocalTime time = new LocalTime(0, 0);

    class InitBehaviour extends OneShotBehaviour {

        private Map<RoomEnum, RoomAgent> roomMap = new HashMap<>();

        @Override
        public void action() {
            Profile profile = new ProfileImpl();
            profile.setParameter(Profile.CONTAINER_NAME, "Rooms");

            jade.wrapper.AgentContainer roomContainer = Runtime.instance().createAgentContainer(profile);

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

        private void defineRooms(Map<RoomEnum, RoomAgent> roomMap) {
            Set<AID> aids;
            for (Map.Entry<RoomEnum, RoomAgent> room : roomMap.entrySet()) {
                switch (room.getKey()) {
                    case LOBBY:
                        aids = new HashSet<>();
                        aids.add(roomMap.get(RoomEnum.TOILET).getAID());
                        aids.add(roomMap.get(RoomEnum.LIVING).getAID());
                        room.getValue().setRoomList(aids);
                        break;
                    case BED:
                        aids = new HashSet<>();
                        aids.add(roomMap.get(RoomEnum.LIVING).getAID());
                        room.getValue().setRoomList(aids);
                        break;
                    case LIVING:
                        aids = new HashSet<>();
                        aids.add(roomMap.get(RoomEnum.BED).getAID());
                        aids.add(roomMap.get(RoomEnum.LOBBY).getAID());
                        room.getValue().setRoomList(aids);
                        break;
                    case TOILET:
                        aids = new HashSet<>();
                        aids.add(roomMap.get(RoomEnum.LOBBY).getAID());
                        room.getValue().setRoomList(aids);
                        break;
                }
            }
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

    class OfferRequestServer extends CyclicBehaviour {

        @Override
        public void action() {
            MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
            ACLMessage msg = myAgent.receive(mt);
            if (msg != null) {
                String title = msg.getContent();

                ACLMessage reply = msg.createReply();
                reply.setPerformative(ACLMessage.INFORM);

                switch (title) {
                    case "lux":
                        //System.out.println("Mam v pici lux.");
                        reply.setContent(time.hourOfDay().getAsText());
                        break;
                    case "temp":
                        //System.out.println("Mam v pici temp.");
                        reply.setContent(time.minuteOfHour().getAsText());
                        break;
                    case "rain":
                        //System.out.println("Mam v pici rain.");
                        float number = (float) (0.0 + (100.0) * (new Random().nextFloat()));
                        reply.setContent(String.format("%.2f", number));   // random float between 0.0 and 100.0
                        break;
                }
                myAgent.send(reply);
            } else {
                block();
            }
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
