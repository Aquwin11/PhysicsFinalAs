package pbgLecture5lab_wrapperForJBox2D;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class BasicKeyListener extends KeyAdapter {
	/* Author: Michael Fairbank
	 * Creation Date: 2016-01-28
	 * Significant changes applied:
	 */
	private static boolean rotateRightKeyPressed, rotateLeftKeyPressed, thrustKeyPressed,moveleft,moveright,SpacePressed; 

	public static boolean isRotateRightKeyPressed() {
		return rotateRightKeyPressed;
	}

	public static boolean isRotateLeftKeyPressed() {
		return rotateLeftKeyPressed;
	}

	public static boolean isThrustKeyPressed() {
		return thrustKeyPressed;
	}
	
	public static boolean isMoveLeft() {
		return moveleft;
	}
	public static boolean isMoveRight() {
		return moveright;
	}
	public static boolean isSpacePressed() {
		return SpacePressed;
	}

	public void keyPressed(KeyEvent e) {
		int key = e.getKeyCode();
		switch (key) {
		case KeyEvent.VK_UP:
			thrustKeyPressed=true;
			break;
		case KeyEvent.VK_LEFT:
			rotateLeftKeyPressed=true;
			break;
		case KeyEvent.VK_RIGHT:
			rotateRightKeyPressed=true;
			break;
		case KeyEvent.VK_A:
			moveleft=true;
			break;
		case KeyEvent.VK_D:
			moveright=true;
			break;
		case KeyEvent.VK_SPACE:
			SpacePressed=true;
			break;
		}
		
		
			
	}

	public void keyReleased(KeyEvent e) {
		int key = e.getKeyCode();
		switch (key) {
		case KeyEvent.VK_UP:
			thrustKeyPressed=false;
			break;
		case KeyEvent.VK_LEFT:
			rotateLeftKeyPressed=false;
			break;
		case KeyEvent.VK_RIGHT:
			rotateRightKeyPressed=false;
			break;
		case KeyEvent.VK_A:
			moveleft=false;
			break;
		case KeyEvent.VK_D:
			moveright=false;
			break;
		case KeyEvent.VK_SPACE:
			SpacePressed=false;
			break;
		}
	}
}
