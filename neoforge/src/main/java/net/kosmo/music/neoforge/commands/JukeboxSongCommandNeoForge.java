package net.kosmo.music.neoforge.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
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

public class JukeboxSongCommandNeoForge {
	//~ if >1.21.1 'CommandSourceStack' -> 'SharedSuggestionProvider'
	public static final SuggestionProvider<SharedSuggestionProvider> AVAILABLE_JUKEBOX_SONGS = SuggestionProviders.register(
		Identifier.withDefaultNamespace("available_jukebox_songs"),
		((commandContext, suggestionsBuilder) ->
			SharedSuggestionProvider.suggestResource(
				commandContext.getSource().registryAccess().lookupOrThrow(Registries.JUKEBOX_SONG).listElementIds().map(ResourceKey::/*? >=1.21.11 {*/identifier/*?} else {*//*location*//*?}*/), suggestionsBuilder)
		));

	private static final DynamicCommandExceptionType ERROR_NOT_FOUND = new DynamicCommandExceptionType((object) -> Component.translatable("commands.jukeboxsong.not_found", object));

	public static void register(CommandDispatcher<CommandSourceStack> commandDispatcher) {
		RequiredArgumentBuilder<CommandSourceStack, Identifier> argumentBuilder = Commands
			.argument("song", IdentifierArgument.id())
			//~ if >1.21.1 'AVAILABLE_JUKEBOX_SONGS' -> 'SuggestionProviders.cast(AVAILABLE_JUKEBOX_SONGS)'
			.suggests(SuggestionProviders.cast(AVAILABLE_JUKEBOX_SONGS))
			.executes(commandContext -> run(
				commandContext.getSource(),
				IdentifierArgument.getId(commandContext, "song")
			));

		commandDispatcher.register(Commands.literal("jukeboxsong").then(argumentBuilder));
	}

	public static int run(CommandSourceStack source, Identifier location) {
		try {
			//? if >1.21.1 {
			Registry<JukeboxSong> r = source.registryAccess().lookupOrThrow(Registries.JUKEBOX_SONG);
			Optional<Holder.Reference<JukeboxSong>> song = r.get(location);
			//? } else {
			/*Optional<Holder.Reference<JukeboxSong>> song = source.registryAccess().registryOrThrow(Registries.JUKEBOX_SONG).getHolder(location);
			*///? }
			if (song.isPresent()) {
				source.sendSuccess(() -> Component.translatable("commands.jukeboxsong.success", Component.translationArg(location),
					song.get().value().comparatorOutput(),
					song.get().value().description(),
					song.get().value().lengthInSeconds(),
					//~ if >1.21.1 'getLocation()' -> 'location()'
					Component.translationArg(song.get().value().soundEvent().value().location())
				), false);
				return 0;
			}

			throw ERROR_NOT_FOUND.create(Component.translationArg(location));
		} catch (Exception e) {
			source.sendFailure(Component.literal("Error searching jukebox songs: " + e.getMessage()));
			return 0;
		}
	}
}
