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
		//	
			int posX=(int)game.particles.get(0).body.getPosition().x;
			int posY=(int)game.particles.get(0).body.getPosition().y;
			
			//g0.translate(BasicPhysicsEngineUsingBox2D.convertWorldXtoScreenX(-posX),BasicPhysicsEngineUsingBox2D.convertWorldYtoScreenY(posY));
		}
		Graphics2D g = (Graphics2D) g0;
		// paint the background
		g.setColor(BG_COLOR);
		g.fillRect(0, 0, getWidth(), getHeight());
		//g.translate(BasicPhysicsEngineUsingBox2D.convertWorldXtoScreenX(-game.polygons.get(0).body.getPosition().x)+BasicPhysicsEngineUsingBox2D.SCREEN_WIDTH,BasicPhysicsEngineUsingBox2D.convertWorldYtoScreenY(-game.polygons.get(0).body.getPosition().y)-BasicPhysicsEngineUsingBox2D.SCREEN_HEIGHT*1.5f);
		g.translate(-game.particles.get(0).body.getPosition().x,game.particles.get(0).body.getPosition().y );
		for (BasicParticle p : game.particles)
			p.draw(g);
		for (BasicPolygon p : game.polygons)
			p.draw(g);	
		for (LegDestination p : game.newLegPos)
			p.draw(g);
		for (ElasticConnector c : game.connectors)
			c.draw(g);
		for (AnchoredBarrier b : game.barriers)
			b.draw(g);
		if (game.showLine) {
			g.setColor(Color.WHITE);
			g.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0, new float[] {10}, 0));
			g.drawLine(game.linex1,game. liney1,game. linex2, game.liney2);
		}
		
	}

	@Override
	public Dimension getPreferredSize() {
		return BasicPhysicsEngineUsingBox2D.FRAME_SIZE;
	}
	
	public synchronized void updateGame(BasicPhysicsEngineUsingBox2D game) {
		this.game=game;
	}
	/*
	 * public double calculateDistanceBetweenPointsWithHypot( int x1, int y1, int
	 * x2, int y2) {
	 * 
	 * float ac = Math.abs(BasicPhysicsEngineUsingBox2D.convertScreenYtoWorldY(y2) -
	 * BasicPhysicsEngineUsingBox2D.convertScreenYtoWorldY(y1)); float cb =
	 * Math.abs(BasicPhysicsEngineUsingBox2D.convertScreenXtoWorldX(x2) -
	 * BasicPhysicsEngineUsingBox2D.convertScreenXtoWorldX(x1));
	 * 
	 * double mag = Math.hypot(ac, cb); return mag; // double newX = ac/mag; //
	 * double newY = cb/mag; // System.out.println(newX + " " +newY); }
	 */
	
	
	
	/*
	 * public double normalizeY( int x1, int y1, int x2, int y2) { float ac = y2 -
	 * y1; float cb = x2 - x1; // float ac = Math.abs(y2 - y1); // float cb =
	 * Math.abs(x2 - x1);
	 * 
	 * double mag = Math.hypot(ac, cb); double newX = ac/mag;
	 * 
	 * double newY = cb/mag; System.out.println("World Coordinates " + newX *
	 * 627.97f + " " +newY * 102.71587); // System.out.println("Screen Coordinates "
	 * + BasicPhysicsEngineUsingBox2D.convertWorldXtoScreenX((float) newX) + " "
	 * +BasicPhysicsEngineUsingBox2D.convertWorldYtoScreenY((float)newY)); return
	 * newY * 102.7158; }
	 */
}