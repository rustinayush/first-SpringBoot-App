package com.example.Argoid_Ecommerce_Spring.resource;
import org.springframework.data.domain.Page;
import com.example.Argoid_Ecommerce_Spring.modal.EcommerceEntity;
import com.example.Argoid_Ecommerce_Spring.services.EcommerceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

//import javax.validation.constraints.Null;
import java.util.List;

@RestController
@RequestMapping("/products")
public class ShoppingResource {

    private final EcommerceService ecommerceService;

    @Autowired
    public ShoppingResource(EcommerceService ecommerceService) {
        this.ecommerceService = ecommerceService;
    }

    @GetMapping
    public Page<EcommerceEntity> getAllProducts(@RequestParam(value = "pageNumber", defaultValue = "0") int pageNumber,
                                                @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
                                                @RequestParam(value = "sortBy", required = false) String sortBy,
                                                @RequestParam(value = "sortOrder", defaultValue = "asc") String sortOrder,
                                                @RequestParam(value = "searchAttribute", required = false) String searchAttribute){


        Pageable pageable;

        if (sortBy != null && !sortBy.isEmpty()) {
            pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.fromString(sortOrder), sortBy));
        } else {
            // Handle case where sortBy is null or empty
            // For example, you can use a default sorting property
            pageable = PageRequest.of(pageNumber, pageSize);
        }

        if (searchAttribute != null && !searchAttribute.isEmpty()) {
            return ecommerceService.filterProducts(searchAttribute, pageable);
        } else {
            return ecommerceService.getAllProducts(pageable);
        }


    }


    @GetMapping("/{id}")
    public EcommerceEntity getProduct(@PathVariable("id") String id) {

        return ecommerceService.getProductById(id);
    }

    @PostMapping
    public EcommerceEntity createProduct(@RequestBody EcommerceEntity entity) {
        return ecommerceService.createProduct(entity);
    }

    @PutMapping("/{id}")
    public void updateProduct(@PathVariable("id") String id, @RequestBody EcommerceEntity ecommerceEntity) {
        ecommerceService.updateProduct(id, ecommerceEntity);
    }

    @DeleteMapping("/{id}")
    public void deleteProduct(@PathVariable("id") String id) {
        ecommerceService.deleteProduct(id);
    }
}

