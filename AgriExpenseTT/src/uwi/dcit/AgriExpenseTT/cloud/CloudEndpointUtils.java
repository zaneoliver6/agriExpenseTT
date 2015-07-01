package uwi.dcit.AgriExpenseTT.cloud;

import android.app.Activity;
import android.util.Log;

import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.googleapis.services.AbstractGoogleClient;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;

import java.io.IOException;

import uwi.dcit.AgriExpenseTT.Main;

/**
 * Common utilities for working with Cloud Endpoints.
 *
 * If you'd like to test using a locally-running version of your App Engine
 * backend (i.e. running on the Development App Server), you need to set
 * LOCAL_ANDROID_RUN to 'true'.
 *
 * See the documentation at
 * http://developers.google.com/eclipse/docs/cloud_endpoints for more
 * information.
 */
public class CloudEndpointUtils {
	//To ensure proper connection between backend and application, obtain the IPv4 Address
	//and set the LOCAL_APP_ENGINE_SERVER_URL to this address.
	public static final boolean LOCAL_ANDROID_RUN = true;
//	protected static final String LOCAL_APP_ENGINE_SERVER_URL = "http://10.0.10.139:8080";
	protected static final String LOCAL_APP_ENGINE_SERVER_URL = "http://10.0.4.180:8080";
//	protected static final String LOCAL_APP_ENGINE_SERVER_URL = "http://10.0.2.2:8080/";
//	protected static final String LOCAL_APP_ENGINE_SERVER_URL_FOR_ANDROID = "http://10.0.2.2:8080";
    protected static final String REMOTE_APP_ENGINE_SERVER_URL = "https://centering-dock-715.appspot.com";

	/**
	 * Updates the Google client builder to connect the appropriate server based
	 * on whether LOCAL_ANDROID_RUN is true or false.
	 *
	 * @param builder
	 *            Google client builder
	 * @return same Google client builder
	 */
	public static <B extends AbstractGoogleClient.Builder> B updateBuilder(B builder) {
		if (LOCAL_ANDROID_RUN) {
            builder.setRootUrl(LOCAL_APP_ENGINE_SERVER_URL + "/_ah/api/");
		}else{
            builder.setRootUrl(REMOTE_APP_ENGINE_SERVER_URL + "/_ah/api/");
        }

        builder.setApplicationName("AgriExpenseAndroid");

		// only enable GZip when connecting to remote server
		final boolean enableGZip = builder.getRootUrl().startsWith("https:");

		builder.setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
			@Override
			public void initialize(AbstractGoogleClientRequest<?> request)
					throws IOException {
				if (!enableGZip) {
					request.setDisableGZipContent(true);
				}
			}
		});

		return builder;
	}

	/**
	 * Logs the given message and shows an error alert dialog with it.
	 *
	 * @param activity
	 *            activity
	 * @param tag
	 *            log tag to use
	 * @param message
	 *            message to log and show or {@code null} for none
	 */
	public static void logAndShow(Activity activity, String tag, String message) {
		Log.e(tag, message);
		showError(activity, message);
	}

	/**
	 * Logs the given throwable and shows an error alert dialog with its
	 * message.
	 *
	 * @param activity
	 *            activity
	 * @param tag
	 *            log tag to use
	 * @param t
	 *            throwable to log and show
	 */
	public static void logAndShow(Activity activity, String tag, Throwable t) {
		Log.e(tag, "Error", t);
		String message = t.getMessage();
		// Exceptions that occur in your Cloud Endpoint implementation classes
		// are wrapped as GoogleJsonResponseExceptions
		if (t instanceof GoogleJsonResponseException) {
			GoogleJsonError details = ((GoogleJsonResponseException) t)
					.getDetails();
			if (details != null) {
				message = details.getMessage();
			}
		}
		showError(activity, message);
	}

	/**
	 * Shows an error alert dialog with the given message.
	 *
	 * @param activity
	 *            activity
	 * @param message
	 *            message to show or {@code null} for none
	 */
	public static void showError(final Activity activity, String message) {
		final String errorMessage = message == null ? "Error" : "[Error ] "
				+ message;
		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Log.e(Main.APP_NAME, errorMessage);
			}
		});
	}
}
