// CartItemParcelable.java
package com.marlodev.app_android.domain.dtoParcelable;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class CartItemParcelable implements Parcelable {

    public Long id;
    public String productName; // para simplificar
    public Integer quantity;
    public String unitPrice; // BigDecimal → String
    public List<String> imageUrls = new ArrayList<>(); // <-- lista de imágenes

    public CartItemParcelable() {}

    protected CartItemParcelable(Parcel in) {
        if (in.readByte() == 0) id = null;
        else id = in.readLong();
        productName = in.readString();
        quantity = in.readByte() == 0 ? null : in.readInt();
        unitPrice = in.readString();
        imageUrls = in.createStringArrayList(); // leer lista de imágenes
    }

    public static final Creator<CartItemParcelable> CREATOR = new Creator<CartItemParcelable>() {
        @Override
        public CartItemParcelable createFromParcel(Parcel in) {
            return new CartItemParcelable(in);
        }

        @Override
        public CartItemParcelable[] newArray(int size) {
            return new CartItemParcelable[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (id == null) dest.writeByte((byte) 0);
        else {
            dest.writeByte((byte) 1);
            dest.writeLong(id);
        }
        dest.writeString(productName);
        if (quantity == null) dest.writeByte((byte) 0);
        else {
            dest.writeByte((byte) 1);
            dest.writeInt(quantity);
        }
        dest.writeString(unitPrice);
        dest.writeStringList(imageUrls); // escribir lista de imágenes
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
