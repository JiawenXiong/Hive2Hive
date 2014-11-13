package org.hive2hive.client.util;

import java.io.IOException;

import net.engio.mbassy.listener.Handler;
import net.engio.mbassy.listener.Listener;
import net.engio.mbassy.listener.References;

import org.apache.commons.io.FileUtils;
import org.hive2hive.client.console.H2HConsoleMenu;
import org.hive2hive.core.api.interfaces.IFileManager;
import org.hive2hive.core.events.framework.interfaces.IFileEventListener;
import org.hive2hive.core.events.framework.interfaces.file.IFileAddEvent;
import org.hive2hive.core.events.framework.interfaces.file.IFileDeleteEvent;
import org.hive2hive.core.events.framework.interfaces.file.IFileMoveEvent;
import org.hive2hive.core.events.framework.interfaces.file.IFileShareEvent;
import org.hive2hive.core.events.framework.interfaces.file.IFileUpdateEvent;
import org.hive2hive.core.exceptions.NoPeerConnectionException;
import org.hive2hive.core.exceptions.NoSessionException;
import org.hive2hive.processframework.exceptions.InvalidProcessStateException;

@Listener(references = References.Strong)
public class FileEventListener implements IFileEventListener {

	private final IFileManager fileManager;

	public FileEventListener(IFileManager fileManager) {
		this.fileManager = fileManager;
	}

	@Override
	@Handler
	public void onFileAdd(IFileAddEvent fileEvent) {
		try {
			H2HConsoleMenu.executeBlocking(fileManager.download(fileEvent.getFile()), "AddFileEvent");
		} catch (InvalidProcessStateException | NoSessionException | NoPeerConnectionException | InterruptedException e) {
			System.err.println("Cannot download the new file " + fileEvent.getFile());
		}
	}

	@Override
	@Handler
	public void onFileUpdate(IFileUpdateEvent fileEvent) {
		try {
			H2HConsoleMenu.executeBlocking(fileManager.download(fileEvent.getFile()), "UpdateFileEvent");
		} catch (NoSessionException | NoPeerConnectionException | InvalidProcessStateException | InterruptedException e) {
			System.err.println("Cannot download the updated file " + fileEvent.getFile());
		}
	}

	@Override
	@Handler
	public void onFileDelete(IFileDeleteEvent fileEvent) {
		fileEvent.getFile().delete();
	}

	@Override
	@Handler
	public void onFileMove(IFileMoveEvent fileEvent) {
		try {
			if (fileEvent.isFile()) {
				FileUtils.moveFile(fileEvent.getSrcFile(), fileEvent.getDstFile());
			} else {
				FileUtils.moveDirectory(fileEvent.getSrcFile(), fileEvent.getDstFile());
			}
		} catch (IOException e) {
			System.err.println("Cannot move the file / folder " + fileEvent.getFile());
		}
	}

	@Override
	@Handler
	public void onFileShare(IFileShareEvent fileEvent) {
		// ignore because it will trigger onFileAdd for every file anyhow
	}

}
