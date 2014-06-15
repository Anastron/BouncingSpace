package sam.bouncingbilly.handlers;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Input.Keys;
import sam.bouncingbilly.handlers.MyInput;

public class MyInputProcessor extends InputAdapter {
	
	public boolean keyDown(int k) {
		if(k == Keys.Y) {
			MyInput.setKey(MyInput.BUTTON1, true);
		}
		if(k == Keys.X) {
			MyInput.setKey(MyInput.BUTTON2, true);
		}
		return true;
	}
	
	public boolean keyUp(int k) {
		if(k == Keys.Y) {
			MyInput.setKey(MyInput.BUTTON1, false);
		}
		if(k == Keys.X) {
			MyInput.setKey(MyInput.BUTTON2, false);
		}
		return true;
	}
	
	public boolean mouseMoved(int x, int y) {
		MyInput.x = x;
		MyInput.y = y;
		return true;
	}
	
	public boolean touchDragged(int x, int y, int pointer) {
		MyInput.x = x;
		MyInput.y = y;
		MyInput.down = true;
		return true;
	}
	
	public boolean touchDown(int x, int y, int pointer, int button) {
		MyInput.x = x;
		MyInput.y = y;
		MyInput.down = true;
		return true;
	}
	
	public boolean touchUp(int x, int y, int pointer, int button) {
		MyInput.x = x;
		MyInput.y = y;
		MyInput.down = false;
		return true;
	}
	
}
