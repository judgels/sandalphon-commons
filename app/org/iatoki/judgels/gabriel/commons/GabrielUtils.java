package org.iatoki.judgels.gabriel.commons;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class GabrielUtils {

    private static Lock scoreboardLock = new ReentrantLock();

    private GabrielUtils() {
        // prevent instantiation
    }

    public static Lock getScoreboardLock() {
        return scoreboardLock;
    }

}
