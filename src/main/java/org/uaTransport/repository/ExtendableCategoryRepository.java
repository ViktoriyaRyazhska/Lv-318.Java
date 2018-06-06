package org.uaTransport.repository;

import org.uaTransport.entity.ExtendableCategory;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ExtendableCategoryRepository extends CrudRepository<ExtendableCategory, Integer> {

    ExtendableCategory findByName(String name);

    List<ExtendableCategory> findByNextLevelCategoryId(Integer id);

    List<ExtendableCategory> findByNextLevelCategory(ExtendableCategory extendableCategory);

    List<ExtendableCategory> findAllByNextLevelCategoryIsNull();

    ExtendableCategory findByNameAndNextLevelCategory(String name, ExtendableCategory nextLevelCategory);

}