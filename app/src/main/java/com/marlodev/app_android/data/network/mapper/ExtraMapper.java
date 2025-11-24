package com.marlodev.app_android.data.network.mapper;
import com.marlodev.app_android.data.network.model.extra.ExtraRequest;
import com.marlodev.app_android.data.network.model.extra.ExtraResponse;
import com.marlodev.app_android.domain.model.Extra;
import java.util.ArrayList;
import java.util.List;

public class ExtraMapper {

    // Response -> Domain
    public static Extra fromResponse(ExtraResponse dto) {
        return Extra.builder()
                .id(dto.getId())
                .name(dto.getName())
                .price(dto.getPrice())
                .build();
    }

    public static List<Extra> fromResponseList(List<ExtraResponse> list) {
        List<Extra> result = new ArrayList<>();
        if (list != null) {
            for (ExtraResponse dto : list) {
                result.add(fromResponse(dto));
            }
        }
        return result;
    }

    // Domain -> Request
    public static ExtraRequest toRequest(Extra extra) {
        return ExtraRequest.builder()
                .name(extra.getName())
                .price(extra.getPrice())
                .build();
    }

    public static List<ExtraRequest> toRequestList(List<Extra> list) {
        List<ExtraRequest> result = new ArrayList<>();
        if (list != null) {
            for (Extra e : list) {
                result.add(toRequest(e));
            }
        }
        return result;
    }
}