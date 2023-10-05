package ru.suhanov.discordgame.config;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import ru.suhanov.discordgame.handler.slashCommandHandler.AccountHandler;
import ru.suhanov.discordgame.handler.slashCommandHandler.GalaxyHandler;
import ru.suhanov.discordgame.handler.slashCommandHandler.MilitaryHandler;

@Configuration
@ComponentScan(basePackages = "ru.suhanov.discordgame")
public class MainConfig {

    @Value("${bot.token}")
    private String TOKEN;

    private final AccountHandler accountHandler;
    private final GalaxyHandler galaxyHandler;
    private final MilitaryHandler militaryHandler;

    @Autowired
    public MainConfig(AccountHandler accountHandler, GalaxyHandler galaxyHandler, MilitaryHandler militaryHandler) {
        this.accountHandler = accountHandler;
        this.galaxyHandler = galaxyHandler;
        this.militaryHandler = militaryHandler;
    }

    @Bean
    public JDA getJDA() {
        JDA jda = JDABuilder.createLight(TOKEN,
                        GatewayIntent.GUILD_MESSAGES,
                        GatewayIntent.DIRECT_MESSAGES,
                        GatewayIntent.MESSAGE_CONTENT,
                        GatewayIntent.GUILD_MEMBERS,
                        GatewayIntent.GUILD_WEBHOOKS,
                        GatewayIntent.DIRECT_MESSAGE_REACTIONS,
                        GatewayIntent.DIRECT_MESSAGE_TYPING,
                        GatewayIntent.GUILD_EMOJIS_AND_STICKERS,
                        GatewayIntent.GUILD_PRESENCES,
                        GatewayIntent.GUILD_MESSAGE_REACTIONS)
                .addEventListeners(accountHandler, galaxyHandler, militaryHandler)
                .setActivity(Activity.playing("GreatSpase"))
                .enableIntents(GatewayIntent.GUILD_MEMBERS)
                .build();

        jda.updateCommands().addCommands(
                Commands.slash("create_galaxy", "Create new Galaxy")
                        .addOption(OptionType.STRING, "title", "Galaxy title")
                        .addOption(OptionType.STRING, "size", "Galaxy size")
                        .addOption(OptionType.STRING, "neighbors", "Galaxy neighbors separated by a space"),
                Commands.slash("profile", "Get profile info"),
                Commands.slash("registration", "Create game user")
                        .addOption(OptionType.STRING, "name", "username"),
                Commands.slash("map", "Show the map"),
                Commands.slash("faction_info", "Faction info"),
                Commands.slash("modifier_info", "Modifier info"),
                Commands.slash("create_new_modifier", "Create new modifier"),
                Commands.slash("add_modifier_to_user", "Add modifier to user"),
                Commands.slash("add_modifier_to_galaxy", "Add modifier to galaxy")
        ).queue();

        return jda;
    }
}
