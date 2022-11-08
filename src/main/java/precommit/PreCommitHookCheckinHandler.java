package precommit;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vcs.CheckinProjectPanel;
import com.intellij.openapi.vcs.changes.CommitExecutor;
import com.intellij.openapi.vcs.checkin.CheckinHandler;
import com.intellij.util.PairConsumer;
import logic.TicketExtractor;
import logic.TicketPrepender;
import util.GitUtil;

class PreCommitHookCheckinHandler extends CheckinHandler {
    private static final String TITLE = "Prepend Ticket Plugin";
    private final Project project;
    private final CheckinProjectPanel checkinPanel;

    PreCommitHookCheckinHandler(final CheckinProjectPanel checkinProjectPanel) {
        this.project = checkinProjectPanel.getProject();
        this.checkinPanel = checkinProjectPanel;
    }

    @Override
    public ReturnResult beforeCheckin(CommitExecutor executor, PairConsumer<Object, Object> additionalDataConsumer) {
        String ticketInMessage = TicketExtractor.extractFromString(checkinPanel.getCommitMessage());
        String currentBranchName = GitUtil.getCurrentBranchName(project);
        String ticketInBranch = TicketExtractor.extractFromString(currentBranchName);
        PreCommitResult preCommitResult = PreCommitResult.COMMIT_ANYWAY;

        if (ticketInMessage.isEmpty() && ticketInBranch.isEmpty())
            return ReturnResult.COMMIT;

        if (ticketInMessage.isEmpty()) {
            preCommitResult = showDialogToUser("No ticket in commit message detected.\nWould you still like to commit?");
        } else {
            // We have a ticket number in the message
            if (!ticketInBranch.equals(ticketInMessage)) {
                preCommitResult = showDialogToUser("The ticket number in your commit message and in your branch don't match.\nWould you still like to commit?");
            }
        }

        switch (preCommitResult) {
            case COMMIT_WITH_TICKET:
                TicketPrepender.prependTicket(checkinPanel, ticketInBranch);
                return ReturnResult.COMMIT;
            case COMMIT_ANYWAY:
                return ReturnResult.COMMIT;
            case CANCEL:
                return ReturnResult.CANCEL;
        }
        return ReturnResult.COMMIT;
    }

    private PreCommitResult showDialogToUser(String text) {
        int dialogResult = Messages.showDialog(project,
                text,
                TITLE,
                new String[]{"Prepend Ticket and Commit", "Commit Anyway", "Cancel"},
                0,
                null);
        switch (dialogResult) {
            case 0:
                return PreCommitResult.COMMIT_WITH_TICKET;
            case 1:
                return PreCommitResult.COMMIT_ANYWAY;
            case 2:
                return PreCommitResult.CANCEL;
            default:
                throw new IllegalStateException("Unexpected value: " + dialogResult);
        }
    }

}
