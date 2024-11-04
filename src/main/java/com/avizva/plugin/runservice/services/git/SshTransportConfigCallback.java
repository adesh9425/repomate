package com.avizva.plugin.runservice.services.git;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.jgit.api.TransportConfigCallback;
import org.eclipse.jgit.transport.SshSessionFactory;
import org.eclipse.jgit.transport.SshTransport;
import org.eclipse.jgit.transport.Transport;
import org.eclipse.jgit.transport.sshd.JGitKeyCache;
import org.eclipse.jgit.transport.sshd.SshdSessionFactoryBuilder;
import org.eclipse.jgit.util.FS;

import com.avizva.plugin.runservice.utils.StringConstants;

public class SshTransportConfigCallback implements TransportConfigCallback {

	private static final Pattern COMPILE = Pattern.compile(".*(rsa|ecdsa|ed25519).*");

	@Override
	public void configure(Transport transport) {
		var sshDir = new File(FS.DETECTED.userHome(), StringConstants.DOT + StringConstants.SSH);
		var sshdSessionFactory = new SshdSessionFactoryBuilder().setPreferredAuthentications(StringConstants.PUBLIC_KEY)
																.setHomeDirectory(FS.DETECTED.userHome())
																.setSshDirectory(sshDir)
																.setDefaultIdentities(home -> loadAllSshKeys(sshDir))
																.build(new JGitKeyCache());
		SshSessionFactory.setInstance(sshdSessionFactory);
		var sshTransport = (SshTransport) transport;
		sshTransport.setSshSessionFactory(sshdSessionFactory);
	}

	private List<Path> loadAllSshKeys(File sshDir) {
		try (Stream<Path> paths = Files.list(sshDir.toPath())) {
			return paths.filter(Files::isRegularFile)  // Only regular files
						.filter(path -> path.getFileName()
											.toString()
											.matches(".*(rsa|ecdsa|ed25519|key).*")) // Key types
						.collect(Collectors.toList());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return List.of(); // Return an empty list if no keys are found
	}

}