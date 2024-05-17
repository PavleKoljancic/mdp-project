package mdp.distributor.rmi;
import java.io.Serializable;

public class DistributorResponse implements Serializable{
	
	public DistributorResponse(boolean success, String message) {
		this.success = success;
		this.message = message;
	}
	public boolean isSuccess() {
		return success;
	} 
	public String getMessage() {
		return message;
	}
	boolean success;
	String message;
	
}
