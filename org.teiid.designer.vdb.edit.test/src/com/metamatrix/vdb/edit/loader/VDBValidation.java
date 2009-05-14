/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.vdb.edit.loader;

import java.io.File;

import com.metamatrix.core.util.FileUtils;
import com.metamatrix.core.util.SmartTestSuite;

/**
 */
public abstract class VDBValidation {
    
    public static final int BOOKS_VDB = 1;
    public static final int PARTS_VDB = 2;
    public static final int DTMS_VDB = 3;
    public static final int BQT_VDB = 4;
    
    private static VDBValidation books = new BooksValidation();
    private static VDBValidation parts = new PartsValidation();
    private static VDBValidation dtms = new DTMSValidation();
    private static VDBValidation bqt = new BQTValidation();
    
    private static final String VDB_LOADER_DATA_DIR = "vdbloader"; //$NON-NLS-1$

    
    
    public static final VDBValidation getValidation(int vdb) {
        switch (vdb) {
            case BOOKS_VDB :
                return books;
            case PARTS_VDB :
                return parts;
            case DTMS_VDB :
                return dtms;
            case BQT_VDB :
                return bqt;
        }

        throw new IllegalArgumentException("Invalid VDB Type of " + vdb);     //$NON-NLS-1$   
    }
    
    public abstract int getNumOfModels();
    public abstract int getNumOfPhysicalModels();
    public abstract int getNumOfVirtualModels();
    public abstract String getVDBFileName();
    public abstract String getVDBName();
    public abstract String getVDBFileLocation();
    public String getVDBDefnLocation() {
        String vdbDir = SmartTestSuite.getTestDataPath()+File.separator+VDB_LOADER_DATA_DIR;
        return vdbDir;
    }
    
    
    
}
class BooksValidation extends VDBValidation {
    public static final String NAME= "BooksXML_VDB";//$NON-NLS-1$
    public static final String FILE_NAME= "BooksXML_VDB.vdb";//$NON-NLS-1$

        public static final int NUM_OF_PHYSICAL_MODELS = 2;
        public static final int NUM_OF_VIRTUAL_MODELS = 1;
        
    @Override
    public String getVDBName() {
        return NAME;
    }
    
    @Override
    public String getVDBFileName() {
        return FILE_NAME;
    }         
                
    @Override
    public int getNumOfModels() {
        return NUM_OF_PHYSICAL_MODELS + NUM_OF_VIRTUAL_MODELS;
    }
    @Override
    public int getNumOfPhysicalModels(){
        return NUM_OF_PHYSICAL_MODELS;
    }

    @Override
    public int getNumOfVirtualModels(){
        return NUM_OF_VIRTUAL_MODELS;
    }
    
    @Override
    public String getVDBFileLocation() {
        String vdbDir = FileUtils.buildDirectoryPath(new String[] {SmartTestSuite.getGlobalTestDataPath(), "src/baselinevdbs/books/", getVDBFileName()});//$NON-NLS-1$
        return vdbDir;
        
    }    


        
}
    
class PartsValidation extends VDBValidation {
    public static final String NAME= "PartsSupplier";//$NON-NLS-1$
    public static final String FILE_NAME= "PartsSupplier_VDB.vdb";//$NON-NLS-1$

    public static final int NUM_OF_PHYSICAL_MODELS = 2;
    public static final int NUM_OF_VIRTUAL_MODELS = 1;
    
    @Override
    public String getVDBName() {
        return NAME;
    }
    
    @Override
    public String getVDBFileName() {
        return FILE_NAME;
    }     
                
    @Override
    public int getNumOfModels() {
        return NUM_OF_PHYSICAL_MODELS + NUM_OF_VIRTUAL_MODELS;
    }
    @Override
    public int getNumOfPhysicalModels(){
        return NUM_OF_PHYSICAL_MODELS;
    }

    @Override
    public int getNumOfVirtualModels(){
        return NUM_OF_VIRTUAL_MODELS;
    }
    
    @Override
    public String getVDBFileLocation() {
        String vdbDir = FileUtils.buildDirectoryPath(new String[] {SmartTestSuite.getGlobalTestDataPath(), "src/baselinevdbs/partssupplier/", getVDBFileName()});//$NON-NLS-1$
        return vdbDir;
    }    

        
}    
    
class DTMSValidation extends VDBValidation {
    public static final String NAME= "DesignTimeCatalog";//$NON-NLS-1$
    public static final String FILE_NAME= "DesignTimeCatalog.vdb";//$NON-NLS-1$
    
    public static final int NUM_OF_PHYSICAL_MODELS = 1;
    public static final int NUM_OF_VIRTUAL_MODELS = 17;
        
          
    @Override
    public String getVDBName() {
        return NAME;
    }
    
    @Override
    public String getVDBFileName() {
        return FILE_NAME;
    }    
                
    @Override
    public int getNumOfModels() {
        return NUM_OF_PHYSICAL_MODELS + NUM_OF_VIRTUAL_MODELS;
    }
    @Override
    public int getNumOfPhysicalModels(){
        return NUM_OF_PHYSICAL_MODELS;
    }

    @Override
    public int getNumOfVirtualModels(){
        return NUM_OF_VIRTUAL_MODELS;
    }
    
    @Override
    public String getVDBFileLocation() {
        String vdbDir = FileUtils.buildDirectoryPath(new String[] {SmartTestSuite.getGlobalTestDataPath(), "src/baselinevdbs/dtms/", getVDBFileName()});//$NON-NLS-1$
        return vdbDir;
        
    }    

        
}    
    
class BQTValidation extends VDBValidation {
    public static final String NAME= "BQT";//$NON-NLS-1$
    public static final String FILE_NAME= "bqt.vdb";//$NON-NLS-1$

    public static final int NUM_OF_PHYSICAL_MODELS = 2;
    public static final int NUM_OF_VIRTUAL_MODELS = 0;
    
    @Override
    public String getVDBName() {
        return NAME;
    }
    
    @Override
    public String getVDBFileName() {
        return FILE_NAME;
    }     
                
    @Override
    public int getNumOfModels() {
        return NUM_OF_PHYSICAL_MODELS + NUM_OF_VIRTUAL_MODELS;
    }
    @Override
    public int getNumOfPhysicalModels(){
        return NUM_OF_PHYSICAL_MODELS;
    }

    @Override
    public int getNumOfVirtualModels(){
        return NUM_OF_VIRTUAL_MODELS;
    }
    
    @Override
    public String getVDBFileLocation() {
        String vdbDir = FileUtils.buildDirectoryPath(new String[] {SmartTestSuite.getGlobalTestDataPath(), "src/baselinevdbs/bqt/", getVDBFileName()});//$NON-NLS-1$
        return vdbDir;
        
    }    

        
}    
