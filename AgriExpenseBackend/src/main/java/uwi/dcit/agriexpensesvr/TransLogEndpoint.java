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
        name = "transLogApi",
        version = "v1",
        resource = "transLog",
        namespace = @ApiNamespace(
                ownerDomain = "agriexpensesvr.dcit.uwi",
                ownerName = "agriexpensesvr.dcit.uwi",
                packagePath = ""
        )
)
public class TransLogEndpoint {

    private static final Logger logger = Logger.getLogger(TransLogEndpoint.class.getName());

    /**
     * This method gets the <code>TransLog</code> object associated with the specified <code>id</code>.
     *
     * @param id The id of the object to be returned.
     * @return The <code>TransLog</code> associated with <code>id</code>.
     */
    @ApiMethod(name = "getTransLog")
    public TransLog getTransLog(@Named("id") Long id) {
        // TODO: Implement this function
        logger.info("Calling getTransLog method");
        return null;
    }

    /**
     * This inserts a new <code>TransLog</code> object.
     *
     * @param transLog The object to be added.
     * @return The object to be added.
     */
    @ApiMethod(name = "insertTransLog")
    public TransLog insertTransLog(TransLog transLog) {
        // TODO: Implement this function
        logger.info("Calling insertTransLog method");
        return transLog;
    }
}