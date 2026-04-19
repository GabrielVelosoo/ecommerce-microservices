package io.github.gabrielvelosoo.customerservice.infrastructure.persistence.specification;

import io.github.gabrielvelosoo.customerservice.domain.entity.Customer;
import org.springframework.data.jpa.domain.Specification;

public class CustomerSpecification {

    public static Specification<Customer> createSpecification(String email, String cpf) {
        Specification<Customer> specs = Specification.allOf();
        if(email != null && !email.isBlank()) {
            specs = specs.and(emailLike(email));
        }
        if(cpf != null && !cpf.isBlank()) {
            specs = specs.and(cpfLike(cpf));
        }
        return specs;
    }

    public static Specification<Customer> emailLike(String email) {
        return (root, query, cb) ->
                cb.like(cb.upper(root.get("email")), "%" + email.toUpperCase() + "%");
    }

    public static Specification<Customer> cpfLike(String cpf) {
        return (root, query, cb) ->
                cb.like(root.get("cpf"), "%" + cpf + "%");
    }
}
