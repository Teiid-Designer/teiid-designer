/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.transformation.ui.wizards.file;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.core.designer.util.I18nUtil;
import org.teiid.core.designer.util.StringUtilities;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.query.proc.ITeiidMetadataFileInfo;
import org.teiid.designer.runtime.spi.ITeiidServer;
import org.teiid.designer.transformation.ui.UiConstants;
import org.teiid.designer.transformation.ui.wizards.xmlfile.TeiidXmlFileInfo;
import org.teiid.designer.ui.viewsupport.ModelNameUtil;


/**
 * Business object used to manage the Teiid Metadata File Import data
 * 
 *
 * @since 8.0
 */
public class TeiidMetadataImportInfo implements UiConstants {
	public static final int FILE_MODE_FLAT_FILE_LOCAL = 0;
	public static final int FILE_MODE_FLAT_FILE_URL = 1;
	public static final int FILE_MODE_TEIID_XML_FILE = 2;
	public static final int FILE_MODE_TEIID_XML_URL = 3;
	public static final int REST_MODE_URL = 4;
	
	private static final String I18N_PREFIX = I18nUtil.getPropertyPrefix(TeiidMetadataImportInfo.class);
	
    private static String getString( final String id ) {
        return Util.getString(I18N_PREFIX + id);
    }
    
	/**
	 * The  <code>Map</code> of <code>TeiidMetadataFileInfo</code> objects to data <code>File</code>
	 */
	private Map<File, TeiidMetadataFileInfo> fileInfoMap;
	
	/**
	 * The  <code>Map</code> of <code>TeiidXmlFileInfo</code> objects to data <code>File</code>
	 */
	private Map<File, TeiidXmlFileInfo> xmlFileInfoMap;
	
	private TeiidXmlFileInfo sourceXmlFileInfo;
	
	private IProject targetProject;
	
    /**
     * The unique source model name (never <code>null</code> or empty).
     * 
     * This model will be generated and contain the standard File Connector procedures
     *   getFiles()
     *   getTextFiles()
     *   saveFile()
     * 
     */
	private String sourceModelName;
	
	/**
	 * The workspace <code>IPath</code> location where the source model will be created in. May be Teiid Model Project
	 * or a folder within a project
	 */
	private IPath sourceModelLocation;
	
	/**
	 * boolean indicator that the target source model is an existing model
	 */
	private boolean sourceModelExists;
	
    /**
     * The unique view model name (never <code>null</code> or empty).
     * 
     * This model may be an existing model or a new view model may be created based on import options.
     * 
     * View tables will be created in this view model representing SQL that accesses the data files specified in
     * the <code>TeiidMetadataFileInfo</code> objects in the <code>fileInfoMap</code>
     * 
     */
	private String viewModelName;
	
	/**
	 * The workspace <code>IPath</code> location where the source model will be created in. May be Teiid Model Project
	 * or a folder within a project
	 */
	private IPath viewModelLocation;
	
	/**
	 * boolean indicator that any new view tables will be created in an existing model
	 */
	private boolean viewModelExists;
	
	/**
	 * The cached connection profile used to locate the data folder
	 */
	private IConnectionProfile connectionProfile;
	
    /**
     * The unique jbossJndiName
     * 
     */
	private String jbossJndiName;
	
	private boolean autoCreateDataSource = true;
	
	/**
	 * Current <code>IStatus</code> representing the state of the input values for this instance of
	 * <code>TeiidMetadataFileInfo</code>
	 */
	private IStatus status;
	
	private IStatus NO_FILES_STATUS;
	
	private int fileMode;
	private String fileFilterText;
	
	/**
	 * Basic constructor
	 */
	public TeiidMetadataImportInfo() {
		super();
		initialize();
	}
	
	private void initialize() {
		fileInfoMap = new HashMap<File, TeiidMetadataFileInfo>();
		xmlFileInfoMap = new HashMap<File, TeiidXmlFileInfo>();
		NO_FILES_STATUS = new Status(IStatus.ERROR, PLUGIN_ID, getString("noDataFilesInDataFolder")); //$NON-NLS-1$
		status = NO_FILES_STATUS;
	}
	
	/**
	 * 
	 * @return sourceModelName the source relational model name
	 */
	public String getSourceModelName() {
        return this.sourceModelName;
	}
	
	/**
	 * 
	 * @param sourceModelName (never <code>null</code> or empty).
	 */
	public void setSourceModelName(String sourceModelName) {
		this.sourceModelName = sourceModelName;
	}
	
	/**
	 * 
	 * @return sourceModelLocation the target location where the source model is going to be created
	 */
	public IPath getSourceModelLocation() {
		return this.sourceModelLocation;
	}
	
	/**
	 * 
	 * @return location the target location where the view model either exists or is going to be created
	 */
	public void setSourceModelLocation(IPath location) {
		CoreArgCheck.isNotNull(location, "location is null"); //$NON-NLS-1$
		this.sourceModelLocation = location;
		validate();
	}
	
	public void setSourceModelExists(boolean sourceModelExists) {
		this.sourceModelExists = sourceModelExists;
	}
	
	public boolean sourceModelExists() {
		return this.sourceModelExists;
	}
	
	/**
	 * 
	 * @return viewModelName the view relational model name
	 */
	public String getViewModelName() {
		if( this.viewModelName != null && !this.viewModelName.toUpperCase().endsWith(".XMI") ) { //$NON-NLS-1$
			return this.viewModelName + ".xmi"; //$NON-NLS-1$
		}
        return this.viewModelName;
	}
	
	/**
	 * 
	 * @param viewModelName (never <code>null</code> or empty).
	 */
	public void setViewModelName(String viewModelName) {
//		CoreArgCheck.isNotEmpty(viewModelName, "viewModelName is null"); //$NON-NLS-1$
		this.viewModelName = viewModelName;
	}
	
	/**
	 * 
	 * @return viewModelLocation the target location where the view model either exists or is going to be created
	 */
	public IPath getViewModelLocation() {
		return this.viewModelLocation;
	}
	
	/**
	 * 
	 * @return sourceModelName the source relational model name
	 */
	public String getJBossJndiName() {
        return this.jbossJndiName;
	}
	
	/**
	 * 
	 * @param sourceModelName (never <code>null</code> or empty).
	 */
	public void setJBossJndiNameName(String jndiName) {
		this.jbossJndiName = jndiName;
	}
	
	/**
	 * 
	 * @return sourceModelName the source relational model name
	 */
	public boolean doCreateDataSource() {
        return this.autoCreateDataSource;
	}
	
	/**
	 * 
	 * @param sourceModelName (never <code>null</code> or empty).
	 */
	public void setCreateDataSource(boolean value) {
		this.autoCreateDataSource = value;
	}
	
	
	/**
	 * 
	 * @return location the target location where the view model either exists or is going to be created
	 */
	public void setViewModelLocation(IPath location) {
		CoreArgCheck.isNotNull(location, "location is null"); //$NON-NLS-1$
		this.viewModelLocation = location;
		validate();
	}
	
	public void setViewModelExists(boolean viewModelExists) {
		this.viewModelExists = viewModelExists;
	}
	
	public boolean viewModelExists() {
		return this.viewModelExists;
	}
	
	
	public void addFileInfo(TeiidMetadataFileInfo fileInfo) {
		this.fileInfoMap.put(fileInfo.getDataFile(), fileInfo);
		validate();
	}
	
	public TeiidMetadataFileInfo getFileInfo(File file) {
		return this.fileInfoMap.get(file);
	}
	
	public void addXmlFileInfo(TeiidXmlFileInfo fileInfo) {
		this.xmlFileInfoMap.put(fileInfo.getDataFile(), fileInfo);
		validate();
	}
	
	public TeiidXmlFileInfo getXmlFileInfo(File file) {
		return this.xmlFileInfoMap.get(file);
	}
	
	public void setSourceXmlFileInfo(TeiidXmlFileInfo fileInfo) {
		this.sourceXmlFileInfo = fileInfo;
		validate();
	}
	
	public TeiidXmlFileInfo getSourceXmlFileInfo() {
		return this.sourceXmlFileInfo;
	}
	
	public void setSourceXmlFileInfo() {
		for (TeiidXmlFileInfo fileInfo : getXmlFileInfos()) {
			if (fileInfo.doProcess()) {
				setSourceXmlFileInfo(fileInfo);
				break;
			}
		}
	}
	
	/**
	 * Convenience method to allow setting doProcess on a cached <code>TeiidMetadataFileInfo</code> object
	 * @param file
	 * @param doProcess
	 */
	public void setDoProcess(File file, boolean doProcess) {
		// if doProcess == TRUE, then set ALL the files to false first
		for( TeiidMetadataFileInfo info : getFileInfos()) {
			info.setDoProcess(false);
		}
		
		TeiidMetadataFileInfo info = getFileInfo(file);
		if( info != null ) {
			info.setDoProcess(doProcess);
		}
		validate();
	}
	
	/**
	 * Convenience method to allow setting doProcess on a cached <code>TeiidMetadataFileInfo</code> object
	 * @param file
	 * @param doProcess
	 */
	public void setDoProcessXml(File file, boolean doProcess) {
		for( TeiidXmlFileInfo info : getXmlFileInfos()) {
			info.setDoProcess(false);
		}
		
		TeiidXmlFileInfo info = getXmlFileInfo(file);
		if( info != null ) {
			info.setDoProcess(doProcess);
			if( doProcess ) {
				if( getSourceModelName() == null ) {
					String initialName = "SourceModel"; //$NON-NLS-1$
					if( getSourceModelLocation() != null ) {
						initialName = ModelNameUtil.getNewUniqueModelName(initialName, getTargetProject());
					}
					setSourceModelName(initialName);
//					String fileName = info.getDataFile().getName();
//					if(fileName.toLowerCase().endsWith(".xml")) { //$NON-NLS-1$
//						fileName = fileName.substring(0, fileName.length()-4);
//					}
//					setSourceModelName(fileName + "_Source"); //$NON-NLS-1$
				}
				if( getViewModelName() == null ) {
					String initialName = "ViewModel"; //$NON-NLS-1$
					if( getViewModelLocation() != null ) {
						initialName = ModelNameUtil.getNewUniqueModelName(initialName, getTargetProject());
					}
					setViewModelName(initialName);
//					String fileName = info.getDataFile().getName();
//					if(fileName.toLowerCase().endsWith(".xml")) { //$NON-NLS-1$
//						fileName = fileName.substring(0, fileName.length()-4);
//					}
//					setViewModelName(fileName + "_View"); //$NON-NLS-1$
				}
			}
		}
		
		if( doProcess ) {
			
		}
		
		validate();
	}
	
    public IProject getTargetProject() {
    	return targetProject;
    }
    
    public boolean setTargetProject(IProject project) {
    	// If project is the same project, do nothing:
    	if( this.targetProject != null && this.targetProject == project ) return false;
    	
    	this.targetProject = project;
    	
    	// If new project, need to set the "location" of the view and source models to the project name
    	if( this.targetProject != null ) {
	    	setViewModelLocation(new Path(this.targetProject.getName()));
	    	setSourceModelLocation(new Path(this.targetProject.getName()));
    	}
    	
    	return true;
    }
	
	public TeiidMetadataFileInfo getCheckedFileInfo() {
		for( TeiidMetadataFileInfo info : getFileInfos()) {
			if( info.doProcess() ) {
				return info;
			}
		}
		
		return null;
	}
	
	/**
	 * 
	 * @return connectionProfile the <code>IConnectionProfile</code> profile used to define the file location (i.e. folder) of the 
	 * Teiid data files
	 */
	public IConnectionProfile getConnectionProfile() {
		return this.connectionProfile;
	}

	/**
	 * 
	 * @param connectionProfile the <code>IConnectionProfile</code>
	 */
	public void setConnectionProfile(IConnectionProfile connectionProfile) {
		this.connectionProfile = connectionProfile;
	}
	
	/**
	 * 
	 * @return status the <code>IStatus</code> representing the validity of the data in this info object
	 */
	public IStatus getStatus() {
		return this.status;
	}
	
	/**
	 * 
	 * @param status the <code>IStatus</code> representing the validity of the data in this info object
	 */
	public void setStatus(IStatus status) {
		this.status = status;
	}
	
	public Collection<TeiidMetadataFileInfo> getFileInfos() {
		return fileInfoMap.values();
	}

	public Collection<TeiidXmlFileInfo> getXmlFileInfos() {
		return xmlFileInfoMap.values();
	}
	
	public void clearFileInfos() {
		this.fileInfoMap.clear();
	}
	
	public void clearXmlFileInfos() {
		this.xmlFileInfoMap.clear();
	}
	
	/**
	 * Analyzes this object's data values and sets the current <code>IStatus</code>
	 */
	public void validate() {
		setStatus(Status.OK_STATUS);
		
		if( isFlatFileLocalMode() || isFlatFileUrlMode() ) {
			if( this.fileInfoMap.isEmpty() ) {
				setStatus(new Status(IStatus.ERROR, PLUGIN_ID, getString("noDataFilesFound"))); //$NON-NLS-1$
				return;
			}
			boolean noneProcessed = true;
			for( ITeiidMetadataFileInfo info : fileInfoMap.values()) {
				if( info.doProcess() ) {
					noneProcessed = false;
				}
			}
			if( noneProcessed ) {
				setStatus(new Status(IStatus.ERROR, PLUGIN_ID, getString("noDataFilesSelected"))); //$NON-NLS-1$
				return;
			}
			
			for( TeiidMetadataFileInfo info : fileInfoMap.values()) {
				if( info.doProcess() && info.getStatus().getSeverity() > IStatus.WARNING ) {
					setStatus(new Status(IStatus.ERROR, PLUGIN_ID, 
							Util.getString(I18N_PREFIX + "errorInImportConfiguration", info.getDataFile().getName()) )); //$NON-NLS-1$
					return;
				}
			}
			
			setStatus(Status.OK_STATUS);
		} else {
			if( this.xmlFileInfoMap.isEmpty() ) {
				setStatus(new Status(IStatus.ERROR, PLUGIN_ID, getString("noXmlDataFilesFound"))); //$NON-NLS-1$
				return;
			}
			boolean noneProcessed = true;
			for( TeiidXmlFileInfo info : xmlFileInfoMap.values()) {
				if( info.doProcess() ) {
					noneProcessed = false;
				}
			}
			if( noneProcessed ) {
				setStatus(new Status(IStatus.ERROR, PLUGIN_ID, getString("noXmlDataFilesSelected"))); //$NON-NLS-1$
				return;
			}
			
			for( TeiidXmlFileInfo info : xmlFileInfoMap.values()) {
				if( info.doProcess() && info.getStatus().getSeverity() > IStatus.WARNING ) {
					setStatus(new Status(IStatus.ERROR, PLUGIN_ID, 
							Util.getString(I18N_PREFIX + "errorInXmlImportConfiguration", info.getDataFile().getName()) )); //$NON-NLS-1$
					return;
				}
			}
			
			setStatus(Status.OK_STATUS);
		}
		
		//
		// Check that view and source model project folders are the SAME
		// if NOT, then set error status
		// Note that some OS's allow same name/different case folder names
		//
		if( getStatus().isOK() && viewModelLocation != null && sourceModelLocation != null ) {
			if( viewModelLocation.segmentCount() > 0 && sourceModelLocation.segmentCount() > 0 ) {
				// Check that locations for source and view model are not different projects
				// Sample case using p1 and P1 as different projects (on Linux, etc.)
				if( ! ( viewModelLocation.segment(0).equals(sourceModelLocation.segment(0)) ) ) {
					setStatus(new Status(IStatus.ERROR, PLUGIN_ID, 
							Util.getString(I18N_PREFIX + "errorFileLocationsInDifferentProjects") )); //$NON-NLS-1$
				}
			}
		}
	}
	
	public boolean isFlatFileLocalMode() {
		return fileMode == FILE_MODE_FLAT_FILE_LOCAL;
	}
	
	public boolean isFlatFileUrlMode() {
		return fileMode == FILE_MODE_FLAT_FILE_URL;
	}
	
	public boolean isXmlLocalFileMode() {
		return fileMode == FILE_MODE_TEIID_XML_FILE;
	}
	
	public boolean isXmlUrlFileMode() {
		return fileMode == FILE_MODE_TEIID_XML_URL;
	}
	
	public boolean isRestUrlFileMode() {
		return fileMode == REST_MODE_URL;
	}
	
	public void setFileMode(int mode) {
		this.fileMode = mode;
	}
	
	public int getFileMode() {
		return this.fileMode;
	}
	
	/**
	 * Set the filter text
	 * @param filterText the filter
	 */
	public void setFileFilterText(String filterText) {
		this.fileFilterText=filterText;
	}
	
	/**
	 * Get the filter text
	 * @return the filter text
	 */
	public String getFileFilterText() {
		return this.fileFilterText;
	}
	
//	public boolean isServerDefined() {
//        // Check to see if server is available and connected
//        ITeiidServer server = ModelerCore.getTeiidServerManager().getDefaultServer();
//        
//        return server != null;
//	}
//	
//	public boolean isServerConnected() {
//		ITeiidServer server = ModelerCore.getTeiidServerManager().getDefaultServer();
//
//        return server != null && server.isConnected();
//	}
	
//	private IProject getTargetProject() {
//		if( getSourceModelLocation() != null ) {
//			IPath sourceModelPath = getSourceModelLocation();
//			IContainer container = ResourcesPlugin.getWorkspace().getRoot().getContainerForLocation(sourceModelPath);
//			return container.getProject();
//		}
//		
//		return null;
//	}
	
    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder text = new StringBuilder();
        text.append("Teiid Metadata Import Info: "); //$NON-NLS-1$
        text.append(" connection profile = ").append(getConnectionProfile().getName()); //$NON-NLS-1$
        text.append(", source model = ").append(getSourceModelName()); //$NON-NLS-1$
        text.append(", source model location = ").append(getViewModelLocation().toString()); //$NON-NLS-1$
        text.append(", view model = ").append(getViewModelName()); //$NON-NLS-1$
        text.append(", view model location= ").append(getSourceModelLocation().toString()); //$NON-NLS-1$
        
        return text.toString();
    }
}
