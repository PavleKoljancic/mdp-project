package mdp.distributor.rmi;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import model.Ingredient;
import model.IngredientDAO;

public class DistributorService implements IDistributorService{
	
	public DistributorService(IngredientDAO dao) {
		super();
		this.dao = dao;
	}

	private IngredientDAO dao;
	@Override
	public List<Ingredient> getIngridents() throws RemoteException {
		
		return new ArrayList<Ingredient>(dao.getAll());
	}

	@Override
	public DistributorResponse RequestOrder(List<Ingredient> order) throws RemoteException {
		
		synchronized (dao) {
			
				if(!dao.getAll().containsAll(order))
					return new DistributorResponse(false, "Not all of the igridients requested are avialable.");
				ArrayList<Ingredient> avialbleigridients = dao.getAll();
				ArrayList<Ingredient> updateList = new ArrayList<Ingredient>();
				double total=0;
				for(Ingredient item : order) 
					{
					
					Ingredient temp =avialbleigridients.get(avialbleigridients.indexOf(item));
						if(!(temp.getQuantityAvailable()>=item.getQuantityAvailable()))
							return new DistributorResponse(false, "Not all of the igridients requested are avialable in the requested quantity.");
						total+= temp.getPricePerQuantity()*item.getQuantityAvailable();
						updateList.add(new Ingredient(temp.getName(),temp.getQuantityAvailable()-item.getQuantityAvailable() , temp.getPricePerQuantity()));
					}
				updateList.stream().forEach((i)-> dao.update(i));
				return new DistributorResponse(true, "Order accapted. The total is:"+total);
		}
		
	}

}
