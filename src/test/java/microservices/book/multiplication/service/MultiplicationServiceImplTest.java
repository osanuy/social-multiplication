package microservices.book.multiplication.service;

import microservices.book.multiplication.domain.Multiplication;
import microservices.book.multiplication.domain.MultiplicationResultAttempt;
import microservices.book.multiplication.domain.User;
import microservices.book.multiplication.repository.MultiplicationRepository;
import microservices.book.multiplication.repository.MultiplicationResultAttemptRepository;
import microservices.book.multiplication.repository.UserRepository;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

public class MultiplicationServiceImplTest {

    private MultiplicationServiceImpl multiplicationServiceImpl;

    @Mock
    private RandomGeneratorService randomGeneratorService;

    @Mock
    private MultiplicationResultAttemptRepository multiplicationResultAttemptRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private MultiplicationRepository multiplicationRepository;

    @BeforeEach
    public void setUp() {
        // With this call to initMocks we tell Mockito to process the annotations
        MockitoAnnotations.initMocks(this);
        multiplicationServiceImpl = new MultiplicationServiceImpl(randomGeneratorService,
                multiplicationResultAttemptRepository,
                userRepository,
                multiplicationRepository);
    }

    @Test
    public void createRandomMultiplicationTest() {
        // given (our mocked Random Generator service will return first 50, then 30)
        given(randomGeneratorService.generateRandomFactor()).willReturn(50, 30);

        // when
        Multiplication multiplication = multiplicationServiceImpl.createRandomMultiplication();

        // assert
        assertThat(multiplication.getFactorA()).isEqualTo(50);
        assertThat(multiplication.getFactorB()).isEqualTo(30);
        //assertThat(multiplication.getResult()).isEqualTo(1500);
    }

    @Test
    public void checkCorrectAttemptTest() {
        //given
        Multiplication multiplication = new Multiplication(50, 60);
        User user = new User("john_doe");

        MultiplicationResultAttempt attempt = new MultiplicationResultAttempt(
                user, multiplication, 3000, false);

        given(userRepository.findByAlias("john_doe")).willReturn(Optional.empty());
        given(multiplicationRepository.findByFactorAAndFactorB(50, 60)).willReturn(Optional.empty());

        //When
        boolean attemptResult = multiplicationServiceImpl.checkAttempt(attempt);

        // then
        assertThat(attemptResult).isTrue();

        MultiplicationResultAttempt verifiedAttempt  =
                new MultiplicationResultAttempt(user, multiplication, 3000, true);
        verify(multiplicationResultAttemptRepository).save(verifiedAttempt);
    }

    @Test
    public void checkWrongAttemptTest() {
        //given
        Multiplication multiplication = new Multiplication(50, 60);

        User user = new User("john_doe");

        MultiplicationResultAttempt attempt =
                new MultiplicationResultAttempt(user, multiplication, 3010, false);
        given(userRepository.findByAlias("john_doe")).willReturn(Optional.empty());
        given(multiplicationRepository.findByFactorAAndFactorB(50, 60)).willReturn(Optional.empty());

        //When
        boolean attemptResult = multiplicationServiceImpl.checkAttempt(attempt);

        //then
        assertThat(attemptResult).isFalse();
        verify(multiplicationResultAttemptRepository).save(attempt);
    }

    @Test
    public void restrieveStatsTest() {
        //given
        Multiplication multiplication = new Multiplication(50, 60);
        User user = new User("john_doe");
        MultiplicationResultAttempt attempt1 = new MultiplicationResultAttempt(user, multiplication, 3010, false);
        MultiplicationResultAttempt attempt2 = new MultiplicationResultAttempt(user, multiplication, 3051, false);

        List<MultiplicationResultAttempt> latestAttempts = Lists.newArrayList(attempt1, attempt2);

        given(userRepository.findByAlias("john_doe")).willReturn(Optional.empty());
        given(multiplicationResultAttemptRepository.findTop5ByUserAliasOrderByIdDesc("john_doe")).willReturn(latestAttempts);

        //When
        List<MultiplicationResultAttempt> latestAttemptsResult = multiplicationServiceImpl.getStatsForUser("john_doe");

        //Then
        assertThat(latestAttemptsResult).isEqualTo(latestAttempts);


    }
}
