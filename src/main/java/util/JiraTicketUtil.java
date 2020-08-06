package util;

import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.IssueType;
import com.atlassian.jira.rest.client.api.domain.Transition;
import org.jetbrains.annotations.NotNull;

public class JiraTicketUtil {

    private JiraTicketUtil() {
    }

    public static Transition getTransitionByName(@NotNull Iterable<Transition> transitions, @NotNull String name) {
        for (Transition transition : transitions) {
            if (transition.getName().equals(name)) return transition;
        }
        return null;
    }

    public static String branchNameFromTicket(@NotNull Issue issue) {
        return (String.format("%s%s %s",
                getBranchPrefixForTicketType(issue.getIssueType()),
                issue.getKey(),
                issue.getSummary().toLowerCase())
        ).trim().replace(" ", "-");
    }

    public static String getBranchPrefixForTicketType(IssueType issue) {
        switch (issue.getName().toLowerCase()) {
            case "bug":
                return "bugfix/";
            case "story":
                return "feature/";
            default:
                return "";
        }
    }
}
