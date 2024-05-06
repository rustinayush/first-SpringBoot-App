package com.example.Argoid_Ecommerce_Spring.repository;


import com.example.Argoid_Ecommerce_Spring.modal.EcommerceEntity;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;

@Repository
public class EcommerceRepositoryImpl implements EcommerceRepository {

    private final Map<String, EcommerceEntity> entities = loadDataFromJson();

    private Map<String, EcommerceEntity> loadDataFromJson() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            ClassPathResource resource = new ClassPathResource("product.json");
            List<EcommerceEntity> entityList = mapper.readValue(resource.getInputStream(), new TypeReference<>() {
            });
            Map<String, EcommerceEntity> entityMap = new HashMap<>();
            for (EcommerceEntity entity : entityList) {
                entityMap.put(entity.getId(), entity);
            }
            return entityMap;
        } catch (IOException e) {
            e.printStackTrace();
            return new HashMap<>();
        }
    }

    @Override
    public Page<EcommerceEntity> findAll(Pageable pageable) {
        System.out.println(entities);
        System.out.println(pageable);

        List<EcommerceEntity> resultList = new ArrayList<>(entities.values());

        // Sorting
        System.out.println(pageable.getSort());
        if (pageable.getSort().isSorted()) {
            Comparator<EcommerceEntity> comparator = getComparatorFromSort(pageable.getSort());
            resultList.sort(comparator);
        }

        int totalElements = resultList.size();

        // Pagination
        int fromIndex = (int) pageable.getOffset();
        int toIndex = Math.min(fromIndex + pageable.getPageSize(), totalElements);

        if (fromIndex >= totalElements) {
            return new PageImpl<>(Collections.emptyList(), pageable, totalElements);
        } else {
            List<EcommerceEntity> pageContent = resultList.subList(fromIndex, toIndex);
            System.out.println(pageContent);
            return new PageImpl<>(pageContent, pageable, totalElements);
        }
    }
    private Comparator<EcommerceEntity> getComparatorFromSort(Sort sort) {


        List<Comparator<EcommerceEntity>> comparators = new ArrayList<>();
        for (Sort.Order order : sort) {
            System.out.println(order);
            Comparator<EcommerceEntity> comparator = getComparator(order.getProperty(), order.getDirection().toString());
            comparators.add(order.isAscending() ? comparator : comparator.reversed());
        }

        return (e1, e2) -> {
            for (Comparator<EcommerceEntity> comparator : comparators) {
                int result = comparator.compare(e1, e2);
                if (result != 0) {
                    return result;
                }
            }
            return 0;
        };
    }


    @Override
    public Page<EcommerceEntity> searchProduct(String attribute, Pageable pageable) {
        List<EcommerceEntity> result = new ArrayList<>();

        try {
            for (EcommerceEntity entity : entities.values()) {
                for (Field field : EcommerceEntity.class.getDeclaredFields()) {
                    field.setAccessible(true);
                    Object fieldValue = field.get(entity);

                    if (fieldValue != null && fieldValue.toString().equalsIgnoreCase(attribute)) {
                        result.add(entity);
                    }
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            throw new RuntimeException("Error accessing field: " + attribute, e);
        }

        // Implement pagination
        int start = (int) pageable.getOffset();
        int end = (start + pageable.getPageSize()) > result.size() ? result.size() : (start + pageable.getPageSize());
        List<EcommerceEntity> paginatedList = result.subList(start, end);

        return new PageImpl<>(paginatedList, pageable, result.size());
    }

    @Override
    public Optional<EcommerceEntity> findById(String id) {
        return Optional.ofNullable(entities.get(id));
    }

    @Override
    public EcommerceEntity save(EcommerceEntity entity) {
        entities.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public Optional<EcommerceEntity> update(String id, EcommerceEntity entity) {
        if (entities.containsKey(id)) {
            entities.put(id, entity);
            return Optional.of(entity);
        }
        return Optional.empty();
    }

    @Override
    public void delete(String id) {
        entities.remove(id);
    }

    public static Comparator<EcommerceEntity> getComparator(String fieldName, String Order) {
        Comparator<EcommerceEntity> comparator = (s1, s2) -> {
            try {
                Field field = EcommerceEntity.class.getDeclaredField(fieldName);
                field.setAccessible(true);
                Object value1 = field.get(s1);
                Object value2 = field.get(s2);

                if (value1 == null && value2 == null) {
                    return 0;
                } else if (value1 == null) {
                    return Order.equalsIgnoreCase("asc") ? 1 : -1; // null is considered greater in ascending order
                } else if (value2 == null) {
                    return Order.equalsIgnoreCase("asc") ? -1 : 1; // null is considered lesser in descending order
                }

                if (value1 instanceof Comparable && value2 instanceof Comparable) {
                    // If both values are comparable, compare them
                    return ((Comparable) value1).compareTo(value2);
                } else {
                    throw new IllegalArgumentException("Field does not implement Comparable");
                }
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
                throw new RuntimeException("Error accessing field: " + fieldName, e);
            }
        };
        if (Order.equalsIgnoreCase("desc")) {
            comparator = comparator.reversed();
        }
        return comparator;
    }


}