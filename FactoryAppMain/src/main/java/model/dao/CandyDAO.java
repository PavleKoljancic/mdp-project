package model.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;

import model.Candy;
import model.datachange.IDateChangedSubscriberCandy;
import model.datachange.IDateChangedSubscriberIngridients;
import redis.clients.jedis.JedisPooled;
import redis.clients.jedis.params.ScanParams;
import redis.clients.jedis.resps.ScanResult;

public class CandyDAO implements Serializable{
	private static final String CANDY = "Candy:";
	private String hostName;
	private int port;
	private HashSet<IDateChangedSubscriberCandy> subscribers;
	
	public CandyDAO(String hostName, int port) {
		this.hostName = hostName;
		this.port = port;
		this.subscribers = new HashSet<IDateChangedSubscriberCandy>();
	}
	public boolean subscribe(IDateChangedSubscriberCandy subscriber) {
		synchronized (this.subscribers) {
			return this.subscribers.add(subscriber);
		}
	}

	public boolean unsubscribe(IDateChangedSubscriberCandy subscriber) {
		synchronized (this.subscribers) {
			return this.subscribers.remove(subscriber);
		}
	}

	private void informSubscribers() {
		synchronized (this.subscribers) {
			this.subscribers.forEach((s) -> s.onDataChanged(this));
		}
	}

	public boolean contains(Candy candy) {
		JedisPooled jedis = new JedisPooled(this.hostName, port);

		boolean result = jedis.exists(CANDY + candy.getName());
		jedis.close();
		return result;
	}
	
	public void set(Candy candy) {
		JedisPooled jedis = new JedisPooled(this.hostName, port);
		jedis.hset(CANDY + candy.getName(), candy.toHashMap());
		jedis.close();
		this.informSubscribers();

	}


	public void delete(Candy candy) {
		JedisPooled jedis = new JedisPooled(this.hostName, port);
		jedis.del(CANDY + candy.getName());
		jedis.close();
		this.informSubscribers();
	}
	
	public ArrayList<Candy> getAll() {
		String cursor = "0";
		String matchPattern = CANDY + "*";
		ScanParams scanParams = new ScanParams().match(matchPattern);
		ArrayList<Candy> result = new ArrayList<Candy>();
		JedisPooled jedis = new JedisPooled(this.hostName, port);
		do {
			ScanResult<String> scanResult = jedis.scan(cursor, scanParams);

			cursor = scanResult.getCursor();

			for (String key : scanResult.getResult()) {

				// Use HSCAN to iterate over fields in the hash
				Map<String, String> hashFields = jedis.hgetAll(key);

				Candy temp = new Candy();
				temp.setName(hashFields.get("name"));
				temp.setQuantity(Double.valueOf(hashFields.get("quantity")));
				temp.setPrice(Double.valueOf(hashFields.get("price")));
				result.add(temp);

			}

		} while (!cursor.equals("0"));
		jedis.close();
		return result;
	}

}
