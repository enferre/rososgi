/*******************************************************************************
 *  ROSOSGi - Bridging the gap between Robot Operating System (ROS) and OSGi
 *  Copyright (C) 2015, 2017  imec - IDLab - UGent
 *
 *  This file is part of DIANNE  -  Framework for distributed artificial neural networks
 *
 *  DIANNE is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *   
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *   
 *  Contributors:
 *      Tim Verbelen, Steven Bohez
 *******************************************************************************/
package be.iminds.iot.robot.api;

import java.util.concurrent.CountDownLatch;

import org.osgi.util.promise.Deferred;
import org.osgi.util.promise.Promise;

import be.iminds.iot.robot.api.Robot;

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
