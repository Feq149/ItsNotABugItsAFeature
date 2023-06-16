package MVVM.view;

import MVVM.viewmodel.GameManager;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

public class MainMenuScreen implements Screen {

    final Game game;

    OrthographicCamera camera;
    private GameManager gameManager;
    private SpriteBatch batch;
    private BitmapFont font;

    public MainMenuScreen(GameManager gameManager) {
        this.gameManager = gameManager;
        game = gameManager.game;
        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 480);
        font = new BitmapFont();
    }

    //...Rest of class omitted for succinctness.
    @Override
    public void show() {
        //throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void render(float f) {
        ScreenUtils.clear(0, 0, 0.2f, 1);

        camera.update();
        batch.setProjectionMatrix(camera.combined);

        batch.begin();
        font.draw(batch, "Welcome !!! ", 100, 150);
        font.draw(batch, "Tap anywhere to go to the next level!", 100, 100);
        batch.end();

        if (Gdx.input.isTouched()) {
            dispose();
            gameManager.menuScreenTapped();
        }
    }

    @Override
    public void resize(int i, int i1) {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
    }

}