package be.iminds.iot.robot.api;

import org.osgi.util.promise.Deferred;
import org.osgi.util.promise.Promise;

public interface Robot {

	Promise<? extends Robot> waitFor(long time);

	default Promise<? extends Robot> waitFor(Promise<?> condition){
		final Deferred<Robot> d = new Deferred<Robot>();
		condition.then( p -> d.resolveWith(waitFor(0)), p -> d.fail(p.getFailure()));
		return d.getPromise();
	}
	
	Promise<? extends Robot> stop();
	
}
