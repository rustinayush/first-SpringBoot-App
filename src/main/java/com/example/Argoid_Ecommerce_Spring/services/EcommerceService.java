package com.example.Argoid_Ecommerce_Spring.services;

import com.example.Argoid_Ecommerce_Spring.modal.EcommerceEntity;
import com.example.Argoid_Ecommerce_Spring.repository.EcommerceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EcommerceService {

    private final EcommerceRepository ecommerceRepository;


    @Autowired
    public EcommerceService(EcommerceRepository ecommerceRepository) {
        this.ecommerceRepository = ecommerceRepository;
    }

    public Page<EcommerceEntity> getAllProducts( Pageable pageable) {
        return ecommerceRepository.findAll(pageable);
    }

    public Page<EcommerceEntity> filterProducts(String attribute, Pageable pageable) {
        return ecommerceRepository.searchProduct(attribute,pageable);
    }

    public EcommerceEntity getProductById(String id) {
        return ecommerceRepository.findById(id).orElse(null);
    }

    public EcommerceEntity createProduct(EcommerceEntity ecommerceEntity) {
        return ecommerceRepository.save(ecommerceEntity);
    }

    public boolean updateProduct(String id, EcommerceEntity ecommerceEntity) {
        return ecommerceRepository.update(id, ecommerceEntity).isPresent();
    }

    public boolean deleteProduct(String id) {
        ecommerceRepository.delete(id);
        return true; // when successfully deleted
    }
}