package pbgLecture5lab_wrapperForJBox2D;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.text.View;
import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.callbacks.RayCastCallback;
import org.jbox2d.collision.Manifold;
import org.jbox2d.collision.WorldManifold;
import org.jbox2d.common.MathUtils;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.contacts.Contact;
import org.jbox2d.dynamics.contacts.ContactEdge;
import org.jbox2d.dynamics.joints.DistanceJoint;
import org.jbox2d.dynamics.joints.DistanceJointDef;
import org.jbox2d.dynamics.joints.Joint;
import org.jbox2d.dynamics.joints.RevoluteJointDef;
import org.jbox2d.dynamics.joints.WeldJointDef;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.AudioInputStream;
import java.io.File;

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
	public static final float GRAVITY=12f;
	public static final boolean ALLOW_MOUSE_POINTER_TO_DRAG_BODIES_ON_SCREEN=false;// There's a load of code in basic mouse listener to process this, if you set it to true

	public static World world; // Box2D container for all bodies and barriers 

	// sleep time between two drawn frames in milliseconds 
	public static final int DELAY = 20;
	public static final int NUM_EULER_UPDATES_PER_SCREEN_REFRESH=10;
	// estimate for time between two frames in seconds 
	public static final float DELTA_T = DELAY / 750.0f;
	
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
		
	int numTrial=1;
	public BasicPolygon shootTrialObj;
	public List<BasicParticle> particles;
	public Body mainPlayerBody;
	public List<BasicParticle> grappleObj;
	public List<BasicProjectile> BulletList;
	public List<BasicPolygon> trials;
	public List<BasicPolygon> polygons;
	public List<BasicPolygon> legSensors;
	public List<BasicNewRect> Spikes;
	public List<AnchoredBarrier> barriers;
	public List<Vec2> DistanceList ;
	public List<Vec2> GhostLegAngle ;
	public List<BodyLeg> LegPos;
	public List<LegDestination> newLegPos;
	public List<LegDestination> newLegPos1;
	public boolean gameover=true;
	BasicParticle otherObj;
	//public List<RevoluteJointDef> bodyParts;
	public static enum LayoutMode {CHALLENGE,MAINLEVEL};
	public BasicPhysicsEngineUsingBox2D() {
		//Wheels = new ArrayList<BasicWheel>();
		int Welcome = JOptionPane.showConfirmDialog(null, "Welcome to Leggy's Logic", "Leggy's Logic", JOptionPane.DEFAULT_OPTION);
	    if (Welcome == JOptionPane.YES_OPTION) 
	    {
	    	//startGame();
	    	int Tutorial_Option = JOptionPane.showConfirmDialog(null, "                          Tutorial:                "+"\n Would you like to go through the tutorial", "Leggy's Logic", JOptionPane.YES_NO_OPTION);
	    	if(Tutorial_Option == JOptionPane.CLOSED_OPTION)
		    {
		    	System.exit(0);
		    }
	    	else if(Tutorial_Option == JOptionPane.YES_OPTION)
		    {
	    		int Tutorial1= JOptionPane.showConfirmDialog(null, "                       Movement Tutorial:                "+""
	    				+ "\n Press A to move left and D to move right " + 
	    				"\n Press SPACE to jump"+
	    				"\n Remember all your legs need to touch the ground to be able to jump"
	    				+"\n Press E to grapple" + 
	    				" \n Remember you can grapple only when your body turns yellow"
	    				+ "\n You can grapple to white circle objects",
	    				"Leggy's Logic", JOptionPane.DEFAULT_OPTION);
	    		if(Tutorial1==JOptionPane.YES_OPTION)
	    		{
	    			int Tutorial2= JOptionPane.showConfirmDialog(null, "                       Shoot Tutorial:                "+""
		    				+ "\n Press RIGHT MOUSE to aim" + 
		    				"\n Press LEFT MOUSE to shoot"
		    				+"\n You can shoot at the white Pentagon to trigger traps",
		    				"Leggy's Logic", JOptionPane.DEFAULT_OPTION);
		    		if(Tutorial2==JOptionPane.YES_OPTION)
		    		{
		    			int Tutorial3= JOptionPane.showConfirmDialog(null, "                     General Tips:                "+""
			    				+ "\n You can interact with objects that are the same color as you." + 
			    				"\n RED objects need to be avoided",
			    				"Leggy's Logic", JOptionPane.DEFAULT_OPTION);
			    		if(Tutorial3==JOptionPane.YES_OPTION)
			    		{
			    			startGame();
			    		}
			    		else if(Tutorial3==JOptionPane.CLOSED_OPTION)
			    		{
			    			startGame();
			    		}
		    		}
		    		else if(Tutorial2==JOptionPane.CLOSED_OPTION)
		    		{
		    			startGame();
		    		}
	    		}
	    		else if(Tutorial1==JOptionPane.CLOSED_OPTION)
	    		{
	    			startGame();
	    		}
		    }
	    	else if(Tutorial_Option == JOptionPane.NO_OPTION)
		    {
		    	startGame();
		    }
	    	else if(Tutorial_Option == JOptionPane.CLOSED_OPTION)
		    {
	    		System.exit(0);
		    }
	    	

	    }
	    else if(Welcome == JOptionPane.CLOSED_OPTION)
	    {
	    	System.exit(0);
	    }
//	    else if (win == JOptionPane.NO_OPTION)
//	    {
//	    	int winn = JOptionPane.showConfirmDialog(null, "          Your Score:"+"\nYou want to close window?", "CLOSE?", JOptionPane.YES_NO_OPTION);
//	        if (winn == JOptionPane.YES_OPTION) 
//	        {
//	        	System.exit(0);
//	        }
//	        else if (winn == JOptionPane.NO_OPTION)
//	        {   
//	        }
//	    }
		//startGame();
		
	}
	public void startGame()
	{
		System.out.println("gameover"+ gameover);
		world = new World(new Vec2(0, -GRAVITY));// create Box2D container for everything
		world.setContinuousPhysics(true);
		particles = new ArrayList<BasicParticle>();
		grappleObj = new ArrayList<BasicParticle>();
		polygons = new ArrayList<BasicPolygon>();
		Spikes = new ArrayList<BasicNewRect>();
		barriers = new ArrayList<AnchoredBarrier>();
		newLegPos = new ArrayList<LegDestination>();
		newLegPos1 = new ArrayList<LegDestination>();
		LegPos = new ArrayList<BodyLeg>();
		legSensors = new ArrayList<BasicPolygon>();
		BulletList = new ArrayList<BasicProjectile>();
		 trials = new ArrayList<BasicPolygon>();
		 DistanceList = new ArrayList<Vec2>();
		 GhostLegAngle = new ArrayList<Vec2>();
		//bodyParts= new ArrayList<RevoluteJointDef>();
		LayoutMode layout=LayoutMode.MAINLEVEL;
		// pinball:
		float linearDragForce=0f/*0.2f*/;
		float r=.3f;
		particles.add(new BasicParticle(3.732203f,5.0525184f,0f,0f,r*1.2f,Color.WHITE,15f,.2f,Constants.BIT_PLAYER_Body,(short) Constants.BIT_WALL,(short)0));
		
		mainPlayerBody = particles.get(0).body;
		particles.add(new BasicParticle(3.732203f,5.0525184f,0f,0f,0.1f,Color.WHITE,0.2f,.2f,Constants.BIT_PLAYER_Body,(short) Constants.BIT_WALL,(short)0));
		grappleObj.add(new BasicParticle(4f,10.525184f,0f,0f,r*1.2f,Color.WHITE,999999f,.2f,Constants.BIT_PLAYER_Body,(short) Constants.BIT_WALL,(short)0));
		grappleObj.add(new BasicParticle(42.732203f,8.325184f,0f,0f,r*1.2f,Color.WHITE,999999f,.2f,Constants.BIT_PLAYER_Body,(short) Constants.BIT_WALL,(short)0));
		polygons.add(new BasicPolygon(4.5847774f, 4.283193f+r*1.2f, 1, 0.25f, Color.WHITE, 4.8f,0.5f, Constants.BIT_PLAYER_Body,(short)Constants.BIT_WALL,(short)0));
		polygons.add(new BasicPolygon(4.5847774f-r*6.5f, 4.283193f+r*1.2f, 1, 0.25f, Color.WHITE, 4.8f,0.5f, Constants.BIT_PLAYER_Body,(short)Constants.BIT_WALL,(short)0));
		polygons.add(new BasicPolygon(4.5847774f, 4.283193f+r*1.2f, 1, 0.25f, Color.WHITE, 4.8f,0.5f, Constants.BIT_PLAYER_Body,(short)Constants.BIT_WALL,(short)0));
		polygons.add(new BasicPolygon(4.5847774f-r*6.5f, 4.283193f+r*1.2f, 1, 0.25f, Color.WHITE, 4.8f,0.5f , Constants.BIT_PLAYER_Body,(short)Constants.BIT_WALL,(short)0));
		polygons.add(new BasicPolygon(5.8847774f-r*6.5f, 4.283193f+r*1.2f, 1.5f, 0.2f, Color.WHITE, 4.8f,0.5f, Constants.BIT_PLAYER_Body,(short)Constants.BIT_WALL,(short)0));
		polygons.add(new BasicPolygon(4.4847774f-r*6.5f, 4.283193f+r*1.2f, 1.5f, 0.2f, Color.WHITE, 4.8f,0.5f, Constants.BIT_PLAYER_Body,(short)Constants.BIT_WALL,(short)0));
		polygons.add(new BasicPolygon(4.4847774f-r*6.5f + 3f, 4.283193f+r*1.2f, 1.5f, 0.2f, Color.WHITE, 6.8f,0.5f, Constants.BIT_PLAYER_Body,(short)Constants.BIT_WALL,(short)0));
		polygons.add(new BasicPolygon(4.4847774f-r*6.5f, 4.283193f+r*1.2f, 1.5f, 0.2f, Color.WHITE, 6.8f,0.5f, Constants.BIT_PLAYER_Body,(short)Constants.BIT_WALL,(short)0));
		newLegPos.add(new LegDestination(4.9963517f,4.8406105f, 0, 0, r/2f, Color.red, 5, 2, Constants.BIT_PLAYER_Body,(short) Constants.BIT_WALL,(short)0));
		newLegPos1.add(new LegDestination(4.9963517f,4.78f, 0, 0, r/2f, Color.GRAY, 5, 2, Constants.BIT_PLAYER_Body,(short) Constants.BIT_WALL,(short)0));
		newLegPos.add(new LegDestination(2.8907132f,4.8429394f, 0, 0,r/2f, Color.blue, 1, 2, Constants.BIT_PLAYER_Body,(short) Constants.BIT_WALL,(short)0));
		newLegPos1.add(new LegDestination(2.8907132f,4.8429394f, 0, 0,r/2f, Color.GRAY, 1, 2, Constants.BIT_PLAYER_Body,(short) Constants.BIT_WALL,(short)0));
		newLegPos.add(new LegDestination(5.828504f,4.840214f, 0, 0, r/2f, Color.cyan, 1, 2, Constants.BIT_PLAYER_Body,(short) Constants.BIT_WALL,(short)0));
		//newLegPos1.add(new LegDestination(5.828504f,4.840214f, 0, 0, r/2f, Color.GRAY, 1, 1, Constants.BIT_PLAYER_Body,(short) Constants.BIT_WALL,(short)0));
		newLegPos.add(new LegDestination(2.1355329f,4.8409095f, 0, 0, r/2f, Color.green, 1, 2, Constants.BIT_PLAYER_Body,(short) Constants.BIT_WALL,(short)0));
		//newLegPos1.add(new LegDestination(2.1355329f,4.8409095f, 0, 0, r/2f, Color.GRAY, 1, 1, Constants.BIT_PLAYER_Body,(short) Constants.BIT_WALL,(short)0));
		LegPos.add(new BodyLeg(4.9963517f,4.8406105f, 0, 0, r/2, Color.WHITE, 1, linearDragForce, Constants.BIT_PLAYER_Body, Constants.BIT_WALL,(short) 0));
		LegPos.add(new BodyLeg(2.8907132f,4.8429394f, 0, 0, r/2, Color.WHITE, 1, linearDragForce, Constants.BIT_PLAYER_Body, Constants.BIT_WALL,(short) 0));
		LegPos.add(new BodyLeg(5.828504f,4.840214f, 0, 0, r/2, Color.WHITE, 1, linearDragForce, Constants.BIT_PLAYER_Body, Constants.BIT_WALL,(short) 0));
		LegPos.add(new BodyLeg(2.1355329f,4.8409095f, 0, 0, r/2, Color.WHITE, 1, linearDragForce, Constants.BIT_PLAYER_Body, Constants.BIT_WALL,(short) 0));
		legSensors.add(new BasicPolygon(4.9963517f,4.8406105f, 0.095f, 0.05f, Color.WHITE, 1, linearDragForce, Constants.BIT_PLAYER_Body, Constants.BIT_WALL,(short) 0));
		//legSensors1.add(new BasicPolygon(4.9963517f,4.8406105f, 0.5f, 0.25f, Color.WHITE, 1, linearDragForce, Constants.BIT_PLAYER_Body, Constants.BIT_WALL,(short) 0));
		legSensors.add(new BasicPolygon(2.8907132f,4.8429394f, 0.095f, 0.05f, Color.WHITE, 3, linearDragForce, Constants.BIT_PLAYER_Body, Constants.BIT_WALL,(short) 0));
		//legSensors1.add(new BasicPolygon(2.8907132f,4.8429394f, 1f, 0.075f, Color.WHITE, 3, linearDragForce, Constants.BIT_PLAYER_Body, Constants.BIT_WALL,(short) 0));
		legSensors.add(new BasicPolygon(5.828504f, 4.840214f, 0.095f, 0.05f, Color.WHITE, 1, linearDragForce, Constants.BIT_PLAYER_Body, Constants.BIT_WALL,(short) 0));
		legSensors.add(new BasicPolygon(2.1355329f,4.8409095f, 0.095f, 0.05f, Color.WHITE, 1, linearDragForce, Constants.BIT_PLAYER_Body, Constants.BIT_WALL,(short) 0));
		shootTrialObj = new BasicPolygon(27, 11f/*4.75f*/, 1, 4, Color.GRAY, 1000f, 0, Constants.BIT_WALL, (short) (Constants.BIT_PLAYER_Body | Constants.BIT_WALL ),(short)0);
		shootTrialObj.body.setFixedRotation(true);
		shootTrialObj.body.m_type = BodyType.STATIC;
		BasicPolygon shootTrial1 = new BasicPolygon(26f,10.525184f , 0, 0, 0.5f, Color.white, 1, 0, 5, shootTrialObj.body, Constants.BIT_TARGET,(short) Constants.BIT_PLAYER_Body,(short)0);
		trials.add(shootTrialObj);
		shootTrial1.body.m_type = BodyType.STATIC;
		System.out.println("is it creating again");
		trials.add(shootTrial1);
		BasicPolygon GRABTrialObj = new BasicPolygon(69f, WORLD_WIDTH/5.95f, 1, 4, Color.GRAY, 1000f, 0, Constants.BIT_WALL, (short) (Constants.BIT_PLAYER_Body | Constants.BIT_WALL ),(short)0);
		trials.add(GRABTrialObj);
		BasicPolygon GRAB = new BasicPolygon(66f,WORLD_WIDTH/5.95f , 0, 0, 1f, Color.white, 1000, 0, 6, GRABTrialObj.body, Constants.BIT_SENSOR,(short)( Constants.BIT_PLAYER_Body | Constants.BIT_ADDITIONALBODY),(short)0);
		GRAB.body.m_type=BodyType.STATIC;
		GRAB.body.setTransform(GRAB.body.getPosition(), 90);
		GRAB.body.setFixedRotation(true);
		trials.add(GRAB);
		//Spikes.add(new BasicNewRect(8f,5f , 0, 0, 0.5f, Color.red, 1000, 0, 3, null, Constants.BIT_OBSTICLE,(short)( Constants.BIT_PLAYER_Body | Constants.BIT_ADDITIONALBODY),(short)0));
//		BasicPolygon shootTrial3 = new BasicPolygon(10f,5.525184f , 0, 0, 0.25f, Color.white, 1, 0, 5, Spikes.get(0).body, Constants.BIT_TARGET,(short) Constants.BIT_PLAYER_Body,(short)0);
//		trials.add(shootTrial3);
		otherObj = new BasicParticle(mainPlayerBody.getPosition().x-1,12f,0f,0f,0.36f,Color.WHITE,9f,.1f,Constants.BIT_WALL,(short) Constants.BIT_PLAYER_Body,(short)0);
		otherObj.body.setActive(false);
		BasicPolygon shootTrial2 = new BasicPolygon(10f,10.525184f , 0, 0, 0.5f, Color.white, 1, 0, 5, otherObj.body, Constants.BIT_TARGET,(short) Constants.BIT_PLAYER_Body,(short)0);
		shootTrial2.body.m_type = BodyType.STATIC;
		trials.add(shootTrial2);
		legSensors.get(2).body.setTransform(legSensors.get(2).body.getPosition(), 0.785398f);
		legSensors.get(3).body.setTransform(legSensors.get(3).body.getPosition(), -0.785398f);
		
		float polyWidth=1;
		float polyHeight=0.25f;
		grappleObj.get(0).body.m_type = BodyType.STATIC;
		grappleObj.get(1).body.m_type = BodyType.STATIC;
		
		
		//weldJoint
		WeldJointDef LowerPivotJoint =  new WeldJointDef();
		LowerPivotJoint.bodyA = particles.get(0).body;
		LowerPivotJoint.bodyB = particles.get(1).body;
		LowerPivotJoint.localAnchorA.set(new Vec2(0,0.32f));
		world.createJoint(LowerPivotJoint);
		
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
	    //bodyParts.add(jointDef);
	    
	    
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
	    //bodyParts.add(SjointDef);
	    
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
	    //bodyParts.add(BottomjointDef);
	    
	    
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
	    //bodyParts.add(secondBottomjointDef);
	    
	    
	    
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
	    //bodyParts.add(jointDef1);
	    
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
	    //bodyParts.add(sjointDef1);
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
	    //bodyParts.add(BottomjointDef1);
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
	    //bodyParts.add(sBottomjointDef1);
	    //
	    DistanceJointDef lfDestJoint = new DistanceJointDef();
	    LegDestination lfDest = newLegPos.get(0);
	    lfDestJoint.bodyA = mainBody.body;
	    lfDestJoint.bodyB = lfDest.body;
	    lfDestJoint.localAnchorA.set(new Vec2(0,0));
	    lfDestJoint.length = 1.25f;
	    lfDestJoint.frequencyHz = 0;
	    lfDestJoint.dampingRatio = 0;
	    lfDestJoint.collideConnected = false;
		world.createJoint(lfDestJoint);
		//bodyParts.add(lfDestJoint);
	    
		
	    
	    
	    
		RevoluteJointDef lfDestJoint1 = new RevoluteJointDef();
		LegDestination lfDest1 = newLegPos1.get(0);
		lfDestJoint1.bodyA = mainBody.body;
	    lfDestJoint1.bodyB = lfDest1.body;
	    lfDestJoint1.localAnchorA = new Vec2(.32f+0.85f,0.25f);
	    lfDestJoint1.enableLimit=true;
	    lfDestJoint1.lowerAngle = -1.785398f;
	    lfDestJoint1.upperAngle = 1.0472f ;
	    lfDestJoint1.collideConnected = false;
	    lfDestJoint1.maxMotorTorque = 0f;
	    lfDestJoint1.motorSpeed = 0f;
		world.createJoint(lfDestJoint1);
	    
		DistanceJointDef lfDestJointTest = new DistanceJointDef();
	    lfDestJointTest.bodyA = lfDest1.body;
	    lfDestJointTest.bodyB = lfDest.body; 
	    lfDestJointTest.length = 0.65f;
	    lfDestJointTest.frequencyHz = 0;
	    lfDestJointTest.dampingRatio = 0;
	    lfDestJointTest.collideConnected = false;
		world.createJoint(lfDestJointTest);
		
		//Distant Joint for LegDeestination LF
	    DistanceJointDef rfDestJoint = new DistanceJointDef();
	    LegDestination rfDest = newLegPos.get(1);
	    rfDestJoint.bodyA = mainBody.body;
	    rfDestJoint.bodyB = rfDest.body;
	    rfDestJoint.frequencyHz = 0;
	    rfDestJoint.dampingRatio = 0;
	    rfDestJoint.length = 1.05f;
	    rfDestJoint.collideConnected = false;
		world.createJoint(rfDestJoint);
		//bodyParts.add(rfDestJoint);
		RevoluteJointDef rfDestJoint1 = new RevoluteJointDef();
	    LegDestination rfDest1 = newLegPos1.get(1);
	    rfDestJoint1.bodyA = mainBody.body;
	    rfDestJoint1.bodyB = rfDest1.body;
	    rfDestJoint1.localAnchorA =  new Vec2(-.28f-0.85f,0f);
	    rfDestJoint1.lowerAngle = -1.0472f;
	    rfDestJoint1.upperAngle =  2.61799f ;
	    rfDestJoint1.collideConnected = false;
	    rfDestJoint1.maxMotorTorque = 80f;
	    rfDestJoint1.motorSpeed = 1f;
	    rfDestJoint1.collideConnected = false;
		world.createJoint(rfDestJoint1);
		
		DistanceJointDef rfDestJointTest = new DistanceJointDef();
	    rfDestJointTest.bodyA = rfDest1.body;
	    rfDestJointTest.bodyB = rfDest.body;
	    rfDestJointTest.frequencyHz = 0;
	    rfDestJointTest.dampingRatio = 0;
	    rfDestJointTest.length = 0.65f;
	    rfDestJointTest.collideConnected = false;
	    world.createJoint(rfDestJointTest);
	    
		//Distant Joint for LegDeestination RS
	    DistanceJointDef lsDestJoint = new DistanceJointDef();
	    LegDestination lsDest = newLegPos.get(2);
	    lsDestJoint.bodyA = mainBody.body;
	    lsDestJoint.bodyB = lsDest.body;
	    lsDestJoint.frequencyHz = 0;
	    lsDestJoint.dampingRatio = 0;
	    lsDestJoint.length = 1.85f;
		world.createJoint(lsDestJoint);
		
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
				srToeScesorJoint.localAnchorB = new Vec2(-.15f,0);
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
		case MAINLEVEL: {
			//barriers.add(new AnchoredBarrier_StraightLine(-5f+2.5f,WORLD_HEIGHT,-5f+2.5f,WORLD_HEIGHT/2.5f+0.435f,Color.RED));
			barriers.add(new AnchoredBarrier_StraightLine(-25f+2.5f,WORLD_HEIGHT/2.5f+0.435f,7+3.5f,WORLD_HEIGHT/2.5f+0.435f,Color.RED));
			barriers.add(new AnchoredBarrier_StraightLine(7+3.5f,WORLD_HEIGHT/2.5f+0.435f,7+3.5f+2.5f,WORLD_HEIGHT/2.5f+1.435f,Color.RED));
			barriers.add(new AnchoredBarrier_StraightLine(7+3.5f+2.5f,WORLD_HEIGHT/2.5f+1.435f,9+3.5f+2.5f,WORLD_HEIGHT/2.5f+0.435f,Color.RED));
			barriers.add(new AnchoredBarrier_StraightLine(9+3.5f+2.5f,WORLD_HEIGHT/2.5f+0.435f,35,WORLD_HEIGHT/2.5f+0.435f,Color.RED));
			barriers.add(new AnchoredBarrier_StraightLine(35,WORLD_HEIGHT/2.5f+0.435f,38,WORLD_HEIGHT/4.5f,Color.RED));
			barriers.add(new AnchoredBarrier_StraightLine(38,WORLD_HEIGHT/4.5f,42,WORLD_HEIGHT/4.5f,Color.RED));
			barriers.add(new AnchoredBarrier_StraightLine(42,WORLD_HEIGHT/4.5f,42,WORLD_HEIGHT/22.5f,Color.RED));
			barriers.add(new AnchoredBarrier_StraightLine(42,WORLD_HEIGHT/4.5f,45,WORLD_HEIGHT/4.5f,Color.RED));//Remove
			barriers.add(new AnchoredBarrier_StraightLine(45,WORLD_HEIGHT/4.5f,45,WORLD_HEIGHT/22.5f,Color.RED));
			barriers.add(new AnchoredBarrier_StraightLine(45,WORLD_HEIGHT/4.5f,55,WORLD_HEIGHT/4.5f,Color.RED));
			barriers.add(new AnchoredBarrier_StraightLine(55,WORLD_HEIGHT/4.5f,56,WORLD_HEIGHT/4,Color.RED));
			barriers.add(new AnchoredBarrier_StraightLine(56,WORLD_HEIGHT/4,57,WORLD_HEIGHT/4.5f,Color.RED));
			barriers.add(new AnchoredBarrier_StraightLine(57,WORLD_HEIGHT/4.5f,58,WORLD_HEIGHT/4f,Color.RED));
			barriers.add(new AnchoredBarrier_StraightLine(58,WORLD_HEIGHT/4f,59,WORLD_HEIGHT/4.5f,Color.RED));
			barriers.add(new AnchoredBarrier_StraightLine(59,WORLD_HEIGHT/4.5f,60,WORLD_HEIGHT/4f,Color.RED));
			barriers.add(new AnchoredBarrier_StraightLine(60,WORLD_HEIGHT/4f,61,WORLD_HEIGHT/4.5f,Color.RED));
			barriers.add(new AnchoredBarrier_StraightLine(61,WORLD_HEIGHT/4.5f,62,WORLD_HEIGHT/4f,Color.RED));
			barriers.add(new AnchoredBarrier_StraightLine(62,WORLD_HEIGHT/4f,63,WORLD_HEIGHT/4.5f,Color.RED));
			barriers.add(new AnchoredBarrier_StraightLine(63,WORLD_HEIGHT/4.5f,64,WORLD_HEIGHT/4f,Color.RED));
			barriers.add(new AnchoredBarrier_StraightLine(64,WORLD_HEIGHT/4f,65,WORLD_HEIGHT/4.5f,Color.RED));
			barriers.add(new AnchoredBarrier_StraightLine(64,WORLD_HEIGHT/4.5f,66,WORLD_HEIGHT/4.5f,Color.RED));
			barriers.add(new AnchoredBarrier_StraightLine(66,WORLD_HEIGHT/4.5f,70,WORLD_HEIGHT/4.5f,Color.RED));
			break;
		}
			case CHALLENGE: {
				
				barriers.add(new AnchoredBarrier_StraightLine(-5f+2.5f,WORLD_HEIGHT/2.5f+0.435f,7+3.5f,WORLD_HEIGHT/2.5f+0.435f,Color.RED));
				barriers.add(new AnchoredBarrier_StraightLine(7+3.5f,WORLD_HEIGHT/2.5f+0.435f,7+3.5f+2.5f,WORLD_HEIGHT/2.5f+1.435f,Color.RED));
				barriers.add(new AnchoredBarrier_StraightLine(7+3.5f+2.5f,WORLD_HEIGHT/2.5f+1.435f,7+3.5f+2.5f,WORLD_HEIGHT/2.5f+0.435f,Color.RED));
				barriers.add(new AnchoredBarrier_StraightLine(7+3.5f+2.5f,WORLD_HEIGHT/2.5f+0.435f,22+3.5f+2.5f,WORLD_HEIGHT/2.5f+0.435f,Color.RED));
				break;
			}
			
		}
		gameover=false;
		System.out.println("gameover2"+ gameover);
	}
	
	public void resetLevel()
	{
		System.out.println("Resetgameover2"+ gameover);
		particles .clear();
		grappleObj .clear();
		polygons.clear();
		Spikes.clear();
		barriers.clear();
		newLegPos.clear();
		newLegPos1.clear();
		LegPos.clear();
		legSensors.clear();
		 BulletList.clear();
		 trials.clear();
		 DistanceList.clear();
		 GhostLegAngle.clear();
		 startGame();
		 //BasicPhysicsEngineUsingBox2D()
		 check= false;
		 numTrial=0;
		 
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
	
	ContactListener contactListener = new ContactListener() {
		
		@Override
		public void preSolve(Contact contact, Manifold oldManifold) {
			
			
		}
		
		@Override
		public void postSolve(Contact contact, ContactImpulse impulse) {
			
			
		}
		
		@Override
		public void endContact(Contact contact) {
			
		}
		
		@Override
		public void beginContact(Contact contact) {
			Fixture f1 = contact.getFixtureA();
			Fixture f2 = contact.getFixtureB();
			
			Body b1 = f1.getBody();
			Body b2 = f2.getBody();
			
			Object o1 = b1.getUserData();
			Object o2 = b2.getUserData();
			if(o1.getClass() == BasicProjectile.class &&  o2.getClass() == AnchoredBarrier_StraightLine.class)
				{
				BulletList.remove(o1);
				world.destroyBody(b1);
				System.out.println("o1 = " + o1 + ", o2 = " + o2);
				}
				
			else if(o1.getClass() == AnchoredBarrier_StraightLine.class && o2.getClass() == BasicProjectile.class)
				{
					BulletList.remove(o2);
					world.destroyBody(b2);
				}
			
			
			if(o1.getClass() == BasicProjectile.class &&  o2.getClass() == BasicPolygon.class)
			{
				
				if(numTrial==0)
				{
					BasicPolygon trigger = (BasicPolygon) o2;
					trigger.changewallPosition();
					numTrial=1;
				}
				else if(numTrial==1)
				{
					otherObj.body.setActive(true);
					grappleObj.add(otherObj);
				}
				else if(numTrial==2)
				{
					BasicPolygon trigger = (BasicPolygon) o2;
					Body Spikebody = trigger.manipulatableBody;
					Spikebody.setActive(false);
					Spikes.remove(0);
					
				}
				BulletList.remove(o1);
				b1.setActive(false);
				world.destroyBody(b1);
				trials.remove(o2);
				b2.setActive(false);
				world.destroyBody(b2);
			}
			else if(o1.getClass() == BasicPolygon.class && o2.getClass() == BasicProjectile.class)
			{
				
				if(numTrial==0)
				{
					BasicPolygon trigger = (BasicPolygon) o1;
					trigger.changewallPosition();
					
					System.out.println("Check2 collision ");
					numTrial=1;
				}
				else if(numTrial==1)
				{
//					BasicPolygon trigger = (BasicPolygon) o1;
//					Body newGrappleObj  = trigger.manipulatableBody;
//					BasicParticle NewGrapple = (BasicParticle) newGrappleObj.getUserData();
//					grappleObj.add(NewGrapple);
					
				}
				else if(numTrial==2)
				{
					BasicPolygon trigger = (BasicPolygon) o1;
					Body Spikebody = trigger.manipulatableBody;
					Spikebody.setActive(false);
					Spikes.remove(0);
				}
				trials.remove(o1);
				world.destroyBody(b1);
				b1.setActive(false);
				BulletList.remove(o2);
				b2.setActive(false);
				world.destroyBody(b2);
			}			
			if(o1.getClass() == BasicNewRect.class && o2.getClass() == BodyLeg.class)
				{
					gameover=true;
					int LOST= JOptionPane.showConfirmDialog(null, "                       You Lost:                "+""
	
		    				+"\n TRY AGAIN ",
		    				"Leggy's Logic", JOptionPane.YES_NO_OPTION);
		    		if(LOST==JOptionPane.YES_OPTION)
		    		{
		    			resetLevel();
		    		}
		    		else if(LOST==JOptionPane.NO_OPTION)
		    		{
		    			System.exit(0);
		    		}
		    		else if(LOST==JOptionPane.CLOSED_OPTION)
		    		{
		    			System.exit(0);
		    		}
				}
			
			if(o1.getClass() == BasicPolygon.class && o2.getClass() == BodyLeg.class)
			{
				BasicPolygon trigger = (BasicPolygon) o1;
				Body wallBody = trigger.manipulatableBody;
				if(wallBody.getPosition().y<12f)
				{
					trigger.changewallPosition();
				}
				else
				{
					wallBody.setLinearVelocity(new Vec2(wallBody.getLinearVelocity()));
				}
				
			}
			else if(o2.getClass() == BasicPolygon.class && o2.getClass() == BodyLeg.class)
			{
				BasicPolygon trigger = (BasicPolygon) o1;
				Body wallBody = trigger.manipulatableBody;
				if(wallBody.getPosition().y<12f)
				{
					trigger.changewallPosition();
				}
				else
				{
					wallBody.setLinearVelocity(new Vec2(wallBody.getLinearVelocity()));
				}
			}
			
		}
	};
	public static void playSound(String filePath) {
	    try {
	        File soundFile = new File(filePath);
	        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(soundFile);
	        Clip clip = AudioSystem.getClip();
	        clip.open(audioInputStream);
	        clip.start();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	
	
	List<BasicPolygon> Newpolygons= new ArrayList<BasicPolygon>();
	DistanceJointDef jointDef = new DistanceJointDef();
	Joint joint;
	
	static BasicKeyListener KeyListener = new BasicKeyListener();  
	float acc= 100;
	float speed=17f;
	float AddForce =3.5f;//2.5f
	public int linex1=0,liney1=0,linex2=0,liney2=0;
	public int ropelineX ,ropelineY;
	boolean check= false;
	public float offsetPointerOrigin=1f;
	public boolean showLine=false;
	public boolean showCurve = false;
	float impulse = 250.75f;
	boolean isHanging=false;
	boolean canHang=false;
	boolean Justhang;
	float shootBuffer=7f;
	float shootCounter=7f;
	boolean canShoot = true;
	boolean canMoveRight = true;
	boolean canMoveLeft = true;
	DistanceJointDef FreezeJointR = new DistanceJointDef();
	boolean FreezeRight;
	Joint jointR;
	DistanceJointDef FreezeJointL= new DistanceJointDef();
	boolean FreezeLeft;
	Joint jointL;
	Vec2 AfterHangForce;
	DistanceJointDef FreezeJointLF= new DistanceJointDef();
	boolean FreezeLeftF;
	Joint jointLF;
	boolean FullBodyGrounded;
	Body GrapplePoint;
	//Body mainBody 
	
	public void update() { 
		
		if(!gameover)
		{
			//System.out.println("Screen "+ SCREEN_WIDTH +" "+SCREEN_HEIGHT);
			System.out.println("MousePoionter " +(linex1) + " " +(liney1)+" " +(linex2)  + " "+ (liney2));
			if(isGrounded1(LegPos.get(0)) && isGrounded1(LegPos.get(1)) && isGrounded1(LegPos.get(2)) && isGrounded1(LegPos.get(3)))
			{
				FullBodyGrounded=true;

			}
			else			
			{
				FullBodyGrounded=false;
			}
			linex1=BasicPhysicsEngineUsingBox2D.convertWorldXtoScreenX(particles.get(0).body.getPosition().x);
	        liney1=BasicPhysicsEngineUsingBox2D.convertWorldYtoScreenY(particles.get(0).body.getPosition().y);
	        
			newRightMousePressed= listener.isRightMouseButtonPressed();
			newLeftMousePressed = listener.isLeftMouseButtonPressed();
			for (int i = 0; i < LegPos.size(); i++) {
				double pointX = particles.get(0).body.getPosition().x;
				double objX = newLegPos.get(i).body.getPosition().x;
		
				double NewDitance = (objX - pointX);
				//double angleDegrees = angleRadians * 180 / Math.PI;
				//System.out.println("Angle in degrees: " + angleDegrees);
				if(Math.abs(NewDitance) < 0.35)
				{
					//System.out.println(" Its greater  and than 60 and -60 " + angleDegrees);
					newLegPos.get(i).body.setLinearVelocity(new Vec2(newLegPos.get(i).body.getLinearVelocity().x + GhostLegAngle.get(i).y,newLegPos.get(i).body.getLinearVelocity().y));
				}
//				else
//				{
//					newLegPos.get(i).body.setLinearVelocity(new Vec2(newLegPos.get(i).body.getLinearVelocity().x ,newLegPos.get(i).body.getLinearVelocity().y));
//				}
//				
//				//System.out.println("+ between each leg " + i + " " + Math.abs(NewDitance));
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
				    playSound("Sound/walk.wav");
				} else {
				    LegPos.get(i).body.setLinearVelocity(new Vec2(0, 0));
				}
				
				
				
			}
			for (int i = 0; i < LegPos.size(); i++) {
				
				isGrounded(newLegPos.get(i));
				isGrounded1(LegPos.get(i));
			}
			for (int i = 0; i < legSensors.size(); i++) {
				
				isWalled(legSensors.get(i));
			}
			int VELOCITY_ITERATIONS=NUM_EULER_UPDATES_PER_SCREEN_REFRESH;
			int POSITION_ITERATIONS=NUM_EULER_UPDATES_PER_SCREEN_REFRESH;
			
			if(BasicKeyListener.isMoveLeft() && canMoveLeft &&(isGrounded(newLegPos.get(0)) || isGrounded(newLegPos.get(1)) ||
					 isGrounded(newLegPos.get(2)) || isGrounded(newLegPos.get(3))))
			{			
				particles.get(0).body.setLinearVelocity(new Vec2(-AddForce,particles.get(0).body.getLinearVelocity().y));
				check = true;
				
				
			}

			else if(BasicKeyListener.isMoveRight() && canMoveRight && (isGrounded(newLegPos.get(0)) || isGrounded(newLegPos.get(1)) ||
					 isGrounded(newLegPos.get(2)) || isGrounded(newLegPos.get(3))))
			{
				check = true;
				 particles.get(0).body.setLinearVelocity(new Vec2(AddForce,particles.get(0).body.getLinearVelocity().y));	 
			}
			
			 else
			 {
				 particles.get(0).body.setLinearVelocity(new Vec2(0,particles.get(0).body.getLinearVelocity().y));
			 }
			 
			if(isHanging && BasicKeyListener.isMoveRight())
			 {
				 //particles.get(0).body.setAngularDamping(10.0f);
				particles.get(0).body.setLinearVelocity(new Vec2(AddForce , particles.get(0).body.getLinearVelocity().y));
				LegPos.get(0).body.setLinearVelocity(new Vec2(AddForce , AddForce));
				//newLegPos1.get(0).body.setLinearVelocity(new Vec2(-AddForce,AddForce));
			 }
			 if(isHanging && BasicKeyListener.isMoveLeft())
			{
				//System.out.println("FreezeRightJoint " + jointR + " " +  FreezeRight );
				particles.get(0).body.setLinearVelocity(new Vec2(-AddForce , AddForce));
				LegPos.get(1).body.setLinearVelocity(new Vec2(-AddForce , AddForce));
				//newLegPos1.get(1).body.setLinearVelocity(new Vec2(AddForce , AddForce));
			}
			 if(!BasicKeyListener.interactPressed() && Justhang && !FullBodyGrounded)
			  {
				  //AfterHangForce = mainPlayerBody.getLinearVelocity();
				  System.out.println(" AfterHangForce " + AfterHangForce);
				  Vec2 NormVec2 = AfterHangForce.normalise();
				  mainPlayerBody.setLinearVelocity(new Vec2(NormVec2.x*35,mainPlayerBody.getLinearVelocity().y));
				  Justhang=false;
			  }
			 
			 if(newRightMousePressed)
			 {
				 showLine=true; 
			 }	 
			 checkTheDistanceBetweenforGrapple();
			 //System.out.println(" Check" + GrapplePoint);
			  if(BasicKeyListener.interactPressed() && canHang && joint==null && particles.get(0).body.getLinearVelocity().y>=0f)
			 {
				  playSound("Sound/grapple.wav");
				  ropelineX = convertWorldXtoScreenX(GrapplePoint.getPosition().x);
				  ropelineY = convertWorldYtoScreenY(GrapplePoint.getPosition().y);
				  showCurve=true;
				  jointDef.bodyA = GrapplePoint;
				  jointDef.bodyB = particles.get(0).body;
				  jointDef.length = Vec2.Distance(jointDef.bodyA.getPosition(), jointDef.bodyB.getPosition());
				  joint = (DistanceJoint) world.createJoint(jointDef);
				  canHang = false;
				  isHanging = true;
				  Justhang=true;
				  Vec2 anchorPosition = jointDef.bodyB.getPosition();
				    Vec2 particlePosition = jointDef.bodyA.getPosition();
				  float degrees = (float) Math.atan2(anchorPosition.y - particlePosition.y, anchorPosition.x - particlePosition.x);
				  System.out.println("degrees " + degrees);
				  particles.get(0).body.setTransform(particles.get(0).body.getPosition(), degrees);
				  
				  AfterHangForce = mainPlayerBody.getLinearVelocity();
			 }		  
			 if(!BasicKeyListener.interactPressed() && joint!=null && isHanging)
			 {
				 isHanging = false;
				System.out.println(" CheckJoint" + joint);
				world.destroyJoint(joint);
				showCurve=false;
				joint=null;
				//System.out.println(" CheckJoint2" + joint);
				//world.destroyBody(particles1.get(0).body);
			 }
			  
			  FreezeLLegs();
			  AddForceifWall(2,3);
			  FreezeRLegs();
			  if (BasicKeyListener.isSpacePressed() 
													 && isGrounded1(LegPos.get(0)) && isGrounded1(LegPos.get(1)) &&
													 isGrounded1(LegPos.get(2)) && isGrounded1(LegPos.get(3))
													 )
			  {
				  //System.out.println(" Check");
				  //dddddaparticles.get(0).body.setTransform(particles.get(0).body.getPosition(), 0);
				  playSound("Sound/jump.wav");
				  particles.get(0).body.setLinearVelocity(new Vec2(particles.get(0).body.getLinearVelocity().x,impulse*1.5f));
				  //impulse++;
			  }
			  else
			  {
				  particles.get(0).body.setLinearVelocity(new Vec2(particles.get(0).body.getLinearVelocity()));
				  impulse=10f;
			  } 
			  
			  if(newLeftMousePressed && showLine && canShoot)
			  {
				  playSound("Sound/shoot.wav");
				  float newLinex1 = convertScreenXtoWorldX(linex1);
				  float newLiney1 = convertScreenYtoWorldY(liney1);
				  float newLinex2 = convertScreenXtoWorldX(linex2);
				  float newLiney2 = convertScreenYtoWorldY(liney2);
				  System.out.println("line "+ newLinex1+ " " + newLiney1 + " " + newLinex2+" " + newLiney2);
				  
				  float DifferenceX = newLinex2-newLinex1;
				  float DifferenceY = newLiney2-newLiney1;
				  System.out.println("Difference " + DifferenceX + " " + DifferenceY );
				  Vec2 CheckDirection = new Vec2(DifferenceX,DifferenceY);
				  //System.out.println("Check " + CheckDirection.normalise()+ " " + CheckDirection);
				  shootCounter=0;
				  showLine=false;
				  canShoot=false;
				  BasicProjectile Bullet = new BasicProjectile(particles.get(0).body.getPosition().x,particles.get(0).body.getPosition().y+0.0f,
						  1f,1f,0.15f,Color.WHITE,0f,.2f,Constants.BIT_PROJECTILE,(short) (Constants.BIT_WALL | Constants.BIT_TARGET ),(short)0);
				  //Vec2 Direction = new Vec2(mainPlayerBody.getPosition().x,mainPlayerBody.getPosition().y).sub(new Vec2(convertWorldXtoScreenX(linex2),convertWorldYtoScreenY(liney2)));
				  //System.out.println( Direction.normalise()+ " " + Direction);
				  Bullet.body.setLinearVelocity(new Vec2(CheckDirection.normalise()).mul(25f));
				  BulletList.add(Bullet);
			  }
			  if(shootCounter>=shootBuffer)
			  {
				  canShoot=true;
			  }
			  else
			  {
				  shootCounter+=0.05f;
			  }
			 if(shootTrialObj.body.getPosition().y>12f)
			 {
				 shootTrialObj.body.m_type = BodyType.STATIC;
			 }
			for (BasicParticle p:particles) {
				p.notificationOfNewTimestep();
				
			}
			for (BasicPolygon p:polygons) {
				p.notificationOfNewTimestep();
				p.body.setFixedRotation(false);
				
			}

			for (BasicNewRect r:Spikes) {
				r.notificationOfNewTimestep();
				r.body.setFixedRotation(false);
				
			}
			
			world.step(DELTA_T, VELOCITY_ITERATIONS, POSITION_ITERATIONS);
			world.setContactListener(contactListener);
		}

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
	
	public boolean isGrounded1(BodyLeg Leg) {
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
				canMoveRight=false;
			}
			else
			{
				canMoveRight=true;
			}
			if(legSensors.get(m).isWalled)
			{
				canMoveLeft=false;
			}
			else
			{
				canMoveLeft=true;
			}
			
	}
	// rotate the object to face the anchor point	
	public void FreezeRLegs()
	{
		float Dist = Vec2.Distance(newLegPos.get(2).body.getPosition(), newLegPos.get(0).body.getPosition());
		
		if(Dist>1.4f && !isGrounded(newLegPos.get(2)))
		{
			FreezeJointR.bodyA= newLegPos.get(0).body;
			FreezeJointR.bodyB = newLegPos.get(2).body;
			FreezeJointR.length = 0.75f;
			FreezeRight=true;
			jointR = (DistanceJoint)world.createJoint(FreezeJointR);
		}
		if(FreezeRight && (isGrounded(newLegPos.get(2)) || isGrounded1(LegPos.get(2)))){
			world.destroyJoint(jointR); 
			FreezeRight=false;
			jointR=null;
		}
	}
	public void FreezeLLegs()
	{
		float Dist = Vec2.Distance(newLegPos.get(3).body.getPosition(), newLegPos.get(1).body.getPosition());
		//System.out.println("Distance " + Dist  );
		if(Dist>1.4f && !isGrounded(newLegPos.get(3)))
		{
			
			FreezeJointL.bodyA= newLegPos.get(1).body;
			FreezeJointL.bodyB = newLegPos.get(3).body;
			FreezeJointL.length = 0.65f;
			FreezeLeft=true;
			jointL = (DistanceJoint)world.createJoint(FreezeJointL);
		}
		
		if(FreezeLeft && isGrounded(newLegPos.get(3)) && jointL != null)
		{
			world.destroyJoint(jointL); 
			FreezeLeft=false;
			jointL=null;
		}
		//System.out.println("FreezeLEftJoint " + FreezeLeft  );
	}

	public void checkTheDistanceBetweenforGrapple()
	{
		
		for(int i=0;i<grappleObj.size()-1;i++)
		{
			
			float DistX1 = particles.get(0).body.getPosition().x - grappleObj.get(i).body.getPosition().x;
			float DistY1 =	particles.get(0).body.getPosition().y - grappleObj.get(i).body.getPosition().y;	
			double distance1 = Math.sqrt((DistX1 * DistX1)+(DistY1 * DistY1));
			if(distance1 <= 4.6f && particles.get(0).body.getPosition().y>0 && !isHanging)
			{
				particles.get(1).col = Color.yellow;
			}
			else
			{
				particles.get(1).col = Color.white;
			}
			//System.out.println(i + " Distanceee" + distance1 );
			if(distance1 <= 4.6f && particles.get(0).body.getPosition().y>=0 && joint==null)
			{
				canHang=true;
				GrapplePoint = grappleObj.get(i).body;
			}
			else
			{
				canHang=false;
				GrapplePoint=null;
			}
			
		}
	}
	
}