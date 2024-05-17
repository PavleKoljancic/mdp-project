package mdp.distributor.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;



public interface IDistributorRegister extends Remote{

	public DistributorResponse register(String identificationName) throws RemoteException;
	public DistributorResponse connect(String identificationName) throws RemoteException;
	public DistributorResponse disconnect(String identificationName) throws RemoteException;
}
