package be.virtualsushi.tick5.backend;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import be.virtualsushi.tick5.model.Tick5Response;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.JsonRequest;
import com.google.gson.Gson;

public class Tick5Request extends JsonRequest<Tick5Response> {

	private final static String TICK5_REQUEST_URL_BASE = "http://tick5.be/api/ticks/";

	private Gson mGson = new Gson();

	public Tick5Request(String key, Listener<Tick5Response> listener, ErrorListener errorListener) {
		super(Method.GET, TICK5_REQUEST_URL_BASE + key, null, listener, errorListener);
	}

	@Override
	protected Response<Tick5Response> parseNetworkResponse(NetworkResponse response) {
		ByteArrayInputStream input = null;
		InputStreamReader reader = null;
		try {
			input = new ByteArrayInputStream(response.data);
			reader = new InputStreamReader(input);
			return Response.success(mGson.fromJson(reader, Tick5Response.class), getCacheEntry());
		} finally {
			try {
				input.close();
			} catch (IOException e) {

			}
			try {
				reader.close();
			} catch (IOException e) {

			}
		}
	}
}
