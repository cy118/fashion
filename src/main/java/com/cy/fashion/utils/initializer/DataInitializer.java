package com.cy.fashion.utils.initializer;

import com.cy.fashion.brand.BrandRepository;
import com.cy.fashion.brand.entity.BrandEntity;
import com.cy.fashion.product.ProductRepository;
import com.cy.fashion.product.domain.ProductCategory;
import com.cy.fashion.product.entity.ProductEntity;
import com.github.f4b6a3.uuid.codec.base.Base62Codec;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class DataInitializer {

    private final ProductRepository productRepository;
    private final BrandRepository brandRepository;

    @Autowired
    public DataInitializer(ProductRepository productRepository, BrandRepository brandRepository) {
        this.productRepository = productRepository;
        this.brandRepository = brandRepository;
    }

    @PostConstruct
    public void init() {
        if (productRepository.count() != 0 || brandRepository.count() != 0) return;

        UUID[] brandIds = new UUID[] {
            Base62Codec.INSTANCE.decode("000000000000000000000A"),
            Base62Codec.INSTANCE.decode("000000000000000000000B"),
            Base62Codec.INSTANCE.decode("000000000000000000000C"),
            Base62Codec.INSTANCE.decode("000000000000000000000D"),
            Base62Codec.INSTANCE.decode("000000000000000000000E"),
            Base62Codec.INSTANCE.decode("000000000000000000000F"),
            Base62Codec.INSTANCE.decode("000000000000000000000G"),
            Base62Codec.INSTANCE.decode("000000000000000000000H"),
            Base62Codec.INSTANCE.decode("000000000000000000000I"),
        };
        int[] prices = new int[] {
            11200, 5500, 4200, 9000, 2000, 1700, 1800, 2300,
            10500, 5900, 3800, 9100, 2100, 2000, 2000, 2200,
            10000, 6200, 3300, 9200, 2200, 1900, 2200, 2100,
            10100, 5100, 3000, 9500, 2500, 1500, 2400, 2000,
            10700, 5000, 3800, 9900, 2300, 1800, 2100, 2100,
            11200, 7200, 4000, 9300, 2100, 1600, 2300, 1900,
            10500, 5800, 3900, 9000, 2200, 1700, 2100, 2000,
            10800, 6300, 3100, 9700, 2100, 1600, 2000, 2000,
            11400, 6700, 3200, 9500, 2400, 1700, 1700, 2400
        };
        int priceIdx = 0;
        for (int brandIdx = 0; brandIdx <= 'I'-'A'; brandIdx++) {
            brandRepository.save(new BrandEntity(brandIds[brandIdx], (char)('A'+brandIdx) + ""));
            for (ProductCategory category : ProductCategory.values()) {
                if (priceIdx == 0) {
                    productRepository.save(new ProductEntity(Base62Codec.INSTANCE.decode("000000000000000000TEST"), brandIds[brandIdx], category, prices[priceIdx++]));
                } else {
                    productRepository.save(new ProductEntity(UUID.randomUUID(), brandIds[brandIdx], category, prices[priceIdx++]));
                }
            }
        }
    }
}