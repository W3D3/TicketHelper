package precommit;

import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vcs.CheckinProjectPanel;
import com.intellij.openapi.vcs.FilePath;
import com.intellij.openapi.vcs.changes.Change;
import com.intellij.openapi.vcs.changes.CommitExecutor;
import com.intellij.openapi.vcs.changes.ContentRevision;
import com.intellij.openapi.vcs.checkin.CheckinHandler;
import com.intellij.util.PairConsumer;
import logic.TicketExtractor;
import logic.TicketPrepender;
import org.jetbrains.annotations.NotNull;
import util.GitUtil;
import util.Notification;

import java.io.*;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Stream;

class PreCommitHookCheckinHandler extends CheckinHandler {

    private static final String title = "Prepend Ticket Plugin";
    private static final String doYouWantToCommitMessage = "Do you want to commit? \nCommit without running pre-commit-hook is not recommended.";
    private final Project project;
    private final CheckinProjectPanel checkinPanel;

    PreCommitHookCheckinHandler(final CheckinProjectPanel checkinProjectPanel) {
        this.project = checkinProjectPanel.getProject();
        this.checkinPanel = checkinProjectPanel;
    }

    public ReturnResult beforeCheckin(CommitExecutor executor, PairConsumer<Object, Object> additionalDataConsumer) {
        if (DumbService.getInstance(project).isDumb()) {
            Messages.showErrorDialog(project, "Cannot commit right now because IDE updates the indices " +
                            "of the project in the background. Please try again later.",
                    title);
            return ReturnResult.CANCEL;
        }
        String ticketInMessage = TicketExtractor.extractFromString(checkinPanel.getCommitMessage());
        String currentBranchName = GitUtil.getCurrentBranchName(project);
        String ticketInBranch = TicketExtractor.extractFromString(currentBranchName);
        PreCommitResult preCommitResult;

        if (ticketInMessage.isEmpty() && ticketInBranch.isEmpty())
            return ReturnResult.COMMIT;

        if(ticketInMessage.isEmpty()) {
            preCommitResult = showDialogToUser("No ticket in commit message detected.\nWould you still like to commit?");
        } else {
            // We have a ticket number in the message
            if (!ticketInBranch.equals(ticketInMessage)) {
                preCommitResult = showDialogToUser("The ticket number in your commit message and in your branch don't match.\nWould you still like to commit?");
            }
            return ReturnResult.COMMIT;
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

    private String[] getChanges() {
        return checkinPanel.getSelectedChanges()
                .stream()
                .flatMap(this::getAllRevisionPaths)
                .distinct()
                .toArray(String[]::new);
    }

    private Stream<String> getAllRevisionPaths(Change change) {
        ContentRevision[] revisions = new ContentRevision[2];
        revisions[0] = change.getBeforeRevision();
        revisions[1] = change.getAfterRevision();

        return Arrays.stream(revisions)
                .filter(Objects::nonNull)
                .map(ContentRevision::getFile)
                .map(FilePath::getPath)
                .distinct();
    }

    @NotNull
    private String readInputStream(InputStream stream)
            throws IOException {
        final int bufferSize = 2048;
        final char[] buffer = new char[bufferSize];
        final StringBuilder out = new StringBuilder();
        Reader in = new InputStreamReader(stream, "UTF-8");
        while (true) {
            int size = in.read(buffer, 0, buffer.length);
            if (size < 0) {
                break;
            }
            out.append(buffer, 0, size);
        }
        return out.toString();
    }

    private PreCommitResult showDialogToUser(String text) {
        int dialogResult = Messages.showDialog(project,
                text,
                title,
                new String[]{"Prepend ticket and commit", "Commit anyway", "Cancel"},
                0,
                null);
        switch (dialogResult) {
            case 0:
                return PreCommitResult.COMMIT_WITH_TICKET;
            case 1:
                return PreCommitResult.COMMIT_ANYWAY;
            case 2:
                return PreCommitResult.CANCEL;
        }
        return PreCommitResult.CANCEL;
    }

}
