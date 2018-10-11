package eastvillage.dsdragon.states;

import eastvillage.dsdragon.ControlsOutput;
import eastvillage.dsdragon.game.DataPacket;

/** The StateSequencer executes the states in sequence. A single State is processed until it is done, when the next
 * State is processed. When the end of the list is reached, it starts over. */
public class StateSequencer {

    private int curIndex = 0;
    private State[] states;
    private boolean firstIteration = true;

    /** The StateSequencer executes the states in sequence. A single State is processed until it is done, when the next
     * State is processed. When the end of the list is reached, it starts over. */
    public StateSequencer(State... states) {
        this.states = states;
    }

    public ControlsOutput process(DataPacket data) {
        State curState = states[curIndex];

        // Init state?
        if (firstIteration) {
            firstIteration = false;
            curState.init(data);
        }

        ControlsOutput controls = curState.process(data);

        // Go to next state?
        if (curState.isDone()) {
            curState.reset();
            curIndex++;
            if (curIndex >= states.length) curIndex = 0;
            firstIteration = true;
        }

        return controls;
    }
}
