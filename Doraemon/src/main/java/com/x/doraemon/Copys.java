package com.x.doraemon;

import com.google.gson.Gson;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class Copys {

    // ------------------------ 变量定义 ------------------------

    // ------------------------ 构造方法 ------------------------

    private Copys() {
    }

    // ------------------------ 方法定义 ------------------------

    /**
     * 深度拷贝对象（和拷贝对象相关的对象都必须实现Serializable接口）
     *
     * @param serializable 拷贝对象（可序列化）
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static <T extends Serializable> T serialCopy(T serializable) throws Exception {
        // 获取对象字节流
        byte[] bytes = serialize(serializable);
        // 反序列化拷贝
        return deserialize(bytes);
    }

    /**
     * 采用json的方式拷贝一个对象，效率慢，对象无需实现序列化接口
     *
     * @param clone
     * @param <T>
     * @return
     */
    public static <T> T jsonCopy(T clone) {
        Gson gson = new Gson();
        String s = gson.toJson(clone);
        System.out.println(s);
        return (T) gson.fromJson(gson.toJson(clone), clone.getClass());

    }

    // ------------------------ 私有方法 ------------------------

    private static byte[] serialize(Serializable serializable) throws IOException {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeObject(serializable);
            oos.flush();
            return bos.toByteArray();
        }
    }

    private static <T extends Serializable> T deserialize(byte[] bytes) throws IOException, ClassNotFoundException {
        try (ByteArrayInputStream bin = new ByteArrayInputStream(bytes);
             ObjectInputStream oin = new ObjectInputStream(bin);) {
            return (T) oin.readObject();
        }
    }

}
