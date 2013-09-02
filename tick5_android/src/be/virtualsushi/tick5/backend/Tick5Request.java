package be.virtualsushi.tick5.backend;

import android.util.Log;
import be.virtualsushi.tick5.model.Tick5Response;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonRequest;
import com.google.gson.Gson;

public class Tick5Request extends JsonRequest<Tick5Response> {

	private final static String TICK5_REQUEST_URL_BASE = "http://tick5.be/api/ticks/";

	private Gson mGson = new Gson();

	public Tick5Request(String key, Listener<Tick5Response> listener, ErrorListener errorListener) {
		super(Method.GET, TICK5_REQUEST_URL_BASE + key, null, listener, errorListener);
		setShouldCache(false);
	}

	@Override
	protected Response<Tick5Response> parseNetworkResponse(NetworkResponse response) {
		try {
			return Response.success(mGson.fromJson(new String(response.data, "UTF-8"), Tick5Response.class), getCacheEntry());
		} catch (Exception e) {
			Log.e("TICK5 REQUEST", e.getMessage());
			return Response.error(new VolleyError("Uanble to parse response: " + e.getMessage()));
		}
	}
}
