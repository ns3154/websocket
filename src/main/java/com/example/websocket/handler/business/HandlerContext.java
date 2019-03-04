package com.example.websocket.handler.business;

import com.example.websocket.annotation.HandScan;
import com.example.websocket.annotation.HandType;
import com.example.websocket.utils.ClassScaner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * <pre>
 *
 * </pre>
 * @author 杨帮东 (qq:397827222)
 * @version 1.0
 * @date 2019/03/01 12:59
 **/
@Component
@HandScan(path = "com.example.websocket.handler.business.impl")
public class HandlerContext implements CommandLineRunner {

    /**
     * Callback used to run the bean.
     * @param args incoming main method arguments
     * @throws Exception on error
     */
    @Override
    public void run(String... args) throws Exception {
        initBusiness();

    }

    /**
     * 初始化 业务逻辑
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    private void initBusiness() throws IllegalAccessException, InstantiationException {
        Set<Class<?>> scan = ClassScaner.scan(HandlerContext.class.getAnnotation(HandScan.class).path(), HandType.class);

        for (Class c : scan) {
            HandType type = (HandType) c.getAnnotation(HandType.class);
            handlerMap.put(type.type() + "", c.newInstance());
        }
    }

    private HandlerContext() {

    }

    private static Map<String, Object> handlerMap = new HashMap<>(5);

    public static BusinessHandler getHandlerContext(String type) {
        Object cs = handlerMap.get(type);

        if (null == cs || "" == cs) {
            throw new IllegalArgumentException("not found handler for type: " + type);
        }

        return (BusinessHandler) cs;
    }
}
