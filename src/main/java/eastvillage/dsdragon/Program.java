package eastvillage.dsdragon;

import eastvillage.dsdragon.util.PortReader;
import rlbot.manager.BotManager;
import rlbot.pyinterop.PythonInterface;
import rlbot.pyinterop.PythonServer;

public class Program {

    public static void main(String[] args) {

        BotManager botManager = new BotManager();
        PythonInterface pythonInterface = new DSDragonPythonInterface(botManager);
        Integer port = PortReader.readPortFromFile("port.cfg");
        PythonServer pythonServer = new PythonServer(pythonInterface, port);
        pythonServer.start();
    }
}