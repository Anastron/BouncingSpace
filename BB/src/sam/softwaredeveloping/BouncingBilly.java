package sam.softwaredeveloping;

import sam.bouncingbilly.handlers.Content;
import sam.bouncingbilly.handlers.GameStateManager;
import sam.bouncingbilly.handlers.MyInput;
import sam.bouncingbilly.handlers.MyInputProcessor;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class BouncingBilly implements ApplicationListener {
	
	public static final String TITLE = "Bouncing Irgendwas";
	public static final String VERSION = "0.1.3";
	public static final int V_WIDTH = 320;
	public static final int V_HEIGHT = 240;
	public static final int SCALE = 2;
	
	public static final float STEP = 1 / 60f;
	
	private SpriteBatch sb;
	private OrthographicCamera cam;
	private OrthographicCamera hudCam;
	
	public static Preferences prefsPoints;
	
	private GameStateManager gsm;
	
	public static Content res;
	
	public void create() {
		
		prefsPoints = Gdx.app.getPreferences("points");
		
		
		Gdx.input.setInputProcessor(new MyInputProcessor());
		
		res = new Content();
		res.loadTexture("res/images/billy.png", "billy");
		res.loadTexture("res/images/crystal.png", "crystal");
		res.loadTexture("res/images/hud.png", "hud");
		res.loadTexture("res/images/menu2.png", "menu");
		res.loadTexture("res/images/bgs.png", "bgs");
		
		// Sounds
		res.loadSound("res/sfx/crystal.wav");
		res.loadSound("res/sfx/hit.wav");
		res.loadSound("res/sfx/jump.wav");
		res.loadSound("res/sfx/changeblock.wav");
		res.loadSound("res/sfx/levelselect.wav");
		
		// can you here it?
		// dont work sry...
/*		res.loadMusic("res/music/bbsong.ogg");
		res.getMusic("bbsong").setLooping(true);
		res.getMusic("bbsong").setVolume(0.5f);
		res.getMusic("bbsong").play();
*/		
		sb = new SpriteBatch();
		cam = new OrthographicCamera();
		cam.setToOrtho(false, V_WIDTH, V_HEIGHT);
		hudCam = new OrthographicCamera();
		hudCam.setToOrtho(false, V_WIDTH, V_HEIGHT);
		
		gsm = new GameStateManager(this);
		
	}
	
	public void render() {
		
		Gdx.graphics.setTitle(TITLE + " v." + VERSION + " -- FPS: " + Gdx.graphics.getFramesPerSecond());
		
		gsm.update(Gdx.graphics.getDeltaTime());
		gsm.render();
		MyInput.update();
		
		
	}
	
	public void dispose() {
		
	}
	
	public SpriteBatch getSpriteBatch() { return sb; }
	public OrthographicCamera getCamera() { return cam; }
	public OrthographicCamera getHUDCamera() { return hudCam; }
	
	public void resize(int w, int h) {}
	public void pause() {}
	public void resume() {}
	
}
