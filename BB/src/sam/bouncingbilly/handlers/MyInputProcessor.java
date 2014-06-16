package sam.bouncingbilly.handlers;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Input.Keys;
import sam.bouncingbilly.handlers.MyInput;

public class MyInputProcessor extends InputAdapter {

	@Override
	public boolean keyDown(int k) {
		if (k == Keys.SPACE) {
			MyInput.setKey(MyInput.BTN_JUMP, true);
		}
		if (k == Keys.UP) {
			MyInput.setKey(MyInput.BTN_UP, true);
		}
		if (k == Keys.DOWN) {
			MyInput.setKey(MyInput.BTN_DOWN, true);
		}
		if (k == Keys.LEFT) {
			MyInput.setKey(MyInput.BTN_LEFT, true);
		}
		if (k == Keys.RIGHT) {
			MyInput.setKey(MyInput.BTN_RIGHT, true);
		}
		return true;
	}

	@Override
	public boolean keyUp(int k) {
		if (k == Keys.SPACE) {
			MyInput.setKey(MyInput.BTN_JUMP, false);
		}
		if (k == Keys.UP) {
			MyInput.setKey(MyInput.BTN_UP, false);
		}
		if (k == Keys.DOWN) {
			MyInput.setKey(MyInput.BTN_DOWN, false);
		}
		if (k == Keys.LEFT) {
			MyInput.setKey(MyInput.BTN_LEFT, false);
		}
		if (k == Keys.RIGHT) {
			MyInput.setKey(MyInput.BTN_RIGHT, false);
		}
		return true;
	}

	@Override
	public boolean mouseMoved(int x, int y) {
		MyInput.x = x;
		MyInput.y = y;
		return true;
	}

	@Override
	public boolean touchDragged(int x, int y, int pointer) {
		MyInput.x = x;
		MyInput.y = y;
		MyInput.down = true;
		return true;
	}

	@Override
	public boolean touchDown(int x, int y, int pointer, int button) {
		MyInput.x = x;
		MyInput.y = y;
		MyInput.down = true;
		return true;
	}

	@Override
	public boolean touchUp(int x, int y, int pointer, int button) {
		MyInput.x = x;
		MyInput.y = y;
		MyInput.down = false;
		return true;
	}

}
