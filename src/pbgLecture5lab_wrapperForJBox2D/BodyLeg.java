package pbgLecture5lab_wrapperForJBox2D;

import java.awt.Color;
import java.awt.Graphics2D;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;

public class BodyLeg  {
	/* Author: Michael Fairbank
	 * Creation Date: 2016-02-05 (JBox2d version)
	 * Significant changes applied:
	 */
	public final int SCREEN_RADIUS;

	private final float linearDragForce,mass;
	public final Color col;
	protected final Body body;
	

	public BodyLeg(float sx, float sy, float vx, float vy, float radius, Color col, float mass, float linearDragForce,short cBits,short mBits, short groupIndex) {
		World w=BasicPhysicsEngineUsingBox2D.world; // a Box2D object
		BodyDef bodyDef = new BodyDef();  // a Box2D object
		bodyDef.type = BodyType.DYNAMIC; // this says the physics engine is to move it automatically
		bodyDef.position.set(sx, sy);
		bodyDef.linearVelocity.set(vx, vy);
		this.body = w.createBody(bodyDef);
		CircleShape circleShape = new CircleShape();// This class is from Box2D
		circleShape.m_radius = radius;
		FixtureDef fixtureDef = new FixtureDef();// This class is from Box2D
		fixtureDef.shape = circleShape;
		fixtureDef.density = (float) (mass/(Math.PI*radius*radius));
		fixtureDef.friction = 1.5f;// this is surface friction;
		fixtureDef.restitution = 0.0f;
		fixtureDef.filter.categoryBits = cBits;
		fixtureDef.filter.maskBits = mBits;
		fixtureDef.filter.groupIndex = groupIndex;
		body.setUserData(this);
		body.createFixture(fixtureDef);
		this.linearDragForce=linearDragForce;
		this.mass=mass;
		this.SCREEN_RADIUS=(int)Math.max(BasicPhysicsEngineUsingBox2D.convertWorldLengthToScreenLength(radius),1);
		this.col=col;
		System.out.println(fixtureDef.filter.groupIndex);
	}

	public void draw(Graphics2D g) {
		int x = BasicPhysicsEngineUsingBox2D.convertWorldXtoScreenX(body.getPosition().x);
		int y = BasicPhysicsEngineUsingBox2D.convertWorldYtoScreenY(body.getPosition().y);
		//int angle = BasicPhysicsEngineUsingBox2D.convertWorldXtoScreenX(body.getAngle());
		g.setColor(col);
		g.fillOval(x - SCREEN_RADIUS, y - SCREEN_RADIUS, 2 * SCREEN_RADIUS, 2 * SCREEN_RADIUS);
		//g.drawLine(x, y, (x+20) * angle*10, (y+20)  * angle*10);
	}



	public void notificationOfNewTimestep() {
		if (linearDragForce>0) {
			Vec2 dragForce1=new Vec2(body.getLinearVelocity());
			dragForce1=dragForce1.mul(-linearDragForce*mass);
			body.applyForceToCenter(dragForce1);
		}
		
	}
	
}
