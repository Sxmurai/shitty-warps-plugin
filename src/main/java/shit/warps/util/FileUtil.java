package shit.warps.util;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.server.v1_8_R3.BlockPosition;
import shit.warps.ShitWarps;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author aesthetical
 * @since 08/08/2022
 */
public class FileUtil {
    public static final Path ROOT = Paths.get("").resolve("ShitWarps");
    public static final Path WARPS_FILE = ROOT.resolve("warps.json");

    public static void loadWarps() {
        if (!Files.exists(ROOT)) {
            try {
                Files.createDirectory(ROOT);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (!Files.exists(WARPS_FILE)) {
            try {
                Files.createFile(WARPS_FILE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        String content = read(WARPS_FILE);
        if (content != null && !content.isEmpty()) {
            JsonObject obj = new JsonParser().parse(content).getAsJsonObject();
            if (obj != null) {
                obj.entrySet().forEach((entry) -> {
                    String name = entry.getKey();

                    JsonObject pos = entry.getValue().getAsJsonObject();
                    BlockPosition position = new BlockPosition(
                            pos.get("x").getAsDouble(),
                            pos.get("y").getAsDouble(),
                            pos.get("z").getAsDouble());

                    ShitWarps.WARPS.put(name, position);
                });
            }
        }
    }

    public static void save() {
        FileOutputStream stream = null;
        try {
            stream = new FileOutputStream(WARPS_FILE.toFile());

            JsonObject object = new JsonObject();
            ShitWarps.WARPS.forEach((name, pos) -> {
                JsonObject p = new JsonObject();
                p.addProperty("x", pos.getX());
                p.addProperty("y", pos.getY());
                p.addProperty("z", pos.getZ());

                object.add(name, p);
            });

            String text = new GsonBuilder().setPrettyPrinting().create().toJson(object);
            stream.write(text.getBytes(StandardCharsets.UTF_8), 0, text.length());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static String read(Path path) {
        FileInputStream stream = null;
        try {
             stream = new FileInputStream(path.toFile());
             StringBuilder builder = new StringBuilder();

             int i;
             while ((i = stream.read()) != -1) {
                 builder.append((char) i);
             }

             return builder.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return null;
    }
}
