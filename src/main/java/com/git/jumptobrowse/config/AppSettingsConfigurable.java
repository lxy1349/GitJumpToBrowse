package com.git.jumptobrowse.config;

import com.intellij.openapi.options.Configurable;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.JComponent;

public class AppSettingsConfigurable implements Configurable {

	private AppSettingsComponent mySettingsComponent;

	// A default constructor with no arguments is required because this implementation
	// is registered as an applicationConfigurable EP

	@Nls(capitalization = Nls.Capitalization.Title)
	@Override
	public String getDisplayName() {
		return "SDK: Application Settings Example";
	}

	@Override
	public JComponent getPreferredFocusedComponent() {
		return mySettingsComponent.getPreferredFocusedComponent();
	}

	@Nullable
	@Override
	public JComponent createComponent() {
		mySettingsComponent = new AppSettingsComponent();
		return mySettingsComponent.getPanel();
	}

	@Override
	public boolean isModified() {
		AppSettingsState settings = AppSettingsState.getInstance();
		boolean modified = !mySettingsComponent.getBaseUrlText().equals(settings.baseUrl);
		modified |= mySettingsComponent.getNumPrefix().equals(settings.numPrefix);
		return modified;
	}

	@Override
	public void apply() {
		AppSettingsState settings = AppSettingsState.getInstance();
		settings.baseUrl = mySettingsComponent.getBaseUrlText();
		settings.numPrefix = mySettingsComponent.getNumPrefix();
	}

	@Override
	public void reset() {
		AppSettingsState settings = AppSettingsState.getInstance();
		mySettingsComponent.setBaseUrlText(settings.baseUrl);
		mySettingsComponent.setNumPrefix(settings.numPrefix);
	}

	@Override
	public void disposeUIResources() {
		mySettingsComponent = null;
	}

}
