package GameStarter;

import MVVM.view.GameLauncher;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.graphics.glutils.HdpiMode;

// Please note that on macOS your application needs to be started with the -XstartOnFirstThread JVM argument
public class DesktopLauncher {
    
    public static final int HORIZONTAL_SCREEN_SIZE = 800; 
    public static final int VERTICIAL_SCREEN_SIZE= 800; 
    
	public static void main (String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setForegroundFPS(60);
		config.useVsync(true);
		config.setTitle("rloop");
		config.setHdpiMode(HdpiMode.Logical);
		config.setFullscreenMode(Lwjgl3ApplicationConfiguration.getDisplayMode(Lwjgl3ApplicationConfiguration.getPrimaryMonitor()));
		//config.setWindowedMode(1280, 720);
//		new Lwjgl3Application(new rloop(), config);
		new Lwjgl3Application(new GameLauncher(), getConfig());
	}
        
        
    private static Lwjgl3ApplicationConfiguration getConfig() {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setTitle("Drop");
        config.setWindowedMode(HORIZONTAL_SCREEN_SIZE, VERTICIAL_SCREEN_SIZE);
        config.useVsync(true);
        config.setForegroundFPS(60);
        return config;
    }
}
