package cn.flood.okhttp.utils;

import java.io.StringWriter;

class UnicodeStringWriter extends StringWriter {
    private static final char[] HEX_CHARS = "0123456789ABCDEF".toCharArray();
    private static final int[] ESCAPE_CODES;

    UnicodeStringWriter() {
    }

    public String toString() {
        String src = super.toString();
        StringBuffer builder = new StringBuffer(src.length() * 4);
        char[] var3 = src.toCharArray();
        int var4 = var3.length;

        for(int var5 = 0; var5 < var4; ++var5) {
            char c = var3[var5];
            if (c >= 128) {
                this.writeUnicodeEscape(builder, c);
            } else {
                int code = c < ESCAPE_CODES.length ? ESCAPE_CODES[c] : 0;
                if (code == 0) {
                    builder.append(c);
                } else if (code < 0) {
                    this.writeUnicodeEscape(builder, (char)(-code - 1));
                } else {
                    builder.append("\\").append((char)code);
                }
            }
        }

        return builder.toString();
    }

    private void writeUnicodeEscape(StringBuffer builder, char c) {
        builder.append("\\u");
        builder.append(HEX_CHARS[c >> 12 & 15]);
        builder.append(HEX_CHARS[c >> 8 & 15]);
        builder.append(HEX_CHARS[c >> 4 & 15]);
        builder.append(HEX_CHARS[c & 15]);
    }

    static {
        int[] table = new int[128];

        for(int i = 0; i < 32; ++i) {
            table[i] = -1;
        }

        table[47] = 47;
        ESCAPE_CODES = table;
    }
}
