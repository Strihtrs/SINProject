package app.agents;

import app.behaviours.PersonEntersBehaviour;

public class PersonAgent extends BaseSensorAgent {

    @Override
    protected void setup() {
        super.setup();

        // inform World that person is coming IN
        addBehaviour(new PersonEntersBehaviour());
    }
}
