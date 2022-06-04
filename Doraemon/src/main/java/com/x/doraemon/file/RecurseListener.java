package com.x.doraemon.file;

import java.io.File;

/**
 *
 * @author AD
 * @date 2021/12/22 19:59
 */
public class RecurseListener implements IFileListener {


    @Override
    public void onFileRename(String oldPath, String newPath) {
        System.out.println("onFileRename:" + oldPath + " >>> " + newPath);
        System.out.println("");
    }


    @Override
    public void onFileModify(File file) {
        System.out.println("onFileModify:" + file.getAbsolutePath());
    }

    @Override
    public void onFileCreate(File file) {
        System.out.println("onFileCreate:" + file.getAbsolutePath());
    }

    @Override
    public void onFileDelete(File file) {
        System.out.println("onFileDelete:" + file.getAbsolutePath());
    }

    @Override
    public void onFolderRename(String oldPath, String newPath) {
        System.out.println("onFolderRename:" + oldPath + " >>> " + newPath);
    }

    @Override
    public void onFolderCreate(File folder) {
        System.out.println("onFolderCreate:" + folder.getAbsolutePath());
        try {
            FolderMonitor monitor = FolderMonitor.get(folder, new RecurseListener());
            monitor.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFolderDelete(File folder) {
        System.out.println("onFolderDelete:" + folder.getAbsolutePath());
    }

}
