package actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.CheckinProjectPanel;
import com.intellij.openapi.vcs.CommitMessageI;
import com.intellij.openapi.vcs.VcsDataKeys;
import com.intellij.openapi.vcs.ui.Refreshable;
import logic.TicketExtractor;
import logic.TicketPrepender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import util.GitUtil;
import util.Notification;

public class TicketNameDetectorInsertAction extends AnAction {

    @Nullable
    private static CommitMessageI getCheckinPanel(@Nullable AnActionEvent e) {
        if (e == null) {
            return null;
        }
        Refreshable data = Refreshable.PANEL_KEY.getData(e.getDataContext());
        if (data instanceof CommitMessageI) {
            return (CommitMessageI) data;
        }
        return VcsDataKeys.COMMIT_MESSAGE_CONTROL.getData(e.getDataContext());
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent actionEvent) {
        Project project = actionEvent.getProject();
        CheckinProjectPanel checkinPanel = (CheckinProjectPanel) getCheckinPanel(actionEvent);
        if (project == null || checkinPanel == null)
            return;

        String currentBranchName = GitUtil.getCurrentBranchName(project);
        String ticketName = TicketExtractor.extractFromString(currentBranchName);

        if (!ticketName.isEmpty()) {
            TicketPrepender.prependTicket(checkinPanel, ticketName, true);
        } else {
            Notification.notifyWarning("Not a valid ticket branch", "Nothing was prepended to your commit message.");
        }
    }

    @Override
    public boolean isDumbAware() {
        return false;
    }
}
