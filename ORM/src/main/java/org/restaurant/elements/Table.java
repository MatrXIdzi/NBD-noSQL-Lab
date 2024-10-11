package org.restaurant.elements;

import jakarta.persistence.*;

@Entity
@DiscriminatorValue("Table")
public class Table extends Element {
    // Fields specific to tables (if any) and getters/setters.
}
