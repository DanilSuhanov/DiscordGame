package ru.suhanov.discordgame.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.suhanov.discordgame.model.OperationTag;
import ru.suhanov.discordgame.model.miner.ResourceType;
import ru.suhanov.discordgame.model.mods.Mod;
import ru.suhanov.discordgame.repository.ModRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class ModService {
    private final ModRepository modRepository;

    public void createMod(ResourceType resourceType, int percent, OperationTag operationTag, String title) {
        Mod mod = new Mod(resourceType, percent, operationTag, title);
        modRepository.save(mod);
    }
}
