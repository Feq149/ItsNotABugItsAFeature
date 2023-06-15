package MVVM.model;

import java.util.HashSet;
import java.util.Set;

public class Character {

    public float x;
    public float y;
    public int speed;
    public int healthPoints;
    public Set<Direction> directions = new HashSet<>();
    public Role role;

    public Character(Role role) {
        this.role = role;
    }
    
    
}
