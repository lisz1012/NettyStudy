package lisz.com.nettystduy.s02;

public enum ProtocolMessage {
	CLOSE("_bye_"),;
	
	private String message;
	
	private ProtocolMessage(String message) {
		this.message = message;
	}
	
	public String getMessage() {
		return message;
	}
}
