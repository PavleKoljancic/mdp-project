package mdp.distributor.rmi;

public interface IConectionSubscriberRMI {
	
	public void disconnected(DistributorRegisterService s);
	public void connected(DistributorRegisterService s);
	public void register(DistributorRegisterService s);
}
