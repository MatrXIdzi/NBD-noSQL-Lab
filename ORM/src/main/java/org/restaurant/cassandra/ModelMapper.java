package org.restaurant.cassandra;

import org.restaurant.model.Client;
import org.restaurant.model.Element;
import org.restaurant.model.Table;
import org.restaurant.model.Hall;

public abstract class ModelMapper {
    public static ClientCassandra toClientCassandra(Client client) {
        ClientCassandra clientCassandra =
                new ClientCassandra(client.getEntityId(), client.getFirstName(), client.getLastName(), client.getPersonalID());
        return clientCassandra;
    }

    public static Client toClient(ClientCassandra clientCassandra) {
        Client client =
                new Client(clientCassandra.getId(), clientCassandra.getFirstName(), clientCassandra.getLastName(), clientCassandra.getPersonalID());
        return client;
    }

    public static ElementCassandra toElementCassandra(Element element) {
        if (element instanceof Table) {
            return new TableCassandra("table", element.getId(), ((Table) element).isPremium(), element.getPricePerPerson(), element.getMaxCapacity(), element.getName());
        }
        else {
            return new HallCassandra("hall", element.getId(), ((Hall) element).getBasePrice(), ((Hall) element).isDanceFloor(), ((Hall) element).isBar(), element.getPricePerPerson(), element.getMaxCapacity(), element.getName());
        }
    }

    public static Element toElement(ElementCassandra elementCassandra) {
        if (elementCassandra instanceof TableCassandra) {
            return new Table(elementCassandra.getId(), elementCassandra.getPricePerPerson(), elementCassandra.getMaxCapacity(), elementCassandra.getName(), ((TableCassandra) elementCassandra).isPremium());
        }
        else {
            return new Hall(elementCassandra.getId(), elementCassandra.getPricePerPerson(), elementCassandra.getMaxCapacity(), elementCassandra.getName(), ((HallCassandra) elementCassandra).isDanceFloor(), ((HallCassandra) elementCassandra).isBar(), ((HallCassandra) elementCassandra).getBasePrice());
        }
    }
}
