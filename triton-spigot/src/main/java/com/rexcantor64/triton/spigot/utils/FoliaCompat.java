package com.rexcantor64.triton.spigot.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

public final class FoliaCompat {

    private static final boolean IS_FOLIA;
    private static Method getAsyncScheduler;
    private static Method getGlobalRegionScheduler;
    private static Method getRegionScheduler;
    private static Method asyncRunNow;
    private static Method asyncRunAtFixedRate;
    private static Method globalRun;
    private static Method globalRunDelayed;
    private static Method globalExecute;
    private static Method regionRun;
    private static Method regionRunDelayed;

    static {
        boolean folia = false;
        try {
            Class.forName("io.papermc.paper.threadedregions.ThreadedRegionizer");
            folia = true;
            getAsyncScheduler = Bukkit.class.getMethod("getAsyncScheduler");
            getGlobalRegionScheduler = Bukkit.class.getMethod("getGlobalRegionScheduler");
            getRegionScheduler = Bukkit.class.getMethod("getRegionScheduler");

            Class<?> asyncScheduler = Class.forName("io.papermc.paper.scheduler.AsyncScheduler");
            asyncRunNow = asyncScheduler.getMethod("runNow", Plugin.class, Runnable.class);
            asyncRunAtFixedRate = asyncScheduler.getMethod("runAtFixedRate", Plugin.class, Runnable.class, long.class, long.class, TimeUnit.class);

            Class<?> regionScheduler = Class.forName("io.papermc.paper.scheduler.RegionScheduler");
            regionRun = regionScheduler.getMethod("run", Plugin.class, Location.class, Runnable.class);
            regionRunDelayed = regionScheduler.getMethod("runDelayed", Plugin.class, Location.class, Runnable.class, long.class);

            Class<?> globalRegionScheduler = Class.forName("io.papermc.paper.scheduler.GlobalRegionScheduler");
            globalRun = globalRegionScheduler.getMethod("run", Plugin.class, Runnable.class);
            globalRunDelayed = globalRegionScheduler.getMethod("runDelayed", Plugin.class, Runnable.class, long.class);
            globalExecute = globalRegionScheduler.getMethod("execute", Plugin.class, Runnable.class);
        } catch (ClassNotFoundException | NoSuchMethodException ignored) {
            folia = false;
        }
        IS_FOLIA = folia;
    }

    public static boolean isFolia() {
        return IS_FOLIA;
    }

    public static void runAsync(Plugin plugin, Runnable runnable) {
        if (IS_FOLIA) {
            try {
                Object scheduler = getAsyncScheduler.invoke(Bukkit.getServer());
                asyncRunNow.invoke(scheduler, plugin, runnable);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, runnable);
        }
    }

    public static void runDelayed(Plugin plugin, Runnable runnable, long delay) {
        if (IS_FOLIA) {
            try {
                Object scheduler = getGlobalRegionScheduler.invoke(Bukkit.getServer());
                globalRunDelayed.invoke(scheduler, plugin, runnable, delay);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Bukkit.getScheduler().runTaskLater(plugin, runnable, delay);
        }
    }

    public static void run(Plugin plugin, Runnable runnable) {
        if (IS_FOLIA) {
            try {
                Object scheduler = getGlobalRegionScheduler.invoke(Bukkit.getServer());
                globalRun.invoke(scheduler, plugin, runnable);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Bukkit.getScheduler().runTask(plugin, runnable);
        }
    }

    public static void runAtLocation(Plugin plugin, Location location, Runnable runnable) {
        if (IS_FOLIA) {
            try {
                Object scheduler = getRegionScheduler.invoke(Bukkit.getServer());
                regionRun.invoke(scheduler, plugin, location, runnable);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Bukkit.getScheduler().runTask(plugin, runnable);
        }
    }

    public static void runDelayedAtLocation(Plugin plugin, Location location, Runnable runnable, long delay) {
        if (IS_FOLIA) {
            try {
                Object scheduler = getRegionScheduler.invoke(Bukkit.getServer());
                regionRunDelayed.invoke(scheduler, plugin, location, runnable, delay);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Bukkit.getScheduler().runTaskLater(plugin, runnable, delay);
        }
    }

    public static void runForPlayer(Plugin plugin, Player player, Runnable runnable) {
        if (IS_FOLIA) {
            try {
                Object scheduler = getRegionScheduler.invoke(Bukkit.getServer());
                regionRun.invoke(scheduler, plugin, player.getLocation(), runnable);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Bukkit.getScheduler().runTask(plugin, runnable);
        }
    }

    public static void cancelTask(Plugin plugin, int taskId) {
        if (!IS_FOLIA) {
            Bukkit.getScheduler().cancelTask(taskId);
        }
    }

    public static void scheduleAtFixedRateAsync(Plugin plugin, Runnable runnable, long initialDelay, long period, TimeUnit unit) {
        if (IS_FOLIA) {
            try {
                Object scheduler = getAsyncScheduler.invoke(Bukkit.getServer());
                asyncRunAtFixedRate.invoke(scheduler, plugin, runnable, period, period, unit);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, runnable, initialDelay, period);
        }
    }

    public static void executeGlobal(Plugin plugin, Runnable runnable) {
        if (IS_FOLIA) {
            try {
                Object scheduler = getGlobalRegionScheduler.invoke(Bukkit.getServer());
                globalExecute.invoke(scheduler, plugin, runnable);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Bukkit.getScheduler().runTask(plugin, runnable);
        }
    }

    private FoliaCompat() {
    }
}
