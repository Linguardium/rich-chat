package io.github.ashley1227.richchat.mixin;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.github.ashley1227.richchat.formatting.Formatter;
import net.minecraft.command.arguments.MessageArgumentType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.SayCommand;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.ArrayList;

@Mixin(value = SayCommand.class, priority = 1)
public abstract class SayCommandMixin {
	private static Formatter formatter;

	private static void setFormatter(MinecraftServer server) {
		formatter = new Formatter(server);
	}

	/**
	 * @author Ashley1227
	 */
	@Overwrite
	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register((LiteralArgumentBuilder) ((LiteralArgumentBuilder) CommandManager.literal("say").requires((serverCommandSource) -> serverCommandSource.hasPermissionLevel(2))).then(CommandManager.argument("message", MessageArgumentType.message())
				.executes((commandContext) -> {
					setFormatter(commandContext.getSource().getMinecraftServer());
					ArrayList<LiteralText> texts = formatter.format(MessageArgumentType.getMessage(commandContext, "message").asString());

					commandContext.getSource().getMinecraftServer().getPlayerManager().sendToAll(
							new TranslatableText("chat.type.announcement", new Object[]{
									commandContext.getSource().getDisplayName(),
									texts.get(0)
							})
					);
					for (int i = 1; i < texts.size(); i++) {
						commandContext.getSource().getMinecraftServer().getPlayerManager().sendToAll(
								texts.get(i)
						);
					}
					return 1;
				})));
	}

}
