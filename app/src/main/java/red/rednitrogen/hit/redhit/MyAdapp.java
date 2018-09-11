package red.rednitrogen.hit.redhit;

import android.app.Application;
import com.google.android.gms.ads.MobileAds;

public class MyAdapp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // initialize the AdMob app
        MobileAds.initialize(this, getString(R.string.admob_app_id));
    }
}

