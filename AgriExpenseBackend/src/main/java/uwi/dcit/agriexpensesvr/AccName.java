package uwi.dcit.agriexpensesvr;

import com.google.appengine.api.datastore.Key;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * Created by Steffan on 03/02/2015.
 */
@Entity
public class AccName {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Key key;
    private String account;

    public Key getKey() {
        return key;
    }

    public void setKey(Key key) {
        this.key = key;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }
}
