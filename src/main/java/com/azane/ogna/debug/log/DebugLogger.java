package com.azane.ogna.debug.log;

import com.azane.ogna.OriginiumArts;
import com.ibm.icu.impl.Pair;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DebugLogger
{
    private static final String modId = OriginiumArts.MOD_ID;
    private static Path gameDir;
    private static final Path logDir = Path.of("logs/%s".formatted(modId));
    private static Path logFile;
    private static final Map<UUID, Pair<Integer,Integer>> reducedLogFreq = new HashMap<>();

    private static final Marker marker = MarkerManager.getMarker(modId+"DebugLog");

    public static Path getGameDir()
    {
        return FMLPaths.GAMEDIR.get();
    }

    public static void init()
    {
        gameDir = getGameDir();
        logFile = gameDir.resolve(logDir).resolve("debug_%s.log".formatted(genFileTime()));
        try
        {
            Files.createDirectories(logFile.getParent());
        } catch (IOException e)
        {
            OriginiumArts.LOGGER.error(marker, "无法创建日志目录: {}", e.getMessage());
        }
        log(LogLv.INFO, marker, "%s Debug Logger initialized.\n Game Directory: %s\n Log File: %s".formatted(
            modId,
            gameDir.toAbsolutePath(),
            logFile.toAbsolutePath()
        ));
    }

    public static void log(LogLv lv, @Nullable Marker marker, String message) {
        try (BufferedWriter writer = Files.newBufferedWriter(
            logFile,
            StandardOpenOption.CREATE,
            StandardOpenOption.APPEND
        )) {
            if(lv != LogLv.NULL)
            {
                String timestamp = LocalDateTime.now().toString();
                writer.write("[%s][%s][%s]: %s\n".formatted(lv, marker == null ? "*" : marker.getName(),timestamp,message));
            }
            else
                writer.write("%s\n".formatted(message));
        } catch (IOException e) {
            OriginiumArts.LOGGER.error(marker, "写入日志失败: {}" ,e.getMessage());
        }
    }
    public static void log(LogLv lv, String message)
    {
        log(lv, null, message);
    }
    public static void log(String message)
    {
        log(LogLv.INFO, null, message);
    }
    /*
    public static void logReduced(UUID typ,String message)
    {
        if(!reducedLogFreq.containsKey(typ))
        {
            reducedLogFreq.put(typ, Pair.of(20,1));
        }
        else
        {
            Pair<Integer, Integer> pair = reducedLogFreq.get(typ);
            if(pair.first >= 10)
            {
                return; // 已经超过频率限制
            }
            pair.first++;
            pair.second++;
        }
        log(LogLv.REDUCED, message);
    }
     */

    public static String genFileTime()
    {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
    }
}
