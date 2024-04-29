package com.git.jumptobrowse.actions;

import java.awt.Desktop;
import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.collections.CollectionUtils;
import com.git.jumptobrowse.config.AppSettingsState;
import com.git.jumptobrowse.i18n.GitJumpToBrowseBundle;
import com.intellij.notification.NotificationGroupManager;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.ui.popup.ListSeparator;
import com.intellij.openapi.ui.popup.PopupStep;
import com.intellij.openapi.ui.popup.util.BaseListPopupStep;
import com.intellij.vcs.log.VcsFullCommitDetails;
import com.intellij.vcs.log.VcsLogCommitSelection;
import com.intellij.vcs.log.VcsLogDataKeys;

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
      tip(e, "Warning", GitJumpToBrowseBundle.message("com.git.browse.no.git.message"), NotificationType.WARNING);
      return;
    }
    String message = cachedFullDetails.get(0).getFullMessage().toUpperCase().trim();
    List<String> nums = getNums(message);
    if (CollectionUtils.isEmpty(nums)) {
      tip(e, "Warning", GitJumpToBrowseBundle.message("com.git.browse.not.exists.commit.message.cannot.open.browse",
          AppSettingsState.getInstance().numPrefix), NotificationType.WARNING);
      return;
    }
    if (nums.size() == 1) {
      openBrowse(e, desktop, nums.get(0));
    } else {
      Desktop finalDesktop = desktop;
      JBPopupFactory.getInstance().createListPopup(
        new BaseListPopupStep<String>(GitJumpToBrowseBundle.message("com.git.browse.num.select"), nums) {
        @Override
        public ListSeparator getSeparatorAbove(String value) {
          return value == null ? new ListSeparator() : null;
        }

        @Override
        public PopupStep<?> onChosen(String selectedValue, boolean finalChoice) {
          openBrowse(e, finalDesktop, selectedValue);
          return FINAL_CHOICE;
        }
      }).showInBestPositionFor(e.getDataContext());
    }
  }

  private void openBrowse(AnActionEvent e, Desktop desktop, String num) {
    try {
      String baseUrl = AppSettingsState.getInstance().baseUrl;
      if (!baseUrl.endsWith("/")) {
        baseUrl = baseUrl + "/";
      }
      desktop.browse(new URI(baseUrl + num));
    } catch (Exception ex) {

      tip(e, "Error", "com.git.open.browse.exception", NotificationType.ERROR);
    }
  }

  private Desktop getDeskTop(AnActionEvent e) {
    if (!Desktop.isDesktopSupported()) {
      tip(e, "Tip", "com.git.open.browse.not.allow", NotificationType.INFORMATION);
      return null;
    }
    Desktop desktop = Desktop.getDesktop();
    // 检查Desktop是否支持打开浏览器
    if (!desktop.isSupported(Desktop.Action.BROWSE)) {
      tip(e, "Tip", "com.git.open.browse.not.allow", NotificationType.INFORMATION);
      return null;
    }
    return desktop;
  }

  private List<String> getNums(String message) {
    String[] numPrefixArr = AppSettingsState.getInstance().numPrefix.trim().toUpperCase().split(";");
    Set<String> result = new LinkedHashSet<>();
    for (String numPrefix : numPrefixArr) {
      Pattern p = Pattern.compile(numPrefix + "\\d+");
      Matcher m = p.matcher(message);
      while (m.find()) {
        result.add(m.group());
      }
    }
    return new ArrayList<>(result);
  }

  private void tip(AnActionEvent e, String title, String content, NotificationType type) {
    NotificationGroupManager.getInstance()
        .getNotificationGroup("listenerGitJump")
        .createNotification(title, content, type)
        .notify(e.getProject());
  }

}
