package eastvillage.dsdragon;

import eastvillage.dsdragon.game.DataPacket;
import rlbot.Bot;
import rlbot.ControllerState;
import rlbot.flat.GameTickPacket;

public class DSDragonBot implements Bot {

    private final int playerIndex;

    public DSDragonBot(int playerIndex) {
        this.playerIndex = playerIndex;
    }

    private ControlsOutput processInput(DataPacket input) {

        return new ControlsOutput().withThrottle(1);
    }

    @Override
    public int getIndex() {
        return this.playerIndex;
    }

    @Override
    public ControllerState processInput(GameTickPacket packet) {
        if (packet.playersLength() <= playerIndex || packet.ball() == null) {
            return new ControlsOutput();
        }
        DataPacket dataPacket = new DataPacket(packet, playerIndex);
        return processInput(dataPacket);
    }

    public void retire() {
        System.out.println("Retiring sample bot " + playerIndex);
    }
}
