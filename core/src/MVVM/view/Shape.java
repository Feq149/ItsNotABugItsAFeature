package MVVM.view;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;

public class Shape {

    public Texture texture;
    public Rectangle rectangle;

    public Shape(Texture texture, Rectangle rectangle) {
        this.texture = texture;
        this.rectangle = rectangle;
    }


}