package uwi.dcit.agriexpensesvr;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;

import java.util.logging.Logger;

import javax.inject.Named;

/**
 * An endpoint class we are exposing
 */
@Api(
        name = "upAccApi",
        version = "v1",
        resource = "upAcc",
        namespace = @ApiNamespace(
                ownerDomain = "agriexpensesvr.dcit.uwi",
                ownerName = "agriexpensesvr.dcit.uwi",
                packagePath = ""
        )
)
public class UpAccEndpoint {

    private static final Logger logger = Logger.getLogger(UpAccEndpoint.class.getName());

    /**
     * This method gets the <code>UpAcc</code> object associated with the specified <code>id</code>.
     *
     * @param id The id of the object to be returned.
     * @return The <code>UpAcc</code> associated with <code>id</code>.
     */
    @ApiMethod(name = "getUpAcc")
    public UpAcc getUpAcc(@Named("id") Long id) {
        // TODO: Implement this function
        logger.info("Calling getUpAcc method");
        return null;
    }

    /**
     * This inserts a new <code>UpAcc</code> object.
     *
     * @param upAcc The object to be added.
     * @return The object to be added.
     */
    @ApiMethod(name = "insertUpAcc")
    public UpAcc insertUpAcc(UpAcc upAcc) {
        // TODO: Implement this function
        logger.info("Calling insertUpAcc method");
        return upAcc;
    }
}