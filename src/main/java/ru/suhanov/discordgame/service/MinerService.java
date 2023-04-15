package ru.suhanov.discordgame.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.suhanov.discordgame.exception.DataBaseException;
import ru.suhanov.discordgame.model.GameUser;
import ru.suhanov.discordgame.model.miner.Miner;
import ru.suhanov.discordgame.repository.MinerRepository;

@Service
public class MinerService {
    private final MinerRepository minerRepository;
    private final GameUserService gameUserService;
    @Autowired
    public MinerService(MinerRepository minerRepository, GameUserService gameUserService) {
        this.minerRepository = minerRepository;
        this.gameUserService = gameUserService;
    }

    public void newMiner(GameUser gameUser, Miner miner) throws DataBaseException {
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
}
