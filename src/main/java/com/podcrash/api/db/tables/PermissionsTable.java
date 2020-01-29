package com.podcrash.api.db.tables;

import com.mongodb.client.model.*;
import com.podcrash.api.db.IPlayerDB;
import com.podcrash.api.db.MongoBaseTable;
import org.bson.Document;

import java.util.*;

/**
 * roles: name:string, permissions:list[string]
 */
public class PermissionsTable extends MongoBaseTable implements IPlayerDB {
    public PermissionsTable() {
        super("roles");
    }

    @Override
    public DataTableType getDataTableType() {
        return DataTableType.PERMISSIONS;
    }

    @Override
    public void createTable() {
        List<IndexModel> models = new ArrayList<>();
        models.add(new IndexModel(Indexes.ascending("name"), new IndexOptions().unique(true)));
        models.add(new IndexModel(Indexes.ascending("permissions"),
                new IndexOptions().collation(Collation.builder().caseLevel(false).build())));
        getCollection().createIndexes(models);
    }
    /**
     * Adds the championskits column to the players table if they don't already have it
     * @param uuid
     */
    private void evaluate(UUID uuid) {
        Document playerDoc = getPlayerDocumentSync(uuid);
        if(playerDoc.containsKey(getName())) return;
        Document addColumn = new Document(getName(), new HashSet<>());

        getPlayerTable().getCollection().updateOne(playerDoc, new Document("$set", addColumn));
    }

    public void addRole(String role) {
        Document document = new Document("name", role)
                .append("permissions", new HashSet<>());
        getCollection().insertOne(document);
    }
    public boolean containsRole(String role) {
        return getCollection().find(Filters.eq("name", role)).first() != null;
    }
    public void removeRole(String role) {
        getCollection().deleteOne(Filters.eq("name", role));
    }
    public void addPermission(String role, String permission) {
        Document roleDoc = getCollection().find(Filters.eq("name", role)).first();
        Set<String> perms = roleDoc.containsKey("permissions") ? (Set<String>) roleDoc.get("permissions") : new HashSet<>();
        perms.add(permission);
        getCollection().updateOne(roleDoc, new Document("$set", new Document("permissions", perms)));
    }
    public void removePermission(String role, String permission) {
        Document roleDoc = getCollection().find(Filters.eq("name", role)).first();
        if(!roleDoc.containsKey("permissions")) return;
        Set<String> perms = (Set<String>) roleDoc.get("permissions");
        perms.remove(permission);
        getCollection().updateOne(roleDoc, new Document("$set", new Document("permissions", perms)));
    }

    public Set<String> getPermissions(String role) {
        Document roleDoc = getCollection().find(Filters.eq("name", role)).first();
        if(!roleDoc.containsKey("permissions")) return new HashSet<>();
        return (Set<String>) roleDoc.get("permissions");
    }
    //
    // PLAYER
    //
    public void addRole(UUID uuid, String role) {
        evaluate(uuid);
        Document playerDoc = getPlayerDocumentSync(uuid);
        Set<String> roleList = (Set<String>) playerDoc.get(getName());
        roleList.add(role);
        Document updated = new Document(getName(), roleList);
        getPlayerTable().getCollection().updateOne(playerDoc, new Document("$set", updated));
    }

    public boolean hasRole(UUID uuid, String role) {
        evaluate(uuid);
        Document playerDoc = getPlayerDocumentSync(uuid);
        Set<String> roleList = (Set<String>) playerDoc.get(getName());
        return roleList.contains(role);
    }

    public Set<String> getRoles(UUID uuid) {
        evaluate(uuid);
        Document playerDoc = getPlayerDocumentSync(uuid);
        return (Set<String>) playerDoc.get(getName());
    }

    public void removeRole(UUID uuid, String role) {
        evaluate(uuid);
        Document playerDoc = getPlayerDocumentSync(uuid);
        Set<String> roleList = (Set<String>) playerDoc.get(getName());
        roleList.remove(role);
        Document updated = new Document(getName(), roleList);
        getPlayerTable().getCollection().updateOne(playerDoc, new Document("$set", updated));
    }
}
