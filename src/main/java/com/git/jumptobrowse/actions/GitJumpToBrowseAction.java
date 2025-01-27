package com.git.jumptobrowse.actions;

import java.awt.Desktop;
import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.apache.commons.collections.CollectionUtils;
import com.git.jumptobrowse.config.AppSettingsState;
import com.git.jumptobrowse.i18n.GitJumpToBrowseBundle;
import com.git.jumptobrowse.util.BaseListPopupElement;
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
import com.intellij.vcs.log.VcsCommitMetadata;
import com.intellij.vcs.log.VcsLogCommitSelection;
import com.intellij.vcs.log.VcsLogDataKeys;

public class GitJumpToBrowseAction extends AnAction {

  @Override
  public void actionPerformed(AnActionEvent e) {
    // TODO: insert action logic here
    Project project = e.getData(CommonDataKeys.PROJECT);
    Desktop desktop = null;
    if (project == null || (desktop = getDeskTop(e)) == null)
      return;
    VcsLogCommitSelection selection = e.getData(VcsLogDataKeys.VCS_LOG_COMMIT_SELECTION);
    List<VcsCommitMetadata> cachedMetadatas = selection.getCachedMetadata();
    if (CollectionUtils.isEmpty(cachedMetadatas)) {
      tip(e, "Warning", GitJumpToBrowseBundle.message("com.git.browse.no.git.message"),
          NotificationType.WARNING);
      return;
    }
    List<String> cachedFullMessages = cachedMetadatas.stream()
        .map(VcsCommitMetadata::getFullMessage).collect(Collectors.toList());
    List<String> nums = getNums(cachedFullMessages);
    if (CollectionUtils.isEmpty(nums)) {
      tip(e, "Warning", GitJumpToBrowseBundle.message(
          "com.git.browse.not.exists.commit.message.cannot.open.browse",
          AppSettingsState.getInstance().numPrefix), NotificationType.WARNING);
      return;
    }
    if (nums.size() == 1) {
      openBrowse(e, desktop, nums.get(0));
    } else {
      List<BaseListPopupElement> entries = getBaseListPopupElements(cachedFullMessages);
      Desktop finalDesktop = desktop;
      JBPopupFactory.getInstance().createListPopup(new BaseListPopupStep<BaseListPopupElement>(
          GitJumpToBrowseBundle.message("com.git.browse.num.select"), entries) {
        @Override
        public ListSeparator getSeparatorAbove(BaseListPopupElement element) {
          return element == null ? new ListSeparator() : null;
        }

        @Override
        public PopupStep<?> onChosen(BaseListPopupElement selectedElement, boolean finalChoice) {
          openBrowse(e, finalDesktop, selectedElement.getValue());
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

  private List<BaseListPopupElement> getBaseListPopupElements(List<String> messages) {
    if (CollectionUtils.isEmpty(messages)) {
      return new ArrayList<>();
    }
    List<BaseListPopupElement> result = new ArrayList<>();
    for (String message : messages) {
      List<String> nums = getNums(message);
      if (nums.isEmpty()) {
        continue;
      }
      if (nums.size() == 1) {
        if (result.stream().noneMatch(element -> element.getValue().equals(nums.get(0)))) {
          result.add(new BaseListPopupElement().setValue(nums.get(0)).setText(message));
        }
      } else {
        for (String num : nums) {
          if (result.stream().noneMatch(element -> element.getValue().equals(num))) {
            result.add(new BaseListPopupElement().setValue(num).setText(num + ": " + message));
          }
        }
      }
    }
    return result;
  }

  private List<String> getNums(List<String> messages) {
    if (CollectionUtils.isEmpty(messages)) {
      return new ArrayList<>();
    }
    List<String> result = new ArrayList<>();
    for (String message : messages) {
      List<String> nums = getNums(message);
      result.addAll(nums);
    }
    return result.stream().distinct().collect(Collectors.toList());
  }

  private List<String> getNums(String message) {
    String[] numPrefixArr = AppSettingsState.getInstance().numPrefix.trim().toUpperCase()
        .split(";");
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
    NotificationGroupManager.getInstance().getNotificationGroup("listenerGitJump")
        .createNotification(title, content, type).notify(e.getProject());
  }

}
