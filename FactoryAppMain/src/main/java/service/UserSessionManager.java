package service;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.HashMap;

import model.User;

public class UserSessionManager {
	
	private static UserSessionManager instance =null;
	
	public static UserSessionManager getInstance() 
	{	
		if(instance==null)
			 instance = new UserSessionManager();
		return instance;
	}
	
	HashMap<String,User> keyMap;
	SecureRandom secureRandom;
	private UserSessionManager() {
		this.keyMap = new HashMap<String, User>();
		this.secureRandom = new SecureRandom();
	}
	
	public String startUserSession(User user) {
		if(this.keyMap.values().contains(user))
			return null;
		byte [] keyBytes = new byte[64];
		secureRandom.nextBytes(keyBytes);
		String key = Base64.getUrlEncoder().encodeToString(keyBytes);
		this.keyMap.put(key, user);
		return key;
	}
	public boolean endUserSession(String key,String username) 
	{
		if(this.keyMap.containsKey(key)&&this.keyMap.get(key).getUsername().equals(username))
		{	
			this.keyMap.remove(key);
			return true;
		}
		return false;
	}
	public boolean checkUserSession(String key,String username) 
	{
		return this.keyMap.containsKey(key)&&this.keyMap.get(key).getUsername().equals(username);
	}
}
