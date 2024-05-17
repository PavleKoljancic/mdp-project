package model;

public interface IDataChangedSubscriber {
	
	public void onDataChanged(IngredientDAO source);
}
