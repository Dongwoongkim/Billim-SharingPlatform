package dblab.sharing_flatform.domain.item;

import org.junit.jupiter.api.Test;

import static dblab.sharing_flatform.factory.item.ItemFactory.createItem;
import static org.assertj.core.api.Assertions.assertThat;

class ItemTest {

    @Test
    public void createTest() throws Exception {
        //given
        Item item = createItem();

        //then
        assertThat(item).isNotNull();
    }

    @Test
    public void updateTest() throws Exception {
        //given
        Item item = createItem();

        //when
        String name = "TestName";
        Long price = 1000L;
        Long quantity = 1000L;
        item.updateItem(name, price, quantity);

        //then
        assertThat(item.getName()).isEqualTo(name);
        assertThat(item.getPrice()).isEqualTo(price);
        assertThat(item.getQuantity()).isEqualTo(quantity);
    }

}