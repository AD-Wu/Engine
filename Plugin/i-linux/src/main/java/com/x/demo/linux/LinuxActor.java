package com.x.demo.linux;

import com.x.demo.core.IActor;
import java.time.LocalTime;
import java.util.Map;
import org.springframework.stereotype.Component;

/**
 * @author AD
 * @date 2022/5/15 20:02
 */
@Component("linux")
public class LinuxActor implements IActor {

    @Override
    public String getOS(Map<String, Object> param) {
        return "Linux-linux:" + LocalTime.now();
    }

}
