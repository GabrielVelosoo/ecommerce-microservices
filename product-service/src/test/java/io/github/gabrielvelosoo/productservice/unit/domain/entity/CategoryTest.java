package io.github.gabrielvelosoo.productservice.unit.domain.entity;

import io.github.gabrielvelosoo.productservice.domain.entity.Category;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CategoryTest {

    private final Category category = new Category();

    @Nested
    class GenerateSlugTests {

        @Test
        void shouldGenerateSlugFromSimpleName() {
            String slug = category.generateSlug("Books");
            assertThat(slug).isEqualTo("books");
        }

        @Test
        void shouldReplaceSpacesWithHyphen() {
            String slug = category.generateSlug("Home Appliances");
            assertThat(slug).isEqualTo("home-appliances");
        }

        @Test
        void shouldRemoveAccents() {
            String slug = category.generateSlug("Coffe with Milk");
            assertThat(slug).isEqualTo("coffe-with-milk");
        }

        @Test
        void shouldRemoveSpecialCharacters() {
            String slug = category.generateSlug("TV & Audio!");
            assertThat(slug).isEqualTo("tv-audio");
        }

        @Test
        void shouldHandleMultipleSpaces() {
            String slug = category.generateSlug("   Smart    Phone   ");
            assertThat(slug).isEqualTo("smart-phone");
        }

        @Test
        void shouldKeepHyphenatedWords() {
            String slug = category.generateSlug("pre-owned items");
            assertThat(slug).isEqualTo("pre-owned-items");
        }
    }
}
