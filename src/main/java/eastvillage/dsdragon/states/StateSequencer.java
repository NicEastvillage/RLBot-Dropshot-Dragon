package eastvillage.dsdragon.states;

import eastvillage.dsdragon.ControlsOutput;
import eastvillage.dsdragon.game.DataPacket;

import java.awt.*;

/** The StateSequencer executes the states in sequence. A single State is processed until it is done, when the next
 * State is processed. When the end of the list is reached, it starts over. */
public class StateSequencer {

    private int curIndex = 0;
    private State[] states;
    private boolean firstIteration = true;

    /** The StateSequencer executes the states in sequence. A single State is processed until it is done, when the next
     * State is processed. When the end of the list is reached, it starts over. */
    public StateSequencer(State... states) {
        if (states.length == 0) throw new IllegalArgumentException("StateSequencer must have at least one state.");
        this.states = states;
    }

    public ControlsOutput process(DataPacket data) {
        State curState = states[curIndex];

        // Init state?
        if (firstIteration) {
            firstIteration = false;
            curState.init(data);
            System.out.println("State: " + curState.getName());
        }

        ControlsOutput controls = curState.process(data);
        data.renderer.drawString3d(curState.getName(), Color.white, data.self.getLocation().toRlbotVector(), 1, 1);

        // Go to next state?
        if (curState.isDone(data)) {
            curState.reset();
            curIndex++;
            if (curIndex >= states.length) curIndex = 0;
            firstIteration = true;
        }

        return controls;
    }
}
