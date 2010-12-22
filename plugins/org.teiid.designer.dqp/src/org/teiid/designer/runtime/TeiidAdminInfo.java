package org.teiid.designer.runtime;

public class TeiidAdminInfo {
	
	private String host;
	private String password;
	private String port;
	private boolean secure;
	private String username;
	
	private boolean persistPassword = false;

	private static String MMS = "mms://"; //$NON-NLS-1$
	private static String MM = "mm://"; //$NON-NLS-1$
	private static String HOST = "host"; //$NON-NLS-1$
	private static String PORT = "port"; //$NON-NLS-1$
	
	public static String DEFAULT_HOST = "localhost"; //$NON-NLS-1$
	public static String DEFAULT_PORT = "31443"; //$NON-NLS-1$
	
	public TeiidAdminInfo(String host, String port, String username,
			String password, boolean persistPassword, boolean secure) {
		super();

		this.host = host;
		this.port = port;
		this.username = username;
		this.password = password;
		this.persistPassword = persistPassword;
		this.secure = secure;
	}
	
	public TeiidAdminInfo() {
		super();
		
		this.host = DEFAULT_HOST;
		this.port = DEFAULT_PORT;
		this.persistPassword = true;
		this.secure = true;
	}
	
	public String getHost() {
		return this.host;
	}
	
	public String getPassword() {
		return this.password;
	}
	
	public String getPort() {
		return this.port;
	}
	
    /**
    * @return the URL (never <code>null</code>)
    */
	public String getURL() {
		// mm<s>://host:port
		StringBuffer sb = new StringBuffer();

		if( this.secure) {
			sb.append(MMS);
		} else { 
			sb.append(MM);
		}
		
		if( this.host == null ) {
			sb.append(HOST);
		} else {
			sb.append(this.host);
		}
		sb.append(':');
		if( this.port == null ) {
			sb.append(PORT);
		} else {
			sb.append(this.port);
		}
		
		return sb.toString();
	}
	
	public String getUsername() {
		return this.username;
	}
	
    /**
     * @return persistPassword <code>true</code> if the password is being persisted
     */
    public boolean isPasswordBeingPersisted() {
        return this.persistPassword;
    }
	
	public boolean isSecure() {
		return this.secure;
	}
	
	public boolean isValidHost() {
		return ServerUtils.isValidHostName(this.host);
	}
	
	public boolean isValidPortNumber() {
        int portNumber;
        try {
            portNumber = Integer.parseInt(port);
        } catch (NumberFormatException nfe) {
            return false;
        }
        if (portNumber < 0 || portNumber > 0xFFFF) {
            return false;
        }
        return true;
	}
	
	public void setHost(String host) {
		this.host = host;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public void setPersistPassword(boolean persist) {
		this.persistPassword = persist;
	}
	

	public void setPort(String port) {
		this.port = port;
	}
	
	public void setSecure(boolean secure) {
		this.secure = secure;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(TeiidAdminInfo.class.getName()).append('\n')
		.append("Host: \t").append(this.host) //$NON-NLS-1$
		.append("Port: \t").append(this.port) //$NON-NLS-1$
		.append("Username:\t").append(this.username) //$NON-NLS-1$
		.append("password:\t").append(this.password) //$NON-NLS-1$
		.append("SSL:\t").append(this.secure) //$NON-NLS-1$
		.append("Save Pwd:\t").append(this.persistPassword); //$NON-NLS-1$
		return super.toString();
	}

	
	
}