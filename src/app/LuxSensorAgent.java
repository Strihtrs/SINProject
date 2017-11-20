package app;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.SimpleBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class LuxSensorAgent extends Agent {

    private AID worldAgentAID;

    @Override
    protected void setup() {
        addBehaviour(new LuxBehaviour(this, 500));





        super.setup();
    }

    private void findWorld() {
        DFAgentDescription dfad = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setType("world");
        //sd.setName("mr-world-wide");
        dfad.addServices(sd);

        DFAgentDescription[] results;
        try {
            results = DFService.search(this, dfad);
            if(results.length > 0)
                worldAgentAID = results[0].getName();
        } catch (FIPAException e) {
            e.printStackTrace();
        }
    }

    class LuxBehaviour extends TickerBehaviour {

        private LuxSensorAgent agent;
        private MessageTemplate mt;
        private String convoId = "luxus";
        private boolean requestSent = false;


        public LuxBehaviour(LuxSensorAgent agent, long period) {

            super(agent, period);
            this.agent = agent;
        }

        @Override
        public void onTick() {
            // Find world
            findWorld();

            if (!this.requestSent) {
                ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
                msg.addReceiver(worldAgentAID);
                msg.setContent("lux");
                msg.setConversationId(convoId);
                msg.setReplyWith("msg" + System.currentTimeMillis());
                agent.send(msg);
                mt = MessageTemplate.and(MessageTemplate.MatchConversationId(convoId), MessageTemplate.MatchInReplyTo(msg.getReplyWith()));
                requestSent = true;
                //System.out.println("Posilam pici");
            } else {
                ACLMessage reply = agent.receive(mt);
                if (reply != null && reply.getPerformative() == ACLMessage.INFORM) {
                    String content = reply.getContent() + "00";
                    try {
                        HttpResponse<JsonNode> res = Unirest.get("http://127.0.0.1:8080/json.htm?type=command&param=udevice&idx=1&svalue="+content).asJson();
                        System.out.println("joyeg");
                    } catch (UnirestException e) {
                        e.printStackTrace();
                    }
                    //System.out.println("Mam pici");
                    requestSent = false;
                }
                else{
                    //System.out.println("nemam kurva");
                    block();
                }

            }
        }
    }
}
