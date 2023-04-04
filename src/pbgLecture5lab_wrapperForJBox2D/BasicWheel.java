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

public class BasicWheel extends BasicParticle{
	/* Author: Michael Fairbank
	 * Creation Date: 2016-02-05 (JBox2d version)
	 * Significant changes applied:
	 */
	float radius;

	public BasicWheel(float sx, float sy, float vx, float vy, float radius, Color col, float mass, float linearDragForce) {
		super(sx, sy, vx, vy, radius,col ,mass, linearDragForce);
		this.radius = radius;
	}

	public void draw(Graphics2D g) {
		super.draw(g);
		int x = BasicPhysicsEngineUsingBox2D.convertWorldXtoScreenX(body.getPosition().x);
		int y = BasicPhysicsEngineUsingBox2D.convertWorldYtoScreenY(body.getPosition().y);
		float angle = body.getAngle();
		float cosT = (float)Math.cos(angle);
		float sinT = (float)Math.sin(angle);
		Vec2 lineEnd =new Vec2 (0,radius);
		lineEnd = new Vec2(lineEnd.x*cosT+lineEnd.y*-sinT,lineEnd.x*sinT+lineEnd.y*cosT);
		g.setColor(Color.WHITE);
		g.drawLine(x, y, BasicPhysicsEngineUsingBox2D.convertWorldXtoScreenX(body.getPosition().x+lineEnd.x), BasicPhysicsEngineUsingBox2D.convertWorldYtoScreenY(body.getPosition().y+lineEnd.y));
		//g.drawLine(x, y, (x+20) * angle*10, (y+20)  * angle*10);
	}

	
}
