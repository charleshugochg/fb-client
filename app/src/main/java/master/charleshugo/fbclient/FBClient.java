package master.charleshugo.fbclient;
import android.os.*;
import java.io.*;
import java.net.*;
import java.util.*;

public class FBClient extends AsyncTask<String, Integer, String>
{
    private String id = null;

    private OnResponseListener listener;

    public FBClient () {
        listener = null;
    }

    public interface OnResponseListener {
        public void onResponse(String hdlink, String sdlink);
    }

    public FBClient setOnResponseListener(OnResponseListener listener){
        this.listener = listener;
        return this;
    }

    public FBClient post(String id){
        if(id.equals(this.id)) return this;

        this.id = id;

        this.execute(id);

        return this;
    }

    private String findHdlink(String body) {
        if(body == null) return null;

        String[] tokens = body.split("id=\"hdlink\"");
        if(tokens.length < 2) return null;
        tokens = tokens[1].split("href=\"");
        if(tokens.length < 2) return null;
        tokens = tokens[1].split("\"");
        if(tokens.length < 2) return null;

        return tokens[0];
    }

    private String findSdlink(String body) {
        if(body == null) return null;

        String[] tokens = body.split("id=\"sdlink\"");
        if(tokens.length < 2) return null;
        tokens = tokens[1].split("href=\"");
        if(tokens.length < 2) return null;
        tokens = tokens[1].split("\"");
        if(tokens.length < 2) return null;

        return tokens[0];
    }

    @Override
    protected String doInBackground(String[] ids)
    {
        String response = null;

        try
        {
            URL url = new URL("https://fbdown.net/download.php");
            HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
            urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0");
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new NameValuePair("URLz", "https://www.facebook.com/video.php?v=" + ids[0]));

            OutputStream os = urlConnection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(getQuery(params));
            writer.flush();
            writer.close();
            os.close();

            urlConnection.connect();
            try
            {
                response = readStream(urlConnection.getInputStream());
            }
            finally
            {
                urlConnection.disconnect();
            }
        }
        catch (MalformedURLException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return response;
    }

    @Override
    protected void onPostExecute(String response)
    {
        // TODO: Implement this method
        if(listener != null){
            listener.onResponse(findHdlink(response), findSdlink(response));
        }
    }

    private String readStream(InputStream stream) throws IOException{
        BufferedReader r = new BufferedReader(new InputStreamReader(stream));
        StringBuilder builder = new StringBuilder();

        for(String line; (line = r.readLine()) != null; ) {
            //Log.d("Debug", line);
            builder.append(line);
        }
        return builder.toString();
    }

    private String getQuery(List<NameValuePair> params) throws UnsupportedEncodingException
    {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        for (NameValuePair pair : params)
        {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(pair.getName(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(pair.getValue(), "UTF-8"));
        }

        return result.toString();
    }
}
