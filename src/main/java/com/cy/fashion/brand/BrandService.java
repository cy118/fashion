package com.cy.fashion.brand;

import com.cy.fashion.brand.domain.Brand;
import com.cy.fashion.brand.dto.request.BrandCreateRequest;
import com.cy.fashion.brand.dto.request.BrandUpdateRequest;
import com.cy.fashion.brand.entity.BrandEntity;
import com.cy.fashion.product.ProductRepository;
import com.cy.fashion.product.entity.ProductEntity;
import com.cy.fashion.utils.exception.DuplicateResourceException;
import com.cy.fashion.utils.exception.ResourceNotFoundException;
import com.github.f4b6a3.uuid.codec.base.Base62Codec;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class BrandService {

    private final BrandRepository brandRepository;
    private final ProductRepository productRepository;

    public BrandService(BrandRepository brandRepository, ProductRepository productRepository) {
        this.brandRepository = brandRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public Brand createBrand(BrandCreateRequest brand) {
        // check if brand name exists
        if (brandRepository.findByBrandName(brand.brandName) != null) {
            throw new DuplicateResourceException(String.format("Brand name %s already exists", brand.brandName));
        }
        BrandEntity entity = brandRepository.save(BrandEntity.fromCreateRequest(brand));
        return Brand.fromEntity(entity);
    }

    @Transactional
    public Brand updateBrand(BrandUpdateRequest brand) {
        // check if brand exists
        if (brandRepository.findById(Base62Codec.INSTANCE.decode(brand.id)).isEmpty()) {
            throw new ResourceNotFoundException("Brand not found with id: " + brand.id);
        }
        // check if brand name exists
        BrandEntity duplicate = brandRepository.findByBrandName(brand.brandName);
        if (duplicate != null && !duplicate.getId().equals(Base62Codec.INSTANCE.decode(brand.id))) {
            throw new DuplicateResourceException(String.format("Brand name %s already exists", brand.brandName));
        }

        BrandEntity entity = brandRepository.save(BrandEntity.fromUpdateRequest(brand));
        return Brand.fromEntity(entity);
    }

    @Transactional
    public void deleteBrand(String brandId) {
        UUID brandUUID = Base62Codec.INSTANCE.decode(brandId);
        // check if brand exists
        if (brandRepository.findById(brandUUID).isEmpty()) {
            throw new ResourceNotFoundException("Brand not found with id: " + brandId);
        }
        // delete products of brands
        List<UUID> brandIds =  new ArrayList<>();
        brandIds.add(brandUUID);
        List<ProductEntity> products = productRepository.findAllByFilter(null, brandIds);
        productRepository.deleteAll(products);
        brandRepository.deleteById(brandUUID);
    }

    public List<Brand> getList() {
        List<BrandEntity> entities;
        entities = brandRepository.findAll();
        List<Brand> brands = new ArrayList<>();

        // change entity to domain
        for (BrandEntity brandEntity : entities) {
            brands.add(Brand.fromEntity(brandEntity));
        }
        return brands;
    }

    @Transactional
    public Brand getBrand(String brandId) {
        Optional<BrandEntity> entity;
        entity = brandRepository.findById(Base62Codec.INSTANCE.decode(brandId));
        if (entity.isEmpty()) {
            throw new ResourceNotFoundException("Brand not found with id: " + brandId);
        }
        return Brand.fromEntity(entity.get());
    }
}
