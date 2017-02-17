package panva;

import android.content.Context;

import java.text.MessageFormat;

public class StringFormater {

    public static String format(String p, Object... args) {
        return MessageFormat.format(p, args);
    }

    public static String format(Context x, int id, Object... args) {
        return format(x.getString(id), args);
    }
}
