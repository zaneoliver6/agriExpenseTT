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
        name = "cycleApi",
        version = "v1",
        resource = "cycle",
        namespace = @ApiNamespace(
                ownerDomain = "agriexpensesvr.dcit.uwi",
                ownerName = "agriexpensesvr.dcit.uwi",
                packagePath = ""
        )
)
public class CycleEndpoint {

    private static final Logger logger = Logger.getLogger(CycleEndpoint.class.getName());

    /**
     * This method gets the <code>Cycle</code> object associated with the specified <code>id</code>.
     *
     * @param id The id of the object to be returned.
     * @return The <code>Cycle</code> associated with <code>id</code>.
     */
    @ApiMethod(name = "getCycle")
    public Cycle getCycle(@Named("id") Long id) {
        // TODO: Implement this function
        logger.info("Calling getCycle method");
        return null;
    }

    /**
     * This inserts a new <code>Cycle</code> object.
     *
     * @param cycle The object to be added.
     * @return The object to be added.
     */
    @ApiMethod(name = "insertCycle")
    public Cycle insertCycle(Cycle cycle) {
        // TODO: Implement this function
        logger.info("Calling insertCycle method");
        return cycle;
    }
}