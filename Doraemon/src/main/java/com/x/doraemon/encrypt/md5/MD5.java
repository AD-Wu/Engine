package com.x.doraemon.encrypt.md5;

import com.x.doraemon.Converts;
import java.io.File;
import java.io.FileInputStream;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @Date 2018-12-19 20:19
 * @Author AD
 */
public final class MD5 {

    private MessageDigest md;

    public MD5() {
        try {
            this.md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public String encode(String str) throws Exception {
       return encode(str.getBytes());
    }

    public String encode(byte[] bytes) throws Exception {
        md.update(bytes);
        return Converts.bytesToHex(md.digest());
    }

    public String encode(File file) throws Exception {

        try (FileInputStream in = new FileInputStream(file);
             FileChannel channel = in.getChannel()) {
            MappedByteBuffer buf = channel.map(FileChannel.MapMode.READ_ONLY, 0, file.length());
            md.update(buf);
        }
        return Converts.bytesToHex(md.digest());
    }

}
