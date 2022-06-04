package com.x.jdk8.file;

import com.google.common.base.Stopwatch;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.URISyntaxException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;
import java.util.zip.CRC32;

/**
 * 对于大文件的读取和计算CRC或MD5值等,选择FileChannel
 *          InputStream	: 77487 ms
 *  BufferedInputStream	:   299 ms
 *     RandomAccessFile	: 88120 ms
 *          FileChannel	:   195 ms
 * @author AD
 * @date 2022/5/10 11:02
 */
public class FileChannelDemo {

    private static final Path path = FileHelper.getResource("rt.jar");


    public static void main(String[] args) throws IOException, URISyntaxException {
        readCrc32ByInputStream();
        readCrc32ByBufferedInputStream();
        readCrc32ByRandomAccessFile();
        readCrc32ByFileChannel();

    }

    private static long readCrc32ByInputStream() throws IOException {
        Stopwatch stopwatch = Stopwatch.createStarted();
        CRC32 crc = new CRC32();
        try (InputStream in = Files.newInputStream(path)) {
            int v = -1;
            while ((v = in.read()) != -1) {
                crc.update(v);
            }
        }
        long elapsed = stopwatch.elapsed(TimeUnit.MILLISECONDS);
        System.out.printf("%20s\t:\t%6d ms\n", "InputStream", elapsed);
        return crc.getValue();
    }

    private static long readCrc32ByBufferedInputStream() throws IOException {
        Stopwatch stopwatch = Stopwatch.createStarted();
        CRC32 crc = new CRC32();
        try (InputStream in = Files.newInputStream(path);
             BufferedInputStream buf = new BufferedInputStream(in)) {
            int v = -1;
            while ((v = buf.read()) != -1) {
                crc.update(v);
            }
        }
        long elapsed = stopwatch.elapsed(TimeUnit.MILLISECONDS);
        System.out.printf("%20s\t:\t%6d ms\n", "BufferedInputStream", elapsed);
        return crc.getValue();
    }

    private static long readCrc32ByRandomAccessFile() throws IOException {
        Stopwatch stopwatch = Stopwatch.createStarted();
        CRC32 crc = new CRC32();
        try (RandomAccessFile file = new RandomAccessFile(path.toFile(), "r")) {
            long length = file.length();
            for (long i = 0; i < length; i++) {
                file.seek(i);
                byte b = file.readByte();
                crc.update(b);
            }
        }
        long elapsed = stopwatch.elapsed(TimeUnit.MILLISECONDS);
        System.out.printf("%20s\t:\t%6d ms\n", "RandomAccessFile", elapsed);
        return crc.getValue();
    }

    private static long readCrc32ByFileChannel() throws IOException {
        Stopwatch stopwatch = Stopwatch.createStarted();
        CRC32 crc = new CRC32();
        try (FileChannel chn = FileChannel.open(path)) {
            long size = chn.size();
            MappedByteBuffer buf = chn.map(MapMode.READ_ONLY, 0, size);
            for (int i = 0; i < size; i++) {
                byte b = buf.get(i);
                crc.update(b);
            }
        }
        long elapsed = stopwatch.elapsed(TimeUnit.MILLISECONDS);
        System.out.printf("%20s\t:\t%6d ms\n", "FileChannel", elapsed);
        return crc.getValue();
    }
}
