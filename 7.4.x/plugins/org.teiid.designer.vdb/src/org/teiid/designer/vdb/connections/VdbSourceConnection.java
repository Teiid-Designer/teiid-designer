package org.teiid.designer.vdb.connections;

/**
 * Class to hold the basic info required for a VDB source connection
 *
 */
public class VdbSourceConnection {
	
	private String modelName;

	private String translatorName;
	
	private String connectionJndiName;
	
	/**
	 * 
	 * @param modelName
	 * @param translatorName
	 * @param connectionJndiName
	 */
	public VdbSourceConnection(String modelName, String translatorName,
			String connectionJndiName) {
		super();
		this.modelName = modelName;
		this.translatorName = translatorName;
		this.connectionJndiName = connectionJndiName;
	}

	/**
	 * 
	 * @return the model name
	 */
	public String getModelName() {
		return modelName;
	}

	/**
	 * 
	 * @param name
	 */
	public void setModelName(String name) {
		this.modelName = name;
	}

	/**
	 * 
	 * @return the translator-name
	 */
	public String getTranslatorName() {
		return translatorName;
	}

	/**
	 * 
	 * @param translatorName
	 */
	public void setTranslatorName(String translatorName) {
		this.translatorName = translatorName;
	}

	/**
	 * 
	 * @return the connection-jndi-name
	 */
	public String getConnectionJndiName() {
		return connectionJndiName;
	}

	/**
	 * 
	 * @param connectionJndiName
	 */
	public void setConnectionJndiName(String connectionJndiName) {
		this.connectionJndiName = connectionJndiName;
	}
}
