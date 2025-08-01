package com.azane.ogna.lib;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.google.gson.stream.JsonToken;
import com.google.gson.TypeAdapter;
import java.io.IOException;

public class HexColorTypeAdapter extends TypeAdapter<Integer> {

    @Override
    public Integer read(JsonReader in) throws IOException {
        JsonToken token = in.peek();
        if (token == JsonToken.NULL) {
            in.nextNull();
            return null;
        } else if (token == JsonToken.STRING) {
            String hexColor = in.nextString();

            // 使用正则表达式匹配并移除开头为 "#" 或 "0x" 的前缀（不区分大小写）
            // 例如 "#FF5733" -> "FF5733"
            // "0xFF5733" -> "FF5733"
            // "ff5733" -> "ff5733"
            hexColor = hexColor.replaceFirst("^(#|0x)", "");

            try {
                return Integer.parseInt(hexColor, 16);
            } catch (NumberFormatException e) {
                throw new IOException("[HexColorTypeAdapter] Invalid hexadecimal color format: " + hexColor, e);
            }
        } else if (token == JsonToken.NUMBER) {
            // 处理常规数字情况
            return in.nextInt();
        } else {
            throw new IOException("[HexColorTypeAdapter] Expected a string or number for color, but found: " + token);
        }
    }

    @Override
    public void write(JsonWriter out, Integer value) throws IOException {
        if (value == null) {
            out.nullValue();
        } else {
            String hexColor = String.format("#%06X", (0xFFFFFF & value));
            out.value(hexColor);
        }
    }
}