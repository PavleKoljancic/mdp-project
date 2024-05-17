package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;



public class CandyOrder implements Serializable{
	
	public CandyOrder() {
	
	}
	public CandyOrder(ArrayList<Candy> orderList, String email) {
		this.timestamp= new Date();
		this.orderItems = new Candy[orderList.size()];
		for(int i=0;i<orderList.size();i++)
			orderItems[i]= new Candy(orderList.get(i).getName(),orderList.get(i).getPrice(),orderList.get(i).getQuantity());
		this.email =email;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public Candy[] getOrderItems() {
		return orderItems;
	}
	public void setOrderItems(Candy[] orderItems) {
		this.orderItems = orderItems;
	}
	public Date getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}
	private Candy [] orderItems;
	private Date timestamp; 
	private String email;
}
