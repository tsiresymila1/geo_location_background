package ts.mila.geo_position_background.utils;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.android.gms.common.internal.ReflectedParcelable;

import org.json.JSONException;

public class Config implements ReflectedParcelable {
    private String serverURL;
    private boolean startOnBoot = false;
    private boolean stopOnTerminate = true;

    public Config(String serverURL, boolean startOnBoot, boolean stopOnTerminate) {
        this.serverURL = serverURL;
        this.startOnBoot = startOnBoot;
        this.stopOnTerminate = stopOnTerminate;
    }

    public Config(String serverURL) {
        this.serverURL = serverURL;
    }

    public String getServerURL() {
        return serverURL;
    }

    public void setServerURL(String serverURL) {
        this.serverURL = serverURL;
    }

    public boolean isStartOnBoot() {
        return startOnBoot;
    }

    public void setStartOnBoot(boolean startOnBoot) {
        this.startOnBoot = startOnBoot;
    }

    public boolean isStopOnTerminate() {
        return stopOnTerminate;
    }

    public void setStopOnTerminate(boolean stopOnTerminate) {
        this.stopOnTerminate = stopOnTerminate;
    }

    public Config(Parcel parcel) throws JSONException {
        this.serverURL = parcel.readString();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            this.startOnBoot = parcel.readBoolean();
            this.stopOnTerminate = parcel.readBoolean();
        }

    }

    public static final Creator<Config> CREATOR = new Creator<Config>() {
        @Override
        public Config createFromParcel(Parcel parcel) {
            try {
                return new Config(parcel);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public Config[] newArray(int i) {
            return new Config[i];
        }
    };


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(serverURL);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            parcel.writeBoolean(startOnBoot);
            parcel.writeBoolean(stopOnTerminate);
        }
    }
}
