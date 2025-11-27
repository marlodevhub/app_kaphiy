package com.marlodev.app_android.utils;

import android.widget.TextView;

import androidx.databinding.BindingAdapter;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

public class BindingAdapters {

    @BindingAdapter("formattedPrice")
    public static void setFormattedPrice(TextView textView, BigDecimal price) {
        if (price == null) {
            textView.setText("S/. 0.00");
        } else {
            NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("es", "PE"));
            textView.setText(format.format(price));
        }
    }
}