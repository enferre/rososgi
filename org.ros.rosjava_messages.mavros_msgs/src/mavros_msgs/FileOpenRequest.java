package mavros_msgs;

public interface FileOpenRequest extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "mavros_msgs/FileOpenRequest";
  static final java.lang.String _DEFINITION = "# FTP::Open\n#\n# :file_path:\tused as session id in read/write/close services\n# :size:\tfile size returned for MODE_READ\n# :success:\tindicates success end of request\n# :r_errno:\tremote errno if applicapable\n\nuint8 MODE_READ = 0\t# open for read\nuint8 MODE_WRITE = 1\t# open for write\nuint8 MODE_CREATE = 2\t# do creat()\n\nstring file_path\nuint8 mode\n";
  static final byte MODE_READ = 0;
  static final byte MODE_WRITE = 1;
  static final byte MODE_CREATE = 2;
  java.lang.String getFilePath();
  void setFilePath(java.lang.String value);
  byte getMode();
  void setMode(byte value);
}
