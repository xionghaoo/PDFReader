package xh.widget.widgets;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.util.FitPolicy;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class PDFReaderActivity extends AppCompatActivity {

    public static final String TAG = "PDFReaderActivity";

    PDFView pdfView;
    ProgressBar progressBar;
    EditText edtURL;
    String dest_file_path = "test.pdf";
    int downloadedSize = 0, totalsize;
    String download_file_url = "http://unec.edu.az/application/uploads/2014/12/pdf-sample.pdf";
    String pdf2 = "http://www.ets.org/Media/Tests/GRE/pdf/gre_research_validity_data.pdf";
    String pdf3 = "http://jz-public-prd.oss-cn-shenzhen.aliyuncs.com/others/protocol/%E8%9E%8D%E8%B5%84%E6%9C%8D%E5%8A%A1%E5%8D%8F%E8%AE%AE_%E6%9C%89%E6%8B%85%E4%BF%9D.pdf";
    float per = 0;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdfreader);

        setTitle(download_file_url);

        pdfView = findViewById(R.id.pdf_view);
        progressBar = findViewById(R.id.progress_bar);
        progressBar.setProgress(0);

        edtURL = findViewById(R.id.url);
        edtURL.setText(pdf3);

        Button btnOpen = findViewById(R.id.open);
        btnOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = edtURL.getText().toString().trim();
                new PDFDownloadTask().execute(url);
            }
        });

    }

    private class PDFDownloadTask extends AsyncTask<String, Integer, File> {
        @Override
        protected File doInBackground(String... params) {
            String pdfUrl = params[0];
            File file = null;
            try {

                URL url = new URL(pdfUrl);
                HttpURLConnection urlConnection = (HttpURLConnection) url
                        .openConnection();

                urlConnection.setRequestMethod("GET");
                urlConnection.setDoOutput(false);
                urlConnection.setConnectTimeout(10 * 1000);
                urlConnection.setReadTimeout(10 * 1000);
                urlConnection.setRequestProperty("Connection", "Keep-Alive");
                urlConnection.setRequestProperty("Charset", "UTF-8");
//                urlConnection.setRequestProperty("Accept-Encoding", "gzip, deflate");
//                urlConnection.setRequestProperty("Referer", "https://www.jinzhucaifu.com/");
                Log.d(TAG, "open connect");
                urlConnection.connect();
                long bytetotal = urlConnection.getContentLength();
                Log.d(TAG, "content: "+bytetotal);

                // set the path where we want to save the file
                File SDCardRoot = Environment.getExternalStorageDirectory();
                // create a new file, to save the downloaded file
                file = new File(PDFReaderActivity.this.getCacheDir(), dest_file_path);

                if (!file.exists()) {
                    file.mkdir();
                }

                Log.d(TAG, "file: " + file);


                FileOutputStream fileOutput = new FileOutputStream(file);

                // Stream used for reading the data from the internet
                InputStream inputStream = urlConnection.getInputStream();

                // this is the total size of the file which we are
                // downloading
                totalsize = urlConnection.getContentLength();
                Log.d(TAG, "Starting PDF download...");

                // create a buffer...
                byte[] buffer = new byte[1024 * 1024];
                int bufferLength = 0;

                while ((bufferLength = inputStream.read(buffer)) > 0) {
                    fileOutput.write(buffer, 0, bufferLength);
                    downloadedSize += bufferLength;
                    per = ((float) downloadedSize / totalsize) * 100;

                    publishProgress((int) per);

//                    Log.d(TAG, "Total PDF File size  : "
//                            + (totalsize / 1024)
//                            + " KB\n\nDownloading PDF " + (int) per
//                            + "% complete");
                }
                // close the output stream when complete //
                fileOutput.close();
                Log.d(TAG, "Download Complete. Open PDF Application installed in the device.");

            } catch (final MalformedURLException e) {
                Log.e(TAG, "Some error occured. Press back and try again.: " + e.getLocalizedMessage());
            } catch (final IOException e) {
                Log.e(TAG, "Some error occured. Press back and try again.: " + e.getLocalizedMessage());

            } catch (final Exception e) {
                Log.e(TAG, "Some error occured. Press back and try again.: " + e.getLocalizedMessage());
            }
            return file;
        }

        @Override
        protected void onPostExecute(File file) {
            pdfView.fromFile(file)
                    .pages(0, 2, 1, 3, 3, 3) // all pages are displayed by default
                    .enableSwipe(true) // allows to block changing pages using swipe
                    .swipeHorizontal(false)
                    .enableDoubletap(true)
                    .defaultPage(0)
//                    // allows to draw something on the current page, usually visible in the middle of the screen
//                    .onDraw(onDrawListener)
//                    // allows to draw something on all pages, separately for every page. Called only for visible pages
//                    .onDrawAll(onDrawListener)
//                    .onLoad(onLoadCompleteListener) // called after document is loaded and starts to be rendered
//                    .onPageChange(onPageChangeListener)
//                    .onPageScroll(onPageScrollListener)
//                    .onError(onErrorListener)
//                    .onPageError(onPageErrorListener)
//                    .onRender(onRenderListener) // called after document is rendered for the first time
//                    // called on single tap, return true if handled, false to toggle scroll handle visibility
//                    .onTap(onTapListener)
                    .enableAnnotationRendering(false) // render annotations (such as comments, colors or forms)
                    .password(null)
                    .scrollHandle(null)
                    .enableAntialiasing(true) // improve rendering a little bit on low-res screens
                    // spacing between pages in dp. To define spacing color, set view background
                    .spacing(0)
//                    .linkHandler(DefaultLinkHandler)
                    .pageFitPolicy(FitPolicy.WIDTH)
                    .load();

        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            progressBar.setProgress(values[0]);
            if (progressBar.getProgress() >= 100) {
                progressBar.setVisibility(View.GONE);
            }
        }
    }

}
