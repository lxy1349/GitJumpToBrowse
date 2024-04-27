package com.git.jumptobrowse.actions;

import com.git.jumptobrowse.config.AppSettingsState;
import com.intellij.notification.NotificationGroupManager;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.vcs.log.VcsFullCommitDetails;
import com.intellij.vcs.log.VcsLogCommitSelection;
import com.intellij.vcs.log.VcsLogDataKeys;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import java.awt.Desktop;
import java.net.URI;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GitJumpToBrowseAction extends AnAction {

	@Override
	public void actionPerformed(AnActionEvent e) {
		// TODO: insert action logic here
		Project project = e.getData(CommonDataKeys.PROJECT);
		Desktop desktop = null;
		if (project == null || (desktop = getDeskTop(e)) == null) return;
		VcsLogCommitSelection selection = e.getRequiredData(VcsLogDataKeys.VCS_LOG_COMMIT_SELECTION);
		List<VcsFullCommitDetails> cachedFullDetails = selection.getCachedFullDetails();
		if (CollectionUtils.isEmpty(cachedFullDetails)) {
			tip(e, "Warning", "没有选择git的提交信息", NotificationType.WARNING);
			return;
		}
		String message = cachedFullDetails.get(0).getFullMessage().toUpperCase().trim();
		String prefixNum = getPrefixNum(e, message);
		if (StringUtils.isBlank(prefixNum)) {
			tip(e, "Warning", "当前提交记录不是以[" + AppSettingsState.getInstance().prefixUrl + "]开头的提交记录，" +
					"无法打开浏览器", NotificationType.WARNING);
			return;
		}
		try {
			String baseUrl = AppSettingsState.getInstance().baseUrl;
			if (!baseUrl.endsWith("/")) {
				baseUrl = baseUrl + "/";
			}
			desktop.browse(new URI(baseUrl + prefixNum));
		} catch (Exception ex) {
			tip(e, "Error", "打开浏览器异常", NotificationType.ERROR);
		}
	}

	private Desktop getDeskTop(AnActionEvent e) {
		if (!Desktop.isDesktopSupported()) {
			tip(e, "Tip", "不支持在默认浏览器中打开 URL", NotificationType.INFORMATION);
			return null;
		}
		Desktop desktop = Desktop.getDesktop();
		// 检查Desktop是否支持打开浏览器
		if (!desktop.isSupported(Desktop.Action.BROWSE)) {
			tip(e, "Tip", "不支持在默认浏览器中打开 URL", NotificationType.INFORMATION);
			return null;
		}
		return desktop;
	}

	private String getPrefixNum(AnActionEvent e, String message) {
		String[] prefixUrlArr = StringUtils.split(AppSettingsState.getInstance().prefixUrl.trim().toUpperCase(), ";");
		String prefix = null;
		for (String prefixUrl : prefixUrlArr) {
			if (message.startsWith(prefixUrl)) {
				prefix = prefixUrl;
				break;
			}
		}
		if (StringUtils.isBlank(prefix)) {
			tip(e, "Warning", "prefixUrl[" + AppSettingsState.getInstance().prefixUrl + "]配置错误",
					NotificationType.WARNING);
			return null;
		}
		Pattern p = Pattern.compile(prefix + "\\d+");
		Matcher m = p.matcher(message);
		if (m.find()) {
			return m.group();
		}
		return null;
	}

	private void tip(AnActionEvent e, String title, String content, NotificationType type) {
		NotificationGroupManager.getInstance()
				.getNotificationGroup("listenerGitJump")
				.createNotification(title, content, type)
				.notify(e.getProject());
	}

}
