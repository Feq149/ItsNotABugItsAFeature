package MVVM.model;

import com.badlogic.gdx.math.Vector2;

import java.util.HashSet;
import java.util.Set;

public class Character {

    public float x;
    public float y;
    public int speed;
    public int healthPoints;
    public Set<Direction> directions = new HashSet<>();
    public Vector2 speedVector;
    public long timeOfLastShotInMillis;
    public long coolDownPeriodInMillis;
    public Role role;

    public Character(Role role) {
        this.role = role;
        healthPoints = 100;
    }


}
