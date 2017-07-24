/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.runtime.client.admin;

import java.io.InputStream;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Properties;
import java.util.Set;

import org.teiid.adminapi.Admin;
import org.teiid.adminapi.AdminException;
import org.teiid.adminapi.CacheStatistics;
import org.teiid.adminapi.EngineStatistics;
import org.teiid.adminapi.PropertyDefinition;
import org.teiid.adminapi.Request;
import org.teiid.adminapi.Session;
import org.teiid.adminapi.Transaction;
import org.teiid.adminapi.Translator;
import org.teiid.adminapi.VDB;
import org.teiid.adminapi.VDB.ConnectionType;
import org.teiid.adminapi.WorkerPoolStatistics;

public class AbstractAdminImpl implements Admin {

	public AbstractAdminImpl() {
		super();
	}


	@Override
	public void removeSource(String vdbName, int vdbVersion, String modelName, String sourceName)
			throws AdminException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void addSource(String vdbName, int vdbVersion, String modelName, String sourceName, String translatorName,
			String dsName) throws AdminException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void updateSource(String vdbName, int vdbVersion, String sourceName, String translatorName, String dsName)
			throws AdminException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void changeVDBConnectionType(String vdbName, int vdbVersion, ConnectionType type) throws AdminException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void deploy(String deployName, InputStream content) throws AdminException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void deployVDB(String fileName, InputStream vdb) throws AdminException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void undeploy(String deployedName) throws AdminException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Collection<? extends VDB> getVDBs() throws AdminException {
		throw new UnsupportedOperationException();
	}

	@Override
	public VDB getVDB(String vdbName, String vdbVersion) throws AdminException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void restartVDB(String vdbName, String vdbVersion, String... models) throws AdminException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Collection<? extends Translator> getTranslators() throws AdminException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Translator getTranslator(String deployedName) throws AdminException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Collection<? extends WorkerPoolStatistics> getWorkerPoolStats() throws AdminException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Collection<String> getCacheTypes() throws AdminException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Collection<? extends Session> getSessions() throws AdminException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Collection<? extends Request> getRequests() throws AdminException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Collection<? extends Request> getRequestsForSession(String sessionId) throws AdminException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Collection<? extends PropertyDefinition> getTemplatePropertyDefinitions(String templateName)
			throws AdminException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Collection<? extends PropertyDefinition> getTranslatorPropertyDefinitions(String translatorName)
			throws AdminException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Collection<? extends PropertyDefinition> getTranslatorPropertyDefinitions(String translatorName,
			TranlatorPropertyType type) throws AdminException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Collection<? extends Transaction> getTransactions() throws AdminException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void clearCache(String cacheType) throws AdminException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void clearCache(String cacheType, String vdbName, String vdbVersion) throws AdminException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Collection<? extends CacheStatistics> getCacheStats(String cacheType) throws AdminException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Collection<? extends EngineStatistics> getEngineStats() throws AdminException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void terminateSession(String sessionId) throws AdminException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void cancelRequest(String sessionId, long executionId) throws AdminException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void terminateTransaction(String transactionId) throws AdminException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void close() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void addDataRoleMapping(String vdbName, int vdbVersion, String dataRole, String mappedRoleName)
			throws AdminException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void removeDataRoleMapping(String vdbName, int vdbVersion, String dataRole, String mappedRoleName)
			throws AdminException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setAnyAuthenticatedForDataRole(String vdbName, int vdbVersion, String dataRole,
			boolean anyAuthenticated) throws AdminException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void mergeVDBs(String sourceVDBName, int sourceVDBVersion, String targetVDBName, int targetVDBVersion)
			throws AdminException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void createDataSource(String deploymentName, String templateName, Properties properties)
			throws AdminException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Properties getDataSource(String deployedName) throws AdminException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void deleteDataSource(String deployedName) throws AdminException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Collection<String> getDataSourceNames() throws AdminException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Set<String> getDataSourceTemplateNames() throws AdminException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void markDataSourceAvailable(String jndiName) throws AdminException {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getSchema(String vdbName, String vdbVersion, String modelName, EnumSet<SchemaObjectType> allowedTypes,
			String typeNamePattern) throws AdminException {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getQueryPlan(String sessionId, int executionId) throws AdminException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void restart() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void refresh() {
		throw new UnsupportedOperationException();
	}


	@Override
	public void removeSource(String vdbName, String vdbVersion, String modelName, String sourceName)
			throws AdminException {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void addSource(String vdbName, String vdbVersion, String modelName, String sourceName, String translatorName,
			String dsName) throws AdminException {
		throw new UnsupportedOperationException();
		
	}


	@Override
	public void updateSource(String vdbName, String vdbVersion, String sourceName, String translatorName, String dsName)
			throws AdminException {
		throw new UnsupportedOperationException();
		
	}


	@Override
	public void changeVDBConnectionType(String vdbName, String vdbVersion, ConnectionType type) throws AdminException {
		throw new UnsupportedOperationException();
		
	}


	@Override
	public void deleteVDB(String vdbName, int version) {
		throw new UnsupportedOperationException();
		
	}

	@Override
	public VDB getVDB(String vdbName, int vdbVersion) throws AdminException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void restartVDB(String vdbName, int vdbVersion, String... models) throws AdminException {
		throw new UnsupportedOperationException();
	}


	@Override
	public void clearCache(String cacheType, String vdbName, int vdbVersion) throws AdminException {
		throw new UnsupportedOperationException();
	}


	@Override
	public void addDataRoleMapping(String vdbName, String vdbVersion, String dataRole, String mappedRoleName)
			throws AdminException {
		throw new UnsupportedOperationException();
	}


	@Override
	public void removeDataRoleMapping(String vdbName, String vdbVersion, String dataRole, String mappedRoleName)
			throws AdminException {
		throw new UnsupportedOperationException();
	}


	@Override
	public void setAnyAuthenticatedForDataRole(String vdbName, String vdbVersion, String dataRole,
			boolean anyAuthenticated) throws AdminException {
		throw new UnsupportedOperationException();
	}


	@Override
	public String getSchema(String vdbName, int vdbVersion, String modelName, EnumSet<SchemaObjectType> allowedTypes,
			String typeNamePattern) throws AdminException {
		throw new UnsupportedOperationException();
	}

}
