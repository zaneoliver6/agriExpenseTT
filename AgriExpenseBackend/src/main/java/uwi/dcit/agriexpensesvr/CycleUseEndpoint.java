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
        name = "cycleUseApi",
        version = "v1",
        resource = "cycleUse",
        namespace = @ApiNamespace(
                ownerDomain = "agriexpensesvr.dcit.uwi",
                ownerName = "agriexpensesvr.dcit.uwi",
                packagePath = ""
        )
)
public class CycleUseEndpoint {

    private static final Logger logger = Logger.getLogger(CycleUseEndpoint.class.getName());

    /**
     * This method gets the <code>CycleUse</code> object associated with the specified <code>id</code>.
     *
     * @param id The id of the object to be returned.
     * @return The <code>CycleUse</code> associated with <code>id</code>.
     */
    @ApiMethod(name = "getCycleUse")
    public CycleUse getCycleUse(@Named("id") Long id) {
        // TODO: Implement this function
        logger.info("Calling getCycleUse method");
        return null;
    }

    /**
     * This inserts a new <code>CycleUse</code> object.
     *
     * @param cycleUse The object to be added.
     * @return The object to be added.
     */
    @ApiMethod(name = "insertCycleUse")
    public CycleUse insertCycleUse(CycleUse cycleUse) {
        // TODO: Implement this function
        logger.info("Calling insertCycleUse method");
        return cycleUse;
    }
}