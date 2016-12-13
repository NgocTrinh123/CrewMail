package com.dazone.crewemail.helper;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Environment;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.dazone.crewemail.R;
import com.dazone.crewemail.utils.Statics;
import com.dazone.crewemail.utils.Util;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorkerHelper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by THANHTUNG on 21/01/2016.
 */
public class SavePdf extends AsyncTask<Void, Void, String> {
    private Context context;
    private View view;
    private ProgressDialog pdia;
    private LinearLayout linearLayout;
    private boolean isHidden;
    private String html;

    //isHidden: true - hidden
    //false - visible
    public SavePdf(Context context, View view, boolean isHidden, String html) {
        this.context = context;
        this.view = view;
        this.isHidden = isHidden;
        this.html = html;
    }

    @Override
    protected String doInBackground(Void... params) {
        String result = "";
        if (view != null) {
            result = Save(view);
        } else {
            result = "fail";
        }
        return result;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        pdia = new ProgressDialog(context);
        pdia.setMessage(Util.getString(R.string.string_saving));
        pdia.show();
        try {
            if (isHidden) {
                linearLayout = (LinearLayout) view.findViewById(R.id.linearMailDetailInfo);
                linearLayout.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        if (isHidden == true) {
            linearLayout = (LinearLayout) view.findViewById(R.id.linearMailDetailInfo);
            linearLayout.setVisibility(View.GONE);
        }
        pdia.dismiss();
        if (result.equalsIgnoreCase("success")) {
            Util.showMessage(Util.getString(R.string.string_save_success));
        } else {
            Util.showMessage(Util.getString(R.string.string_save_fail));
        }
    }

    public String Save(View view) {
        String result = "";
        try {
            Bitmap screen = getBitmapFromView(view);
            //SaveImage(screen);
            Bitmap[] img = splitBitmap(screen);
            //First Check if the external storage is writable
            String state = Environment.getExternalStorageState();
            if (!Environment.MEDIA_MOUNTED.equals(state)) {
            }
            //Create a directory for your PDF
            String extr = Environment.getExternalStorageDirectory().toString();
            File pdfDir = new File(extr + "/CrewMail");
            String timeStamp = new SimpleDateFormat(Statics.DATE_FORMAT_PICTURE,
                    Locale.getDefault()).format(new Date());
            if (!pdfDir.exists()) {
                pdfDir.mkdir();
            }

            File pdfFile = new File(pdfDir, Util.getString(R.string.app_name) + "_" + timeStamp + ".pdf");
            Document document = new Document();
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(pdfFile));
            document.open();
            /*StringBuilder htmlString = new StringBuilder();
            htmlString.append(html);
            InputStream is = new ByteArrayInputStream(htmlString.toString().getBytes());
            XMLWorkerHelper.getInstance().parseXHtml(writer, document, is);*/

            for (Bitmap bitmap : img) {
                document.newPage();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();
                addImage(document, byteArray);
            }
            document.close();
            result = "success";
        } catch (Exception e) {
            e.printStackTrace();
            result = "fail";
        }
        return result;
    }

    public static Bitmap getBitmapFromView(View view) {

        view.setDrawingCacheEnabled(true);
        ScrollView z = (ScrollView) view.findViewById(R.id.scrollMailDetailMain);
        //LinearLayout z = (LinearLayout) view.findViewById(R.id.linearMailDetailMain);
        int totalHeight = z.getChildAt(0).getHeight();
        int totalWidth = z.getChildAt(0).getWidth();
        Bitmap returnedBitmap = Bitmap.createBitmap(totalWidth, totalHeight, Bitmap.Config.ARGB_8888);
        //Bind a canvas to it
        Canvas canvas = new Canvas(returnedBitmap);
        //Get the view's background
        Drawable bgDrawable = view.getBackground();
        if (bgDrawable != null)
            //has background drawable, then draw it on the canvas
            bgDrawable.draw(canvas);
        else
            //does not have background drawable, then draw white background on the canvas
            canvas.drawColor(Color.WHITE);
        // draw the view on the canvas
        view.draw(canvas);
        //return the bitmap
        return returnedBitmap;
    }

    private static void addImage(Document document, byte[] byteArray) {
        Image image = null;
        try {
            image = Image.getInstance(byteArray);
            image.scaleToFit(540, 800);
        } catch (BadElementException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // image.scaleAbsolute(150f, 150f);
        try {
            document.add(image);
        } catch (DocumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void SaveImage(Bitmap finalBitmap) {

        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/saved_images");
        myDir.mkdirs();
        String timeStamp = new SimpleDateFormat(Statics.DATE_FORMAT_PICTURE,
                Locale.getDefault()).format(new Date());
        String fname = Util.getString(R.string.app_name) + "_" + timeStamp + ".jpg";
        File file = new File(myDir, fname);
        if (file.exists()) file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Bitmap[] splitBitmap(Bitmap picture) {

        int height = picture.getHeight();
        int width = picture.getWidth();
        double j = width / 595.0;
        int heightTemp = (int) (842 * j);
        int tempSplit = height / (heightTemp);
        int temp = height % heightTemp;
        if (temp > 1) {
            tempSplit++;
        }
        Bitmap[] imgs = new Bitmap[tempSplit];
        int y = 0;
        for (int i = 0; i < imgs.length; i++) {
            if (y + heightTemp <= height) {
                imgs[i] = Bitmap.createBitmap(picture, 0, y, width, heightTemp);
                y = y + heightTemp;
            } else {
                imgs[i] = Bitmap.createBitmap(picture, 0, y, width, height - y);
            }
        }
        //}
        return imgs;
    }
}
