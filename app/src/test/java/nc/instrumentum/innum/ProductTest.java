package nc.instrumentum.innum;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ProductTest {

    @Test
    public void productDisplaysNameAndQuantity() {
        Product product = new Product(
                1,
                "Milk",
                2,
                1
        );

        assertEquals(
                "Milk   x2",
                product.toString()
        );
    }

    @Test
    public void productStoresItsName() {
        Product product = new Product(
                1,
                "Bread",
                1,
                3
        );

        assertEquals(
                "Bread",
                product.getObj()
        );
    }

    @Test
    public void productStoresItsQuantity() {
        Product product = new Product(
                1,
                "Apples",
                5,
                3
        );

        assertEquals(
                5,
                product.getNum()
        );
    }

    @Test
    public void productStoresItsListId() {
        Product product = new Product(
                1,
                "Water",
                2,
                7
        );

        assertEquals(
                7,
                product.getIdList()
        );
    }
}