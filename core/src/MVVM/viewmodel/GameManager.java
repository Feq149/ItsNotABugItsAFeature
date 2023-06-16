package MVVM.viewmodel;

import MVVM.model.*;
import MVVM.model.Character;
import MVVM.view.DefeatScreen;
import MVVM.view.GameScreen;
import MVVM.view.MainMenuScreen;
import MVVM.view.VictoryScreen;
import MVVM.view.Shape;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;

import static MVVM.model.LevelWinner.ENEMIES;
import static MVVM.model.Role.*;
import static MVVM.view.GameScreen.*;
import static com.badlogic.gdx.math.MathUtils.random;
import static java.lang.System.currentTimeMillis;
import static java.util.Optional.empty;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import java.util.*;

public class GameManager {

    public Game game;
    private Level level;
    private Map<Character, Shape> shapes = new HashMap<>();
    private CharacterMover characterMover;
    private final GameScreen gameScreen;
    private final MainMenuScreen mainMenuScreen;
    private final DefeatScreen defeatScreen;
    private final VictoryScreen victoryScreen;
    private Character hero;
    private boolean bulletFired;
    private HpBarsSupplier hpBarsSupplier = new HpBarsSupplier();
    private CollisionsDetector collisionsDetector = new CollisionsDetector();
    private void removeDeadEnemies() {
        for (var i = shapes.entrySet().iterator(); i.hasNext();) {
            Character enemy = i.next().getKey();
            if (enemy.healthPoints <= 0) {
                i.remove();
            }
        }
    }

    public static final Texture heroImage = new Texture(Gdx.files.internal("hero2.png"));
    private static final Texture enemyImage = new Texture(Gdx.files.internal("enemy2.png"));
    private static final Texture bulletImage = new Texture(Gdx.files.internal("droplet.png"));
    private static final Texture blockImage = new Texture(Gdx.files.internal("wall.png"));
    public static final Sound soundtrackMusic = Gdx.audio.newSound(Gdx.files.internal("music/soundtrack.mp3"));
    public static final Sound defeatMusic = Gdx.audio.newSound(Gdx.files.internal("music/endSound.mp3"));
    public static final Sound victoryMusic = Gdx.audio.newSound(Gdx.files.internal("music/victorySound.mp3"));

    public GameManager(Game game) {
        this.game = game;
        gameScreen = new GameScreen(this);
        mainMenuScreen = new MainMenuScreen(this);
        defeatScreen = new DefeatScreen(this);
        victoryScreen = new VictoryScreen(this);
        game.setScreen(mainMenuScreen);
    }

    public void changeScreenIfEnded() {
        var winner = determineLevelWinnerIfExists();
        if (winner.isPresent()) {
            soundtrackMusic.stop();
            if(winner.get() == ENEMIES) {
                defeatMusic.play();
                game.setScreen(defeatScreen);
            }
            else {
                victoryMusic.play();
                game.setScreen(victoryScreen);
            }
        }
    }

    public void startNewLevel() {
        prepareNewLevel(1);
        game.setScreen(gameScreen);
    }

    public void startNextLevel() {
        int nextLevelNumber = level == null ? 1 : level.number + 1;
        prepareNewLevel(nextLevelNumber);
        game.setScreen(gameScreen);
    }

    public Collection<Shape> getShapesAfterMove() {
        characterMover.moveEnemies(gameScreen.getDeltaTime());
        var newEnemyBullets = fireByEnemiesAndReturnNewBullets();
        for (var bullet : newEnemyBullets) {
            shapes.put(bullet, new Shape(bulletImage,
                    new Rectangle(bullet.x, bullet.y, BULLET_RECTANGLE_SIZE, BULLET_RECTANGLE_SIZE)));
        }
        changeHeroDirections();
        if (bulletFired) {
            addBulletShotByHero();
        }
        characterMover.moveHero(gameScreen.getDeltaTime());
        shapes.entrySet().stream().filter(e -> e.getKey().role == ENEMY || e.getKey().role == HERO)
                .forEach(e -> {
                    var character = e.getKey();
                    var shape = e.getValue();
                    var blocksRunInto = collisionsDetector.fetchBlocksCharacterRanInto(shape, shapes);
                    for(var block : blocksRunInto) {
                        characterMover.fixCharacterChoordinatesToStayClearOfBlock(character, block,
                                shape.rectangle.width, shape.rectangle.height);
                    }
                });
        var bulletOutOfBounds = characterMover.moveBulletsAndReturnBulletsOutOfBounds(gameScreen.getDeltaTime());
        bulletOutOfBounds.forEach(bullet -> shapes.remove(bullet));

        var bulletsThatHit = collisionsDetector.detectHitsAndReturnBulletsToRemove(shapes.keySet());
        bulletsThatHit.forEach(bullet -> shapes.remove(bullet));
        removeDeadEnemies();
        adjustShapesPositionToMatchCharacters();
        List<Shape> hpBars = hpBarsSupplier.generateHpBars(shapes.keySet());
        var allShapes = new LinkedList<>(shapes.values());
        allShapes.addAll(hpBars);
        return allShapes;
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

    public void fireByHero() {
        long currentTime = currentTimeMillis();
        if (currentTime - hero.timeOfLastShotInMillis > hero.coolDownPeriodInMillis) {
            bulletFired = true;
            hero.timeOfLastShotInMillis = currentTime;
        }
    }

    private List<Character> fireByEnemiesAndReturnNewBullets() {
        long currentTime = currentTimeMillis();
        List<Character> bullets = new LinkedList<>();
        shapes.keySet().stream().filter(c -> c.role == ENEMY).forEach(enemy -> {
            if (currentTime - enemy.timeOfLastShotInMillis > enemy.coolDownPeriodInMillis) {
                enemy.timeOfLastShotInMillis = currentTime;
                var bullet = new Character(ENEMY_BULLET);
                bullet.x = enemy.x;
                bullet.y = enemy.y;
                bullet.speed = enemy.speed;
                bullet.speedVector = new Vector2((hero.x - enemy.x), hero.y - enemy.y);
                bullet.speedVector.limit(bullet.speed);
                bullets.add(bullet);
            }
        });
        return bullets;
    }

    private void addBulletShotByHero() {
        bulletFired = false;
        if (hero.directions.isEmpty()) {
            return;
        }
        var bullet = new Character(HERO_BULLET);
        bullet.x = hero.x;
        bullet.y = hero.y;
        bullet.speed = 2 * hero.speed;
        bullet.directions = new HashSet<>(hero.directions);
        shapes.put(bullet, new Shape(bulletImage, new Rectangle(hero.x, hero.y, 20, 20)));
    }

    private void adjustShapesPositionToMatchCharacters() {
        for (var entry : shapes.entrySet()) {
            var shape = entry.getValue();
            var character = entry.getKey();
            shape.rectangle.x = character.x;
            shape.rectangle.y = character.y;
        }
    }

    private void prepareNewLevel(int levelNumber) {
        soundtrackMusic.loop();
        hero = new Character(HERO);
        List<Character> characters = new LinkedList<>();
        hero.x = (CAMERA_WIDTH - HERO_WIDTH) / 2;
        hero.y = (CAMERA_HEIGHT - HERO_HEIGHT) / 2;
        hero.coolDownPeriodInMillis = 1000;
        hero.speed = 200;
        characters.add(hero);
        for (int i = 0; i < levelNumber; i++) {
            var enemy = new Character(ENEMY);
            enemy.x = random(0, CAMERA_WIDTH - ENEMY_WIDTH);
            enemy.y = random(0, CAMERA_HEIGHT - ENEMY_HEIGHT);
            enemy.coolDownPeriodInMillis = 1000;
            enemy.speed = 10 * levelNumber;
            characters.add(enemy);
        }
        for (int i = 0; i < 10; i++) {
            Character block = new Character(BLOCK);
            block.x = random(0, CAMERA_WIDTH - BLOCK_WIDTH);
            block.y = random(0, CAMERA_HEIGHT - BLOCK_HEIGHT);
            characters.add(block);
        }
        level = new Level(levelNumber, characters);
        initializeShapesMap();
    }

    private void initializeShapesMap() {
        shapes.clear();
        addHeroShapeForCurrentLevel();
        addEnemyShapesForCurrentLevel();
        addBlocksForCurrentLevel();
        characterMover = new CharacterMover(shapes);
    }
    private void addBlocksForCurrentLevel() {
        level.characters.stream().filter(c -> c.role == BLOCK).forEach(block -> {
            shapes.put(block, new Shape(blockImage, new Rectangle(block.x, block.y, BLOCK_WIDTH, BLOCK_HEIGHT)));
        });
    }
    private void addHeroShapeForCurrentLevel() {
        var rectangle = new Rectangle(hero.x, hero.y, HERO_WIDTH, HERO_HEIGHT);
        shapes.put(hero, new Shape(heroImage, rectangle));
    }

    private void addEnemyShapesForCurrentLevel() {
        level.characters.stream().filter(c -> c.role == ENEMY).forEach(enemy -> {
            shapes.put(enemy, new Shape(enemyImage, new Rectangle(enemy.x, enemy.y, ENEMY_WIDTH, ENEMY_HEIGHT)));
        });
    }

    private Optional<LevelWinner> determineLevelWinnerIfExists() {
        if (hero.healthPoints <= 0) {
            return Optional.of(ENEMIES);
        }
        Optional<LevelWinner> winner = Optional.of(LevelWinner.HERO);
        for (Character enemy : shapes.keySet().stream().filter(e -> e.role == ENEMY).toList()) {
            if (enemy.healthPoints > 0) {
                winner = empty();
            }
        }
        return winner;
    }
}