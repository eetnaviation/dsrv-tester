package eu.velend.dsrvTester.client;

import eu.velend.dsrvTester.client.commands.CommandManager;
import net.fabricmc.api.ClientModInitializer;

public class DsrvTesterClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        //Register /dsrv-test command
        CommandManager.registerCommands();
    }
}
