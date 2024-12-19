package com.example.productorderservice.product;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.Map;

class ProductServiceTest {

    private ProductService productService;
    private ProductPort productPort;
    private ProductRepository productRepository;

    @BeforeEach
    void setUp() {
        productRepository = new ProductRepository();
        productPort = new ProductAdapter(productRepository);
        productService = new ProductService(productPort);
    }


    @Test
    void 상품등록() {
        String name = "상품명";
        final int price = 1000;
        final DiscountPolicy discountPolicy = DiscountPolicy.NONE;
        final AddProductRequest request = new AddProductRequest(name, price, discountPolicy);
        productService.addProduct(request);
    }

    private record AddProductRequest(String name, int price, DiscountPolicy discountPolicy) {
        private AddProductRequest {
            Assert.hasText(name, "상품명은 필수입니다.");
            Assert.isTrue(price > 0, "상품 가격은 0보다 커야 합니다.");
            Assert.notNull(discountPolicy, "할인 정책은 필수입니다.");
        }
        }
    private class ProductService {
        private final ProductPort productPort;

        private ProductService(ProductPort productPort) {
            this.productPort = productPort;
        }

        public void addProduct(AddProductRequest request) {
            final Product product = new Product(request.name(), request.price(), request.discountPolicy());

            productPort.save(product);
        }
    }

    private class Product {
        private Long id;

        public Product(String name, int price, DiscountPolicy discountPolicy) {
            Assert.hasText(name, "상품명은 필수입니다.");
            Assert.isTrue(price>0, "상품 가격은 0보다 커야 합니다.");
            Assert.notNull(discountPolicy,"할인 정책은 필수입니다.");
        }

        public void assignId(Long aLong) {
            this.id = id;
        }

        public Long getId() {
            return id;
        }
    }

    private interface ProductPort {
        void save(Product product);
    }

    private class ProductAdapter implements ProductPort {
        private final ProductRepository productRepository;

        private ProductAdapter(final ProductRepository productRepository) {
            this.productRepository = productRepository;
        }

        @Override
        public void save(Product product) {
            productRepository.save(product);
        }
    }

    private class ProductRepository {
        private Map<Long, Product> persistence = new HashMap<>();
        private Long sequence = 0L;

        public void save(Product product) {
            product.assignId(++sequence);
            persistence.put(product.getId(),product);
        }
    }

    private enum DiscountPolicy {
        NONE
    }
}
