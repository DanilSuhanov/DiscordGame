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
import org.springframework.context.annotation.Configuration;
import ru.suhanov.discordgame.handler.slashCommandHandler.AccountHandler;
import ru.suhanov.discordgame.handler.slashCommandHandler.GalaxyHandler;
import ru.suhanov.discordgame.handler.slashCommandHandler.MilitaryHandler;
import ru.suhanov.discordgame.handler.slashCommandHandler.MinerHandler;

@Configuration
@ComponentScan(basePackages = "ru.suhanov.discordgame")
public class MainConfig {

    @Value("${bot.token}")
    private String TOKEN;

    private final AccountHandler accountHandler;
    private final GalaxyHandler galaxyHandler;
    private final MinerHandler minerHandler;
    private final MilitaryHandler militaryHandler;

    @Autowired
    public MainConfig(AccountHandler accountHandler, GalaxyHandler galaxyHandler, MinerHandler minerHandler, MilitaryHandler militaryHandler) {
        this.accountHandler = accountHandler;
        this.galaxyHandler = galaxyHandler;
        this.minerHandler = minerHandler;
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
                .addEventListeners(accountHandler, galaxyHandler, minerHandler, militaryHandler)
                .setActivity(Activity.playing("GreatSpase"))
                .enableIntents(GatewayIntent.GUILD_MEMBERS)
                .build();

        jda.updateCommands().addCommands(
                Commands.slash("create_galaxy", "Create new Galaxy")
                        .addOption(OptionType.STRING, "title", "Galaxy title")
                        .addOption(OptionType.STRING, "size", "Galaxy size")
                        .addOption(OptionType.BOOLEAN, "is_starter", "Is this galaxy a starter")
                        .addOption(OptionType.STRING, "neighbors", "Galaxy neighbors separated by a space"),
                Commands.slash("profile", "Get profile info"),
                Commands.slash("registration", "Create game user")
                        .addOption(OptionType.STRING, "name", "username"),
                Commands.slash("move_to", "Moving to another galaxy")
                        .addOption(OptionType.STRING, "galaxy", "Destination"),
                Commands.slash("create_oil_miner", "Create oil miner")
                        .addOption(OptionType.STRING, "title", "Miner title"),
                Commands.slash("create_metal_miner", "Create metal miner")
                        .addOption(OptionType.STRING, "title", "Miner title"),
                Commands.slash("launch_all_miners", "Launch all miners"),
                Commands.slash("launch", "Launch one miner")
                        .addOption(OptionType.STRING, "title", "Miner title"),
                Commands.slash("create_spaceship", "Create spaceship")
                        .addOption(OptionType.STRING, "title", "Spaceship title"),
                Commands.slash("galaxy_info", "Get galaxy info"),
                Commands.slash("create_faction", "Create new faction")
                        .addOption(OptionType.STRING, "title", "Faction title")
                        .addOption(OptionType.STRING, "description", "Description"),
                Commands.slash("invite_to_faction", "Invite to Faction")
                        .addOption(OptionType.STRING, "name", "Name of the member"),
                Commands.slash("check_invitations", "Check invitations"),
                Commands.slash("accept_invitation", "Accept the invitation")
                        .addOption(OptionType.STRING, "title", "Title of the faction")
        ).queue();

        return jda;
    }
}
