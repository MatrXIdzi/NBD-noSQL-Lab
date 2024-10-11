package org.restaurant.repositories;

import org.restaurant.clients.Client;
import org.restaurant.elements.Element;

import java.util.ArrayList;
import java.util.List;

public class ElementRepository implements Repository <Element> {
    private List<Element> elements = new ArrayList<Element>();
    @Override
    public void add(Element element) {

    }

    @Override
    public void remove(Element element) {

    }

    @Override
    public int count() {
        return 0;
    }

    @Override
    public Element get(int ID) {
        return null;
    }
}
