package app;


import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.SimpleBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import org.joda.time.LocalTime;

import java.util.Objects;

public class WorldAgent extends Agent {
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
        addBehaviour(new OfferRequestServer(this));
        super.setup();
    }

    private LocalTime time = new LocalTime(0, 0);

    class TimeBehaviour extends TickerBehaviour{


        private WorldAgent worldAgent;

        public TimeBehaviour(WorldAgent worldAgent, long period) {

            super(worldAgent, period);
            this.worldAgent = worldAgent;
        }

        @Override
        public void onTick() {
            worldAgent.time = time.plusMinutes(30);
//            try {
//                Thread.sleep(100);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            System.out.println(time);
        }
    }

    class OfferRequestServer extends CyclicBehaviour{

        private WorldAgent agent;

        public OfferRequestServer(WorldAgent worldAgent) {

            this.agent = worldAgent;
        }

        @Override
        public void action() {
            MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
            ACLMessage msg = agent.receive(mt);
            if(msg != null){
                String title = msg.getContent();
                System.out.println("Mam pici");

                if(Objects.equals(title, "lux")){
                    ACLMessage reply = msg.createReply();
                    reply.setPerformative(ACLMessage.INFORM);
                    reply.setContent(time.hourOfDay().getAsText());
                    agent.send(reply);
                }
            }
            else
            {
                block();
            }
        }
    }
}
