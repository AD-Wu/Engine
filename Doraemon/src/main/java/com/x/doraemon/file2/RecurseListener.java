package com.x.doraemon.file2;

import java.nio.file.Path;

/**
 * @author AD
 * @date 2021/12/22 19:59
 */
public class RecurseListener implements IFileListener {


    @Override
    public void onFileRename(Path oldPath, Path newPath) {
        System.out.println("onFileRename:" + oldPath + " >>> " + newPath);
        System.out.println("");
    }

    @Override
    public void onFileModify(Path file) {
        System.out.println("onFileModify:" + file.toAbsolutePath().toString());
    }

    @Override
    public void onFileCreate(Path file) {
        System.out.println("onFileCreate:" + file.toAbsolutePath().toString());
    }

    @Override
    public void onFileDelete(Path file) {
        System.out.println("onFileDelete:" + file.toAbsolutePath().toString());
    }

    @Override
    public void onDirRename(Path oldDir, Path newDir) {
        System.out.println("onFolderRename:" + oldDir.toAbsolutePath().toString() + " >>> " + newDir.toAbsolutePath().toString());
    }

    @Override
    public void onDirCreate(Path dir) {
        System.out.println("onFolderCreate:" + dir.toAbsolutePath().toString());
        // try {
        //     FolderMonitor monitor = FolderMonitor.get(dir, new RecurseListener());
        //     monitor.start();
        // } catch (Exception e) {
        //     e.printStackTrace();
        // }
    }

    @Override
    public void onDirDelete(Path dir) {
        System.out.println("onFolderDelete:" + dir.toAbsolutePath().toString());
    }

}
