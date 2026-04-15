package io.github.gabrielvelosoo.productservice.application.usecase.category;

import io.github.gabrielvelosoo.productservice.application.dto.category.CategoryRequestDTO;
import io.github.gabrielvelosoo.productservice.application.dto.category.CategoryResponseDTO;
import io.github.gabrielvelosoo.productservice.application.mapper.CategoryMapper;
import io.github.gabrielvelosoo.productservice.application.validator.custom.CategoryValidator;
import io.github.gabrielvelosoo.productservice.domain.entity.Category;
import io.github.gabrielvelosoo.productservice.domain.service.CategoryService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class CategoryUseCaseImpl implements CategoryUseCase {

    private static final Logger logger = LogManager.getLogger(CategoryUseCaseImpl.class);

    private final CategoryService categoryService;
    private final CategoryMapper categoryMapper;
    private final CategoryValidator categoryValidator;

    public CategoryUseCaseImpl(CategoryService categoryService, CategoryMapper categoryMapper, CategoryValidator categoryValidator) {
        this.categoryService = categoryService;
        this.categoryMapper = categoryMapper;
        this.categoryValidator = categoryValidator;
    }

    @Override
    @Transactional
    public CategoryResponseDTO create(CategoryRequestDTO categoryRequestDTO) {
        logger.debug("Starting category creation. Name='{}', ParentId='{}'",
                categoryRequestDTO.name(),
                categoryRequestDTO.parentCategoryId()
        );
        Category category = categoryMapper.toEntity(categoryRequestDTO);
        if(categoryRequestDTO.parentCategoryId() != null) {
            Long parentId = categoryRequestDTO.parentCategoryId();
            logger.debug("Looking up parent category with id='{}'", parentId);
            Category parent = categoryService.findById(parentId);
            logger.debug("Parent category resolved: id='{}', name='{}'", parent.getId(), parent.getName());
            category.setParentCategory(parent);
        }
        categoryValidator.validateOnCreate(category);
        category.initialize();
        Category createdCategory = categoryService.save(category);
        return categoryMapper.toDTO(createdCategory);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponseDTO> getRootCategories() {
        logger.debug("Getting root categories");
        List<Category> result = categoryService.getParentlessCategories();
        return categoryMapper.toDTOs(result);
    }

    @Override
    @Transactional
    public void delete(Long categoryId) {
        Category category = categoryService.findById(categoryId);
        logger.debug("Category found: id='{}', name='{}'", category.getId(), category.getName());
        categoryValidator.validateOnDelete(category);
        logger.debug("Deleting category='{}' id='{}'", category.getName(), category.getId());
        categoryService.delete(category);
    }
}
