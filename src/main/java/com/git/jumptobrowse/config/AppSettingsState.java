package com.git.jumptobrowse.config;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@State(
		name = "org.intellij.sdk.settings.AppSettingsState",
		storages = @Storage("GitJumpToBrowseSettingsPlugin.xml")
)
public class AppSettingsState implements PersistentStateComponent<AppSettingsState> {

	public String baseUrl = "http://YourBaseUrl";
	public String numPrefix = "HDCS-;HB-";

	public static AppSettingsState getInstance() {
		return ApplicationManager.getApplication().getService(AppSettingsState.class);
	}

	@Nullable
	@Override
	public AppSettingsState getState() {
		return this;
	}

	@Override
	public void loadState(@NotNull AppSettingsState state) {
		XmlSerializerUtil.copyBean(state, this);
	}

}
