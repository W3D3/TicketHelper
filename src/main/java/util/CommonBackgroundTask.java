package util;

import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import git4idea.GitVcs;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
   * Executes common operations before/after executing the actual branch operation.
   */
  public abstract class CommonBackgroundTask extends Task.Backgroundable {

    @Nullable
    private final Runnable myCallInAwtAfterExecution;

    CommonBackgroundTask(@Nullable Project project, @Nls @NotNull String title, @Nullable Runnable callInAwtAfterExecution) {
      super(project, title);
      myCallInAwtAfterExecution = callInAwtAfterExecution;
    }

    @Override
    public final void run(@NotNull ProgressIndicator indicator) {
      execute(indicator);
      if (myCallInAwtAfterExecution != null) {
        Application application = ApplicationManager.getApplication();
        application.invokeAndWait(myCallInAwtAfterExecution, application.getDefaultModalityState());
      }
    }

    abstract void execute(@NotNull ProgressIndicator indicator);

    void runInBackground() {
      GitVcs.runInBackground(this);
    }
  }
