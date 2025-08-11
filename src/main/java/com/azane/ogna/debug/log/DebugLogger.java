package com.azane.ogna.debug.log;

import com.azane.ogna.OriginiumArts;
import lombok.Getter;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.FileAppender;
import org.apache.logging.log4j.core.config.AppenderRef;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author azaneNH37 (2025-06-05)
 */
public class DebugLogger
{
    private static final String modId = OriginiumArts.MOD_ID;
    private static Path gameDir;
    private static final Path logDir = Path.of("logs/%s".formatted(modId));
    private static Path logFile;

    /**
     * -- GETTER --
     *  获取后端Log4j2 Logger（用于高级用法）
     */
    // Log4j2 logger用于实际的对象处理和格式化
    @Getter
    private static Logger backendLogger;

    // 降频日志相关
    private static final Map<String, AtomicInteger> logCounters = new ConcurrentHashMap<>();
    private static final Map<String, Integer> logThresholds = new ConcurrentHashMap<>();
    private static final Map<String, String> lastLogMessages = new ConcurrentHashMap<>();

    // 默认降频阈值
    private static final int DEFAULT_REDUCED_THRESHOLD = 200;

    private static final Marker marker = MarkerManager.getMarker(modId+"DebugLog");

    public static Path getGameDir()
    {
        return FMLPaths.GAMEDIR.get();
    }

    public static void init()
    {
        gameDir = getGameDir();
        logFile = gameDir.resolve(logDir).resolve("debug_%s.log".formatted(genFileTime()));

        // 设置专用的Log4j2后端
        setupLog4j2Backend();

        log(LogLv.INFO, marker, "%s Debug Logger initialized.\n Game Directory: %s\n Log File: %s".formatted(
            modId,
            gameDir.toAbsolutePath(),
            logFile.toAbsolutePath()
        ));
    }

    /**
     * 设置Log4j2后端，用于处理对象格式化和文件输出
     */
    private static void setupLog4j2Backend() {
        LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
        Configuration config = ctx.getConfiguration();

        // 创建我们的自定义日志格式
        PatternLayout layout = PatternLayout.newBuilder()
            .withPattern("[%level][%marker][%d{yyyy-MM-dd'T'HH:mm:ss.SSS}]: %msg%n")
            .build();

        // 创建文件输出器
        FileAppender fileAppender = FileAppender.newBuilder()
            .withFileName(logFile.toString())
            .withLayout(layout)
            .withName(modId + "DebugFileAppender")
            .withAppend(true)
            .build();

        fileAppender.start();
        config.addAppender(fileAppender);

        // 创建专用的logger配置
        LoggerConfig loggerConfig = LoggerConfig.createLogger(
            false, // additivity
            Level.ALL, // level
            modId + ".debug", // name
            "true", // includeLocation
            new AppenderRef[]{
                AppenderRef.createAppenderRef(
                    modId + "DebugFileAppender", null, null)
            },
            null, // properties
            config,
            null // filter
        );

        loggerConfig.addAppender(fileAppender, null, null);
        config.addLogger(modId + ".debug",loggerConfig);
        ctx.updateLoggers();

        // 获取我们的后端logger
        backendLogger = LogManager.getLogger(modId + ".debug");
    }

    // ========== 基础日志方法 ==========

    /**
     * 基础日志方法，使用Log4j2后端处理
     */
    public static void log(LogLv lv, @Nullable Marker marker, String format, Object... args) {
        Level log4jLevel = mapToLog4jLevel(lv);
        backendLogger.log(log4jLevel, marker, format, args);
    }

    public static void log(LogLv lv, String format, Object... args) {
        log(lv, null, format, args);
    }

    public static void log(String format, Object... args) {
        log(LogLv.INFO, null, format, args);
    }

    // 保持向后兼容的纯字符串方法
    public static void log(LogLv lv, @Nullable Marker marker, String message) {
        log(lv, marker, "{}", message);
    }

    public static void log(LogLv lv, String message) {
        log(lv, null, message);
    }

    public static void log(String message) {
        log(LogLv.INFO, null, message);
    }

    // ========== 降频日志方法 ==========

    /**
     * 降频日志 - 使用默认阈值
     */
    public static void logReduced(String logId, LogLv lv, @Nullable Marker marker, String format, Object... args) {
        logReduced(logId, DEFAULT_REDUCED_THRESHOLD, lv, marker, format, args);
    }

    public static void logReduced(String logId, LogLv lv, String format, Object... args) {
        logReduced(logId, DEFAULT_REDUCED_THRESHOLD, lv, null, format, args);
    }

    public static void logReduced(String logId, String format, Object... args) {
        logReduced(logId, DEFAULT_REDUCED_THRESHOLD, LogLv.INFO, null, format, args);
    }

    /**
     * 降频日志 - 自定义阈值，使用Log4j2处理对象
     */
    public static void logReduced(String logId, int threshold, LogLv lv, @Nullable Marker marker, String format, Object... args) {
        // 更新阈值（如果不同的话）
        logThresholds.put(logId, threshold);

        // 获取或创建计数器
        AtomicInteger counter = logCounters.computeIfAbsent(logId, k -> new AtomicInteger(0));

        // 先让Log4j2格式化消息以获取字符串表示（用于存储最后消息）
        String formattedMessage = backendLogger.getMessageFactory().newMessage(format, args).getFormattedMessage();
        lastLogMessages.put(logId, formattedMessage);

        int count = counter.incrementAndGet();

        if (count % threshold == 0) {
            // 达到阈值，写入日志并包含计数信息
            Level log4jLevel = mapToLog4jLevel(lv);
            backendLogger.log(log4jLevel, marker, "[REDUCED {} times] {}", count, formattedMessage);
        }
    }

    public static void logReduced(String logId, int threshold, LogLv lv, String format, Object... args) {
        logReduced(logId, threshold, lv, null, format, args);
    }

    public static void logReduced(String logId, int threshold, String format, Object... args) {
        logReduced(logId, threshold, LogLv.INFO, null, format, args);
    }

    // ========== 特定日志级别的便捷方法 ==========

    public static void debug(String format, Object... args) {
        log(LogLv.DEBUG, format, args);
    }

    public static void debug(@Nullable Marker marker, String format, Object... args) {
        log(LogLv.DEBUG, marker, format, args);
    }

    public static void info(String format, Object... args) {
        log(LogLv.INFO, format, args);
    }

    public static void info(@Nullable Marker marker, String format, Object... args) {
        log(LogLv.INFO, marker, format, args);
    }

    public static void warn(String format, Object... args) {
        log(LogLv.WARN, format, args);
    }

    public static void warn(@Nullable Marker marker, String format, Object... args) {
        log(LogLv.WARN, marker, format, args);
    }

    public static void error(String format, Object... args) {
        log(LogLv.ERROR, format, args);
    }

    public static void error(@Nullable Marker marker, String format, Object... args) {
        log(LogLv.ERROR, marker, format, args);
    }

    public static void fatal(String format, Object... args) {
        log(LogLv.FATAL, format, args);
    }

    public static void fatal(@Nullable Marker marker, String format, Object... args) {
        log(LogLv.FATAL, marker, format, args);
    }

    // ========== 支持异常的方法 ==========

    public static void error(String format, Object[] args, Throwable throwable) {
        backendLogger.error(format, args, throwable);
    }

    public static void error(@Nullable Marker marker, String format, Object[] args, Throwable throwable) {
        backendLogger.error(marker, format, args, throwable);
    }

    public static void warn(String format, Object[] args, Throwable throwable) {
        backendLogger.warn(format, args, throwable);
    }

    public static void debug(String format, Object[] args, Throwable throwable) {
        backendLogger.debug(format, args, throwable);
    }

    // ========== 降频日志的便捷方法 ==========

    public static void debugReduced(String logId, String format, Object... args) {
        logReduced(logId, LogLv.DEBUG, format, args);
    }

    public static void debugReduced(String logId, int threshold, String format, Object... args) {
        logReduced(logId, threshold, LogLv.DEBUG, format, args);
    }

    public static void infoReduced(String logId, String format, Object... args) {
        logReduced(logId, LogLv.INFO, format, args);
    }

    public static void infoReduced(String logId, int threshold, String format, Object... args) {
        logReduced(logId, threshold, LogLv.INFO, format, args);
    }

    public static void warnReduced(String logId, String format, Object... args) {
        logReduced(logId, LogLv.WARN, format, args);
    }

    public static void warnReduced(String logId, int threshold, String format, Object... args) {
        logReduced(logId, threshold, LogLv.WARN, format, args);
    }

    // ========== 工具方法 ==========

    /**
     * 将自定义LogLv映射到Log4j2的Level
     */
    private static Level mapToLog4jLevel(LogLv lv) {
        return switch (lv) {
            case DEBUG -> Level.DEBUG;
            case INFO -> Level.INFO;
            case WARN -> Level.WARN;
            case ERROR -> Level.ERROR;
            case FATAL -> Level.FATAL;
            case REDUCED -> Level.INFO; // REDUCED映射为INFO
            case NULL -> Level.INFO; // NULL特殊处理
        };
    }

    /**
     * 获取降频日志统计信息
     */
    public static Map<String, Integer> getReducedLogStats() {
        Map<String, Integer> stats = new HashMap<>();
        for (Map.Entry<String, AtomicInteger> entry : logCounters.entrySet()) {
            stats.put(entry.getKey(), entry.getValue().get());
        }
        return stats;
    }

    /**
     * 重置特定日志ID的计数器
     */
    public static void resetReducedLogCounter(String logId) {
        AtomicInteger counter = logCounters.get(logId);
        if (counter != null) {
            counter.set(0);
        }
    }

    /**
     * 重置所有降频日志计数器
     */
    public static void resetAllReducedLogCounters() {
        logCounters.clear();
    }

    /**
     * 强制输出所有降频日志的当前状态
     */
    public static void flushReducedLogs() {
        for (Map.Entry<String, AtomicInteger> entry : logCounters.entrySet()) {
            String logId = entry.getKey();
            int count = entry.getValue().get();
            String lastMessage = lastLogMessages.get(logId);

            if (count > 0 && lastMessage != null) {
                backendLogger.info("[FLUSH REDUCED] {}: {} times - Last: {}", logId, count, lastMessage);
            }
        }
    }

    public static String genFileTime() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
    }
}