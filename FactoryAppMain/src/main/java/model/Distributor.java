package model;

import java.util.Objects;

public class Distributor {
	
	String identificationName;
	boolean connected;
	public Distributor() {
		
	}
	@Override
	public int hashCode() {
		return Objects.hash(identificationName);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Distributor other = (Distributor) obj;
		return Objects.equals(identificationName, other.identificationName);
	}
	public Distributor(String identificationName, boolean connected) {
		this.identificationName = identificationName;
		this.connected = connected;
	}
	public String getIdentificationName() {
		return identificationName;
	}
	public void setIdentificationName(String identificationName) {
		this.identificationName = identificationName;
	}
	public boolean isConnected() {
		return connected;
	}
	public void setConnected(boolean connected) {
		this.connected = connected;
	}
}
