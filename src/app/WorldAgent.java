package app;


import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import org.joda.time.LocalTime;

import java.text.DecimalFormat;
import java.util.Objects;
import java.util.Random;

public class WorldAgent extends Agent {

    private int peopleInHouse;

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

        addBehaviour(new TimeBehaviour(this, 500));
        addBehaviour(new OfferRequestServer());
        super.setup();
    }

    private LocalTime time = new LocalTime(0, 0);

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

    class OfferRequestServer extends CyclicBehaviour{

        @Override
        public void action() {
            MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
            ACLMessage msg = myAgent.receive(mt);
            if(msg != null){
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
            }
            else
            {
                block();
            }
        }
    }
}
