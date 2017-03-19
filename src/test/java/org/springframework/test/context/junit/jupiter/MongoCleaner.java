package org.springframework.test.context.junit.jupiter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.event.AfterSaveEvent;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

import static org.springframework.data.mongodb.core.query.Criteria.where;

public class MongoCleaner implements ApplicationListener<AfterSaveEvent> {

    private Map<Class<?>, Set<String>> idsPerCollection = new ConcurrentHashMap<>();

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public void onApplicationEvent(AfterSaveEvent event) {
        Object id = event.getDocument().get("_id");
        addIdToRemove(event.getSource().getClass(), id.toString());
    }

    private void addIdToRemove(final Class<?> entityType, final String id) {
            Set<String> ids = idsPerCollection.get(entityType);
            if (ids != null) {
                ids.add(id);
            }
    }

    public void prepare(final List<Class<?>> entityTypes) {
        idsPerCollection.clear();
        for (Class<?> entityType : entityTypes) {
            idsPerCollection.put(entityType, new ConcurrentSkipListSet<>());
        }
    }

    public Map<Class<?>, Set<String>> cleanup() {
        Map<Class<?>, Set<String>> toDelete = new HashMap(idsPerCollection);
        idsPerCollection.clear();
        toDelete.forEach(this::deleteObjects);
        return toDelete;
    }

    private <T> void deleteObjects(Class<T> entityType, Collection<String> ids) {
        final Criteria criteria = where("_id").in(ids);
        final Query query = new Query(criteria);
        mongoTemplate.findAllAndRemove(query, entityType);
    }
}
