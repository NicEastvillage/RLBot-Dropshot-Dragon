package eastvillage.dsdragon.ai;

import eastvillage.dsdragon.ControlsOutput;
import eastvillage.dsdragon.game.DataPacket;

public interface State {

    String getName();
    void init(DataPacket data);
    double utilityScore(DataPacket data);
    ControlsOutput process(DataPacket data);
    void reset();
}
