package util;

import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.project.Project;
import git4idea.GitLocalBranch;
import git4idea.branch.GitBranchUiHandlerImpl;
import git4idea.branch.GitBranchUtil;
import git4idea.branch.GitBranchWorker;
import git4idea.branch.GitCreateBranchOperation;
import git4idea.commands.Git;
import git4idea.i18n.GitBundle;
import git4idea.repo.GitRepository;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GitUtil {

    private GitUtil() {}

    public static String getCurrentBranchName(@NotNull final Project project) {
        GitRepository repository;
        GitLocalBranch localBranch;
        String branchName = "";
        try {
            repository = GitBranchUtil.getCurrentRepository(project);
            localBranch = repository.getCurrentBranch();
            branchName = localBranch.getName();
        } catch (Exception e) {
            e.getMessage();
        }
        if (branchName == null) {
            branchName = "";
        }
        return branchName;
    }

    public static void checkoutNewBranch(@NotNull Project project, @NotNull String name, @NotNull List<? extends GitRepository> repositories) {
        new CommonBackgroundTask(project, GitBundle.message("branch.checking.out.new.branch.process", name), null) {
            @Override
            public void execute(@NotNull ProgressIndicator indicator) {
                newWorker(project, indicator)
                        .checkoutNewBranch(name, repositories);
            }
        }.runInBackground();
    }

    public static void createNewBranch(@NotNull Project project, @NotNull String name, @NotNull List<? extends GitRepository> repositories) {
        Map<GitRepository, String> startMap = new HashMap<>();
        for (GitRepository repository : repositories) {
            startMap.put(repository, "HEAD");
            // TODO add feature to start from origin/master ?
        }


        new CommonBackgroundTask(project, GitBundle.message("branch.checking.out.new.branch.process", name), null) {
            @Override
            public void execute(@NotNull ProgressIndicator indicator) {
                newWorker(project, indicator)
                        .createBranch(name, startMap);
            }
        }.runInBackground();
    }

    @NotNull
    private static GitBranchWorker newWorker(@NotNull Project project, @NotNull ProgressIndicator indicator) {
        return new GitBranchWorker(project, Git.getInstance(), new GitBranchUiHandlerImpl(project, Git.getInstance(), indicator));
    }



}
