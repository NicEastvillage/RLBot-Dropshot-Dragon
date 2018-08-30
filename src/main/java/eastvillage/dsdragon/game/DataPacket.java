package eastvillage.dsdragon.game;

import rlbot.flat.GameTickPacket;

public class DataPacket {

    public final CarData car;
    public final Ball ball;
    public final int team;
    public final int playerIndex;

    public DataPacket(GameTickPacket request, int playerIndex) {

        this.playerIndex = playerIndex;
        this.ball = new Ball(request.ball());

        rlbot.flat.PlayerInfo myPlayerInfo = request.players(playerIndex);
        this.team = myPlayerInfo.team();
        this.car = new CarData(myPlayerInfo, request.gameInfo().secondsElapsed());
    }
}
