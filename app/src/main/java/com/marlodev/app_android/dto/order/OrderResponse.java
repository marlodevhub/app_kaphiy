package com.marlodev.app_android.dto.order;

import android.os.Parcel;
import android.os.Parcelable;

import com.marlodev.app_android.model.order.CartItemResponse;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse implements Parcelable {

    private Long id;
    private Integer userId;
    private String username;
    private String status;
    private String message;
    private BigDecimal totalAmount;
    private String createdAt;
    private String updatedAt;
    private List<CartItemResponse> items;

    protected OrderResponse(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readLong();
        }
        if (in.readByte() == 0) {
            userId = null;
        } else {
            userId = in.readInt();
        }
        username = in.readString();
        status = in.readString();
        message = in.readString();
        totalAmount = new BigDecimal(in.readString());
        createdAt = in.readString();
        updatedAt = in.readString();
        items = in.createTypedArrayList(CartItemResponse.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(id);
        }
        if (userId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(userId);
        }
        dest.writeString(username);
        dest.writeString(status);
        dest.writeString(message);
        dest.writeString(totalAmount.toString());
        dest.writeString(createdAt);
        dest.writeString(updatedAt);
        dest.writeTypedList(items);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<OrderResponse> CREATOR = new Creator<OrderResponse>() {
        @Override
        public OrderResponse createFromParcel(Parcel in) {
            return new OrderResponse(in);
        }

        @Override
        public OrderResponse[] newArray(int size) {
            return new OrderResponse[size];
        }
    };
}
