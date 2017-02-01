package be.iminds.iot.ros.msgs.generator;

import org.osgi.service.component.annotations.Component;
import org.ros.internal.message.GenerateInterfaces;

/**
 * Wrap a gogo command around the rosjava message generator
 * 
 * @author tverbele
 *
 */
@Component(service = {Object.class},
	property = {"osgi.command.scope=ros", 
	"osgi.command.function=generate"},
	immediate=true)
public class MessageGenerator {
	
	public void generate(){
		try {
			GenerateInterfaces.main(new String[]{"generated_msgs"});
		} catch(Throwable t){
			t.printStackTrace();
		}
	}

	public void generate(String packagePath){
		try {
			GenerateInterfaces.main(new String[]{"generated_msgs","--package-path="+packagePath});
		} catch(Throwable t){
			t.printStackTrace();
		}
	}
}
