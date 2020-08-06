package settings;

import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SearchableConfigurable;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class TicketHelperConfigurable implements SearchableConfigurable {
    SettingsGUI gui;
    Project project;
    TicketHelperConfig config;

    public TicketHelperConfigurable(@NotNull Project project) {
        this.project = project;
        config = TicketHelperConfig.getInstance(project);
    }

    @NotNull
    @Override
    public String getId() {
        return "com.dynatrace.tickethelper";
    }

    @Nls
    @Override
    public String getDisplayName() {
        return "Ticket Helper Plugin";
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        gui = new SettingsGUI();
        gui.createUI(project);
        return gui.getRootPanel();
    }

    @Override
    public boolean isModified() {
        return gui.isModified();
    }

    @Override
    public void apply() throws ConfigurationException {
        gui.apply();
    }

    @Override
    public void reset() {
        gui.reset();
    }


}
