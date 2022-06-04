package com.x.doraemon.file2;

import java.nio.file.Path;

interface IFileListener {

    // ------------------- 文件操作 -------------------
    void onFileRename(Path oldPath, Path newPath);

    void onFileModify(Path file);

    void onFileCreate(Path file);

    void onFileDelete(Path file);

    // ------------------ 文件夹操作 ------------------
    void onDirRename(Path oldDir, Path newDir);

    void onDirCreate(Path dir);

    void onDirDelete(Path dir);

}
