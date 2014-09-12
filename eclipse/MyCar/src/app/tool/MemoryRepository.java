package app.tool;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MemoryRepository<T> implements IRepository<T> {
	private Map<String, T> objs = new HashMap<String, T>();
	
	public void Create(String key, T obj) {
		objs.put(key, obj);
	}

	public T Read(String key) {
		return objs.get(key);
	}

	public void Update(String key, T obj) {
		objs.put(key, obj);
	}

	public void Delete(String key) {
		objs.remove(key);
	}

	public Set<String> Keys() {
		return objs.keySet();
	}

	public void BeginTransaction(IConnectionProvider provider) {

		
	}

	public void EndTransaction(IConnectionProvider provider) {

		
	}

}
