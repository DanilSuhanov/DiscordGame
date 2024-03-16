package ru.suhanov.discordgame.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.suhanov.discordgame.exception.DataBaseException;
import ru.suhanov.discordgame.exception.StepException;
import ru.suhanov.discordgame.model.GameUser;
import ru.suhanov.discordgame.model.map.Galaxy;
import ru.suhanov.discordgame.model.miner.Miner;
import ru.suhanov.discordgame.model.miner.ResourceType;
import ru.suhanov.discordgame.repository.MinerRepository;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class MinerService {
    private final MinerRepository minerRepository;
    private final GameUserService gameUserService;
    private final StepService stepService;

    public void newMiner(long userId, Miner miner) throws DataBaseException, StepException {
        GameUser gameUser = gameUserService.findGameUserByDiscordId(userId);
        stepService.checkStep(gameUser.getName());

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

    public String workAll(long userId) throws DataBaseException, StepException {
        GameUser gameUser = gameUserService.findGameUserByDiscordId(userId);
        stepService.checkStep(gameUser.getName());

        List<Miner> miners = gameUser.getMiners();
        miners.sort(Miner::compareTo);
        StringBuilder stringBuilder = new StringBuilder();
        for (Miner miner : miners) {
            stringBuilder.append("\n").append(miner.start());
        }
        gameUserService.save(gameUser);
        return stringBuilder.toString();
    }

    public String workMiner(long userId, String title) throws DataBaseException, StepException {
        GameUser gameUser = gameUserService.findGameUserByDiscordId(userId);
        stepService.checkStep(gameUser.getName());

        Miner miner = gameUser.getMiners().stream().filter(m -> m.getTitle().equals(title)).findFirst()
                .orElseThrow(() -> new DataBaseException("Майнер не найден!"));
        return miner.start();
    }
}
