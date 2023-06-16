package MVVM.viewmodel;

import static java.lang.Float.max;
import static java.lang.Float.min;
import static java.lang.Math.signum;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

import MVVM.model.Direction;
import MVVM.model.Character;
import MVVM.view.Shape;
import com.badlogic.gdx.math.Vector2;

import static MVVM.model.Role.*;
import static MVVM.view.GameScreen.*;

public class CharacterMover {

    private Map<Character, Shape> shapes;

    public CharacterMover(Map<Character, Shape> shapes) {
        this.shapes = shapes;
    }

    public void moveEnemies(float deltaTime) {
        var hero = shapes.keySet().stream().filter(c -> c.role == HERO).findFirst().get();
        var enemies = shapes.keySet().stream().filter(c -> c.role == ENEMY).collect(toList());
        for (var enemy : enemies) {
            var xDistanceToHero = hero.x - enemy.x;
            var yDistanceToHero = hero.y - enemy.y;
            enemy.x += enemy.speed * deltaTime * signum(xDistanceToHero);
            enemy.y += enemy.speed * deltaTime * signum(yDistanceToHero);
        }
    }

    public List<Character> moveBulletsAndReturnBulletsOutOfBounds(float deltaTime) {
        List<Character> bulletsOutOfBounds = new LinkedList<>();
        var heroBullets = shapes.keySet().stream().filter(c -> c.role == HERO_BULLET).collect(toList());
        for (var bullet : heroBullets) {
            for (var direction : bullet.directions) {
                moveCharacter(bullet, direction, deltaTime);
                if (bullet.x < 0 || bullet.x > CAMERA_WIDTH
                        || bullet.y < 0 || bullet.y > CAMERA_HEIGHT) {
                    bulletsOutOfBounds.add(bullet);
                }
            }
        }
        var enemyBullets = shapes.keySet().stream().filter(c -> c.role == ENEMY_BULLET).collect(toList());
        for (var bullet : enemyBullets) {
            moveCharacter(bullet, bullet.speedVector, deltaTime);
            if (bullet.x < 0 || bullet.x > CAMERA_WIDTH
                    || bullet.y < 0 || bullet.y > CAMERA_HEIGHT) {
                bulletsOutOfBounds.add(bullet);
            }
        }
        return bulletsOutOfBounds;
    }

    public void moveHero(float deltaTime) {
        var hero = shapes.keySet().stream().filter(c -> c.role == HERO).findFirst().get();
        for (var direction : hero.directions) {
            moveCharacter(hero, direction, deltaTime);
            fixCharacterChoordinatesToStayInbounds(hero);
        }
    }

    private void fixCharacterChoordinatesToStayInbounds(Character character) {
        var characterWidth = character.role == ENEMY ? ENEMY_WIDTH : HERO_WIDTH;
        var characterHeight = character.role == ENEMY ? ENEMY_HEIGHT : HERO_HEIGHT;
        character.x = max(character.x, 0);
        character.x = min(character.x, CAMERA_WIDTH - characterWidth);
        character.y = max(character.y, 0);
        character.y = min(character.y, CAMERA_HEIGHT - characterHeight);
    }

    public void moveCharacter(Character character, Direction direction, float deltaTime) {
        switch (direction) {
            case NORTH -> {
                character.y += character.speed * deltaTime;
            }
            case SOUTH -> {
                character.y -= character.speed * deltaTime;
            }
            case EAST -> {
                character.x += character.speed * deltaTime;
            }
            case WEST -> {
                character.x -= character.speed * deltaTime;
            }
            default ->
                    throw new AssertionError();
        }
    }

    public void moveCharacter(Character character, Vector2 speedVector, float deltaTime) {
        character.x += speedVector.x * deltaTime;
        character.y += speedVector.y * deltaTime;
    }
}
