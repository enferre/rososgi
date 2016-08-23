package be.iminds.iot.robot.input;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.http.HttpService;

import be.iminds.iot.robot.api.Arm;
import be.iminds.iot.robot.api.OmniDirectional;

@Component(service = { javax.servlet.Servlet.class }, 
	    property = { "alias:String=/robot/control",
					 "osgi.http.whiteboard.servlet.pattern=/robot/control", 
				     "aiolos.proxy=false" }, 
		immediate = true)
public class InputServlet extends HttpServlet {

	private Arm arm;
	private OmniDirectional base;
	
	private float velocity = 0.5f;
	
	private float vx = 0;
	private float vy = 0;
	private float va = 0;
	
	@Reference
	void setHttpService(HttpService http) {
		try {
			// TODO How to register resources with whiteboard pattern?
			http.registerResources("/robot", "res", null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.sendRedirect("/robot/control.html");
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String type = request.getParameter("type");
		String key = request.getParameter("key");
		
		// control base
		if(type.equals("keydown")){
			switch(key){
				case "ArrowUp":
				case "w":
					vy = velocity;
					base.move(vx, vy, va);
					break;
				case "ArrowDown":
				case "s":
					vy = -velocity;
					base.move(vx, vy, va);
					break;
				case "ArrowLeft":
				case "a":
					vx = velocity;
					base.move(vx, vy, va);
					break;
				case "ArrowRight":
				case "d":
					vx = -velocity;
					base.move(vx, vy, va);
					break;
				case "q":
					va = velocity;
					base.move(vx, vy, va);
					break;
				case "e":
					va = -velocity;
					base.move(vx, vy, va);
					break;
				case "t":
					arm.setPosition(0, arm.getJoints().get(0).positionMax);
					break;
				case "g":
					arm.setPosition(0, arm.getJoints().get(0).positionMin);
					break;
				case "y":
					arm.setPosition(1, arm.getJoints().get(1).positionMax);
					break;
				case "h":
					arm.setPosition(1, arm.getJoints().get(1).positionMin);
					break;
				case "u":
					arm.setPosition(2, arm.getJoints().get(2).positionMax);
					break;
				case "j":
					arm.setPosition(2, arm.getJoints().get(2).positionMin);
					break;
				case "i":
					arm.setPosition(3, arm.getJoints().get(3).positionMax);
					break;
				case "k":
					arm.setPosition(3, arm.getJoints().get(3).positionMin);
					break;
				case "o":
					arm.setPosition(4, arm.getJoints().get(4).positionMax);
					break;
				case "l":
					arm.setPosition(4, arm.getJoints().get(4).positionMin);
					break;	
				case "m":
					arm.openGripper();
					break;
				case "n":
					arm.closeGripper();
					break;	
					
			}
		} else if(type.equals("keyup")){
			switch(key){
				case "ArrowUp":
				case "w":
				case "ArrowDown":
				case "s":
					vy = 0;
					base.move(vx, vy, va);
					break;
				case "ArrowLeft":
				case "a":
				case "ArrowRight":
				case "d":
					vx = 0;
					base.move(vx, vy, va);
					break;
				case "q":
				case "e":
					va = 0;
					base.move(vx, vy, va);
					break;
				case "t":
				case "g":
					arm.stop(0);
					break;
				case "y":
				case "h":
					arm.stop(1);
					break;
				case "u":
				case "j":
					arm.stop(2);
					break;
				case "i":
				case "k":
					arm.stop(3);
					break;
				case "o":
				case "l":
					arm.stop(4);
					break;
			}
		}
	}

	
	@Reference
	void setArm(Arm arm){
		this.arm = arm;
	}
	
	@Reference
	void setBase(OmniDirectional base){
		this.base = base;
	}

}
