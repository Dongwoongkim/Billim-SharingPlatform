package dblab.sharing_flatform.domain.factory;

import dblab.sharing_flatform.domain.product.Product;

public class ProductFactory {

    public static Product createProduct(){
        return new Product("상품1", 10000L, 10L);
    }

}
