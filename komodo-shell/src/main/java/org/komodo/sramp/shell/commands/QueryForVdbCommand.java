/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.komodo.sramp.shell.commands;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

import org.komodo.repository.AtomRepositoryManager;
import org.komodo.repository.RepositoryManager;
import org.komodo.repository.artifact.Artifact;
import org.komodo.sramp.shell.util.ShellConstants;
import org.overlord.sramp.common.ArtifactType;
import org.overlord.sramp.shell.AbstractShellCommand;

/**
 * Adds a VDB to the repository.  
 */
public class QueryForVdbCommand extends AbstractShellCommand implements ShellConstants {

    /**
     * Constructor.
     */
    public QueryForVdbCommand() {
    }

    /**
     * {@inheritDoc}
     *
     * @see org.overlord.sramp.shell.ShellCommand#execute()
     */
    @Override
    public void execute() throws Exception {
        String vdbName = requiredArgument(0, "VDB Name is required.");
        String version = requiredArgument(1, "VDB Version is required.");
        
        final RepositoryManager.QuerySettings settings = new RepositoryManager.QuerySettings();
        settings.artifactType = Artifact.Type.VDB;
        settings.params = new HashMap<String, String>();
        settings.params.put("version", version);
        settings.params.put("name", vdbName);
        
        Object obj = this.getContext().getVariable(KOMODO_CLIENT_QNAME);
        
        if (obj==null){
    		print("Please connect to a Komodo S-RAMP server using connectKomodo {URL} before executing this command.");
			return;
        }
        
        AtomRepositoryManager repo = (AtomRepositoryManager) obj;
        List<ArtifactType> artifactList = repo.query(settings);
        
        if (artifactList.isEmpty()){
        	print("VDB " + vdbName + " with version " + version + " not found");
        }else{
        	print("Found VDB " + vdbName + " with version " + version);
        }
        
      }

    /**
     * {@inheritDoc}
     *
     * @see org.overlord.sramp.shell.ShellCommand#printHelp()
     */
    @Override
    public void printHelp() {
        print("The 'getVdb' queries for a VDB and version in a Komodo repository.");
        print("");
        print("Example usage:");
        print(">  komodo:getVdb {VDB Name} {VDB Version}");
    }

    /**
     * {@inheritDoc}
     *
     * @see org.overlord.sramp.shell.ShellCommand#printUsage()
     */
    @Override
    public void printUsage() {
        print("komodo:getVdb {VDB Name} {VDB Version}");
    }
    
    /**
     * Obtains the content of a file resource.
     * 
     * @param fileName the file name relative to the calling class (cannot be <code>null</code> or empty)
     * @return the input stream to the content; may be <code>null</code> if the resource does not exist
     * @throws FileNotFoundException 
     */
    protected InputStream getResourceAsStream(final String fileName) throws FileNotFoundException {
        return new FileInputStream(fileName);
    }
}
