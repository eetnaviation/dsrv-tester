package eu.velend.dsrvTester.client.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.impl.networking.NetworkHandlerExtensions;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;

import java.util.Objects;

public class CommandManager {
    public static void registerCommands() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(
                    ClientCommandManager.literal("dsrv-test")
                            .then(ClientCommandManager.literal("help")
                                    .executes(context -> {
                                        sendLocalMessage("dsrv-test, help:");
                                        sendLocalMessage("/dsrv-test standard <all | everyone | here | uid | cid> [cid: Discord Channel ID] [uid: Discord User ID]");
                                        sendLocalMessage("/dsrv-test bugs <emoji-parse>");
                                        return 1;
                                    })
                            )
                            // Standard testing tools branch
                            .then(ClientCommandManager.literal("standard")
                                    .then(ClientCommandManager.literal("all") // Test all the things in standard branch
                                            .then(ClientCommandManager.argument("uid", StringArgumentType.word()) // Take input for user id
                                                    .then(ClientCommandManager.argument("cid", StringArgumentType.word()) // and channel id
                                                            .executes(context -> {
                                                                String uid = StringArgumentType.getString(context, "uid");
                                                                String cid = StringArgumentType.getString(context, "cid");
                                                                sendGlobalMessage("@everyone");           // Send all the standard test messages
                                                                sendGlobalMessage("@here");
                                                                sendGlobalMessage("<@" + uid + ">");
                                                                sendGlobalMessage("<#" + cid + ">");
                                                                return 1;
                                                            })
                                                    )
                                            )
                                    )
                                    .then(ClientCommandManager.literal("everyone")                                   // Single standard test message commands
                                            .executes(context -> {
                                                sendGlobalMessage("@everyone");
                                                return 1;
                                            })
                                    )
                                    .then(ClientCommandManager.literal("here")
                                            .executes(context -> {
                                                sendGlobalMessage("@here");
                                                return 1;
                                            })
                                    )
                                    .then(ClientCommandManager.literal("uid")
                                            .then(ClientCommandManager.argument("uid", StringArgumentType.word())
                                                    .executes(context -> {
                                                        String uid = StringArgumentType.getString(context, "uid");
                                                        sendGlobalMessage("<@" + uid + ">");
                                                        return 1;
                                                    })
                                            )
                                    )
                                    .then(ClientCommandManager.literal("cid")
                                            .then(ClientCommandManager.argument("cid", StringArgumentType.word())
                                                    .executes(context -> {
                                                        String cid = StringArgumentType.getString(context, "cid");
                                                        sendGlobalMessage("<#" + cid + ">");
                                                        return 1;
                                                    })
                                            )
                                    )
                            )
                            // Bugs testing tools branch
                            .then(ClientCommandManager.literal("bugs") // How could I add testing for DiscordSideCommandExecutionViaChatInjection?
                                    .then(ClientCommandManager.literal("emoji-parse")
                                            .executes(context -> {
                                                sendGlobalMessage(":thumbsup: // :thumbs_up: // :fish: // :oncoming_automobile:");
                                                return 1;
                                            })
                                    )
                            )
            );
        });
    }

    // Send a chat message to the server
    public static void sendGlobalMessage(String message) {
        try {
            MinecraftClient.getInstance().player.networkHandler.sendChatCommand("say" + message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Send a local message to the player's client chat
    public static void sendLocalMessage(String message) {
        MinecraftClient client = MinecraftClient.getInstance();

        if (client.player != null) {
            client.player.sendMessage(Text.literal(message), false); // false ensures it's only shown locally
        }
    }

    // Example method to use PacketByteBuf, although not needed for chat messages
    public static PacketByteBuf convertStringToPacketByteBuf(String message) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer()); // Create a new PacketByteBuf backed by an Unpooled ByteBuf
        buf.writeString(message); // Write the string to the PacketByteBuf
        return buf;
    }
}
