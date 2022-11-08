package jira;

import com.intellij.ide.lightEdit.LightEditCompatible;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.ui.popup.ListPopup;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import settings.TicketHelperConfig;
import util.Notification;

import javax.swing.*;

public class ChangeJiraTicketsAction extends AnAction implements DumbAware, LightEditCompatible {

    private TicketHelperConfig config;

    public ChangeJiraTicketsAction(@Nls(capitalization = Nls.Capitalization.Title) @Nullable String text) {
        super(text);
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        VirtualFile myFile = e.getData(CommonDataKeys.VIRTUAL_FILE);
        boolean enabled = myFile != null;
        e.getPresentation().setEnabled(enabled);
        e.getPresentation().setVisible(myFile != null);
    }

    @Override
    public final void actionPerformed(@NotNull final AnActionEvent e) {
        DataContext dataContext = e.getDataContext();
        Project project = e.getProject();
        if (project == null) {
            return;
        }
        ListPopup popup = createPopup(project, dataContext);
        if (popup != null) {
            popup.showInBestPositionFor(dataContext);
        }
    }

    @Nullable
    public ListPopup createPopup(@NotNull final Project project, @NotNull final DataContext dataContext) {
        this.config = TicketHelperConfig.getInstance(project);
        DefaultActionGroup group = createActionGroup(this.config);

        return JBPopupFactory.getInstance().createActionGroupPopup(getTemplatePresentation().getText(),
                group, dataContext, JBPopupFactory.ActionSelectionAid.SPEEDSEARCH, false);
    }

    @NotNull
    public DefaultActionGroup createActionGroup(TicketHelperConfig config) {

        return new ChooseJiraTicketAction() {
            @Override
            public void update(@NotNull final AnActionEvent e) {
            }

            @NotNull
            @Override
            protected DefaultActionGroup createPopupActionGroup(JComponent button) {

                if (config.getUsername() == null || config.getUsername().isEmpty()) {
                    Notification.notifyError("Jira Username is not defined", "Please go into settings and enter your Jira Username.");
                    return new DefaultActionGroup();
                }
                if (config.getJqlFilter() == null || config.getJqlFilter().isEmpty()) {
                    Notification.notifyError("Jira Jql filter is not defined", "Please go into settings and enter your jira filter.");
                    return new DefaultActionGroup();
                }

                return createJiraTicketsActionGroup(
                        config.getJiraEndpointURL(),
                        config.getUsername(),
                        config.getPassword(),
                        config.getJqlFilter());
            }
        }.createPopupActionGroup(null);
    }
}
