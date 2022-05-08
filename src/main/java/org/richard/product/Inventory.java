package org.richard.product;

import java.util.Objects;
import org.richard.utils.Strings;

public record Inventory(
    String management,
    int quantity
) {

    public Inventory mergeWith(Inventory inventory) {
        if(inventory == null){
            return this;
        }
        int newValue = (inventory.quantity != quantity && inventory.quantity > 0) ? inventory.quantity : quantity;
        String newManagement = !Objects.equals(management, inventory.management) && Strings.isNotNullOrEmpty(inventory.management)
            ? inventory.management : management;
        return new Inventory(newManagement, newValue);
    }
}
