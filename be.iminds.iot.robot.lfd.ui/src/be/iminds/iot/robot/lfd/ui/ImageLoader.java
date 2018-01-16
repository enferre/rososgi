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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

@Component(service={javax.servlet.Servlet.class},
	property={"alias:String=/lfd/images",
		 	  "osgi.http.whiteboard.servlet.pattern=/lfd/images"},
	immediate=true)
public class ImageLoader extends HttpServlet {

	private static final long serialVersionUID = 1L;
	
	private String demonstrationsLocation = "demonstrations";

	@Activate
	void activate(BundleContext context) {
		String l = context.getProperty("be.iminds.iot.robot.lfd.demonstrations.location");
		if(l != null) {
			demonstrationsLocation = l;
		}
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType("image/jpeg");

		String fileName = req.getRequestURI().substring(11);
		String path = demonstrationsLocation+File.separator+fileName;
		
		try(
			BufferedOutputStream out = new BufferedOutputStream(resp.getOutputStream());
		){
			out.write(Files.readAllBytes(Paths.get(path)));
			out.flush();	
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
	}
}
