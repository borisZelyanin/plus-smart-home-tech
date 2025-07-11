package ru.practicum.yandex.shopping.store.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.yandex.commerce.interfaceapi.dto.QuantityState;
import ru.practicum.yandex.shopping.store.model.Product;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class ProductRepository {

    @PersistenceContext
    private final EntityManager entityManager;

    public List<Product> findByProductCategory(String category, int page, int size) {
        return entityManager.createQuery(
                        "SELECT p FROM Product p WHERE p.productCategory = :category", Product.class)
                .setParameter("category", category)
                .setFirstResult(page * size)
                .setMaxResults(size)
                .getResultList();
    }

    public Optional<Product> findById(UUID id) {
        Product product = entityManager.find(Product.class, id);
        return Optional.ofNullable(product);
    }

    @Transactional
    public Product save(Product product) {
        if (product.getProductId() == null) {
            product.setProductId(UUID.randomUUID());
        }
        entityManager.persist(product);
        return product;
    }

    @Transactional
    public Product update(Product product) {
        return entityManager.merge(product);
    }

    @Transactional
    public boolean remove(UUID productId) {
        Product product = entityManager.find(Product.class, productId);
        if (product != null) {
            entityManager.remove(product);
            return true;
        }
        return false;
    }

    @Transactional
    public boolean updateQuantityState(UUID productId, String quantityState) {
        Product product = entityManager.find(Product.class, productId);
        if (product != null) {
            try {
                QuantityState state = QuantityState.valueOf(quantityState);
                product.setQuantityState(state);
                entityManager.merge(product);
                return true;
            } catch (IllegalArgumentException e) {
                // Некорректное значение статуса
                return false;
            }
        }
        return false;
    }
}