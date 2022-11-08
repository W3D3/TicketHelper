package jira;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.JiraRestClientFactory;
import com.atlassian.jira.rest.client.api.RestClientException;
import com.atlassian.jira.rest.client.api.SearchRestClient;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.SearchResult;
import com.atlassian.jira.rest.client.api.domain.Transition;
import com.atlassian.jira.rest.client.api.domain.input.TransitionInput;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import io.atlassian.util.concurrent.Promise;
import util.JiraTicketUtil;
import util.Notification;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class JiraTicketClient {

    JiraRestClient client;

    public JiraTicketClient(String baseUrl, String user, String password) {
        JiraRestClientFactory factory = new AsynchronousJiraRestClientFactory();
        URI jiraServerUri;
        try {
            jiraServerUri = new URI(baseUrl);
        } catch (URISyntaxException e) {
            throw new RuntimeException("Could not parse the JIRA Base URL: " + e.getMessage(), e);
        }

        client = factory.createWithBasicHttpAuthentication(jiraServerUri, user, password);
    }

    public void startProgress(Issue ticket) {
        final Iterable<Transition> transitions = client.getIssueClient().getTransitions(ticket).claim();
        final Transition startProgressTransition = JiraTicketUtil.getTransitionByName(transitions, "Start Progress");
        if (startProgressTransition != null) {
            client.getIssueClient().transition(ticket, new TransitionInput(startProgressTransition.getId()));
        }
    }

    public Collection<Issue> getIssuesForQuery(String jqlQuery) {
        SearchRestClient searchClient = client.getSearchClient();
        Collection<Issue> issues = new ArrayList<>();

        Promise<SearchResult> searchResultPromise;
        try {
            searchResultPromise = searchClient.searchJql(jqlQuery);
        } catch (RuntimeException exception) {
            Notification.notifyError("Connection to Jira failed", exception.getCause().getLocalizedMessage());
            return Collections.emptyList();
        }

        try {
            SearchResult results = searchResultPromise.claim();
            results.getIssues().forEach(issues::add);
        } catch (RestClientException e) {
            String errorMessage = e.getStatusCode().isPresent() ?
                    String.format("Status code %d with message %s", e.getStatusCode().get(), e.getMessage()) :
                    e.getMessage();

            Notification.notifyError("Could not connect to the Jira server", errorMessage);
        }

        return issues;
    }
}
