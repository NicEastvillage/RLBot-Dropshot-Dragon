package eastvillage.dsdragon.states;

import eastvillage.dsdragon.ControlsOutput;
import eastvillage.dsdragon.game.DataPacket;

public interface State {

    String getName();
    void reset();
    void init(DataPacket data);
    ControlsOutput process(DataPacket data);
    boolean isDone(DataPacket data);
}
