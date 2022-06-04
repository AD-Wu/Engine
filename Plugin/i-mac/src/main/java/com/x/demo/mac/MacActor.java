package com.x.demo.mac;

import com.x.demo.core.IActor;
import com.x.plugin.anno.NoModify;
import java.time.LocalTime;
import java.util.Map;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;

/**
 * @author AD
 * @date 2022/5/15 20:03
 */
@NoModify
@Scope(WebApplicationContext.SCOPE_REQUEST)
@Service("mac")
public class MacActor implements IActor {

    @Override
    public String getOS(Map<String, Object> param) {
        return "Mac-mac:" + LocalTime.now();
    }

}
