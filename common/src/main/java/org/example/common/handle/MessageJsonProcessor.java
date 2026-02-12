package org.example.common.handle;

import org.example.common.message.BaseBo;
import org.example.common.utils.JSONUtils;

public interface MessageJsonProcessor {
    default String process(BaseBo msg) {
        return JSONUtils.toJSONString(msg);
    }
}
