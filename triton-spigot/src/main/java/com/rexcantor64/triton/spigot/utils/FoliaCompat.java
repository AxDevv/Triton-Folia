package com.rexcantor64.triton.spigot.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;

public final class FoliaCompat {

    private static final boolean IS_FOLIA;
    private static final ScheduledExecutorService ASYNC_EXECUTOR = Executors.newScheduledThreadPool(4, new ThreadFactory() {
        private int count = 0;

        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, "Triton-Async-" + (count++));
        }
    });

    static {
        boolean folia = false;
        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            folia = true;
        } catch (ClassNotFoundException ignored) {
            folia = false;
        }
        IS_FOLIA = folia;
        System.out.println("[Triton] Folia detection: " + folia);
    }

    public static boolean isFolia() {
        return IS_FOLIA;
    }

    public static void runAsync(Plugin plugin, Runnable runnable) {
        if (IS_FOLIA) {
            ASYNC_EXECUTOR.execute(runnable);
        } else {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, runnable);
        }
    }

    public static void runDelayed(Plugin plugin, Runnable runnable, long delay) {
        run(plugin, runnable);
    }

    public static void run(Plugin plugin, Runnable runnable) {
        if (IS_FOLIA) {
            try {
                Method method = Bukkit.class.getMethod("getGlobalRegionScheduler");
                Object scheduler = method.invoke(Bukkit.getServer());
                method = scheduler.getClass().getMethod("execute", Plugin.class, Runnable.class);
                method.invoke(scheduler, plugin, runnable);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Bukkit.getScheduler().runTask(plugin, runnable);
        }
    }

    public static void runAtLocation(Plugin plugin, Location location, Runnable runnable) {
        run(plugin, runnable);
    }

    public static void runDelayedAtLocation(Plugin plugin, Location location, Runnable runnable, long delay) {
        run(plugin, runnable);
    }

    public static void runForPlayer(Plugin plugin, Player player, Runnable runnable) {
        run(plugin, runnable);
    }

    public static void cancelTask(Plugin plugin, int taskId) {
        if (!IS_FOLIA) {
            Bukkit.getScheduler().cancelTask(taskId);
        }
    }

    public static void scheduleAtFixedRateAsync(Plugin plugin, Runnable runnable, long initialDelay, long period, TimeUnit unit) {
        ASYNC_EXECUTOR.scheduleAtFixedRate(() -> {
            if (plugin.isEnabled()) {
                try {
                    runnable.run();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, unit.toMillis(initialDelay), unit.toMillis(period), TimeUnit.MILLISECONDS);
    }

    public static void executeGlobal(Plugin plugin, Runnable runnable) {
        run(plugin, runnable);
    }

    private FoliaCompat() {
    }
}
