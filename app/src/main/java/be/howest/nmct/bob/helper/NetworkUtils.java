package be.howest.nmct.bob.helper;

import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import be.howest.nmct.bob.MapsActivity;
import be.howest.nmct.bob.admin.Party;
import be.howest.nmct.bob.admin.PartyAdmin;

/**
 * Created by Nick on 02/05/2015.
 */
public class NetworkUtils
{
    private static int serverResponseCode = 0;

    //http://stackoverflow.com/questions/2938502/sending-post-data-in-android
    public static String post(String url, Party party)
    { // Create a new HttpClient and Post Header
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(url);

            try
        {
            // Add your data
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("PartyID", String.valueOf(party.getID())));
            nameValuePairs.add(new BasicNameValuePair("PartyName", party.getName()));
            nameValuePairs.add(new BasicNameValuePair("Description", party.getDescription()));
            nameValuePairs.add(new BasicNameValuePair("Address", party.getAddress()));
            nameValuePairs.add(new BasicNameValuePair("Zipcode", party.getZipcode()));
            nameValuePairs.add(new BasicNameValuePair("City", party.getCity()));
            nameValuePairs.add(new BasicNameValuePair("FromDate", party.getFromDate().toString()));
            nameValuePairs.add(new BasicNameValuePair("UntilDate", party.getUntilDate().toString()));
            nameValuePairs.add(new BasicNameValuePair("PricePresale", party.getPricePresale().toString()));
            nameValuePairs.add(new BasicNameValuePair("PriceAtTheDoor", party.getPriceAtTheDoor().toString()));
            nameValuePairs.add(new BasicNameValuePair("DiskJockey1", party.getDiskJockey1()));
            nameValuePairs.add(new BasicNameValuePair("DiskJockey2", party.getDiskJockey2()));
            nameValuePairs.add(new BasicNameValuePair("DiskJockey3", party.getDiskJockey3()));
            nameValuePairs.add(new BasicNameValuePair("Latitude", party.getLatitude().toString()));
            nameValuePairs.add(new BasicNameValuePair("Longitude", party.getLongitude().toString()));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            // Execute HTTP Post Request
            HttpResponse response = httpclient.execute(httppost);

            return response.toString();

        }
        catch (ClientProtocolException e)
        {
            Log.d("clientprotocolex", e.toString());
            e.printStackTrace();
        }
        catch (IOException e)
        {
            Log.d("IOexception", e.toString());
            e.printStackTrace();
        }

        return null;
    }



    //https://github.com/DynamsoftRD/JavaHTTPUpload/blob/master/Android/src/com/example/picupload/MainActivity.java
    public static String uploadImage(String s, Bitmap bitmap, String mCurrentPhotoPath)
    {

        String fileName = mCurrentPhotoPath;

        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        File sourceFile = new File(mCurrentPhotoPath);

        if (!sourceFile.isFile()) return null;

            try {

                // open a URL connection to the Servlet
                FileInputStream fileInputStream = new FileInputStream(sourceFile);
                URL url = new URL(s);

                // Open a HTTP  connection to  the URL
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true); // Allow Inputs
                conn.setDoOutput(true); // Allow Outputs
                conn.setUseCaches(false); // Don't use a Cached Copy
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                conn.setRequestProperty("uploadedfile", fileName);

                dos = new DataOutputStream(conn.getOutputStream());

                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=uploadedfile;filename=" + fileName + lineEnd);
                dos.writeBytes(lineEnd);

                // create a buffer of  maximum size
                bytesAvailable = fileInputStream.available();

                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];

                // read file and write it into form...
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                while (bytesRead > 0)
                {
                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                }

                // send multipart form data necesssary after file data...
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                // Responses from the server (code and message)
                serverResponseCode = conn.getResponseCode();
                String serverResponseMessage = conn.getResponseMessage();

                //conn.getInputStream()
                Log.i("uploadFile", "HTTP Response is : " + serverResponseMessage + ": " + serverResponseCode);

                //close the streams //
                fileInputStream.close();
                dos.flush();
                dos.close();

            }
            catch (MalformedURLException ex)
            {
                ex.printStackTrace();
                Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

            return String.valueOf(serverResponseCode);
        }
}




//region **Probeersels POST**
    /*
    //https://www.youtube.com/watch?v=MdyZKewSwFg
    public static String post(String url, Party party)
    {
        if (url == null || party == null) return null;

        //building paramaters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("PartyName", party.getName()));
        params.add(new BasicNameValuePair("Description", party.getDescription()));
        //params.add(new BasicNameValuePair("PartyPicture", new String(party.getPicture())));
        params.add(new BasicNameValuePair("Address", party.getAddress()));
        params.add(new BasicNameValuePair("Zipcode", party.getZipcode()));
        params.add(new BasicNameValuePair("City", party.getCity()));
        params.add(new BasicNameValuePair("From", party.getFromDate().toString()));
        params.add(new BasicNameValuePair("Until", party.getUntilDate().toString()));
        params.add(new BasicNameValuePair("Presale", party.getPricePresale().toString()));
        params.add(new BasicNameValuePair("AtTheDoor", party.getPriceAtTheDoor().toString()));
        params.add(new BasicNameValuePair("DiskJockey1", party.getDiskJockey1()));
        params.add(new BasicNameValuePair("DiskJockey2", party.getDiskJockey2()));
        params.add(new BasicNameValuePair("DiskJockey3", party.getDiskJockey3()));
        params.add(new BasicNameValuePair("Latitude", party.getLatitude().toString()));
        params.add(new BasicNameValuePair("Longitude", party.getLongitude().toString()));

        BufferedReader in = null;

        try
        {
            //setting up default Http-client and Http POST method
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);
            httpPost.setEntity(new UrlEncodedFormEntity(params));

            //getting the response and setting up the entity
            HttpResponse httpResponse = httpClient.execute(httpPost);

            //HttpEntity httpEntity = httpResponse.getEntity();
            //InputStream inputStream = httpEntity.getContent();


            try
            {
                //convert response to string
                in = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent()));
                StringBuffer buf = new StringBuffer();
                char[] chars = new char[1024];
                int read = -1;
                while ((read = in.read(chars)) != -1) buf.append(chars, 0, read);

                return buf.toString();
            }
            finally
            {
                if (in != null) in.close();
            }
        }
        catch (ClientProtocolException e)
        {
            e.printStackTrace();
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }


        return null;
    }

    //http://stackoverflow.com/questions/9767952/how-to-add-parameters-to-httpurlconnection-using-post
    public static String post2(String url, Party party)
    {
        if (url == null || party == null) return null;

        //building paramaters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("PartyName", party.getName()));
        params.add(new BasicNameValuePair("Description", party.getDescription()));
        params.add(new BasicNameValuePair("Address", party.getAddress()));
        params.add(new BasicNameValuePair("Zipcode", party.getZipcode()));
        params.add(new BasicNameValuePair("City", party.getCity()));
        params.add(new BasicNameValuePair("From", party.getFromDate().toString()));
        params.add(new BasicNameValuePair("Until", party.getUntilDate().toString()));
        params.add(new BasicNameValuePair("Presale", party.getPricePresale().toString()));
        params.add(new BasicNameValuePair("AtTheDoor", party.getPriceAtTheDoor().toString()));
        params.add(new BasicNameValuePair("DiskJockey1", party.getDiskJockey1()));
        params.add(new BasicNameValuePair("DiskJockey2", party.getDiskJockey2()));
        params.add(new BasicNameValuePair("DiskJockey3", party.getDiskJockey3()));
        params.add(new BasicNameValuePair("Latitude", party.getLatitude().toString()));
        params.add(new BasicNameValuePair("Longitude", party.getLongitude().toString()));

        BufferedReader in = null;

        try
        {
            URL u = new URL(url);
            HttpsURLConnection conn = (HttpsURLConnection) u.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write(getQuery(params));
            writer.flush();
            writer.close();
            os.close();

            conn.connect();

        }
        catch (MalformedURLException e)
        {
            Log.d("MafformedURL", e.toString());
            e.printStackTrace();
        }
        catch (ProtocolException e)
        {
            Log.d("Protocolex", e.toString());
            e.printStackTrace();
        }
        catch (IOException e)
        {
            Log.d("IOexception", e.toString());
            e.printStackTrace();
        }
        finally
        {
            try
            {
                return getQuery(params).toString();
            }
            catch (UnsupportedEncodingException e)
            {
                e.printStackTrace();
            }
        }

        return null;
    }

    private static String getQuery(List<NameValuePair> params) throws UnsupportedEncodingException
    {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        for (NameValuePair pair : params)
        {
            if (first) first = false;
            else result.append("&");

            result.append(URLEncoder.encode(pair.getName(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(pair.getValue(), "UTF-8"));
        }

        return result.toString();
    }
    */
//endregion
