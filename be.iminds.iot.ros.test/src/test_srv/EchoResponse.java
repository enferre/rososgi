package test_srv;

public interface EchoResponse extends org.ros.internal.message.Message {
	static final java.lang.String _TYPE = "test_srv/EchoRequest";
	static final java.lang.String _DEFINITION = "string data";
	java.lang.String getData();
	void setData(java.lang.String value);
}
