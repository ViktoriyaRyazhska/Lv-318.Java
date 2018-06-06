package org.uaTransport.service.implementation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.uaTransport.entity.NonExtendableCategory;
import org.uaTransport.exception.ResourceNotFoundException;
import org.uaTransport.repository.NonExtendableCategoryRepository;
import org.uaTransport.service.NonExtendableCategoryService;

import java.util.List;


@Service
@RequiredArgsConstructor
public class NonExtendableCategoryServiceImpl implements NonExtendableCategoryService {

    private final NonExtendableCategoryRepository nonExtendableCategoryRepository;

    @Override
    @Transactional
    public NonExtendableCategory addNonExtendableCategory(NonExtendableCategory nonExtendableCategory) {
        if (nonExtendableCategory.getNextLevelCategory() == null) {
            throw new IllegalArgumentException("NonExtendableCategory must contain next level Category");
        }
        return nonExtendableCategoryRepository.save(nonExtendableCategory);
    }

    @Override
    @Transactional
    public void delete(int id) {
        nonExtendableCategoryRepository.deleteById(id);
    }

    @Override
    @Transactional
    public NonExtendableCategory update(NonExtendableCategory nonExtendableCategory) {
        return nonExtendableCategoryRepository.findById(nonExtendableCategory.getId())
                .map(category -> nonExtendableCategoryRepository.save(nonExtendableCategory))
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Category with id '%s' not found", nonExtendableCategory.getId())));
    }

    @Override
    public List<NonExtendableCategory> getByNextLevelCategory(int id) {
        return nonExtendableCategoryRepository.findByNextLevelCategoryId(id);
    }

    @Override
    public NonExtendableCategory getById(Integer id) {
        return nonExtendableCategoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String
                        .format("Category with id '%s' not found", id)));
    }

    @Override
    public List<NonExtendableCategory> getByNames(String name, String next) {
        return nonExtendableCategoryRepository.findByNextLevelCategoryNameAndNextLevelCategoryNextLevelCategoryName(name, next);
    }
}
