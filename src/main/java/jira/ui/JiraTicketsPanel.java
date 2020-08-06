// Copyright 2000-2019 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package jira.ui;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.ListPopup;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileListener;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.openapi.vfs.VirtualFilePropertyEvent;
import com.intellij.openapi.vfs.encoding.EncodingManagerImpl;
import com.intellij.openapi.vfs.encoding.EncodingManagerListener;
import com.intellij.openapi.vfs.impl.BulkVirtualFileListenerAdapter;
import com.intellij.openapi.wm.StatusBarWidget;
import com.intellij.openapi.wm.impl.status.EditorBasedStatusBarPopup;
import com.intellij.util.ObjectUtils;
import com.intellij.util.messages.MessageBusConnection;
import jira.ChangeJiraTicketsAction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class JiraTicketsPanel extends EditorBasedStatusBarPopup {

  public final String ID = "JiraTicket";
  public final String TITLE = "Jira Tickets";

  public JiraTicketsPanel(@NotNull Project project) {
    super(project, false);
  }

  @NotNull
  @Override
  protected WidgetState getWidgetState(@Nullable VirtualFile file) {
    if (myProject == null) {
      return WidgetState.HIDDEN;
    }
    String toolTipText = "Select a ticket to create a local branch and start progress";
    return new WidgetState(toolTipText, TITLE, true);
  }

  @Nullable
  @Override
  protected ListPopup createPopup(DataContext context) {
    ChangeJiraTicketsAction action = new ChangeJiraTicketsAction("Assigned Tickets");
    //action.getTemplatePresentation().setText("Jira Tickets");
    return action.createPopup(getProject(), context);
  }

//  @Override
//  protected void registerCustomListeners() {
//    MessageBusConnection connection = ApplicationManager.getApplication().getMessageBus().connect(this);
//
//    // should update to reflect encoding-from-content
//    connection.subscribe(EncodingManagerListener.ENCODING_MANAGER_CHANGES, new EncodingManagerListener() {
//      @Override
//      public void propertyChanged(@Nullable Document document, @NotNull String propertyName, Object oldValue, Object newValue) {
//        if (propertyName.equals(EncodingManagerImpl.PROP_CACHED_ENCODING_CHANGED)) {
//          updateForDocument(document);
//        }
//      }
//    });
//
//    connection.subscribe(VirtualFileManager.VFS_CHANGES, new BulkVirtualFileListenerAdapter(new VirtualFileListener() {
//        @Override
//        public void propertyChanged(@NotNull VirtualFilePropertyEvent event) {
//          if (VirtualFile.PROP_ENCODING.equals(event.getPropertyName())) {
//            updateForFile(event.getFile());
//          }
//        }
//      }));
//  }

  @NotNull
  @Override
  protected StatusBarWidget createInstance(@NotNull Project project) {
    return new JiraTicketsPanel(project);
  }

  @Override
  @NotNull
  public String ID() {
    return ID;
  }
}
