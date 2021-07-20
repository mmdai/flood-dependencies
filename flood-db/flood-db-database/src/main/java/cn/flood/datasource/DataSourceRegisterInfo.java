package cn.flood.datasource;

/**  
* <p>Title: DataSourceRegisterInfo</p>  
* <p>Description: 多数据源配置文件</p>  
* @author mmdai  
* @date 2020年8月12日  
*/
public class DataSourceRegisterInfo {
	
    private boolean primary = false;

    private String url;
	
	private String username;
	
	private String password;
	
	private String driverClassName;

	public boolean isPrimary() {
		return primary;
	}

	public void setPrimary(boolean primary) {
		this.primary = primary;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getDriverClassName() {
		return driverClassName;
	}

	public void setDriverClassName(String driverClassName) {
		this.driverClassName = driverClassName;
	}
	
	

}
