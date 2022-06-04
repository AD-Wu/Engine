package com.x.doraemon;

import java.util.Arrays;
import org.apache.commons.lang3.ArrayUtils;

/**
 * @author AD
 * @desc
 * @date 2022/1/10 1:41
 */
public class Arrayx extends ArrayUtils {

    private Arrayx() {}

    public static void convert() {
        Byte[] bs = new Byte[2];
        bs[0] = new Byte((byte) 1);
        bs[1] = new Byte((byte) 2);
        Byte[] bytes = toArray(bs);

        System.out.println(Arrays.toString(bytes));
        byte[] bytes1 = toPrimitive(bytes);
        System.out.println(Arrays.toString(bytes1));
    }

}
