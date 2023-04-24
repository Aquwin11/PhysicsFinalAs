package pbgLecture5lab_wrapperForJBox2D;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.BasicStroke;

import javax.swing.JComponent;


public class BasicView extends JComponent {
	/* Author: Michael Fairbank
	 * Creation Date: 2016-01-28
	 * Significant changes applied:
	 */
	// background colour
	public static final Color BG_COLOR = Color.BLACK;

	private BasicPhysicsEngineUsingBox2D game;

	public BasicView(BasicPhysicsEngineUsingBox2D game) {
		this.game = game;
	}
	
	@Override
	public void paintComponent(Graphics g0) {
		BasicPhysicsEngineUsingBox2D game;
		synchronized(this) {
			game=this.game;
		}
		Graphics2D g = (Graphics2D) g0;
		// paint the background
		g.setColor(BG_COLOR);
		g.fillRect(0, 0, getWidth(), getHeight());
		//allows view to follow the player when they move
		g.translate(BasicPhysicsEngineUsingBox2D.convertWorldXtoScreenX(-game.particles.get(0).body.getPosition().x)+BasicPhysicsEngineUsingBox2D.SCREEN_WIDTH,BasicPhysicsEngineUsingBox2D.convertWorldYtoScreenY(-game.particles.get(0).body.getPosition().y)-BasicPhysicsEngineUsingBox2D.SCREEN_HEIGHT*1.25f);
		for (BasicParticle p : game.particles)
			p.draw(g);
		for (BasicParticle p : game.grappleObj)
			p.draw(g);
		for (BasicPolygon p : game.polygons)
			p.draw(g);	
		//Enable this to see the sensors in the leg
		//To Check if the player is near a wall
//		for (BasicPolygon p : game.legSensors)
//			p.draw(g);
		//Enable this to see the new leg Position
		//For Automatic foot placement
//		for (BodyLeg p : game.newLegPos)
//			p.draw(g);
		for (BasicProjectile p : game.BulletList)
			p.draw(g);
		//Enable this to see the spider sholder joints
//		for (BodyLeg p : game.newLegPos1)
//			p.draw(g);
		for (BodyLeg p : game.LegPos)
			p.draw(g);
		for (BasicPolygon p : game.trials)
			p.draw(g);
		for (BasicNewRect p : game.Spikes)
			p.draw(g);
		for (AnchoredBarrier b : game.barriers)
			b.draw(g);
		if (game.showLine) {
			if(game.canShoot)
			{
				g.setColor(Color.WHITE); // change color when player is able to shoot
			}
			else
			{
				g.setColor(Color.DARK_GRAY); // change color when player is not able to shoot
			}
			g.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0, new float[] {10}, 0));//Doted lines
			g.drawLine(game.linex1,game. liney1,game. linex2 , game.liney2 );
		}
		if (game.showCurve) {
			g.setColor(Color.WHITE);
			g.drawLine(game.linex1,game. liney1,game.ropelineX,game.ropelineY);//grapple lines
		}
		
	}

	@Override
	public Dimension getPreferredSize() {
		return BasicPhysicsEngineUsingBox2D.FRAME_SIZE;
	}
	
	public synchronized void updateGame(BasicPhysicsEngineUsingBox2D game) {
		this.game=game;
	}
}