package microservices.book.multiplication.service;

import microservices.book.multiplication.domain.Multiplication;
import microservices.book.multiplication.domain.MultiplicationResultAttempt;
import microservices.book.multiplication.domain.User;
import microservices.book.multiplication.repository.MultiplicationRepository;
import microservices.book.multiplication.repository.MultiplicationResultAttemptRepository;
import microservices.book.multiplication.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class MultiplicationServiceImpl implements MultiplicationService {

    private RandomGeneratorService randomGeneratorService;

    private MultiplicationResultAttemptRepository attemptRepository;

    private UserRepository userRepository;

    private MultiplicationRepository multiplicationRepository;

    @Autowired
    public MultiplicationServiceImpl(RandomGeneratorService randomGeneratorService,
                                     MultiplicationResultAttemptRepository attemptRepository,
                                     UserRepository userRepository,
                                     MultiplicationRepository multiplicationRepository) {
        this.randomGeneratorService = randomGeneratorService;
        this.attemptRepository = attemptRepository;
        this.userRepository = userRepository;
        this.multiplicationRepository = multiplicationRepository;
    }

    @Override
    public Multiplication createRandomMultiplication() {
        int factorA = randomGeneratorService.generateRandomFactor();
        int factorB = randomGeneratorService.generateRandomFactor();
        return new Multiplication(factorA, factorB);
    }

    @Transactional
    @Override
    public boolean checkAttempt(final MultiplicationResultAttempt attempt) {

        Optional<User> optionalUser = userRepository.findByAlias(attempt.getUser().getAlias());

        Optional<Multiplication> optionalMultiplication = multiplicationRepository.findByFactorAAndFactorB(
                attempt.getMultiplication().getFactorA(),
                attempt.getMultiplication().getFactorB()
        );

        // Avoids 'hack' attempts
        Assert.isTrue(!attempt.isCorrect(), "You can't send an attempt marked as correct!!");

        boolean isCorrect = attempt.getResultAttempt() ==
                attempt.getMultiplication().getFactorA() *
                        attempt.getMultiplication().getFactorB();

        MultiplicationResultAttempt checkedAttempt =
                new MultiplicationResultAttempt(optionalUser.orElse(attempt.getUser()),
                        optionalMultiplication.orElse(attempt.getMultiplication()),
                        attempt.getResultAttempt(),
                        isCorrect);

        //Stores the attempt
        attemptRepository.save(checkedAttempt);

        return isCorrect;
    }

    @Override
    public List<MultiplicationResultAttempt> getStatsForUser(String userAlias) {
        return attemptRepository.findTop5ByUserAliasOrderByIdDesc(userAlias);
    }

}
