package com.avizva.plugin.runservice.services.git;

import java.io.File;

import com.avizva.plugin.runservice.services.RunServiceComponent;
import com.avizva.plugin.runservice.services.entity.ProjectMetaData;
import com.avizva.plugin.runservice.utils.StringConstants;
import com.intellij.openapi.wm.ToolWindow;

import git4idea.commands.Git;
import git4idea.commands.GitCommand;
import git4idea.commands.GitLineHandler;

public class RepoSetup {

	private final ToolWindow toolWindow;

	public RepoSetup(ToolWindow toolWindow) {
		this.toolWindow = toolWindow;
	}

	public void cloneRepo(ProjectMetaData projectMetaData, String userName) {
		var repoType = projectMetaData.getRepoType();
		var project = projectMetaData.getName();
		clone(repoType, project, userName);
	}

	private void clone(RepoType repoType, String project, String userName) {
		try {

			var path = projectURI(repoType, project);
			var basePath = toolWindow.getProject()
									 .getBasePath();
			var projectBasePath = getBaseProjectPath(repoType, project);
			var directory = new File(basePath);

			var cloneHandler = new GitLineHandler(toolWindow.getProject(), directory, GitCommand.CLONE);
			cloneHandler.addParameters(path);

			var cloneResult = Git.getInstance()
								 .runCommand(cloneHandler);
			if (!cloneResult.success()) {
				throw new RuntimeException("Clone failed: " + cloneResult.getErrorOutputAsJoinedString());
			}

			var projectBaseDirectory = new File(projectBasePath);

			var branchName = branchName(repoType, userName);

//			var createBranchHandler = new GitLineHandler(toolWindow.getProject(), projectBaseDirectory, GitCommand.BRANCH);
//			createBranchHandler.addParameters(branchName);
//			createBranchHandler.addParameters("-u", StringConstants.ORIGIN + StringConstants.SLASH + repoType.getBranch());
//			var createBranchResult = Git.getInstance()
//										.runCommand(createBranchHandler);
//			if (!createBranchResult.success()) {
//				throw new RuntimeException("Branch creation failed: " + createBranchResult.getErrorOutputAsJoinedString());
//			}

			var checkoutHandler = new GitLineHandler(toolWindow.getProject(), projectBaseDirectory, GitCommand.CHECKOUT);
			checkoutHandler.addParameters("-b", branchName);
			checkoutHandler.addParameters("--track", StringConstants.ORIGIN + StringConstants.SLASH + repoType.getBranch());

			var checkoutResult = Git.getInstance()
									.runCommand(checkoutHandler);
			if (!checkoutResult.success()) {
				throw new RuntimeException("Checkout failed: " + checkoutResult.getErrorOutputAsJoinedString());
			}

			System.out.println("Repository cloned and branch created successfully.");
		} catch (Exception e) {
			throw new RuntimeException("Error during repository setup: " + e.getMessage(), e);
		}
	}

	public void createBranch(RepoType repoType, String project, String userName) {

	}

	public void switchBranch(RepoType repoType, String project, String userName) {

	}

	private String getBaseProjectPath(RepoType repoType, String project) {
		if (repoType == RepoType.USECASES) {
			return usecaseDirectoryPath(RepoType.USECASES, project);
		}
		return directoryPath(repoType, project);
	}

	private String directoryPath(RepoType repoType, String project) {
		var path = toolWindow.getProject()
							 .getBasePath();
		var projectSpace = RunServiceComponent.getConfig(toolWindow);
		var directoryBuilder = new StringBuilder();
		var directory = directoryBuilder.append(path)
										.append(StringConstants.SLASH)
										.append(project)
										.toString();
		return directory;
	}

	private String usecaseDirectoryPath(RepoType repoType, String project) {
		var path = toolWindow.getProject()
							 .getBasePath();
		var projectSpace = RunServiceComponent.getConfig(toolWindow);
		var directoryBuilder = new StringBuilder();
		var directory = directoryBuilder.append(path)
										.append(StringConstants.SLASH)
										.append(project)
										.append(StringConstants.HYPHEN)
										.append(StringConstants.USECASES)
										.toString();
		return directory;
	}

	private String projectURI(RepoType repoType, String project) {
		var projectUriBuilder = new StringBuilder();
		var projectSpace = RunServiceComponent.getConfig(toolWindow);
		projectUriBuilder.append(projectSpace.getProjectSpace())
						 .append(StringConstants.SLASH)
						 .append(project);
		if (repoType == RepoType.USECASES) {
			projectUriBuilder.append(StringConstants.HYPHEN)
							 .append(StringConstants.USECASES);
		}

		var finalURI = projectUriBuilder.append(StringConstants.DOT)
										.append(StringConstants.GIT)
										.toString();

		return finalURI;
	}

	private static String branchName(RepoType repoType, String name) {
		var branchBuilder = new StringBuilder();
		var branch = branchBuilder.append(StringConstants.SANDBOX)
								  .append(StringConstants.HYPHEN)
								  .append(name)
								  .append(StringConstants.HYPHEN)
								  .append(repoType.getBranch())
								  .toString();
		return branch;
	}
}