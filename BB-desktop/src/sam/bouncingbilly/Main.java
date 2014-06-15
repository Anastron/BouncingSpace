package sam.bouncingbilly;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import sam.softwaredeveloping.BouncingBilly;

public class Main {
	public static void main(String[] args) {
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "BouncingBilly";
		cfg.width = 480;
		cfg.height = 320;
		
		new LwjglApplication(new BouncingBilly(), cfg);
	}
}
