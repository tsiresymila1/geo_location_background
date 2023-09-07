package ts.mila.geo_position_background.models;


import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class Device extends RealmObject {
    @PrimaryKey
    private String id;

    @Required
    private String model;

    @Required
    private String uuid;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }


    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Device(String model, String uuid) {
        this.model = model;
        this.uuid = uuid;
    }

    public Device() {
    }
}
