package box;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

/**
 * @author Daniyar Itegulov
 */
public class BoxAccount {
    private static final String FOLDERS_URL = "https://api.box.com/2.0/folders/";
    private static final JsonParser parser = new JsonParser();
    private String accessToken;
    private String refreshToken;
    private BoxElement root;

    public BoxAccount(String json) throws IOException {
        JsonObject jsonObject = parser.parse(json).getAsJsonObject();
        accessToken = jsonObject.get("access_token").getAsString();
        refreshToken = jsonObject.get("refresh_token").getAsString();
        root = BoxParser.parse(list(0), this);
    }

    public String list(long folderId) throws IOException {
        HttpGet request = new HttpGet(FOLDERS_URL + folderId);
        request.addHeader("Authorization", "Bearer " + accessToken);
        HttpClient httpclient = HttpClientBuilder.create().build();
        try (CloseableHttpResponse response = (CloseableHttpResponse) httpclient.execute(request)) {
            StatusLine statusLine = response.getStatusLine();
            System.out.println("List: " + statusLine);
            if (statusLine.getStatusCode() != 200) {
                throw new IllegalStateException("Got status code: " + statusLine.getStatusCode() + ", expected 200");
            }
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                String entityString = EntityUtils.toString(entity);
                System.out.println("List: GOT " + entityString);
                return entityString;
            } else {
                throw new IllegalStateException("No entity");
            }
        }
    }


    @Override
    public String toString() {
        return "BoxAccount{" +
                "accessToken='" + accessToken + '\'' +
                ", refreshToken='" + refreshToken + '\'' +
                ", root=" + root +
                '}';
    }
}
