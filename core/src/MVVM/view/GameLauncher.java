package MVVM.view;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import MVVM.viewmodel.GameManager;

public class GameLauncher extends Game {

    public SpriteBatch batch;
    public BitmapFont font;
    public GameManager gameManager;

    @Override
    public void create() {
        batch = new SpriteBatch();
        font = new BitmapFont(); // use libGDX's default Arial font
        gameManager = new GameManager(this);
    }

    @Override
    public void render() {
        super.render(); // important!
    }

    @Override
    public void dispose() {
        System.out.println("Usuwam");
        batch.dispose();
        font.dispose();
    }

}
