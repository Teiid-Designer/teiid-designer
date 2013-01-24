/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.komodo.sramp.shell.commands;

import java.util.List;

import org.komodo.repository.AtomRepositoryManager;
import org.komodo.sramp.shell.util.ShellConstants;
import org.overlord.sramp.shell.AbstractShellCommand;

/**
 * Connects to an s-ramp server using a Komodo client.
 *
 */
public class ConnectKomodoCommand extends AbstractShellCommand implements ShellConstants{

	/**
	 * Constructor.
	 */
	public ConnectKomodoCommand() {
	}

	/**
	 * @see org.overlord.sramp.common.shell.ShellCommand#printUsage()
	 */
	@Override
	public void printUsage() {
		print("komodo:connectKomodo <endpointUrl>");
	}

	/**
	 * @see org.overlord.sramp.common.shell.ShellCommand#printHelp()
	 */
	@Override
	public void printHelp() {
		print("The 'connectKomodo' command creates a connection to a remote");
		print("S-RAMP repository at its Atom endpoint using a Komodo client.");
		print("Example usage:");
		print(">  komodo:connectKomodo http://localhost:8080/s-ramp-server");
	}
		

	/**
	 * @see org.overlord.sramp.common.shell.ShellCommand#execute()
	 */
	@Override
	public void execute() throws Exception {
		String endpointUrlArg = this.requiredArgument(0, "Please specify a valid s-ramp URL.");
		if (!endpointUrlArg.startsWith("http")) {
			endpointUrlArg = "http://" + endpointUrlArg;
		}
		try {
			AtomRepositoryManager client = new AtomRepositoryManager(endpointUrlArg, true);
			getContext().setVariable(KOMODO_CLIENT_QNAME, client);
			print("Successfully connected to S-RAMP Komodo endpoint: " + endpointUrlArg);
		} catch (Exception e) {
			print("FAILED to connect to S-RAMP Komodo endpoint: " + endpointUrlArg);
			print("\t" + e.getMessage());
		}
	}

	/**
	 * @see org.overlord.sramp.common.shell.AbstractShellCommand#tabCompletion(java.lang.String, java.util.List)
	 */
	@Override
	public int tabCompletion(String lastArgument, List<CharSequence> candidates) {
		if (getArguments().isEmpty()) {
			candidates.add("http://localhost:8080/s-ramp-server");
			return 0;
		} else {
			return -1;
		}
	}

}
