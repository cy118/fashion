package com.cy.fashion.product;

import com.cy.fashion.brand.BrandRepository;
import com.cy.fashion.brand.entity.BrandEntity;
import com.cy.fashion.product.domain.Product;
import com.cy.fashion.product.domain.ProductCategory;
import com.cy.fashion.product.dto.ProductCategoryFilterDTO;
import com.cy.fashion.product.dto.ProductDTO;
import com.cy.fashion.product.dto.ProductFilterDTO;
import com.cy.fashion.product.dto.request.ProductCreateRequest;
import com.cy.fashion.product.dto.request.ProductUpdateRequest;
import com.cy.fashion.product.entity.ProductEntity;
import com.cy.fashion.utils.common.ProductCategoryValidator;
import com.cy.fashion.utils.exception.NoContentException;
import com.cy.fashion.utils.exception.ResourceNotFoundException;
import com.github.f4b6a3.uuid.codec.base.Base62Codec;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final BrandRepository brandRepository;

    public ProductService(ProductRepository productRepository, BrandRepository brandRepository) {
        this.productRepository = productRepository;
        this.brandRepository = brandRepository;
    }

    public List<ProductDTO> getLowestPriceOutfit(ProductFilterDTO filter) {
        List<ProductCategory> categories;
        List<UUID> brandIds;
        // set category filter
        if (filter == null) {
            categories = null;
        } else {
            categories = ProductCategoryValidator.toCategories(filter.categories);
        }
        // set brand Id filter
        if (filter == null || filter.brandIds == null || filter.brandIds.isEmpty()) {
            brandIds = null;
        } else {
            brandIds = new ArrayList<>();
            for (String brandId : filter.brandIds) {
                brandIds.add(Base62Codec.INSTANCE.decode(brandId));
            }
        }
        List<ProductEntity> entities = productRepository.findLowestPricedProductsByCategories(categories, brandIds);
        List<ProductDTO> products = new ArrayList<>();

        if (categories == null) {
            categories = new ArrayList<>(Arrays.asList(ProductCategory.values()));
        }
        // change entity to domain
        for (ProductEntity productEntity : entities) {
            products.add(ProductDTO.fromDomain(Product.fromEntity(productEntity)));
            categories.remove(productEntity.getCategory());
        }

        // set category products to null if no products exists in a category
        for (ProductCategory category : categories) {
            products.add(new ProductDTO(null, null, category.getCategoryName(), 0));
        }

        return products;
    }

    public List<ProductDTO> getLowestPriceOutfitBrand(ProductCategoryFilterDTO filter) {
        List<ProductCategory> categories;
        // set category filter
        if (filter == null) {
            categories = null;
        } else {
            categories = ProductCategoryValidator.toCategories(filter.categories);
        }

        List<ProductEntity> entities = productRepository.findLowestPricedProductsByBrand(categories, categories != null ? categories.size() : ProductCategory.values().length);

        if (entities.isEmpty()) {
            throw new NoContentException("No brand exists that has products in all categories");
        }

        List<ProductDTO> products = new ArrayList<>();

        // change entity to domain
        for (ProductEntity productEntity : entities) {
            products.add(ProductDTO.fromDomain(Product.fromEntity(productEntity)));
        }
        return products;
    }

    public List<ProductDTO>[] getPriceRangeByCategory(String category) {
        ProductCategory productCategory = ProductCategoryValidator.toCategory(category);

        List<ProductEntity> entities = productRepository.findPriceRangeByCategory(productCategory);

        if (entities.isEmpty()) {
            throw new NoContentException("No product exists in requested category");
        }
        List<ProductDTO> lowest = new ArrayList<>();
        List<ProductDTO> highest = new ArrayList<>();

        int lowestPrice = Integer.MAX_VALUE;
        for (ProductEntity product : entities) {
            if (product.getPrice() < lowestPrice) {
                lowestPrice = product.getPrice();
            }
        }
        for (ProductEntity productEntity : entities) {
            if (lowestPrice < productEntity.getPrice()) {
                highest.add(ProductDTO.fromDomain(Product.fromEntity(productEntity)));
            } else {
                lowest.add(ProductDTO.fromDomain(Product.fromEntity(productEntity)));
            }
        }
        if (lowest.size() == 2 && highest.size() == 0) {
            lowest.removeLast();
            highest.add(lowest.getFirst());
        }

        return new List[]{lowest, highest};
    }

    @Transactional
    public ProductDTO createProduct(ProductCreateRequest product) {
        ProductCategory category = ProductCategoryValidator.toCategory(product.category);

        Optional<BrandEntity> brandEntity;
        // check if brand exists
        brandEntity = brandRepository.findById(Base62Codec.INSTANCE.decode(product.brandId));
        if (brandEntity.isEmpty()) {
            throw new ResourceNotFoundException("Brand not found with id: " + product.brandId);
        }

        ProductEntity entity = productRepository.save(ProductEntity.fromCreateRequest(product, category));
        return ProductDTO.fromDomain(Product.fromEntity(entity, brandEntity.get()));
    }

    @Transactional
    public ProductDTO updateProduct(ProductUpdateRequest product) {
        ProductCategory category = ProductCategoryValidator.toCategory(product.category);
        // check if product exists
        if (productRepository.findById(Base62Codec.INSTANCE.decode(product.id)).isEmpty()) {
            throw new ResourceNotFoundException("Product not found with id: " + product.id);
        }

        Optional<BrandEntity> brandEntity;
        // check if brand exists
        brandEntity = brandRepository.findById(Base62Codec.INSTANCE.decode(product.brandId));
        if (brandEntity.isEmpty()) {
            throw new ResourceNotFoundException("Brand not found with id: " + product.brandId);
        }

        ProductEntity entity = productRepository.save(ProductEntity.fromUpdateRequest(product, category));
        return ProductDTO.fromDomain(Product.fromEntity(entity, brandEntity.get()));
    }

    @Transactional
    public void deleteProduct(String productId) {
        // check if product exists
        if (productRepository.findById(Base62Codec.INSTANCE.decode(productId)).isEmpty()) {
            throw new ResourceNotFoundException("Product not found with id: " + productId);
        }
        productRepository.deleteById(Base62Codec.INSTANCE.decode(productId));
    }

    public List<ProductDTO> getList(ProductFilterDTO filter) {
        List<ProductEntity> entities;
        if (filter == null) {
            entities = productRepository.findAllWithBrand();
        } else {
            // set category filter
            List<ProductCategory> categories = ProductCategoryValidator.toCategories(filter.categories);
            List<UUID> brandIds = null;
            // set brand filter
            if (filter.brandIds != null && !filter.brandIds.isEmpty()) {
                brandIds = new ArrayList<>();
                for (String brandId : filter.brandIds) {
                    brandIds.add(Base62Codec.INSTANCE.decode(brandId));
                }
            }

            entities = productRepository.findAllByFilter(categories, brandIds);
        }
        List<ProductDTO> products = new ArrayList<>();

        // change entity to domain
        for (ProductEntity productEntity : entities) {
            products.add(ProductDTO.fromDomain(Product.fromEntity(productEntity)));
        }
        return products;
    }

    public ProductDTO getProduct(String productId) {
        Optional<ProductEntity> entity;
        entity = productRepository.findById(Base62Codec.INSTANCE.decode(productId));
        if (entity.isEmpty()) {
            throw new ResourceNotFoundException("Product not found with id: " + productId);
        }

        BrandEntity brandEntity = brandRepository.getReferenceById(entity.get().getBrandId());
        return ProductDTO.fromDomain(Product.fromEntity(entity.get(), brandEntity));
    }
}
