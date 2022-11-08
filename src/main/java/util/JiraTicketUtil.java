package util;

import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.IssueType;
import com.atlassian.jira.rest.client.api.domain.Transition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class JiraTicketUtil {

    private static final String BUG_TYPE_NAME = "bug";
    private static final String TASK_PREFIX = "task/";
    private static final String BUGFIX_PREFIX = "bugfix/";
    private static final String FEATURE_PREFIX = "feature/";

    private JiraTicketUtil() {
    }

    @Nullable
    public static Transition getTransitionByName(@NotNull Iterable<Transition> transitions, @NotNull String name) {
        for (Transition transition : transitions) {
            if (transition.getName().equals(name)) {
                return transition;
            }
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

    private static String getBranchPrefixForTicketType(IssueType issueType) {
        if (issueType.isSubtask()) {
            return TASK_PREFIX;
        }
        if (BUG_TYPE_NAME.equalsIgnoreCase(issueType.getName())) {
            return BUGFIX_PREFIX;
        }
        return FEATURE_PREFIX;
    }
}
