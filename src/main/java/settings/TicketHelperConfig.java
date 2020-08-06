package settings;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.project.Project;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@State(
        name = "TicketHelperConfig",
        storages = {@Storage("TicketHelperConfig.xml")}
)
public class TicketHelperConfig implements PersistentStateComponent<TicketHelperConfig> {

    private static final String DEFAULT_JIRA_ENDPOINT = "https://dev-jira.dynatrace.org/";
    private static final String DEFAULT_JIRA_FILTER = "status in (Open, \"In Progress\") AND assignee in (currentUser())";

    String jiraEndpointURL = DEFAULT_JIRA_ENDPOINT;
    String username;
    String password;
    String jqlFilter = DEFAULT_JIRA_FILTER;

    public TicketHelperConfig() {
    }

    @Nullable
    public static TicketHelperConfig getInstance(Project project) {
        return ServiceManager.getService(project, TicketHelperConfig.class);
    }

    public String getJiraEndpointURL() {
        return jiraEndpointURL;
    }

    public void setJiraEndpointURL(String jiraEndpointURL) {
        this.jiraEndpointURL = jiraEndpointURL;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getJqlFilter() {
        return jqlFilter;
    }

    public void setJqlFilter(String jqlFilter) {
        this.jqlFilter = jqlFilter;
    }

    @Nullable
    @Override
    public TicketHelperConfig getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull TicketHelperConfig state) {
        XmlSerializerUtil.copyBean(state, this);
    }


}
