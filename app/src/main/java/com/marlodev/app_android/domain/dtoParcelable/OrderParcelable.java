// OrderParcelable.java
package com.marlodev.app_android.domain.dtoParcelable;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class OrderParcelable implements Parcelable {

    public Long id;
    public String status;
    public String message;
    public List<CartItemParcelable> items = new ArrayList<>();

    public OrderParcelable() {}

    protected OrderParcelable(Parcel in) {
        if (in.readByte() == 0) id = null;
        else id = in.readLong();
        status = in.readString();
        message = in.readString();
        items = in.createTypedArrayList(CartItemParcelable.CREATOR);
    }

    public static final Creator<OrderParcelable> CREATOR = new Creator<OrderParcelable>() {
        @Override
        public OrderParcelable createFromParcel(Parcel in) {
            return new OrderParcelable(in);
        }

        @Override
        public OrderParcelable[] newArray(int size) {
            return new OrderParcelable[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (id == null) dest.writeByte((byte) 0);
        else {
            dest.writeByte((byte) 1);
            dest.writeLong(id);
        }
        dest.writeString(status);
        dest.writeString(message);
        dest.writeTypedList(items);
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
