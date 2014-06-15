package sam.bouncingbilly.handlers;

import java.util.Stack;

import sam.softwaredeveloping.BouncingBilly;
import sam.bouncingbilly.states.GameState;
import sam.bouncingbilly.states.LevelSelect;
import sam.bouncingbilly.states.Menu;
import sam.bouncingbilly.states.Play;

public class GameStateManager {
	
	private BouncingBilly game;
	
	private Stack<GameState> gameStates;
	
	public static final int MENU = 83774392;
	public static final int PLAY = 912837;
	public static final int LEVEL = 91231231;
	
	public GameStateManager(BouncingBilly game) {
		this.game = game;
		gameStates = new Stack<GameState>();
		pushState(MENU);
	}
	
	public BouncingBilly game() { return game; }
	
	public void update(float dt) {
		gameStates.peek().update(dt);
	}
	
	public void render() {
		gameStates.peek().render();
	}
	
	private GameState getState(int state) {
		if(state == MENU) return new Menu(this);
		if(state == PLAY) return new Play(this);
		if(state == LEVEL) return new LevelSelect(this);
		return null;
	}
	
	public void setState(int state) {
		popState();
		pushState(state);
	}
	
	public void pushState(int state) {
		gameStates.push(getState(state));
	}
	
	public void popState() {
		GameState g = gameStates.pop();
		g.dispose();
	}
	
}















