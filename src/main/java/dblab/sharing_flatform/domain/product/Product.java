package dblab.sharing_flatform.domain.product;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long id;

    private String name;

    private Long price;

    private Long quantity;

    public Product(String name, Long price, Long quantity) {
        this.name = name;
        this.quantity = quantity;
        this.price = price;
    }


    public void updateProductInfo(String name, Long price, Long quantity){
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }



}
