package com.cy.fashion.brand;


import com.cy.fashion.brand.domain.Brand;
import com.cy.fashion.brand.dto.BrandDTO;
import com.cy.fashion.brand.dto.request.BrandCreateRequest;
import com.cy.fashion.brand.dto.request.BrandUpdateRequest;
import com.cy.fashion.brand.dto.response.BrandListResponse;
import com.cy.fashion.utils.exception.InvalidInputException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Brands", description = "브랜드와 관련된 API")
@RestController
public class BrandController {

    private final BrandService brandService;

    public BrandController(BrandService brandService) {
        this.brandService = brandService;
    }

    /**
     * 구현 4. 브랜드 추가
     */
    @Operation(summary = "[구현4] 브랜드 추가")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = BrandDTO.class))),
        @ApiResponse(responseCode = "409", description = "이미 존재하는 이름의 브랜드를 추가하려 할 때", content = @Content(examples = {
            @ExampleObject(name = "DuplicateResourceException",
                description = "A 브랜드가 이미 존재하는데 A브랜드를 추가하려는 경우",
                value = "{\n" +
                    "    \"statusCode\": 409,\n" +
                    "    \"message\": \"Brand name A already exists\",\n" +
                    "    \"details\": \"uri=/v1/brands\"\n" +
                    "}")
        }, mediaType = MediaType.APPLICATION_JSON_VALUE))})
    @PostMapping("/v1/brands")
    public BrandDTO create(@RequestBody BrandCreateRequest brand) {
        Brand created = brandService.createBrand(brand);

        return BrandDTO.fromDomain(created);
    }

    /**
     * 구현 4. 브랜드 수정
     */
    @Operation(summary = "[구현4] 브랜드 수정")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = BrandDTO.class))),
        @ApiResponse(responseCode = "404", description = "정의되지 않은 브랜드에 대한 브랜드 수정이 요청됨", content = @Content(examples = {
            @ExampleObject(name = "ResourceNotFoundException",
                description = "상품 id가 존재하지 않은 경우",
                value = "{\n" +
                    "    \"statusCode\": 404,\n" +
                    "    \"message\": \"Brand not found with id: id\",\n" +
                    "    \"details\": \"uri=/v1/brands/{brand_id}\"\n" +
                    "}")
        }, mediaType = MediaType.APPLICATION_JSON_VALUE)),
        @ApiResponse(responseCode = "404", description = "path의 brand_id가 RequestBody의 brand_id와 일치하지 않음", content = @Content(examples = {
            @ExampleObject(name = "InvalidInputException",
                description = "path의 brand_id가 RequestBody의 brand_id와 일치하지 않는 경우",
                value = "{\n" +
                    "    \"statusCode\": 404,\n" +
                    "    \"message\": \"Brand id mismatch for update request\",\n" +
                    "    \"details\": \"uri=/v1/brands/{brand_id}\"\n" +
                    "}")
        }, mediaType = MediaType.APPLICATION_JSON_VALUE)),
        @ApiResponse(responseCode = "409", description = "중복된 이름의 브랜드로 수정하려는 경우", content = @Content(examples = {
            @ExampleObject(name = "DuplicateResourceException",
                description = "A 브랜드가 이미 존재하는데 A로 이름을 수정하려는 경우",
                value = "{\n" +
                    "    \"statusCode\": 409,\n" +
                    "    \"message\": \"Brand name A already exists\",\n" +
                    "    \"details\": \"uri=/v1/brands\"\n" +
                    "}")
        }, mediaType = MediaType.APPLICATION_JSON_VALUE))})
    @PutMapping("/v1/brands/{brand_id}")
    public BrandDTO update(@PathVariable(name = "brand_id") String brandId, @RequestBody BrandUpdateRequest brand) {
        if (!brandId.equals(brand.id)) {
            throw new InvalidInputException("Brand id mismatch for update request.");
        }
        Brand updated = brandService.updateBrand(brand);

        return BrandDTO.fromDomain(updated);
    }

    /**
     * 구현 4. 브랜드 삭제
     */
    @Operation(summary = "[구현4] 브랜드 삭제")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200"),
        @ApiResponse(responseCode = "404", description = "정의되지 않은 브랜드에 대한 브랜드 삭제가 요청됨", content = @Content(examples = {
            @ExampleObject(name = "ResourceNotFoundException",
                description = "브랜드 id가 존재하지 않은 경우",
                value = "{\n" +
                    "    \"statusCode\": 404,\n" +
                    "    \"message\": \"Brand not found with id: id\",\n" +
                    "    \"details\": \"uri=/v1/brands/{brand_id}\"\n" +
                    "}")
        }, mediaType = MediaType.APPLICATION_JSON_VALUE))})
    @DeleteMapping("/v1/brands/{brand_id}")
    public void delete(@PathVariable(name = "brand_id") String brandId) {
        brandService.deleteBrand(brandId);
    }

    /**
     * 추가 구현. 전체 브랜드 목록
     */
    @Operation(summary = "전체 브랜드 목록")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = BrandListResponse.class)))})
    @GetMapping("/v1/brands")
    public BrandListResponse list() {
        List<Brand> brands = brandService.getList();

        return BrandListResponse.fromDomain(brands);
    }

    /**
     * 추가 구현. 특정 브랜드 정보
     */
    @Operation(summary = "특정 브랜드 정보")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200"),
        @ApiResponse(responseCode = "404", description = "정의되지 않은 브랜드에 대한 정보가 요청됨", content = @Content(examples = {
            @ExampleObject(name = "ResourceNotFoundException",
                description = "브랜드 id가 존재하지 않은 경우",
                value = "{\n" +
                    "    \"statusCode\": 404,\n" +
                    "    \"message\": \"Brand not found with id: id\",\n" +
                    "    \"details\": \"uri=/v1/brands/{brand_id}\"\n" +
                    "}")
        }, mediaType = MediaType.APPLICATION_JSON_VALUE))})
    @GetMapping("/v1/brands/{brand_id}")
    public BrandDTO get(@PathVariable(name = "brand_id") String brandId) {
        Brand updated = brandService.getBrand(brandId);

        return BrandDTO.fromDomain(updated);
    }
}
