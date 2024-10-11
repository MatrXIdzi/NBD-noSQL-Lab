package org.restaurant.repositories;

import org.restaurant.clients.Client;

import java.util.ArrayList;
import java.util.List;

public class ClientRepository implements Repository <Client> {

    private List <Client> clients = new ArrayList <Client>();
    @Override
    public void add(Client client) {

    }

    @Override
    public void remove(Client client) {

    }

    @Override
    public int count() {
        return 0;
    }

    @Override
    public Client get(int ID) {
        return null;
    }
}
