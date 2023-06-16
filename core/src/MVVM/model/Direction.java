package MVVM.model;

import static com.badlogic.gdx.Input.Keys.*;
import java.util.Set;

public enum Direction {

    NORTH(Set.of(W, UP)), SOUTH(Set.of(S, DOWN)), WEST(Set.of(A, LEFT)),
    EAST(Set.of(D, RIGHT));

    public Set<Integer> keyCodes;

    private Direction(Set<Integer> keyCodes) {
        this.keyCodes = keyCodes;
    }

}

