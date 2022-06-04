package com.x.doraemon.file;

import java.io.File;

interface IFileListener {

    // ------------------- 文件操作 -------------------
    void onFileRename(String oldPath, String newPath);

    void onFileModify(File file);

    void onFileCreate(File file);

    void onFileDelete(File file);

    // ------------------ 文件夹操作 ------------------
    void onFolderRename(String oldPath, String newPath);

    void onFolderCreate(File folder);

    void onFolderDelete(File folder);

}
