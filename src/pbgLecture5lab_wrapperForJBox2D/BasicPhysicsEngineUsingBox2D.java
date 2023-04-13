package pbgLecture5lab_wrapperForJBox2D;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.text.View;

import org.jbox2d.common.MathUtils;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.joints.DistanceJointDef;
import org.jbox2d.dynamics.joints.MouseJoint;
import org.jbox2d.dynamics.joints.PrismaticJoint;
import org.jbox2d.dynamics.joints.PrismaticJointDef;
import org.jbox2d.dynamics.joints.RevoluteJointDef;
import org.jbox2d.dynamics.joints.WheelJointDef;


public class BasicPhysicsEngineUsingBox2D {
	/* Author: Michael Fairbank
	 * Creation Date: 2016-02-05 (JBox2d version)
	 * Significant changes applied:
	 */
	
	// frame dimensions
	public static final int SCREEN_HEIGHT = 680;
	public static final int SCREEN_WIDTH = 640;
	public static final Dimension FRAME_SIZE = new Dimension(
			SCREEN_WIDTH, SCREEN_HEIGHT);
	public static final float WORLD_WIDTH=10;//metres
	public static final float WORLD_HEIGHT=SCREEN_HEIGHT*(WORLD_WIDTH/SCREEN_WIDTH);// meters - keeps world dimensions in same aspect ratio as screen dimensions, so that circles get transformed into circles as opposed to ovals
	public static final float GRAVITY=9.8f;
	public static final boolean ALLOW_MOUSE_POINTER_TO_DRAG_BODIES_ON_SCREEN=true;// There's a load of code in basic mouse listener to process this, if you set it to true

	public static World world; // Box2D container for all bodies and barriers 

	// sleep time between two drawn frames in milliseconds 
	public static final int DELAY = 20;
	public static final int NUM_EULER_UPDATES_PER_SCREEN_REFRESH=10;
	// estimate for time between two frames in seconds 
	public static final float DELTA_T = DELAY / 1000.0f;
	
	private static final float w=0.3f, h=0.9f;
	float linearDragForce=.02f;
	
	public static int convertWorldXtoScreenX(float worldX) {
		return (int) (worldX/WORLD_WIDTH*SCREEN_WIDTH);
	}
	public static int convertWorldYtoScreenY(float worldY) {
		// minus sign in here is because screen coordinates are upside down.
		return (int) (SCREEN_HEIGHT-(worldY/WORLD_HEIGHT*SCREEN_HEIGHT));
	}
	public static float convertWorldLengthToScreenLength(float worldLength) {
		return (worldLength/WORLD_WIDTH*SCREEN_WIDTH);
	}
	public static float convertScreenXtoWorldX(int screenX) {
		return screenX*WORLD_WIDTH/SCREEN_WIDTH;
	}
	public static float convertScreenYtoWorldY(int screenY) {
		return (SCREEN_HEIGHT-screenY)*WORLD_HEIGHT/SCREEN_HEIGHT;
	}
	
	
	
	public List<BasicParticle> particles;
	public List<BasicWheel> Wheels;
	public List<BasicPolygon> polygons;
	public List<BasicNewRect> Rectangle;
	public List<AnchoredBarrier> barriers;
	public List<ElasticConnector> connectors;
	
	public List<LegDestination> newLegPos;
	public static MouseJoint mouseJointDef;
	WheelJointDef jointWheelDef;
	public static enum LayoutMode {CONVEX_ARENA, CONCAVE_ARENA, CONVEX_ARENA_WITH_CURVE, PINBALL_ARENA, RECTANGLE, CANNON,CARTGAME,CHALLENGE,SNOOKER_TABLE};
	public BasicPhysicsEngineUsingBox2D() {
		world = new World(new Vec2(0, -GRAVITY));// create Box2D container for everything
		world.setContinuousPhysics(true);
		Wheels = new ArrayList<BasicWheel>();
		particles = new ArrayList<BasicParticle>();
		polygons = new ArrayList<BasicPolygon>();
		Rectangle = new ArrayList<BasicNewRect>();
		barriers = new ArrayList<AnchoredBarrier>();
		connectors=new ArrayList<ElasticConnector>();
		newLegPos = new ArrayList<LegDestination>();
		LayoutMode layout=LayoutMode.CHALLENGE;
		// pinball:
		float linearDragForce=.2f/*0.2f*/;
		float r=.3f;
		//Rectangle.add(new BasicNewRect(4.5847774f,4.583193f,0f,0f, r*1.5f,Color.RED, 100, linearDragForce,4));
		//Rectangle.add(new BasicRectangle(4.38f,4.583193f,0,0, 4,2,10, Color.BLUE, linearDragForce/10));
//			public BasicRectangle(double sx, double sy, double vx, double vy, double width, double height, double orientation, double angularVeloctiy, boolean improvedEuler, Color col, double mass) {

		float s=1.2f;
		//Rectangle.add(new BasicRectangle(4.38f,3.370f,0,0, r*1.5f,Color.RED, 100, linearDragForce/10,4));
		//particles.add(new BasicParticle(0.35f,WORLD_HEIGHT/3f-0.2f,0,0, r,Color.GREEN, 1, linearDragForce));
		//polygons.add(new BasicPolygon(4.5847774f,4.583193f,0f,0f, r*1.5f,Color.RED, 100, linearDragForce,4));
		particles.add(new BasicWheel(2.0847774f+2,4.283193f+1f,0f,0f,r*1.2f,Color.WHITE,15f,.2f));
		//particles.add(new BasicWheel(7.2847774f,4.075f+1f,0f,0f,r*1.2f,Color.RED,1f,0.2f));
		polygons.add(new BasicPolygon(4.5847774f, 4.283193f+r*1.2f, 1, 0.25f, Color.red, 4.8f,0.5f, Constants.BIT_PLAYER_Body,(short)Constants.BIT_WALL,(short)0));
		polygons.add(new BasicPolygon(4.5847774f-r*6.5f, 4.283193f+r*1.2f, 1, 0.25f, Color.blue, 4.8f,0.5f, Constants.BIT_PLAYER_Body,(short)Constants.BIT_WALL,(short)0));
		polygons.add(new BasicPolygon(4.5847774f, 4.283193f+r*1.2f, 1, 0.25f, Color.gray, 4.8f,0.5f, Constants.BIT_PLAYER_Body,(short)Constants.BIT_WALL,(short)0));
		polygons.add(new BasicPolygon(4.5847774f-r*6.5f, 4.283193f+r*1.2f, 1, 0.25f, Color.gray, 4.8f,0.5f , Constants.BIT_PLAYER_Body,(short)Constants.BIT_WALL,(short)0));
		newLegPos.add(new LegDestination(particles.get(0).body.getPosition().x+1,particles.get(0).body.getPosition().y+1, 
				0, 0, r, Color.green, s, 0, Constants.BIT_SENSOR,(short) Constants.BIT_WALL,(short)0));
		//particles.add(new BasicWheel(8.0847774f+2,4.283193f+1f,0f,0f,r*1.2f,Color.RED,10005f,0.2f));
		
		System.out.println("Main Body Filter " + particles.get(0).body.getFixtureList().getFilterData().groupIndex +
			"  "+particles.get(0).body.getFixtureList().getFilterData().categoryBits + "  "+particles.get(0).body.getFixtureList().getFilterData().maskBits);
		System.out.println("Main Body Filter " + polygons.get(0).body.getFixtureList().getFilterData().groupIndex +
				"  "+polygons.get(0).body.getFixtureList().getFilterData().categoryBits + "  "+polygons.get(0).body.getFixtureList().getFilterData().maskBits);
		System.out.println("Main Body Filter " + polygons.get(1).body.getFixtureList().getFilterData().groupIndex +
				"  "+polygons.get(1).body.getFixtureList().getFilterData().categoryBits + "  "+polygons.get(1).body.getFixtureList().getFilterData().maskBits);
		
		
		float polyWidth=1;
		float polyHeight=0.25f;
		
		
		//particles.add(new ParticleAttachedToMousePointer(WORLD_WIDTH/2-2,WORLD_HEIGHT/2-2.2f,1.5f*s,1.2f*s, r,1));//using ParticleAttachedToMousePointer	
//		particles.add(new BasicParticle(WORLD_WIDTH/2+2,WORLD_HEIGHT/2+2f,-1.2f*s,-1.4f*s, r,Color.BLUE, 2, 0));
//		particles.add(new BasicParticle(3*r+WORLD_WIDTH/2,WORLD_HEIGHT/2,2,6.7f, r*3,Color.BLUE, 90, 0));
//		particles.add(new BasicParticle(r+WORLD_WIDTH/2,WORLD_HEIGHT/2,3.5f,5.2f, r,Color.RED, 2, 0));
		
//		// Example revolute joint creation:
//		BasicPolygon p1 = polygons.get(0);
//		BasicParticle p2 = particles.get(0);
//		RevoluteJointDef jointDef=new RevoluteJointDef();
//		jointDef.bodyA = p1.body;
//		jointDef.bodyB = p2.body;
//		jointDef.collideConnected = false;  // this means we don't want these two connected bodies to have collision detection.
//		jointDef.localAnchorA=new Vec2(0.2f,0.2f);
//		jointDef.localAnchorB=new Vec2(-0.2f,-0.2f);
//		world.createJoint(jointDef);
//		

		//BasicPolygon p1 = polygons.get(0);
//		BasicPolygon p2 = polygons.get(1);
//		RevoluteJointDef jointDef=new RevoluteJointDef();
//		jointDef.bodyA = p1.body;
//		jointDef.bodyB = p2.body;
//		jointDef.collideConnected=false;
//		jointDef.localAnchorA = new Vec2(r*6.5f/2,r*3.5f/2);
//		jointDef.localAnchorB = new Vec2(0f,0f);
//		world.createJoint(jointDef);
		
//		BasicParticle w1 = particles.get(0); 
//		jointWheelDef=new WheelJointDef();
		
//		jointWheelDef.bodyA = p1.body;
//		jointWheelDef.bodyB = w1.body;
//		jointWheelDef.collideConnected=false;
//		jointWheelDef.enableMotor=true;
//		jointWheelDef.frequencyHz= 20f;
//		//jointWheelDef.maxMotorTorque = 280;
//		//jointWheelDef.motorSpeed = 360 * 1;
//		jointWheelDef.localAnchorA.set(0,0f);// = new Vec2(0,0);
//		jointWheelDef.localAnchorB.set(0f,0f);// = new Vec2(0f,0f);
//		world.createJoint(jointWheelDef);
		
//		BasicParticle w2 = particles.get(1); 
//		WheelJointDef jointWheel1Def=new WheelJointDef();
//		jointWheel1Def.bodyA = p1.body;
//		jointWheel1Def.bodyB = w2.body;
//		jointWheel1Def.collideConnected=false;
//		jointWheel1Def.frequencyHz= 20f;
//		jointWheel1Def.localAnchorA.set(r*6.5f,0f);// = new Vec2(r*6.5f,0);
//		jointWheel1Def.localAnchorB.set(0f,0f);// = new Vec2(0f,0f);
//		jointWheelDef.enableMotor=true;
//		//jointWheelDef.maxMotorTorque = 80;
//		//jointWheelDef.motorSpeed = 360 * 1;
//		world.createJoint(jointWheel1Def);
		
		
		
		
		//preventing the main body from rotating
		//particles.get(0).body.setFixedRotation(false);
		//Right Leg top
		BasicParticle mainBody = particles.get(0);
		BasicPolygon tLeft = polygons.get(0);
		RevoluteJointDef jointDef=new RevoluteJointDef();
		jointDef.bodyA = mainBody.body;
		jointDef.bodyB = tLeft.body;
		jointDef.enableLimit = true;
		jointDef.collideConnected=false;
	    jointDef.lowerAngle = -MathUtils.PI / 4.0f;
	    jointDef.upperAngle = MathUtils.PI / 4.0f;
	    System.out.println("1Lower Angle"+ jointDef.lowerAngle);
		System.out.println("1Upper Angle"+ jointDef.upperAngle);
	    world.createJoint(jointDef);
	    
	    //RIght Leg Bottom
	    BasicPolygon tLeftParent = polygons.get(0);
		BasicPolygon tLeftChild = polygons.get(2);
		RevoluteJointDef BottomjointDef=new RevoluteJointDef();
		BottomjointDef.bodyA = tLeftParent.body;
		BottomjointDef.bodyB = tLeftChild.body;
		BottomjointDef.localAnchorA=new Vec2((polyWidth/2)+0.25f ,polyHeight/2 + 0.025f);
		BottomjointDef.enableLimit = true;
		BottomjointDef.collideConnected=false;
		BottomjointDef.lowerAngle = -2.2944f;
		BottomjointDef.upperAngle =  -1.0472f ;
	    world.createJoint(BottomjointDef);
	    
	    
	    //Left Leg TOp
	    BasicPolygon tRight = polygons.get(1);
		RevoluteJointDef jointDef1=new RevoluteJointDef();
		jointDef1.bodyA = mainBody.body;
		jointDef1.bodyB = tRight.body;
		jointDef1.localAnchorB=new Vec2((polyWidth/2)+0.45f ,0);
		jointDef1.enableLimit = true;
		jointDef1.collideConnected=false;
		jointDef1.lowerAngle = -MathUtils.PI / 4.0f;
		jointDef1.upperAngle = MathUtils.PI / 4.0f;		
		
	    world.createJoint(jointDef1);
	    
	  //Left Leg Bottom
		BasicPolygon tRightChild = polygons.get(3);
		RevoluteJointDef BottomjointDef1=new RevoluteJointDef();
		BottomjointDef1.bodyA = tRight.body;
		BottomjointDef1.bodyB = tRightChild.body;
		BottomjointDef1.localAnchorA=new Vec2(-(polyWidth/2)+0.5f ,(polyHeight/2) + 0.15f);
		BottomjointDef1.enableLimit = true;
		BottomjointDef1.collideConnected=false;
		BottomjointDef1.lowerAngle =  -2.26893f;
		BottomjointDef1.upperAngle =  -1.309f;
		System.out.println("2Lower Angle"+ BottomjointDef1.lowerAngle);
		System.out.println("2Upper Angle"+ BottomjointDef1.upperAngle);
	    world.createJoint(BottomjointDef1);
		
		//Distant Joint for LegDeestination Left
	    DistanceJointDef lfDestJoint = new DistanceJointDef();
	    LegDestination lfDest = newLegPos.get(0);
	    lfDestJoint.bodyA = mainBody.body;
	    lfDestJoint.bodyB = lfDest.body;
	    lfDestJoint.length = 0.9f;
		world.createJoint(lfDestJoint);
	   //
		

		if (false) {
			// spaceship flying under gravity
			particles.add(new ControllableSpaceShip(3*r+WORLD_WIDTH/2+1,WORLD_HEIGHT/2-2,0f,2f, r,true, 2*4));
		} else if (false) {
			// spaceship flying with dangling pendulum
			double springConstant=1000000, springDampingConstant=1000;
			double hookesLawTruncation=0.2;
			particles.add(new ControllableSpaceShip(3*r+WORLD_WIDTH/2+1,WORLD_HEIGHT/2-2,0f,2f, r,true, 2*4));
			particles.add(new BasicParticle(3*r+WORLD_WIDTH/2+1,WORLD_HEIGHT/2-4,-3f,9.7f, r,Color.BLUE, 2*4, linearDragForce));
			connectors.add(new ElasticConnector(particles.get(0), particles.get(1), r*6, springConstant, springDampingConstant, false, Color.WHITE, hookesLawTruncation));
		} else if (false) {
			// Simple pendulum attached under mouse pointer
			linearDragForce=.5f;
			double springConstant=10000, springDampingConstant=10;
			Double hookesLawTruncation=null;
			boolean canGoSlack=false;
			particles.add(new ParticleAttachedToMousePointer(WORLD_WIDTH/2,WORLD_HEIGHT/2,0,0, r, 10000));
			particles.add(new BasicParticle(WORLD_WIDTH/2,WORLD_HEIGHT/2-2,0,0, r,Color.BLUE, 2*4, linearDragForce));
			connectors.add(new ElasticConnector(particles.get(0), particles.get(1), r*10, springConstant,springDampingConstant, canGoSlack, Color.WHITE, hookesLawTruncation));
		} else if (false) {
			// 4 link chain
			linearDragForce=1;
			double springConstant=1000000, springDampingConstant=1000;
			Double hookesLawTruncation=null;//0.2;//null;//0.2;
			particles.add(new ParticleAttachedToMousePointer(WORLD_WIDTH/2,WORLD_HEIGHT/2,0,0, r, 10000));
			particles.add(new BasicParticle(WORLD_WIDTH/2,WORLD_HEIGHT/2-2,0,0, r/2,Color.BLUE, 2*4, linearDragForce));
			connectors.add(new ElasticConnector(particles.get(0), particles.get(1), r*6, springConstant,springDampingConstant, false, Color.WHITE, hookesLawTruncation));
			particles.add(new BasicParticle(WORLD_WIDTH/2,WORLD_HEIGHT/2-4,0,0, r/2,Color.BLUE, 2*4, linearDragForce));
			connectors.add(new ElasticConnector(particles.get(1), particles.get(2), r*6, springConstant,springDampingConstant, false, Color.WHITE, hookesLawTruncation));
			particles.add(new BasicParticle(WORLD_WIDTH/2,WORLD_HEIGHT/2-6,0,0, r/2,Color.BLUE, 2*4, linearDragForce));
			connectors.add(new ElasticConnector(particles.get(2), particles.get(3), r*6, springConstant,springDampingConstant, false, Color.WHITE, hookesLawTruncation));
			particles.add(new BasicParticle(WORLD_WIDTH/2,WORLD_HEIGHT/2-7,0,0, r/2,Color.BLUE, 2*4, linearDragForce));
			connectors.add(new ElasticConnector(particles.get(3), particles.get(4), r*6, springConstant,springDampingConstant, false, Color.WHITE, hookesLawTruncation));
		} else if (false) {
			// rectangle box
			linearDragForce=.1f;
			double springConstant=1000000, springDampingConstant=1000;
			double hookesLawTruncation=0.2;
//				particles.add(new ParticleAttachedToMousePointer(WORLD_WIDTH/2,WORLD_HEIGHT/2,0,0, r/2, true, 10000));				
			particles.add(new BasicParticle(WORLD_WIDTH/2,WORLD_HEIGHT/2-1,24,34, r/2,Color.BLUE, 2*4, linearDragForce));
			particles.add(new BasicParticle(WORLD_WIDTH/2+0.1f,WORLD_HEIGHT/2-2,0f,0f, r/2,Color.BLUE, 2*4, linearDragForce));
			connectors.add(new ElasticConnector(particles.get(0), particles.get(1), r*6, springConstant,springDampingConstant, false, Color.WHITE, hookesLawTruncation));
			particles.add(new BasicParticle(WORLD_WIDTH/2+0.1f,WORLD_HEIGHT/2-4,-14,14, r/2,Color.BLUE, 2*4, linearDragForce));
			connectors.add(new ElasticConnector(particles.get(1), particles.get(2), r*6, springConstant,springDampingConstant, false, Color.WHITE, hookesLawTruncation));
			particles.add(new BasicParticle(WORLD_WIDTH/2,WORLD_HEIGHT/2-6,0,0, r/2,Color.BLUE, 2*4, linearDragForce));
			connectors.add(new ElasticConnector(particles.get(2), particles.get(3), r*6, springConstant,springDampingConstant, false, Color.WHITE, hookesLawTruncation));
			connectors.add(new ElasticConnector(particles.get(3), particles.get(0), r*6, springConstant,springDampingConstant, false, Color.WHITE, hookesLawTruncation));
			connectors.add(new ElasticConnector(particles.get(2), particles.get(0), r*6*Math.sqrt(6), springConstant,springDampingConstant, false, Color.GRAY, hookesLawTruncation));
			connectors.add(new ElasticConnector(particles.get(1), particles.get(3), r*6*Math.sqrt(6), springConstant,springDampingConstant, false, Color.GRAY, hookesLawTruncation));
		}
		
		
		
		if (false) {
			Random x=new Random(3);
			for (int i=0;i<40;i++) {
				particles.add(new BasicParticle((0.5f+0.3f*(x.nextFloat()-.5f))*WORLD_HEIGHT,(0.5f+0.3f*(x.nextFloat()-.5f))*WORLD_WIDTH,0f,0f, r/2,new Color(x.nextFloat(), x.nextFloat(), x.nextFloat()), .2f, linearDragForce));				
			}
		}
		
		//particles.add(new BasicParticle(r,r,5,12, r,false, Color.GRAY, includeInbuiltCollisionDetection));

		
		barriers = new ArrayList<AnchoredBarrier>();
		
		switch (layout) {
			case RECTANGLE: {
				// rectangle walls:
				// anticlockwise listing
				// These would be better created as a JBox2D "chain" type object for efficiency and potentially better collision detection at joints. 
				barriers.add(new AnchoredBarrier_StraightLine(0, 0, WORLD_WIDTH, 0, Color.WHITE));
				barriers.add(new AnchoredBarrier_StraightLine(WORLD_WIDTH, 0, WORLD_WIDTH, WORLD_HEIGHT, Color.WHITE));
				barriers.add(new AnchoredBarrier_StraightLine(WORLD_WIDTH, WORLD_HEIGHT, 0, WORLD_HEIGHT, Color.WHITE));
				barriers.add(new AnchoredBarrier_StraightLine(0, WORLD_HEIGHT, 0, 0, Color.WHITE));
				break;
			}
			case CONVEX_ARENA: {
				// These would be better created as a JBox2D "chain" type object for efficiency and potentially better collision detection at joints. 
				barriers.add(new AnchoredBarrier_StraightLine(0,WORLD_HEIGHT/3, WORLD_WIDTH/2, 0, Color.WHITE));
				barriers.add(new AnchoredBarrier_StraightLine(WORLD_WIDTH/2, 0, WORLD_WIDTH, WORLD_HEIGHT/3, Color.WHITE));
				barriers.add(new AnchoredBarrier_StraightLine(WORLD_WIDTH, WORLD_HEIGHT/3, WORLD_WIDTH, WORLD_HEIGHT, Color.WHITE));
				barriers.add(new AnchoredBarrier_StraightLine(WORLD_WIDTH, WORLD_HEIGHT, 0, WORLD_HEIGHT, Color.WHITE));
				barriers.add(new AnchoredBarrier_StraightLine(0, WORLD_HEIGHT, 0, WORLD_HEIGHT/3, Color.WHITE));
				break;
			}
			case CONCAVE_ARENA: {
				// These would be better created as a JBox2D "chain" type object for efficiency and potentially better collision detection at joints. 
				barriers.add(new AnchoredBarrier_StraightLine(0f,WORLD_HEIGHT/3, WORLD_WIDTH/2, 0, Color.WHITE));
				barriers.add(new AnchoredBarrier_StraightLine(WORLD_WIDTH/2, 0, WORLD_WIDTH, WORLD_HEIGHT/3, Color.WHITE));
				barriers.add(new AnchoredBarrier_StraightLine(WORLD_WIDTH, WORLD_HEIGHT/3, WORLD_WIDTH, WORLD_HEIGHT, Color.WHITE));
				barriers.add(new AnchoredBarrier_StraightLine(WORLD_WIDTH, WORLD_HEIGHT, 0, WORLD_HEIGHT, Color.WHITE));
				barriers.add(new AnchoredBarrier_StraightLine(0f, WORLD_HEIGHT, 0, WORLD_HEIGHT/3, Color.WHITE));
				float width=WORLD_HEIGHT/20;
				barriers.add(new AnchoredBarrier_StraightLine(0f, WORLD_HEIGHT*2/3, WORLD_WIDTH/2, WORLD_HEIGHT*1/2, Color.WHITE));
				barriers.add(new AnchoredBarrier_Point(WORLD_WIDTH/2, WORLD_HEIGHT*1/2));
				barriers.add(new AnchoredBarrier_StraightLine(WORLD_WIDTH/2, WORLD_HEIGHT*1/2, WORLD_WIDTH/2, WORLD_HEIGHT*1/2-width, Color.WHITE));
				barriers.add(new AnchoredBarrier_Point(WORLD_WIDTH/2, WORLD_HEIGHT*1/2-width));
				barriers.add(new AnchoredBarrier_StraightLine(WORLD_WIDTH/2, WORLD_HEIGHT*1/2-width, 0, WORLD_HEIGHT*2/3-width, Color.WHITE));
				break;
			}
			case CONVEX_ARENA_WITH_CURVE: {
				// These would be better created as a JBox2D "chain" type object for efficiency and potentially better collision detection at joints. 
				barriers.add(new AnchoredBarrier_StraightLine(0,WORLD_HEIGHT/3, WORLD_WIDTH/2, 0, Color.WHITE));
				barriers.add(new AnchoredBarrier_StraightLine(WORLD_WIDTH/2, 0, WORLD_WIDTH, WORLD_HEIGHT/3, Color.WHITE));
				barriers.add(new AnchoredBarrier_StraightLine(WORLD_WIDTH, WORLD_HEIGHT/3, WORLD_WIDTH, WORLD_HEIGHT, Color.WHITE));
				barriers.add(new AnchoredBarrier_StraightLine(WORLD_WIDTH, WORLD_HEIGHT, 0, WORLD_HEIGHT, Color.WHITE));
				barriers.add(new AnchoredBarrier_StraightLine(0, WORLD_HEIGHT, 0, 0, Color.WHITE));
				barriers.add(new AnchoredBarrier_Curve(WORLD_WIDTH/2, WORLD_HEIGHT-WORLD_WIDTH/2, WORLD_WIDTH/2, 0.0f, 180.0f,Color.WHITE));
				break;
			}
			case PINBALL_ARENA: {
				// These would be better created as a JBox2D "chain" type object for efficiency and potentially better collision detection at joints. 
				// simple pinball board
				barriers.add(new AnchoredBarrier_StraightLine(0, 0, WORLD_WIDTH, 0, Color.WHITE));
				barriers.add(new AnchoredBarrier_StraightLine(WORLD_WIDTH, 0, WORLD_WIDTH, WORLD_HEIGHT, Color.WHITE));
				barriers.add(new AnchoredBarrier_StraightLine(WORLD_WIDTH, WORLD_HEIGHT, 0, WORLD_HEIGHT, Color.WHITE));
				barriers.add(new AnchoredBarrier_StraightLine(0, WORLD_HEIGHT, 0, 0, Color.WHITE));
				barriers.add(new AnchoredBarrier_Curve(WORLD_WIDTH/2, WORLD_HEIGHT-WORLD_WIDTH/2, WORLD_WIDTH/2, 0.0f, 200.0f,Color.WHITE));
				barriers.add(new AnchoredBarrier_Curve(WORLD_WIDTH/2, WORLD_HEIGHT*3/4, WORLD_WIDTH/15, -0.0f, 360.0f,Color.WHITE));
				barriers.add(new AnchoredBarrier_Curve(WORLD_WIDTH*1/3, WORLD_HEIGHT*1/2, WORLD_WIDTH/15, -0.0f, 360.0f,Color.WHITE));
				barriers.add(new AnchoredBarrier_Curve(WORLD_WIDTH*2/3, WORLD_HEIGHT*1/2, WORLD_WIDTH/15, -0.0f, 360.0f,Color.WHITE));
				break;
			}
			case CANNON: {
				// rectangle walls:
				// anticlockwise listing
				// These would be better created as a JBox2D "chain" type object for efficiency and potentially better collision detection at joints. 
//				barriers.add(new AnchoredBarrier_StraightLine(0, 0, WORLD_WIDTH, 0, Color.WHITE));
//				barriers.add(new AnchoredBarrier_StraightLine(WORLD_WIDTH, 0, WORLD_WIDTH, WORLD_HEIGHT, Color.WHITE));
//				barriers.add(new AnchoredBarrier_StraightLine(WORLD_WIDTH, WORLD_HEIGHT, 0, WORLD_HEIGHT, Color.WHITE));
				barriers.add(new AnchoredBarrier_StraightLine(0, WORLD_HEIGHT, 0, 0, Color.WHITE));
				barriers.add(new AnchoredBarrier_StraightLine(0,WORLD_HEIGHT/3.5f,1.5f,WORLD_HEIGHT/3.5f,Color.RED));
				
				barriers.add(new AnchoredBarrier_StraightLine(3.5f,WORLD_HEIGHT/3.5f,8.5f,WORLD_HEIGHT/3.5f,Color.RED));
				break;
			}
			case CARTGAME: {
				// rectangle walls:
				// anticlockwise listing
				// These would be better created as a JBox2D "chain" type object for efficiency and potentially better collision detection at joints. 
//				barriers.add(new AnchoredBarrier_StraightLine(0, 0, WORLD_WIDTH, 0, Color.WHITE));
//				barriers.add(new AnchoredBarrier_StraightLine(WORLD_WIDTH, 0, WORLD_WIDTH, WORLD_HEIGHT, Color.WHITE));
//				barriers.add(new AnchoredBarrier_StraightLine(WORLD_WIDTH, WORLD_HEIGHT, 0, WORLD_HEIGHT, Color.WHITE));
				
				barriers.add(new AnchoredBarrier_StraightLine(-2f+2.5f,WORLD_HEIGHT/2.5f+0.435f,40+3.5f,WORLD_HEIGHT/2.5f+0.435f,Color.RED));
				
				//barriers.add(new AnchoredBarrier_StraightLine(4+3.5f,WORLD_HEIGHT/2.5f+0.435f,12.5f,WORLD_HEIGHT/2.5f+3.435f,Color.RED));
				break;
			}
			case CHALLENGE: {
				// rectangle walls:
				// anticlockwise listing
				// These would be better created as a JBox2D "chain" type object for efficiency and potentially better collision detection at joints. 
//				barriers.add(new AnchoredBarrier_StraightLine(0, 0, WORLD_WIDTH, 0, Color.WHITE));
//				barriers.add(new AnchoredBarrier_StraightLine(WORLD_WIDTH, 0, WORLD_WIDTH, WORLD_HEIGHT, Color.WHITE));
//				barriers.add(new AnchoredBarrier_StraightLine(WORLD_WIDTH, WORLD_HEIGHT, 0, WORLD_HEIGHT, Color.WHITE));
				
				barriers.add(new AnchoredBarrier_StraightLine(-2f+2.5f,WORLD_HEIGHT/2.5f+0.435f,7+3.5f,WORLD_HEIGHT/2.5f+0.435f,Color.RED));
				barriers.add(new AnchoredBarrier_StraightLine(7+3.5f,WORLD_HEIGHT/2.5f+0.435f,7+3.5f+2.5f,WORLD_HEIGHT/2.5f+1.435f,Color.RED));
				barriers.add(new AnchoredBarrier_StraightLine(7+3.5f+2.5f,WORLD_HEIGHT/2.5f+1.435f,7+3.5f+2.5f,WORLD_HEIGHT/2.5f+0.435f,Color.RED));
				barriers.add(new AnchoredBarrier_StraightLine(7+3.5f+2.5f,WORLD_HEIGHT/2.5f+0.435f,22+3.5f+2.5f,WORLD_HEIGHT/2.5f+0.435f,Color.RED));
				//barriers.add(new AnchoredBarrier_StraightLine(4+3.5f,WORLD_HEIGHT/2.5f+0.435f,12.5f,WORLD_HEIGHT/2.5f+3.435f,Color.RED));
				break;
			}
			case SNOOKER_TABLE: {
				// These would be better created as a JBox2D "chain" type object for efficiency and potentially better collision detection at joints. 
				float snookerTableHeight=WORLD_HEIGHT;
				float pocketSize=0.4f;
				float cushionDepth=0.3f;
				float cushionLength = snookerTableHeight/2-pocketSize-cushionDepth;
				float snookerTableWidth=cushionLength+cushionDepth*2+pocketSize*2;
				
				createCushion(barriers, snookerTableWidth-cushionDepth/2, snookerTableHeight*0.25f,0, cushionLength, cushionDepth); 
				createCushion(barriers, snookerTableWidth-cushionDepth/2, snookerTableHeight*0.75f,0, cushionLength, cushionDepth); 
				createCushion(barriers, snookerTableWidth/2, snookerTableHeight-cushionDepth/2, Math.PI/2, cushionLength, cushionDepth); 
				createCushion(barriers, cushionDepth/2, snookerTableHeight*0.25f,Math.PI, cushionLength, cushionDepth); 
				createCushion(barriers, cushionDepth/2, snookerTableHeight*0.75f,Math.PI, cushionLength, cushionDepth); 
				createCushion(barriers, snookerTableWidth/2, cushionDepth/2, Math.PI*3/2, cushionLength, cushionDepth); 
				
				
				break;
			}
		}
	}
	
	private void createBlocks(float sx, float sy, int num) {
		float tx = sx, ty = sy;
		for (int i = num; i > 0; i--) {
			float ttx = tx, tty = ty;
			for (int j = 0; j < i; j++) {
				if(j%2==0) {
					polygons.add(new BasicPolygon(ttx, tty, w, h, Color.GREEN, 1f,0.2f, Constants.BIT_PLAYER_Body,Constants.BIT_WALL,(short)0));
				}
				polygons.add(new BasicPolygon(ttx, tty, w, h, Color.RED, 1f,0.2f, Constants.BIT_PLAYER_Body,Constants.BIT_WALL,(short)0));
				ttx += w + w/3;
			}
			tx += 2*w/3;
			ty += h + 0.1f;
		}
	}
	
	private void createCushion(List<AnchoredBarrier> barriers, float centrex, float centrey, double orientation, float cushionLength, float cushionDepth) {
		// on entry, we require centrex,centrey to be the centre of the rectangle that contains the cushion.
		Color col=Color.WHITE;
		Vec2 p1=new Vec2(cushionDepth/2, -cushionLength/2-cushionDepth/2);
		Vec2 p2=new Vec2(-cushionDepth/2, -cushionLength/2);
		Vec2 p3=new Vec2(-cushionDepth/2, +cushionLength/2);
		Vec2 p4=new Vec2(cushionDepth/2, cushionLength/2+cushionDepth/2);
		p1=rotateVec(p1,orientation);
		p2=rotateVec(p2,orientation);
		p3=rotateVec(p3,orientation);
		p4=rotateVec(p4,orientation);
		// we are being careful here to list edges in an anticlockwise manner, so that normals point inwards!
		barriers.add(new AnchoredBarrier_StraightLine((float)(centrex+p1.x), (float)(centrey+p1.y), (float)(centrex+p2.x), (float)(centrey+p2.y), col));
		barriers.add(new AnchoredBarrier_StraightLine((float)(centrex+p2.x), (float)(centrey+p2.y), (float)(centrex+p3.x), (float)(centrey+p3.y), col));
		barriers.add(new AnchoredBarrier_StraightLine((float)(centrex+p3.x), (float)(centrey+p3.y), (float)(centrex+p4.x), (float)(centrey+p4.y), col));
		// oops this will have concave corners so will need to fix that some time! 
	}
	private static Vec2 rotateVec(Vec2 v, double angle) {
		// I couldn't find a rotate function in Vec2 so had to write own temporary one here, just for the sake of 
		// cushion rotation for snooker table...
		float cos = (float) Math.cos(angle);
		float sin = (float) Math.sin(angle);
		float nx = v.x * cos - v.y * sin;
		float ny = v.x * sin + v.y * cos;
		return new Vec2(nx,ny);
	}
	static boolean newMousePressed;
	static BasicMouseListener listener;
	float mousePressed=0;
	public static void main(String[] args) throws Exception {
		final BasicPhysicsEngineUsingBox2D game = new BasicPhysicsEngineUsingBox2D();
		final BasicView view = new BasicView(game);
		JEasyFrame frame = new JEasyFrame(view, "Basic Physics Engine");
		frame.addKeyListener(new BasicKeyListener());
		listener = new BasicMouseListener(game);
		view.addMouseMotionListener(listener);
		view.addMouseListener(listener);
		
		game.startThread(view);
	}
	private void startThread(final BasicView view) throws InterruptedException {
		final BasicPhysicsEngineUsingBox2D game=this;
		while (true) {
			game.update();
			view.repaint();
			Toolkit.getDefaultToolkit().sync();
			try {
				Thread.sleep(DELAY);
			} catch (InterruptedException e) {
			}
		}
	}
	
//Spider LEg
	

	
	///

	List<BasicPolygon> Newpolygons= new ArrayList<BasicPolygon>();
	static BasicKeyListener KeyListener = new BasicKeyListener();  
	boolean GameOver;
	float acc= 100;
	public int linex1=0,liney1=0,linex2=0,liney2=0;
	public float offsetPointerOrigin=1f;
	public boolean showLine=false;
	public void update() { 
		linex1=BasicPhysicsEngineUsingBox2D.convertWorldXtoScreenX(particles.get(0).body.getPosition().x);
        liney1=BasicPhysicsEngineUsingBox2D.convertWorldYtoScreenY(particles.get(0).body.getPosition().y);
		//BasicMouseListener mouse = new BasicMouseListener();
		//System.out.println(mousePressed + " " + newMousePressed );
		newMousePressed= listener.isMouseButtonPressed();
		//
		
		
		
		
		
		int VELOCITY_ITERATIONS=NUM_EULER_UPDATES_PER_SCREEN_REFRESH;
		int POSITION_ITERATIONS=NUM_EULER_UPDATES_PER_SCREEN_REFRESH;
		float AddForce =5f;
		if(KeyListener.isMoveLeft())
		{			
			//particles.get(0).body.applyTorque(AddForce);
			//polygons.get(0).body.applyTorque(-AddForce*1.45f);
			particles.get(0).body.setLinearVelocity(new Vec2(-AddForce,particles.get(0).body.getLinearVelocity().y));
			

		}
		else if(KeyListener.isMoveRight())
		{
			 //particles.get(0).body.applyTorque(-AddForce);
			//System.out.println("RightKey" + KeyListener.isMoveLeft());
			 particles.get(0).body.setLinearVelocity(new Vec2(AddForce,particles.get(0).body.getLinearVelocity().y));
			 
		}
		
		 else
		 {
			 particles.get(0).body.setLinearVelocity(new Vec2(0,particles.get(0).body.getLinearVelocity().y));
			 //particles.get(0).body.applyTorque(0);
			 //polygons.get(0).body.applyTorque(0);
		 }
		 
		 
		 if(newMousePressed)
		 {
			 showLine=true;
			 
		 }

		 //System.out.println(" Rightleg Rot " + polygons.get(0).body.getAngle());
		 
		for (BasicParticle p:particles) {
			// give the objects an opportunity to add any bespoke forces, e.g. rolling friction
			p.notificationOfNewTimestep();
			
		}
		for (BasicPolygon p:polygons) {
			
			
			// give the objects an opportunity to add any bespoke forces, e.g. rolling friction
			p.notificationOfNewTimestep();
			p.body.setFixedRotation(false);
			
		}
		for (BasicWheel w:Wheels) {
			// give the objects an opportunity to add any bespoke forces, e.g. rolling friction
			w.notificationOfNewTimestep();
			w.body.setFixedRotation(false);
		}
		for (BasicNewRect r:Rectangle) {

			// give the objects an opportunity to add any bespoke forces, e.g. rolling friction
			r.notificationOfNewTimestep();
			r.body.setFixedRotation(false);
			
		}
		world.step(DELTA_T, VELOCITY_ITERATIONS, POSITION_ITERATIONS);
	}
	

}


