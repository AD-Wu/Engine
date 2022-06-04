package com.x.demo.window;

import com.x.demo.core.IActor;
import java.time.LocalTime;
import java.util.Map;
import org.springframework.stereotype.Repository;

/**
 * @author AD
 * @date 2022/5/15 20:03
 */
@Repository("window")
public class WindowActor implements IActor {

    @Override
    public String getOS(Map<String, Object> param) {
        return "Window-window:" + LocalTime.now();
    }
}
