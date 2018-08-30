package eastvillage.dsdragon;

import rlbot.Bot;
import rlbot.manager.BotManager;
import rlbot.pyinterop.DefaultPythonInterface;

public class DSDragonPythonInterface extends DefaultPythonInterface {

    public DSDragonPythonInterface(BotManager botManager) {
        super(botManager);
    }

    protected Bot initBot(int index, String botType, int team) {
        return new DSDragonBot(index);
    }
}
