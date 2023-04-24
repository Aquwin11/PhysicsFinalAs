package pbgLecture5lab_wrapperForJBox2D;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;


public class BasicPolygon  {
	/* Author: Michael Fairbank
	 * Creation Date: 2016-02-05 (JBox2d version)
	 * Significant changes applied:
	 */
	public final float ratioOfScreenScaleToWorldScale;

	private final float rollingFriction,mass;
	public final Color col;
	protected final Body body;
	public final Path2D.Float polygonPath;
	public Body manipulatableBody;
	public boolean isWalled;
	
	//Added more variables to the constructor provide more flexibility when creating objects
	public BasicPolygon(float sx, float sy, float vx, float vy, float radius, BodyType bodyType,Color col, float mass, float rollingFriction, int numSides, Body destroyableBody,short cBits, short mBits,short groupIndex) {
		this(sx, sy, vx, vy, radius, bodyType,col, mass, rollingFriction,mkRegularPolygon(numSides, radius),numSides,destroyableBody,cBits,mBits,groupIndex);
	}
	public BasicPolygon(float sx, float sy, float vx, float vy, float radius, BodyType bodyType,Color col, float mass, float rollingFriction, Path2D.Float polygonPath, int numSides , Body D,short cBits, short mBits,short groupIndex) {
		World w=BasicPhysicsEngineUsingBox2D.world; // a Box2D object
		BodyDef bodyDef = new BodyDef();  // a Box2D object
		bodyDef.type = bodyType; // this says the physics engine is to move it automatically
		bodyDef.position.set(sx, sy);
		bodyDef.linearVelocity.set(vx, vy);
		bodyDef.angularDamping = 0.1f;
		this.body = w.createBody(bodyDef);
		PolygonShape shape = new PolygonShape();
		Vec2[] vertices = verticesOfPath2D(polygonPath, numSides);
		shape.set(vertices, numSides);
		FixtureDef fixtureDef = new FixtureDef();// This class is from Box2D
		fixtureDef.shape = shape;
		fixtureDef.density = (float) (mass/((float) numSides)/2f*(radius*radius)*Math.sin(2*Math.PI/numSides));
		fixtureDef.friction = 1.0f;// this is surface friction;
		fixtureDef.restitution = 0.5f;
		body.createFixture(fixtureDef);
		body.setUserData(this);
		manipulatableBody = D;
		this.rollingFriction=rollingFriction;
		this.mass=mass;
		this.ratioOfScreenScaleToWorldScale=BasicPhysicsEngineUsingBox2D.convertWorldLengthToScreenLength(1);
		this.col=col;
		this.polygonPath=polygonPath;
	}

	public BasicPolygon(float sx, float sy, float width, float hight, Color col, float mass, float rollingFriction , short cBits, short mBits,short groupIndex) {
		World w=BasicPhysicsEngineUsingBox2D.world; // a Box2D object
		BodyDef bodyDef = new BodyDef();  // a Box2D object
		bodyDef.type = BodyType.DYNAMIC; // this says the physics engine is to move it automatically
		bodyDef.position.set(sx, sy);
		bodyDef.linearVelocity.set(0, 0);
		bodyDef.angularDamping = 0.1f;
		this.body = w.createBody(bodyDef);
		PolygonShape shape = new PolygonShape();
		Vec2[] vertices = verticesOfRectangle(width, hight);
		shape.set(vertices, 4);
		FixtureDef fixtureDef = new FixtureDef();// This class is from Box2D
		fixtureDef.shape = shape;
		fixtureDef.density = (float) (mass/2f*(width*hight));
		fixtureDef.friction = 2.6f;// this is surface friction;
		fixtureDef.restitution = 0.5f;
		
		fixtureDef.filter.categoryBits = cBits;
		fixtureDef.filter.maskBits = mBits;
		fixtureDef.filter.groupIndex = groupIndex;
		body.setUserData(this);
		


		this.rollingFriction=rollingFriction;
		this.mass=mass;
		this.ratioOfScreenScaleToWorldScale=BasicPhysicsEngineUsingBox2D.convertWorldLengthToScreenLength(1);
		this.col=col;
		this.polygonPath=mkRegularPolygon(width, hight);
		body.createFixture(fixtureDef);;
	}



	public void draw(Graphics2D g) {
		g.setColor(col);
		Vec2 position = body.getPosition();

		float angle= body.getAngle(); 

		AffineTransform af = new AffineTransform();
		af.translate(BasicPhysicsEngineUsingBox2D.convertWorldXtoScreenX(position.x), BasicPhysicsEngineUsingBox2D.convertWorldYtoScreenY(position.y));
		af.scale(ratioOfScreenScaleToWorldScale, -ratioOfScreenScaleToWorldScale);// there is a minus in here because screenworld is flipped upsidedown compared to physics world
		af.rotate(angle); 
		Path2D.Float p = new Path2D.Float (polygonPath,af);
		g.fill(p);
	}



	public void notificationOfNewTimestep() {
		if (rollingFriction>0) {
			Vec2 rollingFrictionForce=new Vec2(body.getLinearVelocity());
			rollingFrictionForce=rollingFrictionForce.mul(-rollingFriction*mass);
			body.applyForceToCenter(rollingFrictionForce);
		}
	}
	
	// Vec2 vertices of Path2D
	public static Vec2[] verticesOfPath2D(Path2D.Float p, int n) {
		Vec2[] result = new Vec2[n];
		float[] values = new float[6];
		PathIterator pi = p.getPathIterator(null);
		int i = 0;
		while (!pi.isDone() && i < n) {
			int type = pi.currentSegment(values);
			if (type == PathIterator.SEG_LINETO) {
				result[i++] = new Vec2(values[0], values[1]);
			}
			pi.next();
		}
		return result;
	}

	public static  Vec2[] verticesOfRectangle(float w, float h) {
		Vec2[] result = new Vec2[4];
		result[0] = new Vec2(0, 0);
		result[1] = new Vec2(0, h);
		result[2] = new Vec2(w, h);
		result[3] = new Vec2(w, 0);
		return result;
	}


	public static Path2D.Float mkRegularPolygon(int n, float radius) {
		Path2D.Float p = new Path2D.Float();
		p.moveTo(radius, 0);
		for (int i = 0; i < n; i++) {
			float x = (float) (Math.cos((Math.PI * 2 * i) / n) * radius);
			float y = (float) (Math.sin((Math.PI * 2 * i) / n) * radius);
			p.lineTo(x, y);
		}
		p.closePath();
		return p;
	}

	public static Path2D.Float mkRegularPolygon(float w, float h) {
		Path2D.Float p = new Path2D.Float();
		p.moveTo(0, 0);
		p.lineTo(0, h);
		p.lineTo(w,h);
		p.lineTo(w, 0);

		p.closePath();
		return p;
	}
	
	
	public void changewallPosition()
	{
		if(manipulatableBody!=null)
		{
			manipulatableBody.setLinearVelocity(new Vec2(0,50f));
		}
	}
	
}
