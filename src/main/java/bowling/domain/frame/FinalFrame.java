package bowling.domain.frame;

import bowling.domain.dto.FrameResult;
import bowling.domain.pin.PinCount;
import bowling.domain.state.State;
import bowling.domain.state.StateFactory;
import bowling.domain.state.running.Ready;

import java.util.Stack;
import java.util.stream.Collectors;

public class FinalFrame extends Frame {

    private static final int MAX_COUNT = 3;

    private final Stack<State> states = new Stack<>();
    private int playCount;

    private FinalFrame() {
        this.playCount = 0;
        this.states.push(StateFactory.ready());
    }

    public static FinalFrame newInstance() {
        return new FinalFrame();
    }

    @Override
    public void bowl(final PinCount hitCount) {
        increasePlayCount();

        updateLastState(getLastState().bowl(hitCount));
        giveBonusBowl();
    }

    private void increasePlayCount() {
        this.playCount++;
    }

    private State getLastState() {
        return this.states.peek();
    }

    private void updateLastState(final State state) {
        this.states.pop();
        this.states.push(state);
    }

    private void giveBonusBowl() {
        if (getLastState().isCleanState()) {
            states.push(Ready.getInstance());
        }
    }

    @Override
    public Frame initNextFrame() {
        return this;
    }

    @Override
    void addFrame(Frames frames) {
    }

    @Override
    public boolean isGameOver() {
        return isEndedBonusBowl() || getLastState().isMiss();
    }

    private boolean isEndedBonusBowl() {
        return this.playCount == MAX_COUNT;
    }

    @Override
    public int getFrameNo() {
        return FrameNumber.MAX_NUMBER;
    }

    @Override
    public FrameResult getFrameResult() {
        return FrameResult.of(getDesc());
    }

    private String getDesc() {
        return this.states.stream()
                .map(State::getDesc)
                .collect(Collectors.joining("|"));
    }
}

