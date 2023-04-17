package pbgLecture5lab_wrapperForJBox2D;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.text.View;

import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.callbacks.RayCastCallback;
import org.jbox2d.collision.Manifold;
import org.jbox2d.collision.WorldManifold;
import org.jbox2d.common.MathUtils;
import org.jbox2d.common.RaycastResult;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.contacts.Contact;
import org.jbox2d.dynamics.contacts.ContactEdge;
import org.jbox2d.dynamics.joints.DistanceJoint;
import org.jbox2d.dynamics.joints.DistanceJointDef;
import org.jbox2d.dynamics.joints.Joint;
import org.jbox2d.dynamics.joints.JointDef;
import org.jbox2d.dynamics.joints.JointType;
import org.jbox2d.dynamics.joints.MouseJoint;
import org.jbox2d.dynamics.joints.PrismaticJoint;
import org.jbox2d.dynamics.joints.PrismaticJointDef;
import org.jbox2d.dynamics.joints.RevoluteJointDef;
import org.jbox2d.dynamics.joints.RopeJoint;
import org.jbox2d.dynamics.joints.RopeJointDef;
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
	public static final float DELTA_T = DELAY / 750.0f;
	
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
	public List<BasicParticle> particles1;
	public List<BasicWheel> Wheels;
	public List<BasicPolygon> polygons;
	public List<BasicPolygon> legSensors;
	public List<BasicNewRect> Rectangle;
	public List<AnchoredBarrier> barriers;
	public List<ElasticConnector> connectors;
	public List<Vec2> DistanceList = new ArrayList<Vec2>();
	public List<Vec2> GhostLegAngle = new ArrayList<Vec2>();
	public List<GroundTest> GroundTesting = new ArrayList<GroundTest>();
	public List<LegDestination> newLegPos;
	public List<BodyLeg> LegPos;
	public List<RevoluteJointDef> bodyParts;
	public static MouseJoint mouseJointDef;
	WheelJointDef jointWheelDef;
	public static enum LayoutMode {CONVEX_ARENA, CONCAVE_ARENA, CONVEX_ARENA_WITH_CURVE, PINBALL_ARENA, RECTANGLE, CANNON,CARTGAME,CHALLENGE,SNOOKER_TABLE};
	public BasicPhysicsEngineUsingBox2D() {
		world = new World(new Vec2(0, -GRAVITY*1.25f));// create Box2D container for everything
		world.setContinuousPhysics(true);
		Wheels = new ArrayList<BasicWheel>();
		particles = new ArrayList<BasicParticle>();
		particles1 = new ArrayList<BasicParticle>();
		polygons = new ArrayList<BasicPolygon>();
		Rectangle = new ArrayList<BasicNewRect>();
		barriers = new ArrayList<AnchoredBarrier>();
		connectors=new ArrayList<ElasticConnector>();
		newLegPos = new ArrayList<LegDestination>();
		LegPos = new ArrayList<BodyLeg>();
		legSensors = new ArrayList<BasicPolygon>();
		bodyParts= new ArrayList<RevoluteJointDef>();
		LayoutMode layout=LayoutMode.CHALLENGE;
		// pinball:
		float linearDragForce=.2f/*0.2f*/;
		float r=.3f;
		float s=1.2f;
		particles.add(new BasicWheel(3.732203f,5.0525184f,0f,0f,r*1.2f,Color.WHITE,15f,.2f));
		particles1.add(new BasicWheel(3.732203f,10.0525184f,0f,0f,r*1.2f,Color.WHITE,999999f,.2f));
		polygons.add(new BasicPolygon(4.5847774f, 4.283193f+r*1.2f, 1, 0.25f, Color.red, 4.8f,0.5f, Constants.BIT_PLAYER_Body,(short)Constants.BIT_WALL,(short)0));
		polygons.add(new BasicPolygon(4.5847774f-r*6.5f, 4.283193f+r*1.2f, 1, 0.25f, Color.blue, 4.8f,0.5f, Constants.BIT_PLAYER_Body,(short)Constants.BIT_WALL,(short)0));
		polygons.add(new BasicPolygon(4.5847774f, 4.283193f+r*1.2f, 1, 0.25f, Color.CYAN, 4.8f,0.5f, Constants.BIT_PLAYER_Body,(short)Constants.BIT_WALL,(short)0));
		polygons.add(new BasicPolygon(4.5847774f-r*6.5f, 4.283193f+r*1.2f, 1, 0.25f, Color.gray, 4.8f,0.5f , Constants.BIT_PLAYER_Body,(short)Constants.BIT_WALL,(short)0));
		polygons.add(new BasicPolygon(5.8847774f-r*6.5f, 4.283193f+r*1.2f, 1.5f, 0.2f, Color.WHITE, 4.8f,0.5f, Constants.BIT_PLAYER_Body,(short)Constants.BIT_WALL,(short)0));
		polygons.add(new BasicPolygon(4.4847774f-r*6.5f, 4.283193f+r*1.2f, 1.5f, 0.2f, Color.WHITE, 4.8f,0.5f, Constants.BIT_PLAYER_Body,(short)Constants.BIT_WALL,(short)0));
		polygons.add(new BasicPolygon(4.4847774f-r*6.5f + 3f, 4.283193f+r*1.2f, 1.5f, 0.2f, Color.WHITE, 6.8f,0.5f, Constants.BIT_PLAYER_Body,(short)Constants.BIT_WALL,(short)0));
		polygons.add(new BasicPolygon(4.4847774f-r*6.5f, 4.283193f+r*1.2f, 1.5f, 0.2f, Color.WHITE, 6.8f,0.5f, Constants.BIT_PLAYER_Body,(short)Constants.BIT_WALL,(short)0));
		newLegPos.add(new LegDestination(4.9963517f,4.8406105f, 0, 0, r/2f, Color.red, 3, 1, Constants.BIT_PLAYER_Body,(short) Constants.BIT_WALL,(short)0));
		newLegPos.add(new LegDestination(2.8907132f,4.8429394f, 0, 0,r/2f, Color.blue, 3, 1, Constants.BIT_PLAYER_Body,(short) Constants.BIT_WALL,(short)0));
		newLegPos.add(new LegDestination(5.828504f,4.840214f, 0, 0, r/2f, Color.cyan, 3, 1, Constants.BIT_PLAYER_Body,(short) Constants.BIT_WALL,(short)0));
		newLegPos.add(new LegDestination(2.1355329f,4.8409095f, 0, 0, r/2f, Color.green, 3, 1, Constants.BIT_PLAYER_Body,(short) Constants.BIT_WALL,(short)0));
		LegPos.add(new BodyLeg(4.9963517f,4.8406105f, 0, 0, r/2, Color.WHITE, 1, linearDragForce, Constants.BIT_PLAYER_Body, Constants.BIT_WALL,(short) 0));
		LegPos.add(new BodyLeg(2.8907132f,4.8429394f, 0, 0, r/2, Color.WHITE, 3, linearDragForce, Constants.BIT_PLAYER_Body, Constants.BIT_WALL,(short) 0));
		LegPos.add(new BodyLeg(5.828504f,4.840214f, 0, 0, r/2, Color.WHITE, 1, linearDragForce, Constants.BIT_PLAYER_Body, Constants.BIT_WALL,(short) 0));
		LegPos.add(new BodyLeg(2.1355329f,4.8409095f, 0, 0, r/2, Color.WHITE, 1, linearDragForce, Constants.BIT_PLAYER_Body, Constants.BIT_WALL,(short) 0));
		legSensors.add(new BasicPolygon(4.9963517f,4.8406105f, 0.5f, 0.075f, Color.WHITE, 1, linearDragForce, Constants.BIT_PLAYER_Body, Constants.BIT_WALL,(short) 0));
		legSensors.add(new BasicPolygon(2.8907132f,4.8429394f, 0.5f, 0.075f, Color.WHITE, 3, linearDragForce, Constants.BIT_PLAYER_Body, Constants.BIT_WALL,(short) 0));
		legSensors.add(new BasicPolygon(5.828504f, 4.840214f, 0.5f, 0.075f, Color.WHITE, 1, linearDragForce, Constants.BIT_PLAYER_Body, Constants.BIT_WALL,(short) 0));
		legSensors.add(new BasicPolygon(2.1355329f,4.8409095f, 0.5f, 0.075f, Color.WHITE, 1, linearDragForce, Constants.BIT_PLAYER_Body, Constants.BIT_WALL,(short) 0));
//		System.out.println("Main Body Filter " + particles.get(0).body.getFixtureList().getFilterData().groupIndex +
//			"  "+particles.get(0).body.getFixtureList().getFilterData().categoryBits + "  "+particles.get(0).body.getFixtureList().getFilterData().maskBits);
		
		
		float polyWidth=1;
		float polyHeight=0.25f;
		particles1.get(0).body.m_type = BodyType.STATIC;

		BasicParticle mainBody = particles.get(0);
		BasicPolygon ftLeft = polygons.get(0);
		RevoluteJointDef jointDef=new RevoluteJointDef();
		jointDef.bodyA = mainBody.body;
		jointDef.bodyB = ftLeft.body;
		jointDef.enableLimit = true;
		jointDef.collideConnected=false;
	    jointDef.lowerAngle = -MathUtils.PI / 4.0f;
	    jointDef.upperAngle = MathUtils.PI / 4.0f;
	    world.createJoint(jointDef);
	    bodyParts.add(jointDef);
	    
	    
	    //Second Right Leg Top 
	    BasicPolygon stLeft = polygons.get(4);
		RevoluteJointDef SjointDef=new RevoluteJointDef();
		SjointDef.bodyA = mainBody.body;
		SjointDef.bodyB = stLeft.body;
		SjointDef.enableLimit = true;
		SjointDef.collideConnected=false;
		SjointDef.lowerAngle = -MathUtils.PI / 4.0f;
		SjointDef.upperAngle = MathUtils.PI / 4.0f;
	    world.createJoint(SjointDef);
	    bodyParts.add(SjointDef);
	    
	    //RIght Leg Bottom
	    BasicPolygon tLeftParent = polygons.get(0);
		BasicPolygon tLeftChild = polygons.get(2);
		RevoluteJointDef BottomjointDef=new RevoluteJointDef();
		BottomjointDef.bodyA = tLeftParent.body;
		BottomjointDef.bodyB = tLeftChild.body;
		BottomjointDef.localAnchorA=new Vec2((polyWidth/2)+0.25f ,polyHeight/2 + 0.025f);
		BottomjointDef.enableLimit = true;
		BottomjointDef.collideConnected=false;
		BottomjointDef.lowerAngle = -2.3f;
		BottomjointDef.upperAngle =  -1.2172f ;
	    world.createJoint(BottomjointDef);
	    bodyParts.add(BottomjointDef);
	    
	    
	    //RIght Leg second Bottom
	    BasicPolygon sbLeftParent = polygons.get(4);
		BasicPolygon sbLeftChild = polygons.get(6);
		RevoluteJointDef secondBottomjointDef=new RevoluteJointDef();
		secondBottomjointDef.bodyA = sbLeftParent.body;
		secondBottomjointDef.bodyB = sbLeftChild.body;
		secondBottomjointDef.localAnchorA=new Vec2(1.25f,0.15f);
		secondBottomjointDef.enableLimit = true;
		secondBottomjointDef.collideConnected=false;
		secondBottomjointDef.lowerAngle = -2.3f;
		secondBottomjointDef.upperAngle =  -1.380f ;
	    world.createJoint(secondBottomjointDef);
	    bodyParts.add(secondBottomjointDef);
	    
	    
	    
	    //First Left Leg TOp
	    BasicPolygon ftRight = polygons.get(1);
		RevoluteJointDef jointDef1=new RevoluteJointDef();
		jointDef1.bodyA = mainBody.body;
		jointDef1.bodyB = ftRight.body;
		jointDef1.localAnchorB=new Vec2((polyWidth/2)+0.45f ,0);
		jointDef1.enableLimit = true;
		jointDef1.collideConnected=false;
		jointDef1.lowerAngle = -MathUtils.PI / 4.0f;
		jointDef1.upperAngle = MathUtils.PI / 4.0f;		
	    world.createJoint(jointDef1);
	    bodyParts.add(jointDef1);
	    
	  //First Left Leg TOp
	    BasicPolygon stRight = polygons.get(5);
		RevoluteJointDef sjointDef1=new RevoluteJointDef();
		sjointDef1.bodyA = mainBody.body;
		sjointDef1.bodyB = stRight.body;
		sjointDef1.localAnchorB=new Vec2((1.5f) ,0);
		sjointDef1.enableLimit = true;
		sjointDef1.collideConnected=false;
		sjointDef1.lowerAngle = -MathUtils.PI / 4.0f;
		sjointDef1.upperAngle = MathUtils.PI / 4.0f;		
	    world.createJoint(sjointDef1);
	    bodyParts.add(sjointDef1);
	  //Left Leg Bottom
		BasicPolygon tRightChild = polygons.get(3);
		RevoluteJointDef BottomjointDef1=new RevoluteJointDef();
		BottomjointDef1.bodyA = ftRight.body;
		BottomjointDef1.bodyB = tRightChild.body;
		BottomjointDef1.localAnchorA=new Vec2(-(polyWidth/2)+0.5f ,(polyHeight/2) + 0.15f);
		BottomjointDef1.enableLimit = true;
		BottomjointDef1.collideConnected=false;
		BottomjointDef1.lowerAngle =  -2.26893f;
		BottomjointDef1.upperAngle =  -1.309f;
	    world.createJoint(BottomjointDef1);
	    bodyParts.add(BottomjointDef1);
	    // Secnod Left Bottom
	    BasicPolygon sbRightParent = polygons.get(5);
	    BasicPolygon sbRightChild = polygons.get(7);
		RevoluteJointDef sBottomjointDef1=new RevoluteJointDef();
		sBottomjointDef1.bodyA = sbRightParent.body;
		sBottomjointDef1.bodyB = sbRightChild.body;
		sBottomjointDef1.localAnchorA=new Vec2(0,0.15f);
		sBottomjointDef1.enableLimit = true;
		sBottomjointDef1.collideConnected=false;
		sBottomjointDef1.lowerAngle =  -2.26893f;
		sBottomjointDef1.upperAngle =  -1.309f;
	    world.createJoint(sBottomjointDef1);
	    bodyParts.add(sBottomjointDef1);
	    //
	    DistanceJointDef lfDestJoint = new DistanceJointDef();
	    LegDestination lfDest = newLegPos.get(0);
	    lfDestJoint.bodyA = mainBody.body;
	    lfDestJoint.bodyB = lfDest.body;
	    lfDestJoint.length = 1.15f;
	    lfDestJoint.frequencyHz = 0;
	    lfDestJoint.dampingRatio = 0;
	    lfDestJoint.collideConnected = false;
		world.createJoint(lfDestJoint);
		//bodyParts.add(lfDestJoint);
		//Distant Joint for LegDeestination LF
	    DistanceJointDef rfDestJoint = new DistanceJointDef();
	    LegDestination rfDest = newLegPos.get(1);
	    rfDestJoint.bodyA = mainBody.body;
	    rfDestJoint.bodyB = rfDest.body;
	    rfDestJoint.frequencyHz = 0;
	    rfDestJoint.dampingRatio = 0;
	    rfDestJoint.length = 1.25f;
	    rfDestJoint.collideConnected = false;
		world.createJoint(rfDestJoint);
		//bodyParts.add(rfDestJoint);
		//Distant Joint for LegDeestination RS
	    DistanceJointDef lsDestJoint = new DistanceJointDef();
	    LegDestination lsDest = newLegPos.get(2);
	    lsDestJoint.bodyA = mainBody.body;
	    lsDestJoint.bodyB = lsDest.body;
	    lsDestJoint.frequencyHz = 0;
	    lsDestJoint.dampingRatio = 0;
	    lsDestJoint.length = 1.75f;
		world.createJoint(lsDestJoint);
		//bodyParts.add(lsDestJoint);
		//Distant Joint for LegDeestination LF
	    DistanceJointDef rsDestJoint = new DistanceJointDef();
	    LegDestination rsDest = newLegPos.get(3);
	    rsDestJoint.bodyA = mainBody.body;
	    rsDestJoint.bodyB = rsDest.body;
	    rsDestJoint.length = 1.85f;
	    rsDestJoint.frequencyHz = 0;
	    rsDestJoint.dampingRatio = 0;
	    rsDestJoint.collideConnected = false;
		world.createJoint(rsDestJoint);
		//bodyParts.add(rsDestJoint);
		//FinalToe RF
		BodyLeg frToe = LegPos.get(0);
		frToe.body.setFixedRotation(true);
		RevoluteJointDef BottomFToejointDef=new RevoluteJointDef();
		BottomFToejointDef.bodyA = tLeftChild.body;
		BottomFToejointDef.bodyB = frToe.body;
		BottomFToejointDef.collideConnected=false;
		BottomFToejointDef.localAnchorA=new Vec2((polyWidth) ,(polyHeight/2));
		 world.createJoint(BottomFToejointDef);
		// bodyParts.add(BottomFToejointDef);
		//FinalToe Rs
		BodyLeg srToe = LegPos.get(2);
		frToe.body.setFixedRotation(true);
		RevoluteJointDef BottomsToejointDef=new RevoluteJointDef();
		BottomsToejointDef.bodyA = sbLeftChild.body;
		BottomsToejointDef.bodyB = srToe.body;
		BottomsToejointDef.collideConnected=false;
		BottomsToejointDef.localAnchorA=new Vec2((1.5f) ,(polyHeight/2));
		 world.createJoint(BottomsToejointDef);
		// bodyParts.add(BottomsToejointDef);
		//FinalToe LF
		BodyLeg lrToe = LegPos.get(1);
		lrToe.body.setFixedRotation(true);
		RevoluteJointDef BottomLFToejointDef=new RevoluteJointDef();
		BottomLFToejointDef.bodyA = tRightChild.body;
		BottomLFToejointDef.bodyB = lrToe.body;
		BottomLFToejointDef.collideConnected=false;
		BottomLFToejointDef.localAnchorA=new Vec2((polyWidth) ,(polyHeight/2));
		world.createJoint(BottomLFToejointDef);
		//bodyParts.add(BottomLFToejointDef);
		//FinalToe LS
		BodyLeg lsToe = LegPos.get(3);
		lsToe.body.setFixedRotation(true);
		RevoluteJointDef BottomLSToejointDef=new RevoluteJointDef();
		BottomLSToejointDef.bodyA = sbRightChild.body;
		BottomLSToejointDef.bodyB = lsToe.body;
		BottomLSToejointDef.collideConnected=false;
		BottomLSToejointDef.localAnchorA=new Vec2((1.5f) ,(polyHeight/2));
		world.createJoint(BottomLSToejointDef);
		//bodyParts.add(BottomLSToejointDef);
		//LegSceneors RightFirst
		Body frToeScesor = legSensors.get(0).body;
		frToeScesor.setFixedRotation(true);
		RevoluteJointDef frToeScesorJoint=new RevoluteJointDef();
		frToeScesorJoint.bodyA = frToe.body;
		frToeScesorJoint.bodyB = frToeScesor;
		frToeScesorJoint.localAnchorB = new Vec2(.25f,0);
		frToeScesorJoint.collideConnected=false;
		world.createJoint(frToeScesorJoint);
		//bodyParts.add(frToeScesorJoint);
		//LegSceneors RightSceond
				Body srToeScesor = legSensors.get(2).body;
				srToeScesor.setFixedRotation(true);
				RevoluteJointDef srToeScesorJoint=new RevoluteJointDef();
				srToeScesorJoint.bodyA = srToe.body;
				srToeScesorJoint.bodyB = srToeScesor;
				srToeScesorJoint.localAnchorB = new Vec2(.25f,0);
				srToeScesorJoint.collideConnected=false;
				world.createJoint(srToeScesorJoint);

				//LegSceneors LeftFirst
				Body flToeScesor = legSensors.get(1).body;
				flToeScesor.setFixedRotation(true);
				RevoluteJointDef flToeScesorJoint=new RevoluteJointDef();
				flToeScesorJoint.bodyA = lrToe.body;
				flToeScesorJoint.bodyB = flToeScesor;
				flToeScesorJoint.localAnchorB = new Vec2(.25f,0);
				flToeScesorJoint.collideConnected=false;
				world.createJoint(flToeScesorJoint);
				
				//LegSceneors LeftFirst
				Body slToeScesor = legSensors.get(3).body;
				slToeScesor.setFixedRotation(true);
				RevoluteJointDef slToeScesorJoint=new RevoluteJointDef();
				slToeScesorJoint.bodyA = lsToe.body;
				slToeScesorJoint.bodyB = slToeScesor;
				slToeScesorJoint.localAnchorB = new Vec2(.25f,0);
				slToeScesorJoint.collideConnected=false;
				world.createJoint(slToeScesorJoint);
				
		
			 
			//DIstance for each limb
			DistanceList.add(new Vec2(1.47f,0.84f));
			DistanceList.add(new Vec2(1.57f,0.78f));
			DistanceList.add(new Vec2(2.14f,1.66f));
			DistanceList.add(new Vec2(2.1f,1.64f));
			
			
			GhostLegAngle.add(new Vec2(60,+0.75f));
			GhostLegAngle.add(new Vec2(115,-0.75f));
			GhostLegAngle.add(new Vec2(20,+0.75f));
			GhostLegAngle.add(new Vec2(175,-0.75f));
			
		
		//particles.add(new BasicParticle(r,r,5,12, r,false, Color.GRAY, includeInbuiltCollisionDetection));

		
		barriers = new ArrayList<AnchoredBarrier>();
		
		switch (layout) {
			case CHALLENGE: {
				
				barriers.add(new AnchoredBarrier_StraightLine(-5f+2.5f,WORLD_HEIGHT/2.5f+0.435f,7+3.5f,WORLD_HEIGHT/2.5f+0.435f,Color.RED));
				barriers.add(new AnchoredBarrier_StraightLine(7+3.5f,WORLD_HEIGHT/2.5f+0.435f,7+3.5f+2.5f,WORLD_HEIGHT/2.5f+1.435f,Color.RED));
				barriers.add(new AnchoredBarrier_StraightLine(7+3.5f+2.5f,WORLD_HEIGHT/2.5f+1.435f,7+3.5f+2.5f,WORLD_HEIGHT/2.5f+0.435f,Color.RED));
				barriers.add(new AnchoredBarrier_StraightLine(7+3.5f+2.5f,WORLD_HEIGHT/2.5f+0.435f,22+3.5f+2.5f,WORLD_HEIGHT/2.5f+0.435f,Color.RED));
				break;
			}
		}
	}
	
	RayCastCallback cast = new RayCastCallback() {
		
		@Override
		public float reportFixture(Fixture fixture, Vec2 point, Vec2 normal, float fraction) {
			if(fixture!=null)
			{
				Object o = fixture.getUserData();
				if (o!=null  && o.equals("ground"))
				{
					System.out.println("Fixture " + fraction);
				}
			}
			return 0;
		}
	};
	static boolean newRightMousePressed;
	static boolean newLeftMousePressed;
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
	List<BasicPolygon> Newpolygons= new ArrayList<BasicPolygon>();
	DistanceJointDef jointDef = new DistanceJointDef();
	Joint joint;
	static BasicKeyListener KeyListener = new BasicKeyListener();  
	boolean GameOver;
	float acc= 100;
	float speed=17f;
	float AddForce =2.5f;
	public int linex1=0,liney1=0,linex2=0,liney2=0;
	public int ropelineX ,ropelineY;
	boolean check= false;
	public float offsetPointerOrigin=1f;
	public boolean showLine=false;
	public boolean showCurve = false;
	boolean canHang=true;
	//Body mainBody 
	
	public void update() { 
		linex1=BasicPhysicsEngineUsingBox2D.convertWorldXtoScreenX(particles.get(0).body.getPosition().x);
        liney1=BasicPhysicsEngineUsingBox2D.convertWorldYtoScreenY(particles.get(0).body.getPosition().y);
        ropelineX = convertWorldXtoScreenX(particles1.get(0).body.getPosition().x);
		ropelineY = convertWorldYtoScreenY(particles1.get(0).body.getPosition().y);
		newRightMousePressed= listener.isRightMouseButtonPressed();
		newLeftMousePressed = listener.isLeftMouseButtonPressed();
		for (int i = 0; i < LegPos.size(); i++) {
			double pointX = particles.get(0).body.getPosition().x;
			double pointY = particles.get(0).body.getPosition().y;
			double objX = newLegPos.get(i).body.getPosition().x;
			double objY = newLegPos.get(i).body.getPosition().y;
	
			double NewDitance = (objX - pointX);
			//double angleDegrees = angleRadians * 180 / Math.PI;
			//System.out.println("Angle in degrees: " + angleDegrees);
			if(Math.abs(NewDitance) < 0.35)
			{
				//System.out.println(" Its greater  and than 60 and -60 " + angleDegrees);
				newLegPos.get(i).body.setLinearVelocity(new Vec2(newLegPos.get(i).body.getLinearVelocity().x + GhostLegAngle.get(i).y,newLegPos.get(i).body.getLinearVelocity().y));
			}
//			else
//			{
//				newLegPos.get(i).body.setLinearVelocity(new Vec2(newLegPos.get(i).body.getLinearVelocity().x ,newLegPos.get(i).body.getLinearVelocity().y));
//			}
//			
//			//System.out.println("Distance between each leg " + i + " " + Math.abs(NewDitance));
//
	}
		
		for (int i = 0; i < LegPos.size(); i++) {
			float DistX = particles.get(0).body.getPosition().x - LegPos.get(i).body.getPosition().x;
			float DistY =	particles.get(0).body.getPosition().y - LegPos.get(i).body.getPosition().y;	
			double distance = Math.sqrt((DistX * DistX)+(DistY * DistY)); 
			if ((distance > DistanceList.get(i).x || distance < DistanceList.get(i).y) && newLegPos.get(i).isgrouned && check)  {
			    Vec2 direction = new Vec2(newLegPos.get(i).body.getPosition().x - LegPos.get(i).body.getPosition().x, newLegPos.get(i).body.getPosition().y - LegPos.get(i).body.getPosition().y);
			    direction.normalize();
			    LegPos.get(i).body.setLinearVelocity(new Vec2(speed * direction.x, speed * direction.y)); 
			} else {
			    LegPos.get(i).body.setLinearVelocity(new Vec2(0, 0));
			}
			
			
			
		}
		for (int i = 0; i < LegPos.size(); i++) {
			
			isGrounded(newLegPos.get(i));
			newLegPos.get(i).body.m_linearDamping = 2f;
			if(!newLegPos.get(i).isgrouned)
			{
				newLegPos.get(i).body.setPosition(LegPos.get(i).body.getPosition());
				//newLegPos.get(i).body.setLinearVelocity(Vec2.VecZero());
			}
		}
		for (int i = 0; i < legSensors.size(); i++) {
			
			isWalled(legSensors.get(i));
		}
		int VELOCITY_ITERATIONS=NUM_EULER_UPDATES_PER_SCREEN_REFRESH;
		int POSITION_ITERATIONS=NUM_EULER_UPDATES_PER_SCREEN_REFRESH;
		
		if(KeyListener.isMoveLeft())
		{			
			particles.get(0).body.setLinearVelocity(new Vec2(-AddForce,particles.get(0).body.getLinearVelocity().y));
			check = true;
			AddForceifWall(1,3);
			if(!isGrounded(newLegPos.get(0)) && !isGrounded(newLegPos.get(1)) &&
					 !isGrounded(newLegPos.get(2)) && !isGrounded(newLegPos.get(3)))
			{
				AddForce=7f;
				particles.get(0).body.setAngularDamping(10.0f);
			}
			else
			{
				AddForce=2.5f;
				
			}

		}
		
		else if(KeyListener.isMoveRight())
		{
			check = true;
			 particles.get(0).body.setLinearVelocity(new Vec2(AddForce,particles.get(0).body.getLinearVelocity().y));
			 AddForceifWall(0,2);
			 if(!isGrounded(newLegPos.get(0)) && !isGrounded(newLegPos.get(1)) &&
												 !isGrounded(newLegPos.get(2)) && !isGrounded(newLegPos.get(3)))
			 {
				 AddForce=7f;
				 particles.get(0).body.setAngularDamping(10.0f);
			 }
			 else
			 {
				 AddForce=2.5f;
			 }
		}
		
		 else
		 {
			 particles.get(0).body.setLinearVelocity(new Vec2(0,particles.get(0).body.getLinearVelocity().y));
		 }
		 
		 
		 if(newRightMousePressed)
		 {
			 showLine=true;
			 
		 }
		  if(newLeftMousePressed && canHang)
		 {
			  showCurve=true;
			  showLine=false;
			  jointDef.bodyA = particles1.get(0).body;
			  jointDef.bodyB = particles.get(0).body;
			  jointDef.length = Vec2.Distance(jointDef.bodyA.getPosition(), jointDef.bodyB.getPosition())-.5f;
			  joint = (DistanceJoint) world.createJoint(jointDef);
			  canHang = false;
			  Vec2 dir = jointDef.bodyA.getPosition().sub(jointDef.bodyB.getPosition());
			  float angle = (float)Math.atan2(dir.y, dir.x);
			  
			  // Convert the angle to degrees and set the rotation of the first particle
			  Vec2 anchorPosition = jointDef.bodyB.getPosition();
			    Vec2 particlePosition = jointDef.bodyA.getPosition();
			  float degrees = (float) Math.atan2(anchorPosition.y - particlePosition.y, anchorPosition.x - particlePosition.x);
			  System.out.println("degrees " + degrees);
			  particles.get(0).body.setTransform(particles.get(0).body.getPosition(), degrees);
		 }
		  
		  else if(!newLeftMousePressed && !canHang && !particles1.isEmpty())
		 {
			world.destroyJoint(joint);
			canHang=true;
			showCurve=false;
		 }
		  
		  if (KeyListener.isSpacePressed() 
												 && isGrounded(newLegPos.get(0)) && isGrounded(newLegPos.get(1)) &&
												 isGrounded(newLegPos.get(2)) && isGrounded(newLegPos.get(3))
												 )
		  {
			  System.out.println(" Check");
			  particles.get(0).body.setTransform(particles.get(0).body.getPosition(), 0);
			  particles.get(0).body.setLinearVelocity(new Vec2(particles.get(0).body.getLinearVelocity().x,particles.get(0).body.getLinearVelocity().y+25f));
		  }
		  else
		  {
			  particles.get(0).body.setLinearVelocity(new Vec2(particles.get(0).body.getLinearVelocity()));
		  } 
		for (BasicParticle p:particles) {
			p.notificationOfNewTimestep();
			
		}
		for (BasicPolygon p:polygons) {
			p.notificationOfNewTimestep();
			p.body.setFixedRotation(false);
			
		}
		for (BasicWheel w:Wheels) {
			w.notificationOfNewTimestep();
			w.body.setFixedRotation(false);
		}
		for (BasicNewRect r:Rectangle) {
			r.notificationOfNewTimestep();
			r.body.setFixedRotation(false);
			
		}
		world.step(DELTA_T, VELOCITY_ITERATIONS, POSITION_ITERATIONS);
	}
	WorldManifold worldManifold = new WorldManifold();
	public boolean isGrounded(LegDestination Leg) {
		Body body = Leg.body; 
	    for (ContactEdge ce = body.getContactList(); ce != null; ce = ce.next) {
	        Contact contact = ce.contact;
	        if (contact.isTouching()) {
	            contact.getWorldManifold(worldManifold);
	            //System.out.println( " grounded");
	            return Leg.isgrouned=true;
	        }
	    }
	    return Leg.isgrouned=false;
	}
	public boolean isWalled(BasicPolygon Leg) {
		Body body = Leg.body; 
	    for (ContactEdge ce = body.getContactList(); ce != null; ce = ce.next) {
	        Contact contact = ce.contact;
	        if (contact.isTouching()) {
	            contact.getWorldManifold(worldManifold);
	            //System.out.println( " grounded");
	            return Leg.isWalled=true;
	        }
	    }
	    return Leg.isWalled=false;
	}
	public void AddForceifWall(int n, int m)
	{			
			if(legSensors.get(n).isWalled)
			{
				LegPos.get(n).body.setLinearVelocity(new Vec2(LegPos.get(n).body.getLinearVelocity().x, LegPos.get(n).body.getLinearVelocity().y+4f));
				newLegPos.get(n).body.setPosition(new Vec2(LegPos.get(n).body.getPosition()));
			}
			
			if(legSensors.get(m).isWalled)
			{
				LegPos.get(m).body.setLinearVelocity(new Vec2(LegPos.get(m).body.getLinearVelocity().x, LegPos.get(m).body.getLinearVelocity().y+4f));
				newLegPos.get(m).body.setPosition(new Vec2(LegPos.get(m).body.getPosition()));
			}
	}
	// rotate the object to face the anchor point
	public float FaceAnchor(Vec2 playPos,Vec2 anchorPos)
	{ 
		Vec2 direction = anchorPos.sub(playPos); 
		float angle = direction.getAngle(anchorPos, direction);
		float degrees = (float) Math.toDegrees(angle); // Convert to degrees
		System.out.println(degrees);
		return degrees;
	}
	
	
	public void FreezeTheLegs()
	{
		for(int i=0;i< bodyParts.size();i++)
		{
			
		}
	}
}