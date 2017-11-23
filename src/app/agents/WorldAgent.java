package app.agents;


import jade.core.Agent;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;
import org.ajbrown.namemachine.NameGenerator;
import org.joda.time.LocalTime;

import java.util.Random;

public class WorldAgent extends Agent {

    private int peopleInWorld;
    private jade.wrapper.AgentContainer peopleContainer;


    @Override
    protected void setup() {

        DFAgentDescription dfad = new DFAgentDescription();
        dfad.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType("world");
        sd.setName("mr-world-wide");
        dfad.addServices(sd);

        try {
            DFService.register(this, dfad);
        } catch (FIPAException e) {
            e.printStackTrace();
        }


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

        @Override
        public void action() {
            Profile profile = new ProfileImpl();
            profile.setParameter(Profile.CONTAINER_NAME, "Rooms");

            jade.wrapper.AgentContainer roomContainer = Runtime.instance().createAgentContainer(profile);


            try {
                AgentController livingRoom =
                        roomContainer
                                .createNewAgent("living-room", RoomAgent.class.getCanonicalName(), null);
                livingRoom.start();

                roomContainer
                        .createNewAgent("bed-room", RoomAgent.class.getCanonicalName(), null)
                        .start();

            } catch (StaleProxyException e) {
                e.printStackTrace();
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
                        System.out.println("Mam v pici lux.");
                        reply.setContent(time.hourOfDay().getAsText());
                        break;
                    case "temp":
                        System.out.println("Mam v pici temp.");
                        reply.setContent(time.minuteOfHour().getAsText());
                        break;
                    case "rain":
                        System.out.println("Mam v pici rain.");
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
            if (peopleInWorld <= 10 && new Random().nextFloat() > 0.5) {
                try {
                    peopleContainer
                            .createNewAgent(new NameGenerator().generateName().getFirstName(), PersonAgent.class.getCanonicalName(), null)
                            .start();
                    peopleInWorld++;
                } catch (StaleProxyException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
