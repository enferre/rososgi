package be.iminds.iot.input.keyboard.api;

public class KeyboardEvent {

	
	public enum Type {
		PRESSED,
		RELEASED
	}
	
	public final Type type;
	public final String key;
	
	public KeyboardEvent(Type type, String key){
		this.type = type;
		this.key = key;
	}
	
	@Override
	public String toString(){
		return key+" "+type;
	}
}
