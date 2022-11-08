package logic;

import com.intellij.openapi.vcs.CheckinProjectPanel;

public class TicketPrepender {

    private TicketPrepender() {
        // hidden constructor
    }

    /**
     * Prepends the given ticket string to the commit message of the given checkinPanel
     *
     * @param checkinPanel the currently open checkInPanel
     * @param ticket       new ticket string to be inserted
     * @param override     if true, first ticket number in previous get replaced by new one
     */
    public static void prependTicket(CheckinProjectPanel checkinPanel, String ticket, boolean override) {
        String prevCommitMessage = checkinPanel.getCommitMessage();
        String newCommitMessage;
        if (override) {
            String oldTicket = TicketExtractor.extractFromString(prevCommitMessage);
            if (!oldTicket.isEmpty()) {
                newCommitMessage = prevCommitMessage.replaceFirst(oldTicket, ticket);
                checkinPanel.setCommitMessage(newCommitMessage);
                return;
            }
        }
        newCommitMessage = String.format("%s %s", ticket, prevCommitMessage);
        checkinPanel.setCommitMessage(newCommitMessage);
    }

    public static void prependTicket(CheckinProjectPanel checkinPanel, String ticket) {
        prependTicket(checkinPanel, ticket, false);
    }

}
