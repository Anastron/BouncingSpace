package sam.bouncingbilly.states;



import static sam.bouncingbilly.handlers.B2DVars.PPM;
import sam.bouncingbilly.handlers.Animation;
import sam.bouncingbilly.handlers.Background;
import sam.bouncingbilly.handlers.GameButton;
import sam.bouncingbilly.handlers.GameStateManager;
import sam.softwaredeveloping.BouncingBilly;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;


public class Menu extends GameState {
	
	private boolean debug = false;
	
	private Background bg;
	private Animation animation;
	private GameButton playButton;
	
	private World world;
	private Box2DDebugRenderer b2dRenderer;
	
	private TextureRegion crystal;
	private TextureRegion[] font;
	
	private int allpoints;
	
	
	public Menu(GameStateManager gsm) {
		
		super(gsm);
		
/*		int allPoints2 = BouncingBilly.prefsPoints.getInteger("1_lvlPoints");
		BouncingBilly.prefsPoints.putInteger("allPoints", allPoints2);
		BouncingBilly.prefsPoints.flush();	
*/		
		allpoints = BouncingBilly.prefsPoints.getInteger("allPoints");
		
		Texture tex = BouncingBilly.res.getTexture("menu");
		bg = new Background(new TextureRegion(tex), cam, 1f);
		bg.setVector(-20, 0);
		
/*		tex = BouncingBilly.res.getTexture("billy");
		TextureRegion[] reg = new TextureRegion[4];
		for(int i = 0; i < reg.length; i++) {
			reg[i] = new TextureRegion(tex, i * 32, 0, 32, 32);
		}
		
		animation = new Animation(reg, 1 / 12f);
*/		
		tex = BouncingBilly.res.getTexture("hud");
		
		// playerbutton from hud
		playButton = new GameButton(new TextureRegion(tex, 0, 34, 58, 27), 160, 145, cam);
		
		// crystal from hud
		crystal = new TextureRegion(tex, 80, 0, 16, 16);
		
		// font from hud
		font = new TextureRegion[11];
		for(int i = 0; i < 6; i++) {
			font[i] = new TextureRegion(tex, 32 + i * 9, 16, 9, 9);
		}
		for(int i = 0; i < 5; i++) {
			font[i + 6] = new TextureRegion(tex, 32 + i * 9, 25, 9, 9);
		}
		
		cam.setToOrtho(false, BouncingBilly.V_WIDTH, BouncingBilly.V_HEIGHT);
		
		world = new World(new Vector2(0, -9.8f * 5), true);
		//world = new World(new Vector2(0, 0), true);
		b2dRenderer = new Box2DDebugRenderer();
		
//		createTitleBodies();
		
	}

	@Override
	public void handleInput() {
		// mouse/touch input
		if(playButton.isClicked()) {
//			BouncingBilly.res.getSound("levelselect").play();
			gsm.setState(GameStateManager.LEVEL);
		}
		
	}

	@Override
	public void update(float dt) {
		handleInput();
		
		world.step(dt / 5, 8, 3);
		
		bg.update(dt);
//		animation.update(dt);
		
		playButton.update(dt);
		
	}

	@Override
	public void render() {
		sb.setProjectionMatrix(cam.combined);
		
		// draw background
		bg.render(sb);
		
		// draw button
		playButton.render(sb);
		
		// draw billy
		sb.begin();
//		sb.draw(animation.getFrame(), 146, 31);
		
		// draw all crystal
		int xi = 132;
		
		sb.draw(crystal, xi, 172);
		drawString(sb, "" + allpoints, xi + 32, 175);
		
		sb.end();
		
		// debug draw box2d
		if(debug) {
			cam.setToOrtho(false, BouncingBilly.V_WIDTH / PPM, BouncingBilly.V_HEIGHT / PPM);
			b2dRenderer.render(world, cam.combined);
			cam.setToOrtho(false, BouncingBilly.V_WIDTH, BouncingBilly.V_HEIGHT);
		}
		
/*		// draw title
		for(int i = 0; i < blocks.size; i++) {
			blocks.get(i).render(sb);
		}
*/		
	}
	
	private void drawString(SpriteBatch sb, String s, float x, float y) {
		for(int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			if(c == '/'){
				c = 10;
			}
			else if(c >= '0' && c <= '9'){
				c -= '0';
			}
			else continue;
			sb.draw(font[c], x + i * 9, y);
		}
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}
}