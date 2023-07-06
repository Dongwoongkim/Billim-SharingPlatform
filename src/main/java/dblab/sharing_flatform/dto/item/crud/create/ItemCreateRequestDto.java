package dblab.sharing_flatform.dto.item.crud.create;

import dblab.sharing_flatform.domain.item.Item;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@ApiModel(value = "물품 생성 요청")
@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ItemCreateRequestDto {

    @ApiModelProperty(value = "대여 물품 이름", notes = "물품 이름을 입력해주세요", required = true)
    private String name;

    @ApiModelProperty(value = "대여료", notes = "대여료를 입력해주세요", required = true)
    private Long price;

    @ApiModelProperty(value = "개수", notes = "개수를 입력해주세요", required = true)
    private Long quantity;

    public static Item toEntity(ItemCreateRequestDto itemCreateRequestDto) {
        return new Item(itemCreateRequestDto.getName(),
                itemCreateRequestDto.getPrice(),
                itemCreateRequestDto.getQuantity());
    }


}
