package jira;

import com.atlassian.jira.rest.client.api.domain.Issue;
import com.intellij.icons.AllIcons;
import com.intellij.ide.lightEdit.LightEditCompatible;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.actionSystem.ex.ComboBoxAction;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.project.Project;
import com.intellij.util.ui.EmptyIcon;
import git4idea.branch.GitBranchUtil;
import git4idea.branch.GitNewBranchOptions;
import git4idea.repo.GitRepository;
import git4idea.repo.GitRepositoryManager;
import org.jetbrains.annotations.NotNull;
import util.GitUtil;
import util.JiraTicketUtil;
import util.Notification;

import javax.swing.*;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public abstract class ChooseJiraTicketAction extends ComboBoxAction {

    JiraTicketClient ticketClient;

    protected ChooseJiraTicketAction() {

    }

    public abstract void update(@NotNull AnActionEvent var1);

    private void fillTicketActions(@NotNull DefaultActionGroup group, @NotNull Collection<Issue> tickets) {

        for (Issue ticket : tickets) {
            AnAction action = new TicketAction(ticket.getKey() + ticket.getSummary(), ticket.getSummary(), EmptyIcon.ICON_16) {
                public void actionPerformed(@NotNull AnActionEvent e) {
                    Project project = getEventProject(e);
                    if(project == null) {
                        Notification.notifyError("Could not get current project", "Cannot operate without an active project.");
                        return;
                    }

                    List<GitRepository> repositories = GitRepositoryManager.getInstance(project).getRepositories();

                    GitNewBranchOptions newBranchOptions = GitBranchUtil.getNewBranchNameFromUser(project, repositories,
                            String.format("Create new branch for ticket %s", ticket.getKey()),
                            JiraTicketUtil.branchNameFromTicket(ticket));

                    if (newBranchOptions == null) return;
                    if (newBranchOptions.getName().isEmpty()) {
                        Notification.notify("Aborted", "Aborted creation.");
                        return;
                    }
                    if (newBranchOptions.shouldCheckout()) {
                        GitUtil.checkoutNewBranch(project,
                                newBranchOptions.getName(),
                                Collections.singletonList(GitBranchUtil.getCurrentRepository(project)));
                    } else {
                        GitUtil.createNewBranch(project,
                                newBranchOptions.getName(),
                                Collections.singletonList(GitBranchUtil.getCurrentRepository(project)));
                    }

                    ticketClient.startProgress(ticket);
                }

                @Override
                public void update(@NotNull AnActionEvent e) {
                    super.update(e);
                    String title = ticket.getKey() + " | " + ticket.getSummary();
                    Icon icon;
                    switch (ticket.getIssueType().getName().toLowerCase()) {
                        case "bug":
                            icon = AllIcons.Actions.StartDebugger;
                            break;
                        case "story":
                            icon = AllIcons.General.TodoImportant;
                            break;
                        default:
                            icon = AllIcons.General.ArrowRight;
                            break;
                    }

                    e.getPresentation().setIcon(icon);
                    e.getPresentation().setDescription(title);
                    e.getPresentation().setText(title);
                }
            };
            group.add(action);
        }

    }

    @NotNull
    protected DefaultActionGroup createJiraTicketsActionGroup(@NotNull String baseUrl,
                                                              @NotNull String jiraUser,
                                                              @NotNull String jiraPassword,
                                                              @NotNull String jqlQuery) {


        DefaultActionGroup group = new DefaultActionGroup();

        ticketClient = new JiraTicketClient(baseUrl, jiraUser, jiraPassword);

        this.fillTicketActions(group, ticketClient.getIssuesForQuery(jqlQuery));

        return group;
    }

    private abstract static class TicketAction extends DumbAwareAction implements LightEditCompatible {
        TicketAction(String name, String description, Icon icon) {
            super(name, description, icon);
        }
    }
}
