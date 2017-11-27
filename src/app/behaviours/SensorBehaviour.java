package app.behaviours;

import app.agents.BaseSensorAgent;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.UUID;

public class SensorBehaviour<T extends BaseSensorAgent> extends TickerBehaviour {

    private T agent;
    private MessageTemplate mt;
    private UUID conversationId;
    private boolean requestSent = false;

    public SensorBehaviour(T a, long period, UUID conversationId) {
        super(a, period);
        this.conversationId = conversationId;
        this.agent = a;
    }

    @Override
    protected void onTick() {

        if (agent.roomAID != null) {
            if (!this.requestSent) {
                ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
                msg.addReceiver(agent.roomAID);
                msg.setContent(agent.msgContent);
                msg.setConversationId(String.valueOf(conversationId));
                msg.setReplyWith("msg" + System.currentTimeMillis());
                agent.send(msg);
                mt = MessageTemplate.and(MessageTemplate.MatchConversationId(String.valueOf(conversationId)),
                                         MessageTemplate.MatchInReplyTo(msg.getReplyWith()));
                requestSent = true;
            } else {
                ACLMessage reply = agent.receive(mt);
                if (reply != null && reply.getPerformative() == ACLMessage.INFORM) {
                    String content = reply.getContent();
                    try {
                        Unirest.get("http://127.0.0.1:8080/json.htm?type=command&param=switchlight&idx=" + agent.getIDX() + "&switchcmd=" + content).asJson();
                        System.out.println(content);
                    } catch (UnirestException e) {
                        e.printStackTrace();
                    }
                    requestSent = false;
                }
                else{
                    block();
                }
            }
        } else {
            System.out.println("Nemám pokojského agenta.");
        }
    }
}
