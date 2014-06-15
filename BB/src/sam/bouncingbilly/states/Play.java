package sam.bouncingbilly.states;

import static sam.bouncingbilly.handlers.B2DVars.PPM;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

import sam.bouncingbilly.handlers.BoundedCamera;

import sam.bouncingbilly.entities.Crystal;
import sam.bouncingbilly.entities.HUD;
import sam.bouncingbilly.entities.Player;
import sam.bouncingbilly.handlers.B2DVars;
import sam.bouncingbilly.handlers.GameStateManager;
import sam.bouncingbilly.handlers.MyContactListener;
import sam.bouncingbilly.handlers.MyInput;
import sam.bouncingbilly.handlers.Background;
import sam.softwaredeveloping.BouncingBilly;

public class Play extends GameState {

	private boolean debug = false;

	private World world;
	private Box2DDebugRenderer b2dr;

	private BoundedCamera b2dCam;
	private MyContactListener cl;

	private TiledMap tileMap;
	private float tileSize;
	private int tileMapWidth;
	private int tileMapHeight;
	private OrthogonalTiledMapRenderer tmr;

	private Player player;
	private Array<Crystal> crystals;

	private Background[] backgrounds;

	private HUD hud;

	public static int level;

	public Play(GameStateManager gsm) {

		super(gsm);

		// set up box2d stuff
		world = new World(new Vector2(0, -9.81f), true);
		cl = new MyContactListener();
		world.setContactListener(cl);
		b2dr = new Box2DDebugRenderer();

		// create player
		createPlayer();

		// create tiles
		createTiles();

		// create crystals
		createCrystals();

		// create backgrounds
		Texture bgs = BouncingBilly.res.getTexture("bgs");
		TextureRegion sky = new TextureRegion(bgs, 0, 0, 320, 240);
		TextureRegion clouds = new TextureRegion(bgs, 0, 240, 320, 240);
		TextureRegion mountains = new TextureRegion(bgs, 0, 480, 320, 240);
		backgrounds = new Background[3];
		backgrounds[0] = new Background(sky, cam, 0f);
		backgrounds[1] = new Background(clouds, cam, 0.1f);
		backgrounds[2] = new Background(mountains, cam, 0.2f);

		// set up box2d cam
		b2dCam = new BoundedCamera();
		b2dCam.setToOrtho(false, BouncingBilly.V_WIDTH / PPM,
				BouncingBilly.V_HEIGHT / PPM);
		b2dCam.setBounds(0, (tileMapWidth * tileSize) / PPM, 0,
				(tileMapHeight * tileSize) / PPM);

		// set up hud
		hud = new HUD(player);
	}

	public void handleInput() {

		// player jump
		if (MyInput.isPressed(MyInput.BUTTON1)) {
			if (cl.isPlayerOnGround()) {
				player.getBody().applyForceToCenter(0, 250, true);
				BouncingBilly.res.getSound("jump").play();
			}
		}

		// mouse/touch input for android
		// left side of screen to switch blocks
		// right side of screen to jump

		if (MyInput.isPressed() && cl.isPlayerOnGround()
				&& MyInput.x > Gdx.graphics.getWidth() / 2) {
			player.getBody().applyForceToCenter(0, 250, true);
			BouncingBilly.res.getSound("jump").play();
		}

		if (MyInput.isPressed() && (MyInput.x < Gdx.graphics.getWidth() / 2)) {
			switchBlock();
			BouncingBilly.res.getSound("changeblock").play();
		}

		// switch block color
		if (MyInput.isPressed(MyInput.BUTTON2)) {
			switchBlock();
			BouncingBilly.res.getSound("changeblock").play();
		}

	}

	public void update(float dt) {

		// check input
		handleInput();

		// update box2d
		world.step(dt, 6, 2);

		// remove crystals
		Array<Body> bodies = cl.getBodiesToRemove();
		for (int i = 0; i < bodies.size; i++) {
			Body b = bodies.get(i);
			crystals.removeValue((Crystal) b.getUserData(), true);
			world.destroyBody(b);
			player.collectCrystal();
			BouncingBilly.res.getSound("crystal").play();
		}
		bodies.clear();

		player.update(dt);

		// player win
		if (player.getBody().getPosition().x * PPM > tileMapWidth * tileSize) {

			checkPoints();

			gsm.setState(GameStateManager.MENU);
		}

		// check player failed
		if (player.getBody().getPosition().y < 0) {
			BouncingBilly.res.getSound("hit").play();

			checkPoints();

			gsm.setState(GameStateManager.LEVEL);
		}
		if (player.getBody().getLinearVelocity().x < 0.001f) {
			BouncingBilly.res.getSound("hit").play();

			checkPoints();

			gsm.setState(GameStateManager.LEVEL);
		}
		if (cl.isPlayerDead()) {
			BouncingBilly.res.getSound("hit").play();

			checkPoints();

			gsm.setState(GameStateManager.LEVEL);
		}

		for (int i = 0; i < crystals.size; i++) {
			crystals.get(i).update(dt);
		}

	}

	private void checkPoints() {
		int allPoints2 = 0;
		int points = BouncingBilly.prefsPoints.getInteger(level + "_lvl_Points");

		if (points < player.getNumCrystals()) {
			BouncingBilly.prefsPoints.putInteger(level + "_lvl_Points",
					player.getNumCrystals());
			BouncingBilly.prefsPoints.flush();
		}
		for (int i = 1; i < 6; i++) {
			allPoints2 += BouncingBilly.prefsPoints.getInteger( i + "_lvl_Points");
		}
		
		BouncingBilly.prefsPoints.putInteger("allPoints", allPoints2);
		BouncingBilly.prefsPoints.flush();
	}

	public void render() {

		// clear screen
		Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);

		// set camere to follow player
		cam.position.set(player.getPosition().x * PPM + BouncingBilly.V_WIDTH
				/ 4, BouncingBilly.V_HEIGHT / 2, 0);
		cam.update();

		// draw background
		sb.setProjectionMatrix(hudCam.combined);
		for (int i = 0; i < backgrounds.length; i++) {
			backgrounds[i].render(sb);
		}

		// draw tile map
		tmr.setView(cam);
		tmr.render();

		// draw player
		sb.setProjectionMatrix(cam.combined);
		player.render(sb);

		// draw crystals
		for (int i = 0; i < crystals.size; i++) {
			crystals.get(i).render(sb);
		}

		// draw hud
		sb.setProjectionMatrix(hudCam.combined);
		hud.render(sb);

		// draw box2d
		if (debug) {
			b2dr.render(world, b2dCam.combined);
		}

	}

	public void dispose() {
	}

	private void createPlayer() {

		BodyDef bdef = new BodyDef();
		FixtureDef fdef = new FixtureDef();
		PolygonShape shape = new PolygonShape();

		// create player
		bdef.position.set(100 / PPM, 200 / PPM);
		bdef.type = BodyType.DynamicBody;
		bdef.linearVelocity.set(.8f, 0);
		Body body = world.createBody(bdef);

		shape.setAsBox(13 / PPM, 13 / PPM);
		fdef.shape = shape;
		fdef.filter.categoryBits = B2DVars.BIT_PLAYER;
		fdef.filter.maskBits = B2DVars.BIT_RED | B2DVars.BIT_CRYSTAL;
		body.createFixture(fdef).setUserData("player");

		// create foot sensor
		shape.setAsBox(13 / PPM, 2 / PPM, new Vector2(0, -13 / PPM), 0);
		fdef.shape = shape;
		fdef.filter.categoryBits = B2DVars.BIT_PLAYER;
		fdef.filter.maskBits = B2DVars.BIT_RED;
		fdef.isSensor = true;
		body.createFixture(fdef).setUserData("foot");

		// create player
		player = new Player(body);

		body.setUserData(player);

	}

	private void createTiles() {

		// load tile map
		tileMap = new TmxMapLoader().load("res/maps/level" + level + ".tmx");

		// tileMap = new TmxMapLoader().load("res/maps/test.tmx");

		tmr = new OrthogonalTiledMapRenderer(tileMap);
		tileSize = (Integer) tileMap.getProperties().get("tilewidth");

		tileMapWidth = (Integer) tileMap.getProperties().get("width");
		tileMapHeight = (Integer) tileMap.getProperties().get("height");

		TiledMapTileLayer layer;

		layer = (TiledMapTileLayer) tileMap.getLayers().get("red");
		createLayer(layer, B2DVars.BIT_RED);

		layer = (TiledMapTileLayer) tileMap.getLayers().get("green");
		createLayer(layer, B2DVars.BIT_GREEN);

		layer = (TiledMapTileLayer) tileMap.getLayers().get("blue");
		createLayer(layer, B2DVars.BIT_BLUE);

	}

	private void createLayer(TiledMapTileLayer layer, short bits) {

		BodyDef bdef = new BodyDef();
		FixtureDef fdef = new FixtureDef();

		// go through all the cells in the layer
		for (int row = 0; row < layer.getHeight(); row++) {
			for (int col = 0; col < layer.getWidth(); col++) {

				// get cell
				Cell cell = layer.getCell(col, row);

				// check if cell exists
				if (cell == null)
					continue;
				if (cell.getTile() == null)
					continue;

				// create a body + fixture from cell
				bdef.type = BodyType.StaticBody;
				bdef.position.set((col + 0.5f) * tileSize / PPM, (row + 0.5f)
						* tileSize / PPM);

				ChainShape cs = new ChainShape();
				Vector2[] v = new Vector2[3];
				v[0] = new Vector2(-tileSize / 2 / PPM, -tileSize / 2 / PPM);
				v[1] = new Vector2(-tileSize / 2 / PPM, tileSize / 2 / PPM);
				v[2] = new Vector2(tileSize / 2 / PPM, tileSize / 2 / PPM);
				cs.createChain(v);
				fdef.friction = 0;
				fdef.shape = cs;
				fdef.filter.categoryBits = bits;
				fdef.filter.maskBits = B2DVars.BIT_PLAYER;
				fdef.isSensor = false;
				world.createBody(bdef).createFixture(fdef);

			}
		}
	}

	private void createCrystals() {

		crystals = new Array<Crystal>();

		MapLayer layer = tileMap.getLayers().get("crystals");

		BodyDef bdef = new BodyDef();
		FixtureDef fdef = new FixtureDef();

		for (MapObject mo : layer.getObjects()) {

			bdef.type = BodyType.StaticBody;

			float x = (Float) mo.getProperties().get("x") / PPM;
			float y = (Float) mo.getProperties().get("y") / PPM;

			bdef.position.set(x, y);

			CircleShape cshape = new CircleShape();
			cshape.setRadius(8 / PPM);

			fdef.shape = cshape;
			fdef.isSensor = true;
			fdef.filter.categoryBits = B2DVars.BIT_CRYSTAL;
			fdef.filter.maskBits = B2DVars.BIT_PLAYER;

			Body body = world.createBody(bdef);
			body.createFixture(fdef).setUserData("crystal");

			Crystal c = new Crystal(body);
			crystals.add(c);

			body.setUserData(c);

		}

	}

	private void switchBlock() {

		Filter filter = player.getBody().getFixtureList().first()
				.getFilterData();
		short bits = filter.maskBits;

		// switch to next color
		// red -> green -> blue -> red
		if ((bits & B2DVars.BIT_RED) != 0) {
			bits &= ~B2DVars.BIT_RED;
			bits |= B2DVars.BIT_GREEN;
		} else if ((bits & B2DVars.BIT_GREEN) != 0) {
			bits &= ~B2DVars.BIT_GREEN;
			bits |= B2DVars.BIT_BLUE;
		} else if ((bits & B2DVars.BIT_BLUE) != 0) {
			bits &= ~B2DVars.BIT_BLUE;
			bits |= B2DVars.BIT_RED;
		}

		// set new mask bits
		filter.maskBits = bits;
		player.getBody().getFixtureList().first().setFilterData(filter);

		// set new mask bits for foot
		filter = player.getBody().getFixtureList().get(1).getFilterData();
		bits &= ~B2DVars.BIT_CRYSTAL;
		filter.maskBits = bits;
		player.getBody().getFixtureList().get(1).setFilterData(filter);
	}

}
