package MVVM.viewmodel;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static MVVM.model.Role.*;
import static MVVM.model.Role.ENEMY;
import static MVVM.view.GameScreen.*;
import static MVVM.view.GameScreen.ENEMY_WIDTH;
import MVVM.model.*;
import MVVM.model.Character;
import MVVM.view.Shape;

public class CollisionsDetector {

    public List<Character> detectHitsAndReturnBulletsToRemove(Set<Character> characters) {
        List<Character> bulletsToRemove = new LinkedList<>();
        var correction = BULLET_RECTANGLE_SIZE / 2;
        var hero = characters.stream().filter(c -> c.role == HERO).findFirst().get();
        characters.stream().filter(c -> c.role == ENEMY_BULLET).forEach(bullet -> {

            var withinX = bullet.x + correction >= hero.x && bullet.x + correction <= hero.x + HERO_WIDTH;
            var withinY = bullet.y + correction >= hero.y && bullet.y + correction <= hero.y + HERO_HEIGHT;
            if (withinX && withinY) {
                hero.healthPoints -= 10;
                bulletsToRemove.add(bullet);
            }
        });
        characters.stream().filter(c -> c.role == HERO_BULLET).forEach(bullet -> {
            characters.stream().filter(c -> c.role == ENEMY).forEach(enemy -> {

                var withinX = bullet.x + correction >= enemy.x && bullet.x + correction <= enemy.x + ENEMY_WIDTH;
                var withinY = bullet.y + correction >= enemy.y && bullet.y + correction <= enemy.y + ENEMY_WIDTH;
                if (withinX && withinY) {
                    enemy.healthPoints -= 10;
                    bulletsToRemove.add(bullet);
                }
            });
        });
        characters.stream().filter(c -> c.role == ENEMY_BULLET || c.role == HERO_BULLET).forEach(bullet -> {
            characters.stream().filter(c -> c.role == BLOCK).forEach(block -> {
                var withinX = bullet.x + correction >= block.x && bullet.x + correction <= block.x + BLOCK_WIDTH;
                var withinY = bullet.y + correction >= block.y && bullet.y + correction <= block.y + BLOCK_HEIGHT;
                if (withinX && withinY) {
                    bulletsToRemove.add(bullet);
                }
            });
        });
        return bulletsToRemove;
    }
    public List<Character> fetchBlocksCharacterRanInto(Shape character, Map<Character, Shape> shapes) {
        return shapes.entrySet().stream()
                .filter(e -> e.getKey().role == BLOCK)
                .filter(e -> e.getValue().rectangle.overlaps(character.rectangle))
                .map(e -> e.getKey())
                .toList();

    }

}
