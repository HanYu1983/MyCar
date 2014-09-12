package app.tool;

import java.sql.Connection;

public interface IConnectionProvider {
	Connection getConnection();
	
	public static class SimpleConnectionProvider implements IConnectionProvider{
		private Connection connection;
		public SimpleConnectionProvider(Connection connection){
			this.connection = connection;
		}
		public Connection getConnection() {
			return connection;
		}
		public void setConnection(Connection connection) {
			this.connection = connection;
		}
	}
	public static class ThreadLocalConnectionProvider implements IConnectionProvider{
		private ThreadLocal<Connection> connection = new ThreadLocal<Connection>();
		public ThreadLocalConnectionProvider(Connection connection){
			this.connection.set(connection);
		}
		public Connection getConnection() {
			return connection.get();
		}
		public void setConnection(Connection connection) {
			if(connection == null){
				this.connection.remove();
			}else{
				this.connection.set(connection);
			}
		}
	}
}
