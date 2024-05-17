package mdp.distributor.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import model.Ingredient;

public interface IDistributorService extends Remote {
	
	public List<Ingredient> getIngridents() throws RemoteException;
	public DistributorResponse RequestOrder(List<Ingredient> order) throws RemoteException;

}
