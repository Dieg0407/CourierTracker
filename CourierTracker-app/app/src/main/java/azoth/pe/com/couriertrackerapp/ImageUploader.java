package azoth.pe.com.couriertrackerapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class ImageUploader extends AsyncTask<Bitmap, Void, String> {

    private Bitmap bitmap;
    private String url;
    private String codigo;
    private int numero;
    private Context ctx;

    public ImageUploader(Bitmap bitmap, String codigo, int numero, String url, Context ctx){
        this.bitmap = bitmap;
        this.url = url;
        this.codigo = codigo;
        this.numero = numero;
        this.ctx = ctx;
    }

    @Override
    protected String doInBackground(Bitmap... bitmaps) {
        try{
            URL url = new URL(this.url+"/TrackerAPI/img/putImage/"+
                    codigo+"/"+this.numero);

            HttpsURLConnection httpsUrlConnection = (HttpsURLConnection) url.openConnection();
            httpsUrlConnection.setUseCaches(false);
            httpsUrlConnection.setDoOutput(true);

            httpsUrlConnection.setRequestMethod("POST");
            httpsUrlConnection.setRequestProperty("Connection", "Keep-Alive");
            httpsUrlConnection.setRequestProperty("Cache-Control", "no-cache");
            httpsUrlConnection.setRequestProperty(
                    "Content-Type", "multipart/form-data;boundary=" + "*****");


            String attachmentName = "Imagen";
            String attachmentFileName = "Imagen.jpg";
            String crlf = "\r\n";
            String twoHyphens = "--";
            String boundary =  "*****";


            //CONTENT WRAPPER
            DataOutputStream request = new DataOutputStream(
                    httpsUrlConnection.getOutputStream());

            request.writeBytes("--" + "*****" + "\r\n");
            request.writeBytes("Content-Disposition: form-data; name=\"" +
                    "Imagen" + "\";filename=\"" +
                    "Imagen.jpg" + "\"" + "\r\n");
            request.writeBytes("\r\n");
            //BITMAP TO ByteBuffer
            byte[] pixels = new byte[bitmap.getWidth() * bitmap.getHeight()];
            for (int i = 0; i < bitmap.getWidth(); ++i) {
                for (int j = 0; j < bitmap.getHeight(); ++j) {
                    //we're interested only in the MSB of the first byte,
                    //since the other 3 bytes are identical for B&W images
                    pixels[i + j] = (byte) ((bitmap.getPixel(i, j) & 0x80) >> 7);
                }
            }

            request.write(pixels);

            //END WRAPPER
            request.writeBytes(crlf);
            request.writeBytes(twoHyphens + boundary +
                    twoHyphens + crlf);

            request.flush();
            request.close();


            //GETTING THE RESPONSE
            InputStream responseStream = new
                    BufferedInputStream(httpsUrlConnection.getInputStream());

            BufferedReader responseStreamReader =
                    new BufferedReader(new InputStreamReader(responseStream));

            String line = "";
            StringBuilder stringBuilder = new StringBuilder();

            while ((line = responseStreamReader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
            responseStreamReader.close();
            String response = stringBuilder.toString();

            responseStream.close();
            httpsUrlConnection.disconnect();

            return "Carga exitosa";
        }
        catch(MalformedURLException e){

            return "Error en la URL";
        }
        catch (IOException e){
            return "Error externo: "+e.getMessage();
        }
        //mImageView.setImageBitmap(imageBitmap);
    }
    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        //this method will be running on UI thread
        Toast.makeText(ctx, "Intentando cargar la imagen...", Toast.LENGTH_SHORT).show();
    }

}
