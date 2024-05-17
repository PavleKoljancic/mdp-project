package model.dao;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;

import model.Ingredient;
import model.datachange.IDateChangedSubscriberIngridients;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPooled;
import redis.clients.jedis.params.ScanParams;
import redis.clients.jedis.resps.ScanResult;

public class IngredientDAO {

	private static final String INGREDIENT = "Ingredient:";
	private String hostName;
	private int port;
	private HashSet<IDateChangedSubscriberIngridients> subscribers;

	public IngredientDAO(String hostName, int port) {
		this.hostName = hostName;
		this.port = port;
		this.subscribers = new HashSet<IDateChangedSubscriberIngridients>();
	}

	public void create(Ingredient ingredient) {
		JedisPooled jedis = new JedisPooled(this.hostName, port);
		jedis.hset(INGREDIENT + ingredient.getName(), ingredient.toHashMap());
		jedis.close();
		this.informSubscribers();

	}

	public void updateQuantity(String ingridientName, double val) {
		JedisPooled jedis = new JedisPooled(this.hostName, port);
		jedis.hsetObject(INGREDIENT + ingridientName, "quantity", val);
		jedis.close();
		this.informSubscribers();

	}

	public void delete(String ingridientName) {
		JedisPooled jedis = new JedisPooled(this.hostName, port);
		jedis.del(INGREDIENT + ingridientName);
		jedis.close();
		this.informSubscribers();
	}

	public ArrayList<Ingredient> getAll() {
		String cursor = "0";
		String matchPattern = INGREDIENT + "*";
		ScanParams scanParams = new ScanParams().match(matchPattern);
		ArrayList<Ingredient> result = new ArrayList<Ingredient>();
		JedisPooled jedis = new JedisPooled(this.hostName, port);
		do {
			ScanResult<String> scanResult = jedis.scan(cursor, scanParams);

			cursor = scanResult.getCursor();

			for (String key : scanResult.getResult()) {

				// Use HSCAN to iterate over fields in the hash
				Map<String, String> hashFields = jedis.hgetAll(key);

				Ingredient temp = new Ingredient();
				temp.setName(hashFields.get("name"));
				temp.setQuantityAvailable(Double.valueOf(hashFields.get("quantity")));
				result.add(temp);

			}

		} while (!cursor.equals("0"));
		jedis.close();
		return result;
	}

	public boolean subscribe(IDateChangedSubscriberIngridients subscriber) {
		synchronized (this.subscribers) {
			return this.subscribers.add(subscriber);
		}
	}

	public boolean unsubscribe(IDateChangedSubscriberIngridients subscriber) {
		synchronized (this.subscribers) {
			return this.subscribers.remove(subscriber);
		}
	}

	private void informSubscribers() {
		synchronized (this.subscribers) {
			this.subscribers.forEach((s) -> s.onDataChanged(this));
		}
	}

	public boolean contains(String name) {
		JedisPooled jedis = new JedisPooled(this.hostName, port);

		boolean result = jedis.exists(INGREDIENT + name);
		jedis.close();
		return result;
	}
}
