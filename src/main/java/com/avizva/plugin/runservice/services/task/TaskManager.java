package com.avizva.plugin.runservice.services.task;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.avizva.plugin.runservice.services.RunManager;
import com.avizva.plugin.runservice.services.entity.ProjectMetaData;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.NlsContexts;
import com.intellij.openapi.vcs.ui.VcsBalloonProblemNotifier;
import com.intellij.openapi.wm.ToolWindow;

public class TaskManager extends Task.Backgroundable {

	private ProjectMetaData projectMetaData;
	private ToolWindow toolWindow;

	private String userName;

	public TaskManager(@Nullable Project project, @NlsContexts.ProgressTitle @NotNull String title, boolean canBeCancelled) {
		super(project, title, canBeCancelled, ALWAYS_BACKGROUND);

	}

	@Override
	public void run(@NotNull ProgressIndicator indicator) {

		var runManager = RunManager.getInstance();
		indicator.setIndeterminate(false);
		indicator.setFraction(0.5);
		runManager.clone(toolWindow, projectMetaData, userName);
		indicator.setFraction(1.0);

	}

	@Override
	public void onCancel() {
		showDownloadErrorNotification("Download was canceled.");
	}

	@Override
	public void onThrowable(@NotNull Throwable error) {
		showDownloadErrorNotification("An error occurred: " + error.getMessage());
	}

	public static void showDownloadCompleteNotification() {
		var notification = VcsBalloonProblemNotifier.NOTIFICATION_GROUP.createNotification("Download complete", "All tasks have been completed successfully.", NotificationType.INFORMATION);
		Notifications.Bus.notify(notification);
	}

	public void showDownloadErrorNotification(String errorMessage) {
		var notification = VcsBalloonProblemNotifier.NOTIFICATION_GROUP.createNotification("Download error", errorMessage, NotificationType.ERROR);
		Notifications.Bus.notify(notification);
	}

	public ProjectMetaData getProjectMetaData() {
		return projectMetaData;
	}

	public void setProjectMetaData(ProjectMetaData projectMetaData) {
		this.projectMetaData = projectMetaData;
	}

	public ToolWindow getToolWindow() {
		return toolWindow;
	}

	public void setToolWindow(ToolWindow toolWindow) {
		this.toolWindow = toolWindow;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}
}