package com.marlodev.app_android.data.network.model.order;
import android.os.Parcel;
import android.os.Parcelable;

import com.marlodev.app_android.data.network.model.product.ProductResponse;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class CartItemResponse implements Parcelable {
    private Long id;
    private ProductResponse product;
//    private ProductVariantResponse variant;
//    private List<ExtraResponse> extras;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;

    protected CartItemResponse(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readLong();
        }
        product = in.readParcelable(ProductResponse.class.getClassLoader());
        if (in.readByte() == 0) {
            quantity = null;
        } else {
            quantity = in.readInt();
        }
        unitPrice = new BigDecimal(in.readString());
        totalPrice = new BigDecimal(in.readString());
    }

    public static final Creator<CartItemResponse> CREATOR = new Creator<CartItemResponse>() {
        @Override
        public CartItemResponse createFromParcel(Parcel in) {
            return new CartItemResponse(in);
        }

        @Override
        public CartItemResponse[] newArray(int size) {
            return new CartItemResponse[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(id);
        }
//        dest.writeParcelable(product, flags);
        if (quantity == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(quantity);
        }
        dest.writeString(unitPrice.toString());
        dest.writeString(totalPrice.toString());
    }
}