package be.virtualsushi.tick5.backend;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.StringRequest;

public class PublicKeyRequest extends StringRequest {

	private static final String PUBLIC_KEY_URL = "http://tick5.be/js/pub.js";
	private static final String KEY_START_PHRASE = "var pub_key = '";
	private static final String KEY_END_PHRASE = "';";

	public PublicKeyRequest(Listener<String> listener, ErrorListener errorListener) {
		super(Method.GET, PUBLIC_KEY_URL, listener, errorListener);
		setShouldCache(false);
	}

	@Override
	protected void deliverResponse(String response) {
		super.deliverResponse(extractDynamicKey(response));
	}

	private String extractDynamicKey(String keyString) {
		String result = keyString.substring(keyString.indexOf(KEY_START_PHRASE) + KEY_START_PHRASE.length());
		return result.substring(0, result.indexOf(KEY_END_PHRASE));
	}

}
