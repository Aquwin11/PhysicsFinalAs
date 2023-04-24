package pbgLecture5lab_wrapperForJBox2D;

import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;

import javax.swing.event.MouseInputAdapter;

import org.jbox2d.callbacks.QueryCallback;
import org.jbox2d.collision.AABB;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.joints.MouseJoint;
import org.jbox2d.dynamics.joints.MouseJointDef;

public class BasicMouseListener extends MouseInputAdapter {
	/* Author: Michael Fairbank
	 * Creation Date: 2016-01-28
	 * Significant changes applied: 2016-02-10 added mouseJoint code to allow dragging of bodies
	 */
	private static int mouseX, mouseY;
	private static boolean RightmouseButtonPressed;
	private static boolean LeftmouseButtonPressed;

	private static MouseJoint mouseJoint;
	
	private final BasicPhysicsEngineUsingBox2D game;
	public BasicMouseListener(BasicPhysicsEngineUsingBox2D game){
		this.game = game;
		
	}
	public void mouseMoved(MouseEvent e) {
		mouseX=e.getX();
		mouseY=e.getY();
		RightmouseButtonPressed=false;
		if (mouseJoint!=null) {
			// we're obviously not dragging any more, so drop the current mouseJoint
			linkMouseDragEventToANewMouseJoint(null);
		}
	}
	float mousePressed=0;
	public boolean isRightMouseButtonPressed() {
		
		return RightmouseButtonPressed;
	}
public boolean isLeftMouseButtonPressed() {
		
		return LeftmouseButtonPressed;
	}
	public void mousePressed(MouseEvent e)
	{
		//LeftMouseButton
		 int modifiers = e.getModifiers();
         if ((modifiers & e.BUTTON1_MASK) == InputEvent.BUTTON1_MASK) {
        	 LeftmouseButtonPressed=true;
        	 RightmouseButtonPressed=false;
         }
         if ((modifiers & InputEvent.BUTTON2_MASK) == InputEvent.BUTTON2_MASK) {
         }
         //Right mouse Button
         if ((modifiers & InputEvent.BUTTON3_MASK) == InputEvent.BUTTON3_MASK) {
        	 	LeftmouseButtonPressed=false;
				Vec2 pointerLocator = getWorldCoordinatesOfMousePointer();
				// adding offset for compensating for the view functions translate
	             game.linex2=BasicPhysicsEngineUsingBox2D.convertWorldXtoScreenX(pointerLocator.x)-game.SCREEN_WIDTH + game.linex1;// adding offset for compensating for the view fuctions translate
	             game.liney2=BasicPhysicsEngineUsingBox2D.convertWorldYtoScreenY(pointerLocator.y)-(int)(game.SCREEN_HEIGHT/1.25f) + game.liney1;
	             game.showLine=true;
	             RightmouseButtonPressed = true;
         }
		super.mousePressed(e);
	}
	public void RightmouseIsFalse()
	{
		RightmouseButtonPressed=false;
	}
	public void mouseReleased(MouseEvent e)
	{
		game.showLine=false;
		RightmouseButtonPressed = false;
		LeftmouseButtonPressed=false;
		super.mouseReleased(e);
	}

	//World cordinates of the mouse pointer
	public static Vec2 getWorldCoordinatesOfMousePointer() {
		
		return new Vec2(BasicPhysicsEngineUsingBox2D.convertScreenXtoWorldX(mouseX),
				BasicPhysicsEngineUsingBox2D.convertScreenYtoWorldY(mouseY)/*-1.0f*/);
	}
	
	double maxDistance=100f;
	public void mouseDragged(MouseEvent e) {	
		if(RightmouseButtonPressed)
		{
			mouseX=e.getX();
			mouseY=e.getY();
			Vec2 pointerLocator = getWorldCoordinatesOfMousePointer();
			// adding offset for compensating for the view functions translate
            game.linex2=BasicPhysicsEngineUsingBox2D.convertWorldXtoScreenX(pointerLocator.x)-BasicPhysicsEngineUsingBox2D.SCREEN_WIDTH + game.linex1;
            game.liney2=BasicPhysicsEngineUsingBox2D.convertWorldYtoScreenY(pointerLocator.y)-(int)(BasicPhysicsEngineUsingBox2D.SCREEN_HEIGHT/1.25f) + game.liney1;
			
		}
		Vec2 worldCoordinatesOfMousePointer = getWorldCoordinatesOfMousePointer();
		if (mouseJoint!=null) {
			// we are already dragging a body on the screen
			// update the target of the existing mouse joint.
			mouseJoint.setTarget(worldCoordinatesOfMousePointer);
		} 
		else if (BasicPhysicsEngineUsingBox2D.ALLOW_MOUSE_POINTER_TO_DRAG_BODIES_ON_SCREEN) {
			// Mouse dragging has started, so pick up the object at the mouse pointer and create a mouseJoint linked to that body.
			MouseJointDef mj=new MouseJointDef();
			Body bodyUnderMousePointer=findBodyAtWorldCoords(worldCoordinatesOfMousePointer);
			if (bodyUnderMousePointer!=null) {
				mj.bodyA=bodyUnderMousePointer;// bodyA is not used, but you have to set this to something to avoid null pointer error 
				mj.bodyB=bodyUnderMousePointer;// bodyB is the object that you want to follow the mouse pointer.
				mj.target.set(new Vec2(worldCoordinatesOfMousePointer));//This specifies the world coordinates 
				// of the point where you want to stick the pin into - this would usually correspond to 
				// the screen coordinates of the point where you first start dragging the mouse pointer. 
				mj.collideConnected=false;
				mj.maxForce = 1000 * mj.bodyB.getMass();
				mj.dampingRatio = 0;
				MouseJoint mouseJoint = (MouseJoint) BasicPhysicsEngineUsingBox2D.world.createJoint(mj);
				BasicMouseListener.linkMouseDragEventToANewMouseJoint(mouseJoint);// this tells the BasicMouseListener 
				// listener to update the target for this mouseJoint, every time the mouse is dragged.
			}

			
		}
	}

	public static void linkMouseDragEventToANewMouseJoint(MouseJoint mj) {
		if (mouseJoint!=null) {
			// tidy up and destroy old one
			BasicPhysicsEngineUsingBox2D.world.destroyJoint(mouseJoint); 
            mouseJoint = null;
		}
		mouseJoint=mj;
	}
	

	private static final AABB queryAABB = new AABB();  // This is an axis aligned bounding box (AABB)
	private static final TestQueryCallback callback = new TestQueryCallback();


	public static Body findBodyAtWorldCoords(Vec2 worldCoords) {
		// Set up a tiny axis aligned bounding box around the tiny area of screen around the mouse pointer:
		queryAABB.lowerBound.set(worldCoords.x - .001f, worldCoords.y - .001f);
		queryAABB.upperBound.set(worldCoords.x + .001f, worldCoords.y + .001f);
		callback.point.set(worldCoords);
		callback.fixture = null;
		// Now ask the world object which bodies are positioned at the point of the screen we are interested in:
		BasicPhysicsEngineUsingBox2D.world.queryAABB(callback, queryAABB);

		if (callback.fixture != null) {
			Body body = callback.fixture.getBody();
			return body;
		} else
			return null;
	}
	
	private static class TestQueryCallback implements QueryCallback {
		// This is a callback class we need to use when we are querying which objects lie under the point of the screen that we are interested in?
		public final Vec2 point;
		public Fixture fixture;

		public TestQueryCallback() {
			point = new Vec2();
			fixture = null;
		}

		@Override
		public boolean reportFixture(Fixture argFixture) {
			Body body = argFixture.getBody();
			if (body.getType() == BodyType.DYNAMIC) {
				boolean inside = argFixture.testPoint(point);
				if (inside) {
					fixture = argFixture;

					return false;
				}
			}
			return true;
		}
		
	}
}
