package control;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import model.City;

public class GetDataFromApi {

	public void execute(String cityname) {
		String query;
		HttpURLConnection urlConnection = null;
		BufferedReader reader = null;
		try {
			String BASE_URL = "http://api.goeuro.com/api/v2/position/suggest/en/" + cityname;
			URL url = new URL(BASE_URL);
			urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.setRequestMethod("GET");
			urlConnection.connect();
			InputStream inputStream = urlConnection.getInputStream();
			StringBuffer buffer = new StringBuffer();
			if (inputStream == null) {
				// Nothing to do.
				return;
			}
			reader = new BufferedReader(new InputStreamReader(inputStream));

			String line;
			while ((line = reader.readLine()) != null) {
				// Since it's JSON, adding a newline isn't necessary (it won't
				// affect parsing)
				// But it does make debugging a *lot* easier if you print out
				// the completed
				// buffer for debugging.
				buffer.append(line + "\n");
				// buffer.append(line.replace("[", "").replace("]", "") + "\n");
			}

			if (buffer.length() == 0) {
				// Stream was empty. No point in parsing.
				return;
			}
			query = buffer.toString();
			// ;
			createCSVFile(parseJson(query));
		} catch (Exception ex) {

		} finally {
			if (urlConnection != null) {
				urlConnection.disconnect();
			}
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	public List<City> parseJson(String jsonData) throws JSONException {
		final String ID = "_id";
		final String NAME = "name";
		final String TYPE = "type";
		final String LON = "longitude";
		final String LAT = "latitude";
		final String GEO_POSITION = "geo_position";
		List<City> list = new ArrayList<>();
		JSONArray array = new JSONArray(jsonData);
		for (int i = 0; i < array.length(); i++) {
			JSONObject cityJson = array.getJSONObject(i);
			JSONObject cityCordinate = cityJson.getJSONObject(GEO_POSITION);
			City city = new City();
			city.setId(cityJson.getInt(ID));
			city.setName(cityJson.getString(NAME));
			city.setType(cityJson.getString(TYPE));
			city.setLatitude(cityCordinate.getDouble(LAT));
			city.setLongitude(cityCordinate.getDouble(LON));
			list.add(city);
		}
		return list;
	}

	public void createCSVFile(List<City> cities) throws FileNotFoundException {
		PrintWriter pw = new PrintWriter(new File("test.csv"));
		StringBuilder sb = new StringBuilder();
		sb.append("_id");
		sb.append(',');
		sb.append("name");
		sb.append(',');
		sb.append("type");
		sb.append(',');
		sb.append("latitude");
		sb.append(',');
		sb.append("longitude");
		sb.append('\n');
		for (City city : cities) {
			sb.append(city.getId());
			sb.append(',');
			sb.append(city.getName());
			sb.append(',');
			sb.append(city.getType());
			sb.append(',');
			sb.append(city.getLatitude());
			sb.append(',');
			sb.append(city.getLongitude());
			sb.append('\n');
		}
		pw.write(sb.toString());
		pw.close();
	}

}
