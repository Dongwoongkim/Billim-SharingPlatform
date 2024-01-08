package dblab.sharing_platform.domain.embedded.item;

import dblab.sharing_platform.dto.item.ItemCreateRequest;
import dblab.sharing_platform.dto.item.ItemUpdateRequest;
import javax.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Embeddable
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Item {

    private String name;

    private Long price;

    private Long quantity;

    public void updateItem(ItemUpdateRequest itemUpdateRequest) {
        this.name = itemUpdateRequest.getName();
        this.quantity = itemUpdateRequest.getQuantity();
        this.price = itemUpdateRequest.getPrice();
    }

    public static Item toEntity(ItemCreateRequest itemCreateRequest) {
        return new Item(itemCreateRequest.getName(),
                itemCreateRequest.getPrice(),
                itemCreateRequest.getQuantity());
    }
}
