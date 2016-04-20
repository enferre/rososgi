package be.iminds.iot.robot.api;

import java.util.concurrent.CountDownLatch;

import org.osgi.util.promise.Deferred;
import org.osgi.util.promise.Promise;

public interface Robot {

	Promise<? extends Robot> waitFor(long time);

	default Promise<? extends Robot> waitFor(Promise<?> condition){
		final Deferred<Robot> d = new Deferred<Robot>();
		condition.then( p -> d.resolveWith(waitFor(0)), p -> d.fail(p.getFailure()));
		return d.getPromise();
	}
	
	default Promise<? extends Robot> waitFor(Promise<?>... conditions){
		final Deferred<Robot> d = new Deferred<Robot>();
		final CountDownLatch latch = new CountDownLatch(conditions.length);
		for(Promise<?> condition : conditions){
			condition.then(p -> {
				latch.countDown();
				if(latch.getCount()==0){
					d.resolveWith(waitFor(0));
				}
				return null;
			}, p -> d.fail(p.getFailure()));
		}
		return d.getPromise();
	}
	
	Promise<? extends Robot> stop();
	
}
