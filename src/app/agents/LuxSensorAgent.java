package app.agents;

import app.behaviours.SensorBehaviour;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.UUID;

public class LuxSensorAgent extends BaseSensorAgent {

    @Override
    protected void setup() {
        super.setup();
        addBehaviour(new SensorBehaviour<>(this, 400, "lux", 1, conversationId));
    }
}
