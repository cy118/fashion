package com.cy.fashion.product;


import com.cy.fashion.brand.dto.BrandDTO;
import com.cy.fashion.brand.dto.request.BrandCreateRequest;
import com.cy.fashion.brand.dto.response.BrandListResponse;
import com.cy.fashion.product.domain.ProductCategory;
import com.cy.fashion.product.dto.ProductCategoryFilterDTO;
import com.cy.fashion.product.dto.ProductDTO;
import com.cy.fashion.product.dto.ProductFilterDTO;
import com.cy.fashion.product.dto.request.ProductCreateRequest;
import com.cy.fashion.product.dto.request.ProductUpdateRequest;
import com.cy.fashion.product.dto.response.LowestPriceOutfitBrandResponse;
import com.cy.fashion.product.dto.response.LowestPriceOutfitResponse;
import com.cy.fashion.product.dto.response.PriceRangeByCategoryResponse;
import com.cy.fashion.product.dto.response.ProductListResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.f4b6a3.uuid.codec.base.Base62Codec;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.util.*;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ProductIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        // Configure MockMvc with UTF-8 encoding filter
        mockMvc = MockMvcBuilders
            .webAppContextSetup(context)
            .addFilter(new CharacterEncodingFilter("UTF-8", true))
            .alwaysDo(print())
            .build();
    }

    /**
     * 구현 1
     */
    @Test
    public void getLowestPriceOutfit_shouldReturnIntendedProductsWithIntendedBrand() throws Exception {
        // create 8 new brands and create products with price 99(expected as minimum price) for every category
        // each product belongs to each brand with brand name that matches category of the product
        List<BrandDTO> brands = create8NewBrandsAndLowestPriceProducts();

        // get lowest price outfit
        String outfitResponse = mockMvc.perform(get("/v1/products:lowestPriceOutfit"))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

        LowestPriceOutfitResponse lowestPriceOutfit = objectMapper.readValue(outfitResponse, LowestPriceOutfitResponse.class);
        assertEquals(lowestPriceOutfit.products.size(), brands.size());
        assertEquals(lowestPriceOutfit.totalPrice, 99 * brands.size());
        for (ProductDTO product : lowestPriceOutfit.products) {
            assertEquals(product.price, 99);
            // check if correct brand is selected
            BrandDTO brand = brands.stream()
                .filter(b -> ProductCategory.getByName(product.category).toString().equals(b.brandName.toUpperCase()))
                .findAny()
                .orElse(null);
            assertNotEquals(brand, null);
            assertEquals(product.brand.id, brand.id);
        }
    }

    /**
     * 구현 1 - 카테고리 필터링
     */
    @Test
    public void getLowestPriceOutfitWithCategoryFilter_shouldReturnIntendedProductsWithIntendedBrand() throws Exception {
        // create 8 new brands and create products with price 99(expected as minimum price) for every category
        // each product belongs to each brand with brand name that matches category of the product
        List<BrandDTO> brands = create8NewBrandsAndLowestPriceProducts();

        // set category filter
        List<String> shuffledCategories = new ArrayList<>();
        for (ProductCategory category : ProductCategory.values()) shuffledCategories.add(category.getCategoryName());
        Collections.shuffle(shuffledCategories);
        List<String> filteredCategory = shuffledCategories.subList(0, new Random().nextInt(1, Math.min(5, ProductCategory.values().length)));
        ProductFilterDTO filter = new ProductFilterDTO(null, filteredCategory);
        // get lowest price outfit
        String outfitResponse = mockMvc.perform(get("/v1/products:lowestPriceOutfit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(filter)))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

        LowestPriceOutfitResponse lowestPriceOutfit = objectMapper.readValue(outfitResponse, LowestPriceOutfitResponse.class);
        assertEquals(lowestPriceOutfit.products.size(), filteredCategory.size());
        assertEquals(lowestPriceOutfit.totalPrice, 99 * filteredCategory.size());
        for (ProductDTO product : lowestPriceOutfit.products) {
            assertEquals(product.price, 99);
            // check if correct brand is selected
            BrandDTO brand = brands.stream()
                .filter(b -> ProductCategory.getByName(product.category).toString().equals(b.brandName.toUpperCase()))
                .findAny()
                .orElse(null);
            assertNotEquals(brand, null);
            assertEquals(product.brand.id, brand.id);
            String category = filteredCategory.stream()
                .filter(b -> Objects.equals(b, product.category))
                .findAny()
                .orElse(null);
            assertNotEquals(category, null);
        }
    }

    /**
     * 구현 1 - 브랜드 필터링
     */
    @Test
    public void getLowestPriceOutfitWithBrandFilter_shouldReturnIntendedBrand() throws Exception {
        // set brand filter
        List<BrandDTO> brands = getBrandList();
        List<String> brandIds = new ArrayList<>();
        for (BrandDTO brand : brands) brandIds.add(brand.id);
        Collections.shuffle(brandIds);
        List<String> filteredBrand = brandIds.subList(0, new Random().nextInt(1, Math.min(brandIds.size(), 4)));
        ProductFilterDTO filter = new ProductFilterDTO(filteredBrand, null);
        // get lowest price outfit
        String outfitResponse = mockMvc.perform(get("/v1/products:lowestPriceOutfit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(filter)))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

        LowestPriceOutfitResponse lowestPriceOutfit = objectMapper.readValue(outfitResponse, LowestPriceOutfitResponse.class);
        assertEquals(lowestPriceOutfit.products.size(), ProductCategory.values().length);
        for (ProductDTO product : lowestPriceOutfit.products) {
            // check if product is selected with correct brand
            if (product.brand == null) continue;
            String brand = brandIds.stream()
                .filter(b -> Objects.equals(b, product.brand.id))
                .findAny()
                .orElse(null);
            assertNotEquals(brand, null);
        }
    }

    /**
     * 구현 1 : 정의되지 않은 카테고리에 대해 필터링 할 경우
     */
    @Test
    public void getLowestPriceOutfitWithCategoryFilterUndefined_shouldThrow404() throws Exception {
        List<String> filteredCategory = new ArrayList<>();
        filteredCategory.add("우산");
        ProductFilterDTO filter = new ProductFilterDTO(null, filteredCategory);
        // get lowest price outfit
        mockMvc.perform(get("/v1/products:lowestPriceOutfit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(filter)))
            .andExpect(status().isNotFound());
    }

    /**
     * 구현 2
     */
    @Test
    public void getLowestPriceOutfitBrand_shouldReturnIntendedProductsWithIntendedBrand() throws Exception {
        // create a brand and create products with price 99(expected as minimum price) for every category with created brand
        BrandDTO createdBrand = createANewBrandAndLowestPriceProducts();

        // get lowest price outfit brand
        String outfitResponse = mockMvc.perform(get("/v1/products:lowestPriceOutfitBrand"))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

        LowestPriceOutfitBrandResponse lowestPriceOutfit = objectMapper.readValue(outfitResponse, LowestPriceOutfitBrandResponse.class);
        assertEquals(lowestPriceOutfit.products.size(), ProductCategory.values().length);
        assertEquals(lowestPriceOutfit.totalPrice, 99 * ProductCategory.values().length);
        for (ProductDTO product : lowestPriceOutfit.products) {
            assertEquals(product.price, 99);
            assertEquals(product.brand.id, createdBrand.id);
        }
    }

    /**
     * 구현 2 - 카테고리 필터링
     */
    @Test
    public void getLowestPriceOutfitBrandWithCategoryFilter_shouldReturnIntendedProductsWithIntendedBrand() throws Exception {
        // create a brand and create products with price 99(expected as minimum price) for every category with created brand
        BrandDTO createdBrand = createANewBrandAndLowestPriceProducts();

        // set category filter
        List<String> shuffledCategories = new ArrayList<>();
        for (ProductCategory category : ProductCategory.values()) shuffledCategories.add(category.getCategoryName());
        Collections.shuffle(shuffledCategories);
        List<String> filteredCategory = shuffledCategories.subList(0, new Random().nextInt(1, Math.min(5, ProductCategory.values().length)));
        ProductCategoryFilterDTO filter = new ProductCategoryFilterDTO(filteredCategory);

        // get lowest price outfit brand
        String outfitResponse = mockMvc.perform(get("/v1/products:lowestPriceOutfitBrand")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(filter)))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

        LowestPriceOutfitBrandResponse lowestPriceOutfit = objectMapper.readValue(outfitResponse, LowestPriceOutfitBrandResponse.class);
        assertEquals(lowestPriceOutfit.products.size(), filteredCategory.size());
        assertEquals(lowestPriceOutfit.totalPrice, 99 * filteredCategory.size());
        for (ProductDTO product : lowestPriceOutfit.products) {
            assertEquals(product.price, 99);
            assertEquals(product.brand.id, createdBrand.id);
            String category = filteredCategory.stream()
                .filter(b -> Objects.equals(b, product.category))
                .findAny()
                .orElse(null);
            assertNotEquals(category, null);
        }
    }

    /**
     * 구현 2 - 정의되지 않은 카테고리에 대해 필터링할 경우
     */
    @Test
    public void getLowestPriceOutfitBrandWithCategoryFilterNotDefined_shouldThrow404() throws Exception {
        List<String> filteredCategory = new ArrayList<>();
        filteredCategory.add("우산");
        ProductCategoryFilterDTO filter = new ProductCategoryFilterDTO(filteredCategory);

        // get lowest price outfit brand
        mockMvc.perform(get("/v1/products:lowestPriceOutfitBrand")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(filter)))
            .andExpect(status().isNotFound());
    }

    /**
     * 구현 2 - 모든 카테고리의 상품을 갖고 있는 브랜드가 없을 경우
     */
    @Test
    public void getLowestPriceOutfitBrandWithCategoryFilterNotDefined_shouldThrow204() throws Exception {
        // delete all products in a random category
        String deletedCategory = ProductCategory.values()[new Random().nextInt(ProductCategory.values().length)].getCategoryName();
        ProductFilterDTO filter = new ProductFilterDTO(null, Collections.singletonList(deletedCategory));
        List<ProductDTO> productsToDelete = getProductListWithFilter(filter);
        for (ProductDTO product : productsToDelete) {
            mockMvc.perform(delete("/v1/products/" + product.id))
                .andExpect(status().isOk());
        }

        // get lowest price outfit brand
        mockMvc.perform(get("/v1/products:lowestPriceOutfitBrand"))
            .andExpect(status().isNoContent());
    }

    /**
     * 구현 3
     */
    @Test
    public void getPriceRangeByCategory_shouldReturnIntendedProductsWithIntendedBrand() throws Exception {
        // create three brand, two for lowest price and another for highest price
        // choose category and make two products in that category: price of one is 99 and the other is 9999999
        BrandDTO lowestBrand1 = createABrandWithName("The Lowest Price Brand 1");
        BrandDTO lowestBrand2 = createABrandWithName("The Lowest Price Brand 2");
        BrandDTO highestBrand = createABrandWithName("The Highest Price Brand");
        String category = ProductCategory.values()[new Random().nextInt(ProductCategory.values().length)].getCategoryName();
        createProductWithBrandCategoryPrice(lowestBrand1.id, category, 99);
        createProductWithBrandCategoryPrice(lowestBrand2.id, category, 99);
        createProductWithBrandCategoryPrice(highestBrand.id, category, 9999999);

        // get the lowest priced products and highest priced products
        String response = mockMvc.perform(get("/v1/products:priceRangeByCategory?category=" + category))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

        PriceRangeByCategoryResponse priceRange = objectMapper.readValue(response, PriceRangeByCategoryResponse.class);
        assertEquals(priceRange.lowest.size(), 2);
        assertEquals(priceRange.highest.size(), 1);
        assertEquals(priceRange.highest.getFirst().price, 9999999);
        assertEquals(priceRange.highest.getFirst().brand.id, highestBrand.id);
        assertEquals(priceRange.highest.getFirst().category, category);
        for (ProductDTO product : priceRange.lowest) {
            assertEquals(product.price, 99);
            assertEquals(product.category, category);
            assertTrue(Objects.equals(lowestBrand1.id, product.brand.id) || Objects.equals(lowestBrand2.id, product.brand.id));
        }
        assertNotEquals(priceRange.lowest.get(0).brand.id, priceRange.lowest.get(1).brand.id);
    }

    /**
     * 구현 3 - 정의되지 않은 카테고리에 대해
     */
    @Test
    public void getPriceRangeByCategoryNotDefined_shouldReturn404() throws Exception {
        // get the lowest priced products and highest priced products
        mockMvc.perform(get("/v1/products:priceRangeByCategory?category=" + "우산"))
            .andExpect(status().isNotFound());
    }

    /**
     * 구현 3 - 해당 카테고리에 상품이 존재하지 않을 때
     */
    @Test
    public void getPriceRangeByCategoryWithNoProduct_shouldReturn204() throws Exception {
        String category = ProductCategory.values()[new Random().nextInt(ProductCategory.values().length)].getCategoryName();

        // delete all products from category
        ProductFilterDTO filter = new ProductFilterDTO(null, Collections.singletonList(category));
        List<ProductDTO> productsToDelete = getProductListWithFilter(filter);
        for (ProductDTO product : productsToDelete) {
            mockMvc.perform(delete("/v1/products/" + product.id))
                .andExpect(status().isOk());
        }

        // get the lowest priced products and highest priced products
        mockMvc.perform(get("/v1/products:priceRangeByCategory?category=" + category))
            .andExpect(status().isNoContent());
    }

    /**
     * 구현 4 - 상품 생성
     */
    @Test
    public void createProduct_shouldBeCreated() throws Exception {
        // get initial product list
        List<ProductDTO> initialProducts = getProductList();
        // get brand list
        List<BrandDTO> brands = getBrandList();

        // create product
        ProductCreateRequest productCreateRequest = new ProductCreateRequest(
            brands.get(new Random().nextInt(brands.size())).id,
            ProductCategory.values()[new Random().nextInt(ProductCategory.values().length)].getCategoryName(),
            new Random().nextInt(10, 999) * 100);
        String createResponseContent = mockMvc.perform(post("/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productCreateRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.brand.id", is(productCreateRequest.brandId)))
            .andExpect(jsonPath("$.category", is(productCreateRequest.category)))
            .andExpect(jsonPath("$.price", is(productCreateRequest.price)))
            .andReturn()
            .getResponse()
            .getContentAsString();

        ProductDTO createdProduct = objectMapper.readValue(createResponseContent, ProductDTO.class);

        // get changed product list
        List<ProductDTO> changedProducts = getProductList();

        // compare initial list with changed list
        assertEquals(initialProducts.size() + 1, changedProducts.size());
        assertEquals(createdProduct.id, changedProducts.getLast().id);
        assertEquals(createdProduct.brand.id, changedProducts.getLast().brand.id);
        assertEquals(createdProduct.price, changedProducts.getLast().price);
        assertEquals(createdProduct.category, changedProducts.getLast().category);
    }

    /**
     * 구현 4 - 상품 생성 : 존재하지 않는 브랜드로 생성
     */
    @Test
    public void createProductWithNotExistingBrand_shouldThrow404() throws Exception {
        // create product with randomly generated not existing brand id
        ProductCreateRequest productCreateRequest = new ProductCreateRequest(
            Base62Codec.INSTANCE.encode(UUID.randomUUID()),
            ProductCategory.values()[new Random().nextInt(ProductCategory.values().length)].getCategoryName(),
            new Random().nextInt(10, 999) * 100);
        mockMvc.perform(post("/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productCreateRequest)))
            .andExpect(status().isNotFound());
    }

    /**
     * 구현 4 - 상품 생성 : 정의되지 않은 카테고리로 생성
     */
    @Test
    public void createProductWithNotDefinedCategory_shouldThrow404() throws Exception {
        // get brand list
        List<BrandDTO> brands = getBrandList();

        // create product with randomly generated not existing brand id
        ProductCreateRequest productCreateRequest = new ProductCreateRequest(
            brands.get(new Random().nextInt(brands.size())).id,
            "우산",
            new Random().nextInt(10, 999) * 100);
        mockMvc.perform(post("/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productCreateRequest)))
            .andExpect(status().isNotFound());
    }

    /**
     * 구현 4 - 상품 수정
     */
    @Test
    public void updateProduct_shouldBeUpdated() throws Exception {
        // get initial product list
        List<ProductDTO> initialProducts = getProductList();
        // get brand list
        List<BrandDTO> brands = getBrandList();

        // update product
        ProductDTO productToUpdate = initialProducts.get(new Random().nextInt(initialProducts.size()));
        ProductUpdateRequest productUpdateRequest = new ProductUpdateRequest(
            productToUpdate.id,
            brands.get(new Random().nextInt(brands.size())).id,
            ProductCategory.values()[new Random().nextInt(ProductCategory.values().length)].getCategoryName(),
            new Random().nextInt(10, 999) * 100);
        mockMvc.perform(put("/v1/products/" + productToUpdate.id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productUpdateRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.brand.id", is(productUpdateRequest.brandId)))
            .andExpect(jsonPath("$.category", is(productUpdateRequest.category)))
            .andExpect(jsonPath("$.price", is(productUpdateRequest.price)));

        // check product is updated
        ProductDTO product = getProduct(productToUpdate.id);
        assertEquals(product.brand.id, productUpdateRequest.brandId);
        assertEquals(product.price, productUpdateRequest.price);
        assertEquals(product.category, productUpdateRequest.category);
    }

    /**
     * 구현 4 - 상품 수정 : 존재하지 않는 상품에 대해
     */
    @Test
    public void updateNotExistingProduct_shouldThrow404() throws Exception {
        // get brand list
        List<BrandDTO> brands = getBrandList();

        // update product with randomly generated not existing id
        ProductUpdateRequest productUpdateRequest = new ProductUpdateRequest(
            Base62Codec.INSTANCE.encode(UUID.randomUUID()),
            brands.get(new Random().nextInt(brands.size())).id,
            ProductCategory.values()[new Random().nextInt(ProductCategory.values().length)].getCategoryName(),
            new Random().nextInt(10, 999) * 100);
        mockMvc.perform(put("/v1/products/" + productUpdateRequest.id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productUpdateRequest)))
            .andExpect(status().isNotFound());
    }

    /**
     * 구현 4 - 상품 수정 : 존재하지 않는 브랜드로의 수정
     */
    @Test
    public void updateProductWithNotExistingBrand_shouldThrow404() throws Exception {
        // get initial product list
        List<ProductDTO> initialProducts = getProductList();

        // update product with randomly generated not existing brand id
        ProductDTO productToUpdate = initialProducts.get(new Random().nextInt(initialProducts.size()));
        ProductUpdateRequest productUpdateRequest = new ProductUpdateRequest(
            productToUpdate.id,
            Base62Codec.INSTANCE.encode(UUID.randomUUID()),
            ProductCategory.values()[new Random().nextInt(ProductCategory.values().length)].getCategoryName(),
            new Random().nextInt(10, 999) * 100);
        mockMvc.perform(put("/v1/products/" + productToUpdate.id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productUpdateRequest)))
            .andExpect(status().isNotFound());
    }

    /**
     * 구현 4 - 상품 수정 : 정의되지 않은 카테고리로의 수정
     */
    @Test
    public void updateProductWithNotExistingCategory_shouldThrow404() throws Exception {
        // get initial product list
        List<ProductDTO> initialProducts = getProductList();
        // get brand list
        List<BrandDTO> brands = getBrandList();

        // update product with randomly generated not existing brand id
        ProductDTO productToUpdate = initialProducts.get(new Random().nextInt(initialProducts.size()));
        ProductUpdateRequest productUpdateRequest = new ProductUpdateRequest(
            productToUpdate.id,
            brands.get(new Random().nextInt(brands.size())).id,
            "우산",
            new Random().nextInt(10, 999) * 100);
        mockMvc.perform(put("/v1/products/" + productToUpdate.id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productUpdateRequest)))
            .andExpect(status().isNotFound());
    }

    /**
     * 구현 4 - 상품 수정 : path parameter의 상품 id와 request body의 상품 id가 일치하지 않는 요청
     */
    @Test
    public void updateProductMismatch_shouldThrow404() throws Exception {
        // get initial product list
        List<ProductDTO> initialProducts = getProductList();
        // get brand list
        List<BrandDTO> brands = getBrandList();

        // request updating product while product id in path parameter doesn't match with product id in request body
        ProductDTO productToUpdate = initialProducts.get(0);
        ProductUpdateRequest productUpdateRequest = new ProductUpdateRequest(
            productToUpdate.id,
            brands.get(new Random().nextInt(brands.size())).id,
            ProductCategory.values()[new Random().nextInt(ProductCategory.values().length)].getCategoryName(),
            new Random().nextInt(10, 999) * 100);
        mockMvc.perform(put("/v1/products/" + initialProducts.get(1))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productUpdateRequest)))
            .andExpect(status().isNotFound());
    }

    /**
     * 구현 4 - 상품 삭제
     */
    @Test
    public void deleteProduct_shouldBeDeleted() throws Exception {
        // get initial product list
        List<ProductDTO> initialProducts = getProductList();

        // delete product
        ProductDTO productToDelete = initialProducts.get(new Random().nextInt(initialProducts.size()));
        mockMvc.perform(delete("/v1/products/" + productToDelete.id))
            .andExpect(status().isOk());

        // check product is deleted
        mockMvc.perform(get("/v1/products/" + productToDelete.id))
            .andExpect(status().isNotFound());
    }

    /**
     * 구현 4 - 상품 삭제 : 존재하지 않는 상품에 대해
     */
    @Test
    public void deleteNotExistingProduct_shouldThrow404() throws Exception {
        // delete product with randomly generated not existing id
        mockMvc.perform(delete("/v1/products/" + Base62Codec.INSTANCE.encode(UUID.randomUUID())))
            .andExpect(status().isNotFound());
    }

    List<ProductDTO> getProductList() throws Exception {
        String productListResponse = mockMvc.perform(get("/v1/products"))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
        return objectMapper.readValue(productListResponse, new TypeReference<ProductListResponse>() {}).products;
    }

    List<ProductDTO> getProductListWithFilter(ProductFilterDTO filter) throws Exception {
        String productListResponse = mockMvc.perform(get("/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(filter)))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
        return objectMapper.readValue(productListResponse, new TypeReference<ProductListResponse>() {}).products;
    }

    List<BrandDTO> getBrandList() throws Exception {
        String brandListResponse = mockMvc.perform(get("/v1/brands"))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
        return objectMapper.readValue(brandListResponse, new TypeReference<BrandListResponse>() {}).brands;
    }

    ProductDTO getProduct(String id) throws Exception {
        String productResponse = mockMvc.perform(get("/v1/products/" + id))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
        return objectMapper.readValue(productResponse, new TypeReference<>() {});
    }

    BrandDTO createABrandWithName(String brandName) throws Exception {
        BrandCreateRequest brandCreateRequest = new BrandCreateRequest();
        brandCreateRequest.brandName = brandName;
        String brandResponseContent = mockMvc.perform(post("/v1/brands")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(brandCreateRequest)))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
        return objectMapper.readValue(brandResponseContent, BrandDTO.class);
    }

    ProductDTO createProductWithBrandCategoryPrice(String brandId, String category, int price) throws Exception {
        ProductCreateRequest productCreateRequest = new ProductCreateRequest(brandId, category, price);
        String createResponseContent = mockMvc.perform(post("/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productCreateRequest)))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

        return objectMapper.readValue(createResponseContent, ProductDTO.class);
    }

    List<BrandDTO> create8NewBrandsAndLowestPriceProducts() throws Exception {
        String[] newBrands = {"top", "outer", "pants", "sneakers", "bag", "hat", "socks", "accessories"};
        List<BrandDTO> brands = new ArrayList<>();
        for (String brand : newBrands) {
            BrandDTO brandDTO = createABrandWithName(brand);
            brands.add(brandDTO);

            createProductWithBrandCategoryPrice(
                brandDTO.id,
                ProductCategory.valueOf(brandDTO.brandName.toUpperCase()).getCategoryName(),
                99);
        }
        return brands;
    }

    BrandDTO createANewBrandAndLowestPriceProducts() throws Exception {
        BrandDTO createdBrand = createABrandWithName("The Lowest Price Brand");

        for (ProductCategory category : ProductCategory.values()) {
            createProductWithBrandCategoryPrice(createdBrand.id, category.getCategoryName(), 99);
        }
        return createdBrand;
    }
}
