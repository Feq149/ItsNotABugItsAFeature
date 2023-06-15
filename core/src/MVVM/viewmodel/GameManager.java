package MVVM.viewmodel;

import MVVM.model.Character;
import MVVM.model.Direction;
import MVVM.model.Level;
import MVVM.model.Role;
import MVVM.view.GameScreen;
import MVVM.view.Shape;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import static com.badlogic.gdx.math.MathUtils.random;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class GameManager {

    public Game game;
    private Level level;
    private Map<Character, Shape> shapes = new HashMap<>();
    private CharacterMover characterMover;
    private final GameScreen gameScreen;
    private Character hero;
    
    public static final Texture heroImage  = new Texture(Gdx.files.internal("hero2.png"));
    private static final Texture enemyImage  = new Texture(Gdx.files.internal("enemy2.png"));

    public GameManager(Game game) {
        this.game = game;
        gameScreen = new GameScreen(this);
        game.setScreen(gameScreen);
        prepareNewLevel();
    }
    
    
    public void changeHeroDirections() {
        hero.directions.clear();
        for (var direction : Direction.values()) {
            for (var keyCode : direction.keyCodes) {
                if (gameScreen.isKeyPressed(keyCode)) {
                    hero.directions.add(direction);
                }
            }
        }
    }
    
    public Collection<Shape> getShapesAfterMove() {
        characterMover.moveEnemies(gameScreen.getDeltaTime());
        changeHeroDirections();
        characterMover.moveHero(gameScreen.getDeltaTime());
        adjustShapesPositionToMatchCharacters();
        return shapes.values();
    }
    
    private void adjustShapesPositionToMatchCharacters() {
        for (var entry : shapes.entrySet()) {
            var shape = entry.getValue();
            var character = entry.getKey();
            shape.rectangle.x = character.x;
            shape.rectangle.y = character.y;
        }
    }
    
    private void prepareNewLevel() {
        var levelNumber = level == null ? 1 : level.number + 1;
        hero = new Character(Role.HERO);
        List<Character> characters = new LinkedList<>();
        hero.x = (GameScreen.CAMERA_WIDTH - GameScreen.HERO_WIDTH) / 2;
        hero.y = (GameScreen.CAMERA_HEIGHT - GameScreen.HERO_HEIGHT) / 2;
        hero.speed = 200;
        characters.add(hero);
        for (int i = 0; i < levelNumber; i++) {
            var enemy = new Character(Role.ENEMY);
            enemy.x = MathUtils.random(0, GameScreen.CAMERA_WIDTH - GameScreen.ENEMY_WIDTH);
            enemy.y = MathUtils.random(0, GameScreen.CAMERA_HEIGHT - GameScreen.ENEMY_HEIGHT);
            enemy.speed = 100;
            characters.add(enemy);
        }
        level = new Level(levelNumber, characters);
        initializeCharactersMap();
        characterMover = new CharacterMover(characters);
    }

    private void initializeCharactersMap() {
        shapes.clear();
        addHeroShapeForCurrentLevel();
        addEnemyShapesForCurrentLevel();
    }

    private void addHeroShapeForCurrentLevel() {
        var rectangle = new Rectangle(hero.x, hero.y, GameScreen.HERO_WIDTH, GameScreen.HERO_HEIGHT);
        shapes.put(hero, new Shape(heroImage, rectangle));
    }

    private void addEnemyShapesForCurrentLevel() {
        level.characters.stream().filter(c -> c.role == Role.ENEMY).forEach(enemy -> {
            shapes.put(enemy, new Shape(enemyImage, new Rectangle(enemy.x, enemy.y, GameScreen.ENEMY_WIDTH, GameScreen.ENEMY_HEIGHT)));
        });
    }
}
