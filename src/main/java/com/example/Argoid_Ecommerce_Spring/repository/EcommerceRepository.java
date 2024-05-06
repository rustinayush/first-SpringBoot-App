package com.example.Argoid_Ecommerce_Spring.repository;

import com.example.Argoid_Ecommerce_Spring.modal.EcommerceEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface EcommerceRepository {

    Page<EcommerceEntity>   findAll( Pageable pageable);

    Page<EcommerceEntity>  searchProduct(String attribute, Pageable pageable);

    Optional<EcommerceEntity> findById(String id);

    EcommerceEntity save(EcommerceEntity entity);

    Optional<EcommerceEntity> update(String id, EcommerceEntity entity);

    void delete(String id);
}
