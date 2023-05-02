package ru.suhanov.discordgame.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.suhanov.discordgame.exception.DataBaseException;
import ru.suhanov.discordgame.model.GameUser;
import ru.suhanov.discordgame.model.miner.Miner;
import ru.suhanov.discordgame.model.miner.ResourceType;
import ru.suhanov.discordgame.repository.MinerRepository;

import java.util.List;

@Service
@Transactional
public class MinerService {
    private final MinerRepository minerRepository;
    private final GameUserService gameUserService;
    @Autowired
    public MinerService(MinerRepository minerRepository, GameUserService gameUserService) {
        this.minerRepository = minerRepository;
        this.gameUserService = gameUserService;
    }

    public void newMiner(Long userId, Miner miner) throws DataBaseException {
        GameUser gameUser = gameUserService.findGameUserByDiscordId(userId);

        if (!minerRepository.existsMinerByTitle(miner.getTitle())) {
            if (miner.pay(gameUser)) {
                miner.setLocation(gameUser.getLocation());
                miner.setOwner(gameUser);
                gameUserService.save(gameUser);
                minerRepository.save(miner);
            } else {
                throw new DataBaseException("Не хватает метала для создания майнера!");
            }
        }
        else {
            throw new DataBaseException("Майнер с таким названием уже существует!");
        }
    }

    public String workAll(Long userId) throws DataBaseException {
        GameUser gameUser = gameUserService.findGameUserByDiscordId(userId);
        List<Miner> miners = gameUser.getMiners();
        miners.sort(Miner::compareTo);
        StringBuilder stringBuilder = new StringBuilder();
        for (Miner miner : miners) {
            stringBuilder.append("\n").append(miner.start());
        }
        gameUserService.save(gameUser);
        return stringBuilder.toString();
    }
}
