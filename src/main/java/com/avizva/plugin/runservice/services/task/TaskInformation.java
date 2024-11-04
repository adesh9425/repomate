package com.avizva.plugin.runservice.services.task;

import org.jetbrains.annotations.NotNull;

import com.intellij.openapi.progress.TaskInfo;
import com.intellij.openapi.util.NlsContexts;

public class TaskInformation implements TaskInfo {

	private final String title;

	private final String cancelText;

	private final String cancelTooltipText;

	private final boolean cancellable;

	public TaskInformation(String title, String cancelText, String cancelTooltipText, boolean cancellable) {
		this.title = title;
		this.cancelText = cancelText;
		this.cancelTooltipText = cancelTooltipText;
		this.cancellable = cancellable;
	}

	@Override
	public @NotNull @NlsContexts.ProgressTitle String getTitle() {
		return title;
	}

	@Override
	public @NlsContexts.Button String getCancelText() {
		return cancelText;
	}

	@Override
	public @NlsContexts.Tooltip String getCancelTooltipText() {
		return cancelTooltipText;
	}

	@Override
	public boolean isCancellable() {
		return cancellable;
	}

}