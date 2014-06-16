package sam.bouncingbilly.handlers;

public class MyInput {

	public static boolean[] keys;
	public static boolean[] pkeys;

	public static boolean down;
	public static boolean pdown;

	public static int x;
	public static int y;

	public static final int NUM_KEYS = 5;
	public static final int BTN_JUMP = 0;
	public static final int BTN_UP = 1;
	public static final int BTN_DOWN = 2;
	public static final int BTN_LEFT = 3;
	public static final int BTN_RIGHT = 4;

	static {
		keys = new boolean[NUM_KEYS];
		pkeys = new boolean[NUM_KEYS];
	}

	public static void update() {
		pdown = down;

		for (int i = 0; i < NUM_KEYS; i++) {
			pkeys[i] = keys[i];
		}
	}

	public static void setKey(int i, boolean b) {
		keys[i] = b;
	}

	public static boolean isDown(int i) {
		return keys[i];
	}

	public static boolean isPressed(int i) {
		return keys[i] && !pkeys[i];
	}

	public static boolean isPressed() {
		return down && !pdown;
	}

	public static boolean isDown() {
		return down;
	}

	public static boolean isReleased() {
		return !down && pdown;
	}

}
