package MVVM.view;

import MVVM.viewmodel.GameManager;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.ScreenUtils;

public class MainMenuScreen implements Screen {

    final Game game;

    OrthographicCamera camera;
    private GameManager gameManager;
    private SpriteBatch batch;
    private BitmapFont font;
    private TextureRegion button;

    public MainMenuScreen(GameManager gameManager) {
        this.gameManager = gameManager;
        game = gameManager.game;
        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 480);
        font = new BitmapFont();
        button = new TextureRegion(new Texture("exitButton.png"), 100, 50);
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
        font.draw(batch, "Welcome !!! ", 100, 250);
        font.draw(batch, "Tap anywhere to go to the next level!", 100, 200);
        batch.draw(button.getTexture(), 100, 100, button.getRegionWidth(), button.getRegionHeight());
        batch.end();

        if (Gdx.input.isTouched()) {
            checkIfButtonIsPresses();
            dispose();
            gameManager.startNewLevel();
        }
    }

    private void checkIfButtonIsPresses(){
        int mouseX = Gdx.input.getX();
        int mouseY = Gdx.graphics.getHeight() - Gdx.input.getY();
        if(mouseX >= 100 && mouseX <= 200){
            if(mouseY >= 170 && mouseY  <= 250){
                dispose();
                Gdx.app.exit();
            }
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