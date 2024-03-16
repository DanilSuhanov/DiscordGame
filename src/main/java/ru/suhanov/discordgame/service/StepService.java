package ru.suhanov.discordgame.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.suhanov.discordgame.exception.StepException;
import ru.suhanov.discordgame.exception.UserNotFoundException;
import ru.suhanov.discordgame.repository.GameUserRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class StepService {
    private final GameUserRepository gameUserRepository;

    public void checkStep(String name) throws UserNotFoundException, StepException {
        if (!gameUserRepository.findGameUserByName(name).orElseThrow(UserNotFoundException::new).isStep()) {
            throw new StepException();
        }
    }
}
