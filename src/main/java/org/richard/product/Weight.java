package org.richard.product;

import java.util.Objects;
import org.richard.utils.Strings;

public record Weight(double value, String units) {

    public Weight mergeWith(Weight weight) {
        if(weight == null){
            return this;
        }
        double newValue = weight.value != value ? weight.value : value;
        String newUnits = !Objects.equals(units, weight.units) && Strings.isNotNullOrEmpty(weight.units)
            ? weight.units : units;
        return new Weight(newValue, newUnits);
    }
}
