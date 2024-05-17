package model.datachange;

import model.dao.CandyDAO;

public interface IDateChangedSubscriberCandy {
	public void onDataChanged(CandyDAO source);
}
