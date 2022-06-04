package com.x.doraemon;

import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author AD
 * @date 2022/4/14 10:33
 */
public class Filex {

    /**
     * 将 文件|文件夹 拷贝至目标路径
     * @param source 文件|文件夹
     * @param target 目标路径(不存在则统一按照文件夹处理)
     * @return {@code true} 拷贝成功; {@code false} 拷贝失败或文件已存在
     * @throws IOException
     */
    public static boolean copy(Path source, Path target, CopyOption... options) throws IOException {
        // 修复target目标路径,如果source是文件夹,则target需要包含source自身文件夹名
        Path targetDir = Files.isDirectory(source) ? target.resolve(source.getFileName()) : target;
        // 目标不存在时,统一按文件夹处理
        if (!Files.exists(targetDir)) {
            // 创建目标文件夹
            Files.createDirectories(targetDir);
        }
        // walk()会过滤出多层级path,而list()则是单层级
        Path[] paths = Files.walk(source).toArray(Path[]::new);
        for (int i = 0; i < paths.length; i++) {
            Path subPath = paths[i];
            // 第一个subPath=source, 也就是第一个 subTarget=targetDir
            Path subTarget = targetDir.resolve(source.relativize(subPath));
            try {
                if (Files.isDirectory(subPath)) {
                    Files.createDirectories(subTarget);
                } else {
                    if (Files.isDirectory(subTarget)) {
                        subTarget = subTarget.resolve(subPath.getFileName());
                    }
                    Files.copy(subPath, subTarget, options);
                }
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

}
