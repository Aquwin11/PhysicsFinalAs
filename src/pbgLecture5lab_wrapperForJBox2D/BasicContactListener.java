package pbgLecture5lab_wrapperForJBox2D;
import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.collision.Manifold;
import org.jbox2d.dynamics.contacts.Contact;

public class BasicContactListener implements ContactListener {

    @Override
    public void beginContact(Contact contact) {
        // Called when two fixtures start touching each other
        // Implement your logic here
    }

    @Override
    public void endContact(Contact contact) {
        // Called when two fixtures stop touching each other
        // Implement your logic here
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {
        // Called before collision resolution
        // Implement your logic here
    }


	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {
		// TODO Auto-generated method stub
		
	}
}
