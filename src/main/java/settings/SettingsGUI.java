package settings;

import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;

import javax.swing.*;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;

public class SettingsGUI {
    private JPanel rootPanel;
    private JTextField textFieldJiraEndpoint;
    private JTextField textFieldUsername;
    private JButton buttonCheck;
    private JPasswordField passwordField;
    private JTextField textFieldFilter;

    private TicketHelperConfig ticketHelperConfig;

    void createUI(Project project) {
        ticketHelperConfig = TicketHelperConfig.getInstance(project);
        textFieldJiraEndpoint.setText(ticketHelperConfig.getJiraEndpointURL());
        textFieldUsername.setText(ticketHelperConfig.getUsername());
        textFieldFilter.setText(ticketHelperConfig.getJqlFilter());
        checkEndpoint();

        buttonCheck.addActionListener(e -> checkEndpoint());
    }

    private void checkEndpoint() {
        // TODO implement
    }

    boolean isModified() {
        boolean modified = false;
        modified |= !textFieldJiraEndpoint.getText().equals(ticketHelperConfig.getJiraEndpointURL());
        modified |= !textFieldUsername.getText().equals(ticketHelperConfig.getUsername());
        String currentPassword = ticketHelperConfig.getPassword();
        if (currentPassword != null) {
            modified |= !Arrays.equals(passwordField.getPassword(), currentPassword.toCharArray());
        }
        modified |= !textFieldFilter.getText().equals(ticketHelperConfig.getJqlFilter());
        return modified;
    }

    void apply() throws ConfigurationException {
        try {
            URL url = new URL(textFieldJiraEndpoint.getText());
            url.toURI();
        } catch (MalformedURLException | URISyntaxException e) {
            throw new ConfigurationException("Invalid URL.");
        }
        ticketHelperConfig.setJiraEndpointURL(textFieldJiraEndpoint.getText());
        ticketHelperConfig.setUsername(textFieldUsername.getText());
        ticketHelperConfig.setPassword(String.valueOf(passwordField.getPassword()));
        ticketHelperConfig.setJqlFilter(textFieldFilter.getText());
    }

    void reset() {
        textFieldJiraEndpoint.setText(ticketHelperConfig.getJiraEndpointURL());
        textFieldUsername.setText(ticketHelperConfig.getUsername());
        passwordField.setText(ticketHelperConfig.getPassword());
        textFieldFilter.setText(ticketHelperConfig.getJqlFilter());
    }

    JPanel getRootPanel() {
        return rootPanel;
    }
}
