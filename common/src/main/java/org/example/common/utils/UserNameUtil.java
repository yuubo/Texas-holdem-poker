package org.example.common.utils;

import org.example.common.init.MessageSourceUtils;

import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

public class UserNameUtil {

    private static final List<String> nameList = new LinkedList<>();

    private static final ReentrantLock lock = new ReentrantLock();

    private static final Random randoms = new Random();

    public static String getUserName() {
        if (nameList.isEmpty()) {
            init();
        }
        lock.lock();
        try {
            return nameList.remove(randoms.nextInt(nameList.size()));
        } finally {
            lock.unlock();
        }
    }

    private synchronized static void init() {
        if (!nameList.isEmpty()) {
            return;
        }

        String[] prefix = MessageSourceUtils.getMessage("player.name.prefix").split(",");
        String[] suffix = MessageSourceUtils.getMessage("player.name.suffix").split(",");
        for (int i = 0; i < prefix.length; i++) {
            for (int j = 0; j < suffix.length; j++) {
                nameList.add(prefix[i] + suffix[j]);
            }
        }
    }
}
