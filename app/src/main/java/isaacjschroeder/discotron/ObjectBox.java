package isaacjschroeder.discotron;

import android.content.Context;

import io.objectbox.BoxStore;
import isaacjschroeder.discotron.data.MyObjectBox;

public class ObjectBox {

    private static BoxStore boxStore;

    public static final long INVALID_ID = -1;           //to use as value for no id
    public static final String ID_EXTRA = "ID_EXTRA";   //For when passing ids through intents

    public static void init(Context context) {
        boxStore = MyObjectBox.builder()
                .androidContext(context.getApplicationContext())
                .build();
    }

    public static BoxStore get() {
        return boxStore;
    }
}
