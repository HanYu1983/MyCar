package app.tool;

import java.util.Set;

public interface IRepository <T> {
	void Create(String key, T obj)throws Exception;
	T Read(String key)throws Exception;
	void Update(String key, T obj)throws Exception;
	void Delete(String key)throws Exception;
	Set<String> Keys();
}
