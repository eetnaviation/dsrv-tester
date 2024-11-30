package eu.velend.dsrvTester.client.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public class CommandManager {
    public static void registerCommands() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(
                    ClientCommandManager.literal("dsrv-test")
                            // Standard testing tools branch
                            .then(ClientCommandManager.literal("standard")
                                    .then(ClientCommandManager.literal("all") // Test all the things in standard branch
                                            .then(ClientCommandManager.argument("uid", StringArgumentType.word()) // Take input for user id
                                                    .then(ClientCommandManager.argument("cid", StringArgumentType.word()) // and channel id
                                                            .executes(context -> {
                                                                String uid = StringArgumentType.getString(context, "uid");
                                                                String cid = StringArgumentType.getString(context, "cid");
                                                                sendMessageToChat("@everyone");           // Send all of the standard test messages
                                                                sendMessageToChat("@here");
                                                                sendMessageToChat("<@" + uid + ">");
                                                                sendMessageToChat("<#" + cid + ">");
                                                                return 1;
                                                            })
                                                    )
                                            )
                                    )
                                    .then(ClientCommandManager.literal("everyone")                                   // Single standard test message commands
                                            .executes(context -> {
                                                sendMessageToChat("@everyone");
                                                return 1;
                                            })
                                    )
                                    .then(ClientCommandManager.literal("here")
                                            .executes(context -> {
                                                sendMessageToChat("@here");
                                                return 1;
                                            })
                                    )
                                    .then(ClientCommandManager.literal("uid")
                                            .then(ClientCommandManager.argument("uid", StringArgumentType.word())
                                                    .executes(context -> {
                                                        String uid = StringArgumentType.getString(context, "uid");
                                                        sendMessageToChat("<@" + uid + ">");
                                                        return 1;
                                                    })
                                            )
                                    )
                                    .then(ClientCommandManager.literal("cid")
                                            .then(ClientCommandManager.argument("cid", StringArgumentType.word())
                                                    .executes(context -> {
                                                        String cid = StringArgumentType.getString(context, "cid");
                                                        sendMessageToChat("<#" + cid + ">");
                                                        return 1;
                                                    })
                                            )
                                    )
                            )
                            // Bypass command branch
                            .then(ClientCommandManager.literal("bypass")
                                    .executes(context -> {
                                        sendMessageToChat("Executing: /dsrv-test bypass");
                                        return 1;
                                    })
                            )
            );
        });
    }

    // Logic for the "/hello [name]" command
    private static int sendHelloMessage(CommandContext<?> context) {
        String name = StringArgumentType.getString(context, "name");
        sendMessageToChat("Hello, " + name + "!");
        return 1;
    }

    // Utility method to send a message to the player's chat
    private static void sendMessageToChat(String message) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player != null) {
            client.player.sendMessage(Text.literal(message), false);
        }
    }
}
