package app.agents;

import app.Helper;
import app.SensorEnum;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.UUID;

public class BaseSensorAgent extends Agent {

    public AID worldAgentAID;
    private AID roomAID;
    protected UUID conversationId;

    public String msgContent;
    private int IDX;

    BaseSensorAgent(String msgContent) {
        this.msgContent = msgContent;
        this.conversationId = UUID.randomUUID();
    }

    @Override
    protected void setup() {

        addBehaviour(new FindWorldBehaviour());
        super.setup();
    }

    public AID getRoomAID() {
        return roomAID;
    }

    public void setRoomAID(AID roomAID) {
        this.roomAID = roomAID;
    }

    public int getIDX() {
        return IDX;
    }

    public void setIDX(int IDX) {
        this.IDX = IDX;
    }

    private class FindWorldBehaviour extends Behaviour {

        @Override
        public void action() {
            worldAgentAID = Helper.findAgentByName(myAgent, "mr-world-wide");
        }

        @Override
        public boolean done() {
            return worldAgentAID != null;
        }
    }

    protected boolean shouldSend(String content) {
        return true;
    }

    class SensorBehaviour<T extends BaseSensorAgent> extends TickerBehaviour {

        private T agent;
        private SensorEnum sensorType;
        private MessageTemplate mt;
        private UUID conversationId;
        private boolean requestSent = false;
        private int ttl = 3;

        SensorBehaviour(T a, long period, UUID conversationId, SensorEnum type) {
            super(a, period);
            this.conversationId = conversationId;
            this.agent = a;
            this.sensorType = type;
        }

        @Override
        protected void onTick() {
            if (ttl-- <= 0) {
                requestSent = false;
            }
            if (agent.getRoomAID() != null) {
                if (!this.requestSent) {
                    ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
                    msg.addReceiver(agent.getRoomAID());
                    msg.setContent(agent.msgContent);
                    msg.setConversationId(String.valueOf(conversationId));
                    msg.setReplyWith("msg" + System.currentTimeMillis());
                    agent.send(msg);
                    mt = MessageTemplate.and(MessageTemplate.MatchConversationId(String.valueOf(conversationId)),
                            MessageTemplate.MatchInReplyTo(msg.getReplyWith()));
                    requestSent = true;
                    ttl = 3;
                } else {
                    ACLMessage reply = agent.receive(mt);
                    if (reply != null && reply.getPerformative() == ACLMessage.INFORM) {
                        String content = reply.getContent();
                        if (shouldSend(content)) {
                            try {
                                Unirest.get(getUrl(sensorType, content, agent.getIDX())).asJson();
                                System.out.println(content + " " + reply.getSender().getLocalName() + " " + conversationId);
                            } catch (UnirestException e) {
                                e.printStackTrace();
                            }
                            requestSent = false;
                        }
                    } else {
                        block();
                    }
                }
            } else {
                System.out.println("Nemám pokojského agenta.");
            }
        }

        private String getUrl(SensorEnum type, String value, int idx) {
            String url = "http://127.0.0.1:8080/json.htm?type=command&";
            switch (type) {
                case LUX:
                    return url + "param=udevice&idx=" + idx + "&svalue=" + value;
                case RAIN:
                case TEMPERATURE:
                    return url + "param=udevice&idx=" + idx + "&nvalue=0&svalue=" + value;
                case MOTION:
                    return url + "param=switchlight&idx=" + idx + "&switchcmd=" + value;
                default:
                    return null;
            }
        }
    }
}
