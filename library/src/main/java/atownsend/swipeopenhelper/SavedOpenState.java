package atownsend.swipeopenhelper;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Enum for saving the opened state of the view holders
 */
public enum SavedOpenState implements Parcelable {
    START_OPEN, END_OPEN;

    @Override public int describeContents() {
        return 0;
    }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(ordinal());
    }

    public static final Parcelable.Creator<SavedOpenState> CREATOR =
            new Parcelable.Creator<SavedOpenState>() {
                @Override public SavedOpenState createFromParcel(Parcel source) {
                    return SavedOpenState.values()[source.readInt()];
                }

                @Override public SavedOpenState[] newArray(int size) {
                    return new SavedOpenState[size];
                }
            };

}
