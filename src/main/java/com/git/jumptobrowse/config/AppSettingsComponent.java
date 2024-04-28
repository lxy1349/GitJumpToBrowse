package com.git.jumptobrowse.config;

import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;
import org.jetbrains.annotations.NotNull;

import javax.swing.JComponent;
import javax.swing.JPanel;

public class AppSettingsComponent {

	private final JPanel myMainPanel;
	private final JBTextField baseUrl = new JBTextField();
	private final JBTextField numPrefix = new JBTextField();

	public AppSettingsComponent() {
		myMainPanel = FormBuilder.createFormBuilder()
				.addLabeledComponent(new JBLabel("BaseUrl: "), baseUrl, 1, false)
				.addLabeledComponent(new JBLabel("NumPrefix: "), numPrefix, 2, false)
				.addComponentFillVertically(new JPanel(), 0)
				.getPanel();
	}

	public JPanel getPanel() {
		return myMainPanel;
	}

	public JComponent getPreferredFocusedComponent() {
		return baseUrl;
	}

	@NotNull
	public String getBaseUrlText() {
		return baseUrl.getText();
	}

	public void setBaseUrlText(@NotNull String newText) {
		baseUrl.setText(newText);
	}

	@NotNull
	public String getNumPrefix() {
		return numPrefix.getText();
	}

	public void setNumPrefix(@NotNull String newText) {
		numPrefix.setText(newText);
	}

}
