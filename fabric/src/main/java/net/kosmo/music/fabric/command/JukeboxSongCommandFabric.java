package net.kosmo.music.fabric.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.IdentifierArgument;
import net.minecraft.commands.synchronization.SuggestionProviders;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.JukeboxSong;

import java.util.Optional;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommands.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommands.literal;

public class JukeboxSongCommandFabric {
	public static final SuggestionProvider<FabricClientCommandSource> AVAILABLE_JUKEBOX_SONGS = SuggestionProviders.register(
		Identifier.withDefaultNamespace("available_jukebox_songs"),
		((commandContext, suggestionsBuilder) ->
			SharedSuggestionProvider.suggestResource(
				commandContext.getSource().registryAccess().lookupOrThrow(Registries.JUKEBOX_SONG).listElementIds().map(ResourceKey::/*? >=1.21.11 {*/identifier/*?} else {*//*location*//*?}*/), suggestionsBuilder)
		));
	private static final DynamicCommandExceptionType ERROR_NOT_FOUND = new DynamicCommandExceptionType((object) -> Component.translatable("commands.jukeboxsong.not_found", object));


	public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandBuildContext commandBuildContext) {
		RequiredArgumentBuilder<FabricClientCommandSource, Identifier> argumentBuilder =
			argument("song", IdentifierArgument.id())
				.suggests(AVAILABLE_JUKEBOX_SONGS)
				.executes(commandContext -> JukeboxSongCommandFabric.run(
					commandContext.getSource(),
					commandContext.getArgument("song", Identifier.class)
				));

		dispatcher.register(literal("jukeboxsong").then(argumentBuilder));
	}

	public static int run(FabricClientCommandSource source, Identifier location) {
		try {
			//? if >1.21.1 {
			Registry<JukeboxSong> r = source.registryAccess().lookupOrThrow(Registries.JUKEBOX_SONG);
			Optional<Holder.Reference<JukeboxSong>> song = r.get(location);
			//? } else {
			/*Optional<Holder.Reference<JukeboxSong>> song = source.registryAccess().registryOrThrow(Registries.JUKEBOX_SONG).getHolder(location);
			*///? }
			if (song.isPresent()) {
				source.sendFeedback(Component.translatable("commands.jukeboxsong.success", Component.translationArg(location),
					song.get().value().comparatorOutput(),
					song.get().value().description(),
					song.get().value().lengthInSeconds(),
					//~ if >1.21.1 'getLocation()' -> 'location()'
					Component.translationArg(song.get().value().soundEvent().value().location())
				));
				return 0;
			}

			throw ERROR_NOT_FOUND.create(Component.translationArg(location));
		} catch (Exception e) {
			source.sendError(Component.literal("Error searching jukebox songs: " + e.getMessage()));
			return 0;
		}
	}
}
