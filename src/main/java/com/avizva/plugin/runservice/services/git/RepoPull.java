package com.avizva.plugin.runservice.services.git;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.MergeResult;
import org.eclipse.jgit.api.PullResult;
import org.eclipse.jgit.api.RebaseCommand;
import org.eclipse.jgit.api.RebaseResult;
import org.eclipse.jgit.api.errors.CheckoutConflictException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.merge.MergeStrategy;

import com.avizva.plugin.runservice.utils.StringConstants;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.vcs.ui.VcsBalloonProblemNotifier;

public class RepoPull {
	private static final CompletableFuture<Void>[] EMPTY_FUTURE_ARRAY = new CompletableFuture[0];

	public void getLatestPull(String basePath, List<String> projects) {

		var completableFutures = projects.stream()
										 .map(project -> CompletableFuture.runAsync(() -> {
											 try {
												 var git = Git.open(new File(basePath + StringConstants.SLASH + project));

												 handlePull(git, project);
											 } catch (IOException e) {
												 throw new RuntimeException(e);
											 }

										 }))
										 .collect(Collectors.toCollection(CopyOnWriteArrayList::new));

		var allTasks = CompletableFuture.allOf(completableFutures.toArray(EMPTY_FUTURE_ARRAY));

		allTasks.whenComplete((result, error) -> {
			ApplicationManager.getApplication()
							  .invokeLater(() -> {
								  if (error != null) {
									  if (error.getCause() instanceof InterruptedException) {
										  completableFutures.forEach(future -> future.cancel(true));
									  } else {

										  error.printStackTrace();
									  }
								  }
							  });
		});
	}

	private static void handlePull(Git git, String project) {
		try {
			var transportConfigCallback = new SshTransportConfigCallback();
			var pullResult = pullWithMerge(git, transportConfigCallback, project);

			if (pullResult.isSuccessful()) {
				showNotification(project, "Pull with merge was successful.");
				return;
			}

			if ((pullResult.getMergeResult() != null) && pullResult.getMergeResult()
																   .getMergeStatus()
																   .equals(MergeResult.MergeStatus.CONFLICTING)) {
				showNotification(project, "Merge conflicts detected. Attempting rebase...");
				pullResult = pullWithRebase(git, transportConfigCallback);

				if (pullResult.isSuccessful()) {
					showNotification(project, "Pull with rebase was successful.");
					return;
				}
				if ((pullResult.getRebaseResult() != null) && pullResult.getRebaseResult()
																		.getStatus()
																		.equals(RebaseResult.Status.CONFLICTS)) {
					showNotification(project, "Rebase conflicts detected. Please resolve conflicts manually.");

					resolveRebaseConflicts(git, project);
				}
			}

			if ((pullResult.getMergeResult() != null) && pullResult.getMergeResult()
																   .getMergeStatus()
																   .isSuccessful()) {
				showNotification(project, "Pull operation completed successfully.");
			} else {
				showNotification(project, "Pull operation failed.");
			}
		} catch (CheckoutConflictException e) {
			showNotification(project, "Checkout conflicts: " + String.join(", ", e.getConflictingPaths()));
		} catch (Exception e) {
			showNotification(project, "An error occurred during pull operation: " + e.getMessage());
		}
	}

	private static PullResult pullWithMerge(Git git, SshTransportConfigCallback transportConfigCallback, String project) throws GitAPIException {
		return git.pull()
				  .setTransportConfigCallback(transportConfigCallback)
				  .setStrategy(MergeStrategy.RECURSIVE)
				  .call();
	}

	private static PullResult pullWithRebase(Git git, SshTransportConfigCallback transportConfigCallback) throws GitAPIException {
		return git.pull()
				  .setTransportConfigCallback(transportConfigCallback)
				  .setRebase(true)
				  .call();
	}

	private static void resolveRebaseConflicts(Git git, String project) {
		try {

			git.rebase()
			   .setOperation(RebaseCommand.Operation.ABORT)
			   .call();
			showNotification(project, "Rebase aborted due to conflicts. Please resolve conflicts manually and retry.");
		} catch (Exception e) {
			showNotification(project, "An error occurred while resolving rebase conflicts: " + e.getMessage());
		}
	}

	public static void showNotification(String project, String message) {
		var notification = VcsBalloonProblemNotifier.NOTIFICATION_GROUP.createNotification("Pull update of " + project, message, NotificationType.INFORMATION);
		Notifications.Bus.notify(notification);
	}

}