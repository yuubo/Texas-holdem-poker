package org.example.common.utils;

import org.example.common.init.MessageSourceUtils;

import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

public class UserNameUtil {

    private static final List<String> nameList = new LinkedList<>();

    private static final ReentrantLock lock = new ReentrantLock();

    private static final Random randoms = new Random();

    public static String getUserName() {
        lock.lock();
        if (nameList.isEmpty()) {
            init();
        }
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

        String prefix = MessageSourceUtils.getMessage("player.name.prefix");
        String suffix = MessageSourceUtils.getMessage("player.name.suffix");

        String[] prefixArr = prefix.split(",");
        String[] suffixArr = suffix.split(",");
        for (int i = 0; i < prefixArr.length; i++) {
            for (int j = 0; j < suffixArr.length; j++) {
                nameList.add(prefixArr[i].trim() + suffixArr[j].trim());
            }
        }
    }

}
