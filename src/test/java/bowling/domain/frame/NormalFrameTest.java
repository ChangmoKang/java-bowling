package bowling.domain.frame;

import bowling.domain.pin.Pins;
import bowling.exception.FrameNotCorrectException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("NormalFrame Test")
public class NormalFrameTest {

    static Stream<Arguments> provideFinishCheckFrame() {
        return Stream.of(
                Arguments.of(new int[]{10}, true),
                Arguments.of(new int[]{9, 1}, true),
                Arguments.of(new int[]{5, 4}, true),
                Arguments.of(new int[]{0, 0}, true),
                Arguments.of(new int[]{9}, false),
                Arguments.of(new int[]{1}, false)
        );
    }

    @ParameterizedTest(name = "{0} -> {1}")
    @MethodSource("provideFinishCheckFrame")
    @DisplayName("완료된 프레임인지 확인한다.")
    void finishFrameCheck(int[] numbers, boolean actual) {
        // given
        Frame frame = NormalFrame.of(1, numbers);

        // when
        boolean finished = frame.isFinished();

        // then
        assertThat(finished).isEqualTo(actual);
    }

    @Test
    @DisplayName("마지막 Normal 프레임이 끝나면 다음은 Final 프레임이다")
    void normalFrameFinalNextIsFinalFrame() {
        //given
        Frame givenFrame = NormalFrame.of(NormalFrame.ROUND_BEFORE_LAST_ROUND).bowl(10);

        // when
        Frame nextFrame = givenFrame.nextFrame();

        // then
        assertThat(nextFrame.getClass()).isEqualTo(FinalFrame.class);
    }

    @Test
    @DisplayName("프레임에 완료되지 않으면 현재 프레임 그대로 가져온다")
    void nextFrameIsCurrentFrame() {
        // given
        Frame givenFrame = NormalFrame.of(1).bowl(9);

        // when
        Frame actualFrame = givenFrame.nextFrame();

        // then
        assertThat(actualFrame).isEqualTo(givenFrame);
    }

    @Test
    @DisplayName("한 프레임이 완료되면 다음 프레임을 가져온다.")
    void nextFrameIsNewFrame() {
        // given
        Frame frame = NormalFrame.first().bowl(10);

        // when
        Frame nextFrame = frame.nextFrame();
        int actual = nextFrame.getRoundNumber();

        // then
        assertThat(actual).isEqualTo(NormalFrame.FIRST_ROUND_NUMBER + 1);
    }

    @Test
    @DisplayName("Normal 프레임 Exception")
    void normalFrameException() {
        // given
        int[] numbers = new int[]{5, 6};

        // when
        // then
        assertThatThrownBy(() ->
                NormalFrame.of(1, numbers)
        ).isInstanceOf(FrameNotCorrectException.class);
    }

    @Test
    @DisplayName("스페어일 경우 점수 계산")
    void spareCalculateScore() {
        //given
        Frame firstFrame = NormalFrame.first().bowl(9).bowl(1);
        firstFrame.nextFrame().bowl(9);

        //when
        int actualScore = firstFrame.getScore();

        //then
        assertThat(actualScore).isEqualTo(19);
    }

    @Test
    @DisplayName("스트라이크이고 다음 프레임이 스트라이크가 아닌 경우")
    void strikeCalculateScore() {
        //given
        Frame firstFrame = NormalFrame.first().bowl(10);
        Frame secondFrame = firstFrame.nextFrame();
        secondFrame.bowl(9).bowl(1);

        //when
        int actualScore = firstFrame.getScore();

        //then
        assertThat(actualScore).isEqualTo(20);
    }

    @Test
    @DisplayName("프레임이 스트라이크고 다음 프레임도 스트라이크일 경우")
    void doubleCalculateScore() {
        // given
        Frame givenFrame = NormalFrame.first().bowl(10);
        Frame secondFrame = givenFrame.nextFrame();
        secondFrame.bowl(10);
        Frame thirdFrame = secondFrame.nextFrame();
        thirdFrame.bowl(5);

        // when
        int actualScore = givenFrame.getScore();

        // then
        assertThat(actualScore).isEqualTo(25);
    }
}
