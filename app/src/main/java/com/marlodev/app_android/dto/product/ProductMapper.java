package com.marlodev.app_android.dto.product;

        import com.marlodev.app_android.domain.*;
        import com.marlodev.app_android.dto.ProductVariant.ProductVariantMapper;
        import com.marlodev.app_android.dto.extra.ExtraMapper;
        import com.marlodev.app_android.dto.tag.TagMapper;

        import java.util.ArrayList;
        import java.util.List;

public class ProductMapper {

    // -------------------------------
    // Product
    // -------------------------------
    public static Product fromResponse(ProductResponse dto) {
        Product p = new Product();
        p.setId(dto.getId());
        p.setName(dto.getName());
        p.setDescription(dto.getDescription());
        p.setPrice(dto.getPrice());
        p.setOldPrice(dto.getOldPrice());
        p.setDiscountPercent(dto.getDiscountPercent());
        p.setIsNew(dto.getIsNew());
        p.setRating(dto.getRating());
        p.setReviewsCount(dto.getReviewsCount());
        p.setStoreId(dto.getStoreId());
        p.setCategoryId(dto.getCategoryId());
        p.setImageUrls(dto.getImageUrls());
        p.setImagePublicIds(dto.getImagePublicIds());
        p.setVariants(ProductVariantMapper.fromResponseList(dto.getVariants()));
        p.setExtras(ExtraMapper.fromResponseList(dto.getExtras()));
        p.setTags(TagMapper.fromResponseList(dto.getTags()));
        return p;
    }

    public static List<Product> fromResponseList(List<ProductResponse> list) {
        List<Product> result = new ArrayList<>();
        for (ProductResponse r : list) result.add(fromResponse(r));
        return result;
    }

    public static ProductRequest toRequest(Product p) {
        ProductRequest req = new ProductRequest();
        req.setName(p.getName());
        req.setDescription(p.getDescription());
        req.setPrice(p.getPrice());
        req.setOldPrice(p.getOldPrice());
        req.setDiscountPercent(p.getDiscountPercent());
        req.setIsNew(p.getIsNew());
        req.setRating(p.getRating());
        req.setReviewsCount(p.getReviewsCount());
        req.setCategoryId(p.getCategoryId());
        req.setStoreId(p.getStoreId());
        req.setImageUrls(p.getImageUrls());
        req.setVariants(ProductVariantMapper.toRequestList(p.getVariants()));
        req.setExtras(ExtraMapper.toRequestList(p.getExtras()));
        req.setTags(TagMapper.toRequestList(p.getTags()));
        return req;
    }

    public static List<ProductRequest> toRequestList(List<Product> list) {
        List<ProductRequest> result = new ArrayList<>();
        for (Product p : list) result.add(toRequest(p));
        return result;
    }
}