package dblab.sharing_flatform.domain.product;

import dblab.sharing_flatform.domain.factory.ProductFactory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static dblab.sharing_flatform.domain.factory.ProductFactory.createProduct;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

class ProductTest {

    @Test
    public void createProductTest() throws Exception {
        //given
        Product product = createProduct();

        //then
        assertThat(product).isNotNull();
    }

    @Test
    public void updateProductTest() throws Exception {
        //given
        Product product = createProduct();
        Product product1 = new Product("상품 2", 20000L, 2L);

        //when
        String name = "상품 2";
        Long price = 20000L;
        Long quantity = 2L;
        product.updateProductInfo(name,price,quantity);

        //then
        assertThat(product).isEqualTo(product1);
    }

}