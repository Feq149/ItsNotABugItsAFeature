package MVVM.viewmodel;

import MVVM.view.Shape;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;


import static MVVM.model.Role.ENEMY;
import static MVVM.model.Role.HERO;
import static MVVM.view.GameScreen.*;
import static com.badlogic.gdx.graphics.Color.GREEN;
import static com.badlogic.gdx.graphics.Color.RED;
import static com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888;
import MVVM.model.Character;
public class HpBarsSupplier {

    List<Shape> generateHpBars(Set<Character> characters) {
        List<Shape> shapes = new LinkedList<>();
        characters.stream().filter(c -> c.role == ENEMY || c.role == HERO).forEach(character -> {
            shapes.addAll(createHpBars(character));
        });
        return shapes;
    }

    private List<Shape> createHpBars(Character character) {
        var characteHeight = character.role == HERO ? HERO_HEIGHT : ENEMY_HEIGHT;
        var characteWidth = character.role == HERO ? HERO_WIDTH : ENEMY_WIDTH;
        List<Shape> hpBars = new LinkedList<>();
        if (character.healthPoints > 0) {
            Pixmap hpLeft = new Pixmap(character.healthPoints, 10, RGBA8888);
            hpLeft.setColor(GREEN);
            hpLeft.fill();
            var hpLeftTexture = new Texture(hpLeft);
            var shape = new Shape(hpLeftTexture,
                    new Rectangle(character.x, character.y + characteHeight, characteWidth * character.healthPoints / 100, 10));
            hpBars.add(shape);
            hpLeft.dispose();
        }
        Pixmap hpUsed = new Pixmap(100 - character.healthPoints, 10, RGBA8888);
        hpUsed.setColor(RED);
        hpUsed.fill();
        var hpUsedTexture = new Texture(hpUsed);
        var shape = new Shape(hpUsedTexture,
                new Rectangle(character.x + characteWidth * character.healthPoints / 100, character.y + characteHeight, characteWidth * (100 - character.healthPoints) / 100, 10));
        hpBars.add(shape);
        hpUsed.dispose();
        return hpBars;
    }

}
