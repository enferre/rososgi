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
package be.iminds.iot.robot.lfd.ui;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.http.HttpService;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

import be.iminds.iot.robot.lfd.api.Demonstration;
import be.iminds.iot.robot.lfd.api.Demonstrator;
import be.iminds.iot.robot.lfd.api.Step;

@Component(service={javax.servlet.Servlet.class},
	property={"alias:String=/lfd",
		 	  "osgi.http.whiteboard.servlet.pattern=/lfd"},
	immediate=true)
public class LfDui extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private Demonstrator demonstrator;
	
	private JsonParser parser = new JsonParser();
	
	private String imagePrefix = File.separator+"lfd"+File.separator+"images"+File.separator;
	
	@Reference
	void setHttpService(HttpService http){
		try {
			// TODO How to register resources with whiteboard pattern?
			http.registerResources("/lfd/ui", "res", null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Reference
	void setDemonstrator(Demonstrator d) {
		this.demonstrator = d;
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.sendRedirect("/lfd/ui/lfd.html");
	}
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("application/json");
		String method = request.getParameter("method");

		try {
			if(method.equals("demonstrations")){
				JsonArray result = new JsonArray();
				demonstrator.demonstrations().forEach(d -> result.add(new JsonPrimitive(d)));
				response.getWriter().print(result.toString());
				response.getWriter().flush();
			} else if(method.equals("load")){
				String name = request.getParameter("name");
				Demonstration d = demonstrator.load(name);
				response.getWriter().print(toJson(d));
				response.getWriter().flush();
			} else if(method.equals("step")) {
				String name = request.getParameter("name");
				String type = request.getParameter("type");
				Step.Type t = Step.Type.valueOf(type);
				Step s = demonstrator.step(name, t);
				response.getWriter().print(toJson(s));
				response.getWriter().flush();
			} else if(method.equals("save")) {
				String demonstrationJson = request.getParameter("demonstration");
				JsonObject json = (JsonObject)parser.parse(demonstrationJson);
				Demonstration d = demonstrationFromJson(json);
				demonstrator.save(d);
			} else if(method.equals("execute")) {
				boolean reversed = false;
				if(request.getParameter("reversed") != null) {
					reversed = Boolean.parseBoolean(request.getParameter("reversed"));
				}
				
				String name =  request.getParameter("name");
				if(name != null) {
					Demonstration d = demonstrator.load(name);
					demonstrator.execute(d, reversed)
								.then(p -> {executionSuccess(response.getWriter()); return null;}, 
								      p -> executionError(response.getWriter(), p.getFailure())).getValue();
				}
				
				String demonstrationJson = request.getParameter("demonstration");
				if(demonstrationJson != null) {
					JsonObject json = (JsonObject)parser.parse(demonstrationJson);
					Demonstration d = demonstrationFromJson(json);
					demonstrator.execute(d, reversed)
								.then(p -> {executionSuccess(response.getWriter()); return null;}, 
									p -> executionError(response.getWriter(), p.getFailure())).getValue();					
				}
				
				String stepJson = request.getParameter("step");
				if(stepJson != null) {
					JsonObject json = (JsonObject)parser.parse(stepJson);
					Step s = stepFromJson(json);
					demonstrator.execute(s, reversed)
								.then(p -> {executionSuccess(response.getWriter()); return null;}, 
									  p -> executionError(response.getWriter(), p.getFailure())).getValue();				
				}
			} else if(method.equals("interrupt")) {
				demonstrator.interrupt();
			} else if(method.equals("recover")) {
				demonstrator.recover();
			} else if(method.equals("guide")) {
				demonstrator.guide();
			} else if(method.equals("record")) {
				// TODO parameterize the rate
				UUID id = demonstrator.record(10);
				response.getWriter().println("\""+id+"\"");
				response.getWriter().flush();
			} else if(method.equals("stop")) {
				if(request.getParameter("id") != null) {
					UUID id = UUID.fromString(request.getParameter("id"));
					demonstrator.stop(id);
					response.getWriter().println("\""+id+"\"");
					response.getWriter().flush();
				} else {
					demonstrator.stop();
				}
			}
		} catch(Throwable t) {
			t.printStackTrace();
		}
	}
	
	private JsonObject toJson(Step step) {
		JsonObject s = new JsonObject();
		s.add("type", new JsonPrimitive(""+step.type));
		step.properties.entrySet().forEach(e -> {
			String key = e.getKey();
			String value = e.getValue();
			if(value != null && !value.isEmpty()) {
				if(value.endsWith(".jpg")) {
					// remap image urls
					value = imagePrefix+value;
				}
				s.add(key, new JsonPrimitive(value));
			}
		});
		return s;
	}
	
	private void executionError(PrintWriter writer, Throwable e) {
		// TODO recover?
		
		// set back to guide mode after execution?
		demonstrator.guide();
		
		JsonObject result = new JsonObject();
		result.add("success", new JsonPrimitive(false));
		result.add("message", new JsonPrimitive(e.getMessage()));
		writer.println(result.toString());
		writer.flush();
	}
	
	private void executionSuccess(PrintWriter writer) {
		// set back to guide mode after execution
		demonstrator.guide();
		
		JsonObject result = new JsonObject();
		result.add("success", new JsonPrimitive(true));
		writer.println(result.toString());
		writer.flush();
	}
	
	private JsonObject toJson(Demonstration demonstration) {
		JsonObject d = new JsonObject();
		d.add("name", new JsonPrimitive(demonstration.name));
		JsonArray s = new JsonArray();
		demonstration.steps.forEach(step -> s.add(toJson(step)));
		d.add("steps", s);
		return d;
	}
	
	private Demonstration demonstrationFromJson(JsonObject json) {
		Demonstration d = new Demonstration();
		d.name = json.get("name").getAsString();
		
		JsonArray steps = json.getAsJsonArray("steps");
		steps.forEach(jsonStep -> {
			JsonObject jsonObject = ((JsonObject)jsonStep);
			Step step = stepFromJson(jsonObject);
			d.steps.add(step);
		});
		return d;
	}
	
	private Step stepFromJson(JsonObject json) {
		Step step = new Step();
		step.type = Step.Type.valueOf( json.get("type").getAsString());
		json.entrySet().forEach(e -> {
			if(!e.getKey().equals("type")) {
				String key = e.getKey();
				String value = e.getValue().getAsString();
				if(value.startsWith(imagePrefix)) {
					// map back to original image paths
					value = value.substring(imagePrefix.length());
				}
				step.properties.put(key, value);
			}
		});
		return step;
	}
}
