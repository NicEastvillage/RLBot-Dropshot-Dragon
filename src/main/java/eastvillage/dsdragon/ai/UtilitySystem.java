package eastvillage.dsdragon.ai;

import eastvillage.dsdragon.ControlsOutput;
import eastvillage.dsdragon.game.DataPacket;

public abstract class UtilitySystem implements State {

    protected String name;
    protected double continueBias;
    protected State[] states;
    protected int currentState = -1;

    public UtilitySystem(String name, double continueBias, State... states) {
        this.name = name;
        this.continueBias = continueBias;
        this.states = states;
    }

    @Override
    public String getName() {
        return name + " > " + states[currentState];
    }

    @Override
    public void reset() {
        currentState = -1;
    }

    @Override
    public void init(DataPacket data) {

    }

    @Override
    public ControlsOutput process(DataPacket data) {
        int prevBest = currentState;
        int bestIndex = 0; // defaults to state 0. Only happens when all choices' utility score is 0
        double bestScore = 0;
        for (int i = 0; i < states.length; i++) {
            double score = states[i].utilityScore(data);
            if (i == prevBest) {
                score += continueBias;
            }
            if (score > bestScore) {
                bestScore = score;
                bestIndex = i;
            }
        }

        currentState = bestIndex;

        if (prevBest != bestIndex) {
            onStateChange(data, prevBest);
            // Reset previous best. Init new best
            if (prevBest != -1) {
                states[prevBest].reset();
            }
            states[currentState].init(data);
        }

        return states[currentState].process(data);
    }

    protected abstract void onStateChange(DataPacket data, int prevState);
}
