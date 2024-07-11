package com.cy.fashion.brand;


import com.cy.fashion.brand.dto.BrandDTO;
import com.cy.fashion.brand.dto.request.BrandCreateRequest;
import com.cy.fashion.brand.dto.request.BrandUpdateRequest;
import com.cy.fashion.brand.dto.response.BrandListResponse;
import com.cy.fashion.product.dto.ProductFilterDTO;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class BrandIntegrationTest {

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
     * 구현 4 - 브랜드 생성
     */
    @Test
    public void createBrand_shouldBeCreated() throws Exception {
        // get initial brand list
        List<BrandDTO> initialBrands = getBrandList();

        // create brand with name "NewBrand"
        BrandCreateRequest brandCreateRequest = new BrandCreateRequest();
        brandCreateRequest.brandName = "NewBrand";
        String createResponseContent = mockMvc.perform(post("/v1/brands")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(brandCreateRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.brandName", is("NewBrand")))
            .andReturn()
            .getResponse()
            .getContentAsString();

        BrandDTO createdBrand = objectMapper.readValue(createResponseContent, BrandDTO.class);

        // get changed brand list
        List<BrandDTO> changedBrands = getBrandList();

        // compare initial list with changed list
        assertEquals(initialBrands.size() + 1, changedBrands.size());
        assertEquals(createdBrand.id, changedBrands.getLast().id);
        assertEquals(createdBrand.brandName, changedBrands.getLast().brandName);
    }

    /**
     * 구현 4 - 브랜드 생성 : 이미 존재하는 브랜드 이름일 경우
     */
    @Test
    public void createDuplicateBrand_shouldThrow409() throws Exception {
        // get initial brand list
        List<BrandDTO> initialBrands = getBrandList();

        // create brand with name duplicated
        BrandCreateRequest brandCreateRequest = new BrandCreateRequest();
        brandCreateRequest.brandName = initialBrands.get(new Random().nextInt(initialBrands.size())).brandName;
        mockMvc.perform(post("/v1/brands")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(brandCreateRequest)))
            .andExpect(status().isConflict());
    }

    /**
     * 구현 4 - 브랜드 수정
     */
    @Test
    public void updateBrand_shouldBeUpdated() throws Exception {
        // get initial brand list
        List<BrandDTO> initialBrands = getBrandList();

        // update brand
        BrandDTO brandToUpdate = initialBrands.get(new Random().nextInt(initialBrands.size()));
        BrandUpdateRequest brandUpdateRequest = new BrandUpdateRequest(brandToUpdate.id, "UpdatedName");
        mockMvc.perform(put("/v1/brands/" + brandToUpdate.id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(brandUpdateRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.brandName", is("UpdatedName")));

        // check brand is updated
        BrandDTO brand = getBrand(brandToUpdate.id);
        assertEquals(brand.brandName, "UpdatedName");
    }

    /**
     * 구현 4 - 브랜드 수정 : 존재하지 않은 브랜드에 대해
     */
    @Test
    public void updateNotExistingBrand_shouldThrow404() throws Exception {
        // update brand with randomly generated not existing id
        String notExistingRandomId = Base62Codec.INSTANCE.encode(UUID.randomUUID());
        BrandUpdateRequest brandUpdateRequest = new BrandUpdateRequest(notExistingRandomId, "UpdatedName");
        mockMvc.perform(put("/v1/brands/" + notExistingRandomId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(brandUpdateRequest)))
            .andExpect(status().isNotFound());
    }
    
    /**
     * 구현 4 - 브랜드 수정 : path parameter의 브랜드 id와 request body의 브랜드 id가 일치하지 않는 요청
     */
    @Test
    public void updateBrandMismatch_shouldThrow404() throws Exception {
        // get initial brand list
        List<BrandDTO> initialBrands = getBrandList();

        // request updating brand while brand id in path parameter doesn't match with brand id in request body
        BrandUpdateRequest brandUpdateRequest = new BrandUpdateRequest(initialBrands.get(0).id, "UpdatedName");
        mockMvc.perform(put("/v1/brands/" + initialBrands.get(1).id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(brandUpdateRequest)))
            .andExpect(status().isNotFound());
    }

    /**
     * 구현 4 - 브랜드 수정 : 수정하려는 브랜드의 이름이 이미 존재할 경우
     */
    @Test
    public void updateBrandWithDuplicatedName_shouldThrow409() throws Exception {
        // get initial brand list
        List<BrandDTO> initialBrands = getBrandList();

        // update brand with brand name that is already been used
        BrandUpdateRequest brandUpdateRequest = new BrandUpdateRequest(initialBrands.get(0).id, initialBrands.get(1).brandName);
        mockMvc.perform(put("/v1/brands/" + initialBrands.get(0).id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(brandUpdateRequest)))
            .andExpect(status().isConflict());
    }

    /**
     * 구현 4 - 브랜드 수정 : 수정하려는 브랜드의 이름이 자기 자신일 경우 (수정하려는 브랜드의 이름이 이미 존재할 경우의 예외 상황)
     */
    @Test
    public void updateBrandWithDuplicatedNameButMine_shouldBeUpdated() throws Exception {
        // get initial brand list
        List<BrandDTO> initialBrands = getBrandList();

        // update brand with brand name that is already been used, but that name belongs to the brand that requests updating
        BrandDTO brandToUpdate = initialBrands.get(new Random().nextInt(initialBrands.size()));
        BrandUpdateRequest brandUpdateRequest = new BrandUpdateRequest(brandToUpdate.id, brandToUpdate.brandName);
        mockMvc.perform(put("/v1/brands/" + brandToUpdate.id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(brandUpdateRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.brandName", is(brandToUpdate.brandName)));

        // check brand is updated
        BrandDTO brand = getBrand(brandToUpdate.id);
        assertEquals(brand.brandName, brandToUpdate.brandName);
    }

    /**
     * 구현 4 - 브랜드 삭제
     */
    @Test
    public void deleteBrand_shouldBeDeleted() throws Exception {
        // get initial brand list
        List<BrandDTO> initialBrands = getBrandList();

        // delete brand
        BrandDTO brandToDelete = initialBrands.get(new Random().nextInt(initialBrands.size()));
        mockMvc.perform(delete("/v1/brands/" + brandToDelete.id))
            .andExpect(status().isOk());

        // check brand is deleted
        mockMvc.perform(get("/v1/brands/" + brandToDelete.id))
            .andExpect(status().isNotFound());

        // check all products of deleted brand are deleted
        ProductFilterDTO filter = new ProductFilterDTO();
        filter.brandIds = new ArrayList<>();
        filter.brandIds.add(brandToDelete.id);
        String productListResponse = mockMvc.perform(get("/v1/products")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(filter)))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
        ProductListResponse products = objectMapper.readValue(productListResponse, new TypeReference<>() {});
        assertEquals(products.products.size(), 0);
    }

    /**
     * 구현 4 - 브랜드 삭제 : 존재하지 않는 브랜드에 대해
     */
    @Test
    public void deleteNotExistingBrand_shouldThrow404() throws Exception {
        // delete brand with randomly generated not existing id
        mockMvc.perform(delete("/v1/brands/" + Base62Codec.INSTANCE.encode(UUID.randomUUID())))
            .andExpect(status().isNotFound());
    }

    List<BrandDTO> getBrandList() throws Exception {
        String brandListResponse = mockMvc.perform(get("/v1/brands"))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
        return objectMapper.readValue(brandListResponse, new TypeReference<BrandListResponse>() {}).brands;
    }

    BrandDTO getBrand(String id) throws Exception {
        String brandResponse = mockMvc.perform(get("/v1/brands/" + id))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
        return objectMapper.readValue(brandResponse, new TypeReference<>() {});
    }
}
