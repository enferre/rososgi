/*******************************************************************************
 *  ROSOSGi - Bridging the gap between Robot Operating System (ROS) and OSGi
 *  Copyright (C) 2015, 2016  imec - IDLab - UGent
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
package be.iminds.iot.keyboard.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.http.HttpService;

import be.iminds.iot.input.keyboard.api.KeyboardEvent;
import be.iminds.iot.input.keyboard.api.KeyboardEvent.Type;
import be.iminds.iot.input.keyboard.api.KeyboardListener;

@Component(service = { javax.servlet.Servlet.class }, 
	    property = { "alias:String=/keyboard/servlet",
					 "osgi.http.whiteboard.servlet.pattern=/keyboard/servlet", 
				     "aiolos.proxy=false" }, 
		immediate = true)
public class KeyboardServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private List<KeyboardListener> listeners = new ArrayList<>();
	
	@Reference(cardinality=ReferenceCardinality.MULTIPLE, policy=ReferencePolicy.DYNAMIC)
	synchronized void addKeyboardListener(KeyboardListener l, Map<String, Object> properties){
		List<KeyboardListener> copy = new ArrayList<>(listeners);
		copy.add(l);
		listeners = copy;
	}
	
	synchronized void removeKeyboardListener(KeyboardListener l, Map<String, Object> properties){
		List<KeyboardListener> copy = new ArrayList<>(listeners);
		copy.remove(l);
		listeners = copy;
	}
	
	@Reference
	void setHttpService(HttpService http) {
		try {
			// TODO How to register resources with whiteboard pattern?
			http.registerResources("/keyboard", "res", null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		KeyboardEvent.Type type = request.getParameter("type").equals("keydown") ? Type.PRESSED : Type.RELEASED;
		String key = request.getParameter("key");
		if(key==null){
			// on chrome you only get code?!
			// use code instead or convert to key or pass both?!
			String code = request.getParameter("code");
			if(code.startsWith("Key")){
				key = code.substring(3).toLowerCase();
			} else if(code.startsWith("Digit")){
				key = code.substring(5).toLowerCase();
			} else {
				key = code;
			}
		}
		
		for(KeyboardListener l : listeners){
			l.onEvent(new KeyboardEvent(type, key));
		}
	}

}
