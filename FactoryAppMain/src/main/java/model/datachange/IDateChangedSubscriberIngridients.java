package model.datachange;

import model.dao.IngredientDAO;

public interface IDateChangedSubscriberIngridients {
	
	public void onDataChanged(IngredientDAO source);
}
