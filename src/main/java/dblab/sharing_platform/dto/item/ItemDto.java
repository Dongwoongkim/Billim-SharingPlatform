package dblab.sharing_platform.dto.item;

import dblab.sharing_platform.domain.embedded.item.Item;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ItemDto {

    private String name;

    private Long price;

    private Long quantity;

    public static ItemDto toDto(Item item) {
        if (item != null) {
            return new ItemDto(item.getName(),
                    item.getPrice(),
                    item.getQuantity());
        }
        return null;
    }
}
