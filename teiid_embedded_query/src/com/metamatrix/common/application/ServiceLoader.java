package com.metamatrix.common.application;

import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.teiid.connector.metadata.runtime.Datatype;
import org.teiid.metadata.CompositeMetadataStore;
import com.metamatrix.api.exception.MetaMatrixComponentException;
import com.metamatrix.common.application.exception.ApplicationInitializationException;
import com.metamatrix.common.application.exception.ApplicationLifecycleException;
import com.metamatrix.common.vdb.api.ModelInfo;
import com.metamatrix.common.vdb.api.VDBArchive;
import com.metamatrix.dqp.service.DQPServiceNames;
import com.metamatrix.dqp.service.MetadataService;
import com.metamatrix.dqp.service.VDBService;
import com.metamatrix.embeddedquery.workspace.WorkspaceInfo;
import com.metamatrix.embeddedquery.workspace.WorkspaceInfoHolder;
import com.metamatrix.query.metadata.QueryMetadataInterface;

public class ServiceLoader {

    static String ADMIN = "admin"; //$NON-NLS-1$

    static class WorkspaceMetadataService implements MetadataService {
        private MetadataService service;

        public WorkspaceMetadataService( MetadataService service ) {
            this.service = service;
        }

        /**
         * @param arg0
         * @param arg1
         * @return
         * @throws MetaMatrixComponentException
         * @see com.metamatrix.dqp.service.MetadataService#getMetadataObjectSource(java.lang.String, java.lang.String)
         */
        public CompositeMetadataStore getMetadataObjectSource( String arg0,
                                                               String arg1 ) throws MetaMatrixComponentException {
            return service.getMetadataObjectSource(arg0, arg1);
        }

        /**
         * @param arg0
         * @throws ApplicationInitializationException
         * @see com.metamatrix.common.application.ApplicationService#initialize(java.util.Properties)
         */
        public void initialize( Properties arg0 ) throws ApplicationInitializationException {
            service.initialize(arg0);
        }

        /**
         * @param arg0
         * @param arg1
         * @return
         * @throws MetaMatrixComponentException
         * @see com.metamatrix.dqp.service.MetadataService#lookupMetadata(java.lang.String, java.lang.String)
         */
        public QueryMetadataInterface lookupMetadata( String vdbName,
                                                      String arg1 ) throws MetaMatrixComponentException {
            WorkspaceInfo workspaceInfo = WorkspaceInfoHolder.getInfo();
            if (vdbName.equalsIgnoreCase(ADMIN) && workspaceInfo != null) {
                Object metadata = workspaceInfo.getMetadata();
                return (QueryMetadataInterface)metadata;
            }
            return service.lookupMetadata(vdbName, arg1);
        }

        /**
         * @param arg0
         * @throws ApplicationLifecycleException
         * @see com.metamatrix.common.application.ApplicationService#start(com.metamatrix.common.application.ApplicationEnvironment)
         */
        public void start( ApplicationEnvironment arg0 ) throws ApplicationLifecycleException {
            service.start(arg0);
        }

        /**
         * @throws ApplicationLifecycleException
         * @see com.metamatrix.common.application.ApplicationService#stop()
         */
        public void stop() throws ApplicationLifecycleException {
            service.stop();
        }

        /**
         * {@inheritDoc}
         * 
         * @see com.metamatrix.dqp.service.MetadataService#getBuiltinDatatypes()
         */
        @Override
        public Map<String, Datatype> getBuiltinDatatypes() {
            return null;
        }

    }

    static class WorkspaceVDBService implements VDBService {
        private VDBService vdbService;

        public WorkspaceVDBService( VDBService vdbService ) {
            this.vdbService = vdbService;
        }

        /**
         * @param arg0
         * @param arg1
         * @param arg2
         * @throws ApplicationLifecycleException
         * @throws MetaMatrixComponentException
         * @see com.metamatrix.dqp.service.VDBService#changeVDBStatus(java.lang.String, java.lang.String, int)
         */
        public void changeVDBStatus( String arg0,
                                     String arg1,
                                     int arg2 ) throws ApplicationLifecycleException, MetaMatrixComponentException {
            vdbService.changeVDBStatus(arg0, arg1, arg2);
        }

        /**
         * @return
         * @throws MetaMatrixComponentException
         * @see com.metamatrix.dqp.service.VDBService#getAvailableVDBs()
         */
        public List<VDBArchive> getAvailableVDBs() throws MetaMatrixComponentException {
            return vdbService.getAvailableVDBs();
        }

        /**
         * @param arg0
         * @param arg1
         * @param arg2
         * @return
         * @throws MetaMatrixComponentException
         * @see com.metamatrix.dqp.service.VDBService#getConnectorBindingNames(java.lang.String, java.lang.String,
         *      java.lang.String)
         */
        public List getConnectorBindingNames( String vdbName,
                                              String arg1,
                                              String modelName ) throws MetaMatrixComponentException {
            WorkspaceInfo workspaceInfo = WorkspaceInfoHolder.getInfo();
            if (vdbName.equalsIgnoreCase(ADMIN) && workspaceInfo != null) {
                return workspaceInfo.getBinding(modelName);
            }
            return vdbService.getConnectorBindingNames(vdbName, arg1, modelName);
        }

        /**
         * @param arg0
         * @return
         * @throws MetaMatrixComponentException
         * @see com.metamatrix.dqp.service.VDBService#getConnectorName(java.lang.String)
         */
        public String getConnectorName( String arg0 ) throws MetaMatrixComponentException {
            return vdbService.getConnectorName(arg0);
        }

        /**
         * @param arg0
         * @param arg1
         * @param arg2
         * @return
         * @throws MetaMatrixComponentException
         * @see com.metamatrix.dqp.service.VDBService#getFileVisibility(java.lang.String, java.lang.String, java.lang.String)
         */
        public int getFileVisibility( String arg0,
                                      String arg1,
                                      String arg2 ) throws MetaMatrixComponentException {
            return vdbService.getFileVisibility(arg0, arg1, arg2);
        }

        /**
         * @param arg0
         * @param arg1
         * @param arg2
         * @return
         * @throws MetaMatrixComponentException
         * @see com.metamatrix.dqp.service.VDBService#getModelVisibility(java.lang.String, java.lang.String, java.lang.String)
         */
        public int getModelVisibility( String vdbName,
                                       String arg1,
                                       String arg2 ) throws MetaMatrixComponentException {
            if (vdbName.equalsIgnoreCase(ADMIN)) {
                return ModelInfo.PUBLIC;
            }
            return vdbService.getModelVisibility(vdbName, arg1, arg2);
        }

        /**
         * @param arg0
         * @param arg1
         * @return
         * @throws MetaMatrixComponentException
         * @see com.metamatrix.dqp.service.VDBService#getMultiSourceModels(java.lang.String, java.lang.String)
         */
        public List getMultiSourceModels( String arg0,
                                          String arg1 ) throws MetaMatrixComponentException {
            return vdbService.getMultiSourceModels(arg0, arg1);
        }

        /**
         * @param arg0
         * @param arg1
         * @return
         * @throws MetaMatrixComponentException
         * @see com.metamatrix.dqp.service.VDBService#getVDBStatus(java.lang.String, java.lang.String)
         */
        public int getVDBStatus( String arg0,
                                 String arg1 ) throws MetaMatrixComponentException {
            return vdbService.getVDB(arg0, arg1).getStatus();
        }

        /**
         * @param arg0
         * @throws ApplicationInitializationException
         * @see com.metamatrix.common.application.ApplicationService#initialize(java.util.Properties)
         */
        public void initialize( Properties arg0 ) throws ApplicationInitializationException {
            vdbService.initialize(arg0);
        }

        /**
         * @param arg0
         * @throws ApplicationLifecycleException
         * @see com.metamatrix.common.application.ApplicationService#start(com.metamatrix.common.application.ApplicationEnvironment)
         */
        public void start( ApplicationEnvironment arg0 ) throws ApplicationLifecycleException {
            vdbService.start(arg0);
        }

        /**
         * @throws ApplicationLifecycleException
         * @see com.metamatrix.common.application.ApplicationService#stop()
         */
        public void stop() throws ApplicationLifecycleException {
            vdbService.stop();
        }

        /**
         * {@inheritDoc}
         * 
         * @see com.metamatrix.dqp.service.VDBService#getVDB(java.lang.String, java.lang.String)
         */
        @Override
        public VDBArchive getVDB( String vdbName,
                                  String vdbVersion ) throws MetaMatrixComponentException {
            return vdbService.getVDB(vdbName, vdbVersion);
        }

    }

    public ApplicationService loadService( String serviceType,
                                           ApplicationService service ) {
        if (serviceType.equals(DQPServiceNames.METADATA_SERVICE)) {
            return new WorkspaceMetadataService((MetadataService)service);
        } else if (serviceType.equals(DQPServiceNames.VDB_SERVICE)) {
            return new WorkspaceVDBService((VDBService)service);
        }
        return service;
    }

}
