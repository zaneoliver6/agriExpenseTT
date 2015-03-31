package uwi.dcit.agriexpensesvr;

import com.google.appengine.api.datastore.Key;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Key key;
    private String keyrep;
    private long lastUpdated;
    private String account;
    private int signedIn;
    private String county;
    private String country;
    private String address;

    public Account(Key key, String keyrep, long lastUpdated, String account) {
        super();
        this.key = key;
        this.keyrep = keyrep;
        this.lastUpdated = lastUpdated;
        this.account = account;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Account() {
        super();
    }

    public Key getKey() {
        return key;
    }

    public void setKey(Key key) {
        this.key = key;
    }

    public String getKeyrep() {
        return keyrep;
    }

    public void setKeyrep(String keyrep) {
        this.keyrep = keyrep;
    }

    public long getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(long lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public int getSignedIn() {
        return signedIn;
    }

    public void setSignedIn(int signedIn) {
        this.signedIn = signedIn;
    }

    @Override
    public String toString(){
        StringBuilder stb = new StringBuilder();
        stb.append("Name: ").append(account);
        if (key != null)stb.append("Key: ").append(key);
        if (lastUpdated != -1)stb.append("Updated Time in Millis: ").append(lastUpdated);
        return stb.toString();
    }
}