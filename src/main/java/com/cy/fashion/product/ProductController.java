package com.cy.fashion.product;

import com.cy.fashion.product.dto.*;
import com.cy.fashion.product.dto.request.ProductCreateRequest;
import com.cy.fashion.product.dto.request.ProductUpdateRequest;
import com.cy.fashion.product.dto.response.LowestPriceOutfitBrandResponse;
import com.cy.fashion.product.dto.response.LowestPriceOutfitResponse;
import com.cy.fashion.product.dto.response.PriceRangeByCategoryResponse;
import com.cy.fashion.product.dto.response.ProductListResponse;
import com.cy.fashion.utils.exception.InvalidInputException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


@Tag(name = "Products", description = "상품과 관련된 API")
@RestController
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    /**
     * 구현 1. 카테고리 별 최저가격 브랜드와 상품 가격, 총액을 조회하는 API
     * 모든 카테고리 별로 하나씩 product 가 골라져 있는 상태를 Outfit이라 정의한다.
     */
    @Operation(summary = "[구현1] 카테고리 별 최소 금액 상품과 총액")
    @Parameter(name = "filter", description = "원하는 브랜드와 카테고리만 필터링 하고 싶을 경우 (null 이거나 길이가 0인 리스트면 필터링 하지 않음을 의미)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = LowestPriceOutfitResponse.class))),
        @ApiResponse(responseCode = "404", description = "정의되지 않은 카테고리가 filter 로 요청됨", content = @Content(examples = {
            @ExampleObject(name = "ResourceNotFoundException",
                description = "filter의 category에 정의되지 않은 카테고리인 '신발'을 넣은 경우",
                value = "{\n" +
                    "    \"statusCode\": 404,\n" +
                    "    \"message\": \"Category [신발] is not a valid category name. Available Categories: 상의, 아우터, 바지, 스니커즈, 가방, 모자, 양말, 액세서리\",\n" +
                    "    \"details\": \"uri=/v1/products:lowestPriceOutfit\"\n" +
                    "}")
        }, mediaType = MediaType.APPLICATION_JSON_VALUE))})
    @GetMapping("/v1/products:lowestPriceOutfit")
    public LowestPriceOutfitResponse getLowestPriceOutfit(@RequestBody Optional<ProductFilterDTO> filter) {
        List<ProductDTO> products = productService.getLowestPriceOutfit(filter.orElse(null));

        return LowestPriceOutfitResponse.fromResult(products);
    }

    /**
     * 구현 2. 단일 브랜드로 구매 시 최저 가격에 판매하는 브랜드와 상품구성 및 가격 API
     */
    @Operation(summary = "[구현2] 단일 브랜드로 구매 시 최저 가격에 판매하는 브랜드와 상품구성 및 가격")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = LowestPriceOutfitBrandResponse.class))),
        @ApiResponse(responseCode = "204", description = "응답 값이 없음", content = @Content(examples = {
            @ExampleObject(name = "NoContentException",
                description = "모든 카테고리의 상품을 갖고 있는 브랜드가 존재하지 않음")
        }, mediaType = MediaType.APPLICATION_JSON_VALUE)),
        @ApiResponse(responseCode = "404", description = "정의되지 않은 카테고리가 filter 로 요청됨", content = @Content(examples = {
            @ExampleObject(name = "ResourceNotFoundException",
                description = "filter의 category에 정의되지 않은 카테고리인 '신발'을 넣은 경우",
                value = "{\n" +
                    "    \"statusCode\": 404,\n" +
                    "    \"message\": \"Category [신발] is not a valid category name. Available Categories: 상의, 아우터, 바지, 스니커즈, 가방, 모자, 양말, 액세서리\",\n" +
                    "    \"details\": \"uri=/v1/products:lowestPriceOutfitBrand\"\n" +
                    "}")
        }, mediaType = MediaType.APPLICATION_JSON_VALUE))})
    @GetMapping("/v1/products:lowestPriceOutfitBrand")
    public LowestPriceOutfitBrandResponse getLowestPriceOutfitBrand(@RequestBody Optional<ProductCategoryFilterDTO> filter) {
        List<ProductDTO> products = productService.getLowestPriceOutfitBrand(filter.orElse(null));

        return LowestPriceOutfitBrandResponse.fromResult(products);
    }

    /**
     * 구현 3. 카테고리 이름으로 최저, 최고 가격 브랜드와 상품 가격을 조회하는 API
     */
    @Operation(summary = "[구현3] 단일 브랜드로 구매 시 최저, 최고 가격에 판매하는 브랜드와 상품구성 및 가격")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = PriceRangeByCategoryResponse.class))),
        @ApiResponse(responseCode = "204", description = "응답 값이 없음", content = @Content(examples = {
            @ExampleObject(name = "NoContentException",
                description = "해당 카테고리에 상품이 존재하지 않음")
        }, mediaType = MediaType.APPLICATION_JSON_VALUE)),
        @ApiResponse(responseCode = "404", description = "정의되지 않은 카테고리가 요청됨", content = @Content(examples = {
            @ExampleObject(name = "ResourceNotFoundException",
                description = "RequestParam 의 category 에 정의되지 않은 카테고리인 '신발'을 넣은 경우",
                value = "{\n" +
                    "    \"statusCode\": 404,\n" +
                    "    \"message\": \"Category [신발] is not a valid category name. Available Categories: 상의, 아우터, 바지, 스니커즈, 가방, 모자, 양말, 액세서리\",\n" +
                    "    \"details\": \"uri=/v1/products:priceRangeByCategory\"\n" +
                    "}")
        }, mediaType = MediaType.APPLICATION_JSON_VALUE))})
    @GetMapping("/v1/products:priceRangeByCategory")
    public PriceRangeByCategoryResponse getPriceRangeByCategory(@RequestParam String category) {
        List<ProductDTO>[] products = productService.getPriceRangeByCategory(category);

        return new PriceRangeByCategoryResponse(products[0], products[1]);
    }

    /**
     * 구현 4. 상품 추가
     */
    @Operation(summary = "[구현4] 상품 추가")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = ProductDTO.class))),
        @ApiResponse(responseCode = "404", description = "정의되지 않은 브랜드에 대한 상품 생성이 요청됨", content = @Content(examples = {
            @ExampleObject(name = "ResourceNotFoundException",
                description = "존재하지 않는 brand id에 대해서 상품을 추가하려는 경우",
                value = "{\n" +
                    "    \"statusCode\": 404,\n" +
                    "    \"message\": \"Brand not found with id: some-uuid\",\n" +
                    "    \"details\": \"uri=/v1/products\"\n" +
                    "}")
        }, mediaType = MediaType.APPLICATION_JSON_VALUE)),
        @ApiResponse(responseCode = "404", description = "정의되지 않은 카테고리가 요청됨", content = @Content(examples = {
            @ExampleObject(name = "ResourceNotFoundException",
                description = "추가하려는 상품의 카테고리가 유효하지 않은 경우",
                value = "{\n" +
                    "    \"statusCode\": 404,\n" +
                    "    \"message\": \"Category [신발] is not a valid category name. Available Categories: 상의, 아우터, 바지, 스니커즈, 가방, 모자, 양말, 액세서리\",\n" +
                    "    \"details\": \"uri=/v1/products\"\n" +
                    "}")
        }, mediaType = MediaType.APPLICATION_JSON_VALUE))})
    @PostMapping("/v1/products")
    public ProductDTO create(@RequestBody @Valid ProductCreateRequest product) {
        return productService.createProduct(product);
    }

    /**
     * 구현 4. 상품 수정
     */
    @Operation(summary = "[구현4] 상품 수정")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = ProductDTO.class))),
        @ApiResponse(responseCode = "404", description = "정의되지 않은 상품에 대한 상품 수정이 요청됨", content = @Content(examples = {
            @ExampleObject(name = "ResourceNotFoundException",
                description = "상품 id가 존재하지 않은 경우",
                value = "{\n" +
                    "    \"statusCode\": 404,\n" +
                    "    \"message\": \"Product not found with id: some-uui\",\n" +
                    "    \"details\": \"uri=/v1/products/{product_id}\"\n" +
                    "}")
        }, mediaType = MediaType.APPLICATION_JSON_VALUE)),
        @ApiResponse(responseCode = "404", description = "정의되지 않은 브랜드에 대한 상품 수정이 요청됨", content = @Content(examples = {
            @ExampleObject(name = "ResourceNotFoundException",
                description = "존재하지 않는 brand id에 대해서 상품을 수정하려는 경우",
                value = "{\n" +
                    "    \"statusCode\": 404,\n" +
                    "    \"message\": \"Brand not found with id: some-uuid\",\n" +
                    "    \"details\": \"uri=/v1/products/{product_id}\"\n" +
                    "}")
        }, mediaType = MediaType.APPLICATION_JSON_VALUE)),
        @ApiResponse(responseCode = "404", description = "정의되지 않은 카테고리가 요청됨", content = @Content(examples = {
            @ExampleObject(name = "ResourceNotFoundException",
                description = "수정하려는 상품의 카테고리가 유효하지 않은 경우",
                value = "{\n" +
                    "    \"statusCode\": 404,\n" +
                    "    \"message\": \"Category [신발] is not a valid category name. Available Categories: 상의, 아우터, 바지, 스니커즈, 가방, 모자, 양말, 액세서리\",\n" +
                    "    \"details\": \"uri=/v1/products/{product_id}\"\n" +
                    "}")
        }, mediaType = MediaType.APPLICATION_JSON_VALUE)),
        @ApiResponse(responseCode = "404", description = "path의 product_id가 RequestBody의 product_id와 일치하지 않음", content = @Content(examples = {
            @ExampleObject(name = "InvalidInputException",
                description = "path의 product_id가 RequestBody의 product_id와 일치하지 않는 경우",
                value = "{\n" +
                    "    \"statusCode\": 404,\n" +
                    "    \"message\": \"Product id mismatch for update request\",\n" +
                    "    \"details\": \"uri=/v1/products/{product_id}\"\n" +
                    "}")
        }, mediaType = MediaType.APPLICATION_JSON_VALUE))})
    @PutMapping("/v1/products/{product_id}")
    public ProductDTO update(@PathVariable(name = "product_id") String productId, @RequestBody @Valid ProductUpdateRequest product) {
        if (!productId.equals(product.id)) {
            throw new InvalidInputException("Product id mismatch for update request.");
        }
        return productService.updateProduct(product);
    }

    /**
     * 구현 4. 상품 삭제
     */
    @Operation(summary = "[구현4] 상품 삭제")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200"),
        @ApiResponse(responseCode = "404", description = "정의되지 않은 상품에 대한 정보가 요청됨", content = @Content(examples = {
            @ExampleObject(name = "ResourceNotFoundException",
                description = "상품 id가 존재하지 않은 경우",
                value = "{\n" +
                    "    \"statusCode\": 404,\n" +
                    "    \"message\": \"Product not found with id: some-uui\",\n" +
                    "    \"details\": \"uri=/v1/products/{product_id}\"\n" +
                    "}")
        }, mediaType = MediaType.APPLICATION_JSON_VALUE))})
    @DeleteMapping("/v1/products/{product_id}")
    public void delete(@PathVariable(name = "product_id") String productId) {
        productService.deleteProduct(productId);
    }

    /**
     * 추가 구현. 전체 상품 목록
     */
    @Operation(summary = "전체 상품 목록")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = ProductListResponse.class)))})
    @GetMapping("/v1/products")
    public ProductListResponse list(@RequestBody Optional<ProductFilterDTO> filter) {
        List<ProductDTO> products = productService.getList(filter.orElse(null));

        return new ProductListResponse(products);
    }

    /**
     * 추가 구현. 특정 상품 정보
     */
    @Operation(summary = "특정 상품 정보")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200"),
        @ApiResponse(responseCode = "404", description = "정의되지 않은 상품에 대한 상품 삭제가 요청됨", content = @Content(examples = {
            @ExampleObject(name = "ResourceNotFoundException",
                description = "상품 id가 존재하지 않은 경우",
                value = "{\n" +
                    "    \"statusCode\": 404,\n" +
                    "    \"message\": \"Product not found with id: some-uui\",\n" +
                    "    \"details\": \"uri=/v1/products/{product_id}\"\n" +
                    "}")
        }, mediaType = MediaType.APPLICATION_JSON_VALUE))})
    @GetMapping("/v1/products/{product_id}")
    public ProductDTO get(@PathVariable(name = "product_id") String productId) {
        return productService.getProduct(productId);
    }
}
