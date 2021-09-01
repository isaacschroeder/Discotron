package isaacjschroeder.discotron;

import android.app.Application;

public class MainApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        //Point of this class is to initialize objectbox and sharedprefmanager when app starts (both places where info will be retreived)
        ObjectBox.init(this);
        SharedPreferencesManager.init(this);
    }
}
