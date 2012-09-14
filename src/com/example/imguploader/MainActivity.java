package com.example.imguploader;

import java.io.File;

import com.example.imguploader.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;

/*
 * Code from 
 * http://gargidas.blogspot.com/2012/01/hello-all-as-promised-in-one-of.html
 * 
 * Modificado por @waxkun
 * 
 */



public class MainActivity extends Activity {

	static final int REQUEST_CODE = 0;
	static final String url = "http://anakena.dcc.uchile.cl/~mquezada/upload.php";
	String captured_image;
	ProgressDialog simpleWaitDialog;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // intent para tomar una foto (abrira la app camara)
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        
        // nombre de la foto que vamos a tomar
        captured_image = "TEST.jpg";
        
        // ubicacion de la foto que guardaremos
        File file = new File(Environment.getExternalStorageDirectory(), captured_image); 
//        captured_image = file.getAbsolutePath();
        
        Uri outputFileUri = Uri.fromFile(file);
        System.out.println(outputFileUri);
        // le pasamos los datos de donde queremos guardar la foto
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri); 
        intent.putExtra("return-data", true);
        
        // iniciamos la camara, pasandole un codigo para saber despues que el resultado vino de ahi
        startActivityForResult(intent, REQUEST_CODE);
        
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	switch (requestCode) { 
    	// este es el resultado de tomar la foto!
        case REQUEST_CODE:
            switch( resultCode )  {
                case RESULT_CANCELED:
                	// usuario cancelo la foto
                    break;
                case RESULT_OK:
                    // la foto se guardo, falta subirla a un servidor
                	System.out.println(captured_image);
                	new ImageUploaderTask().execute(captured_image, url);
                    break;
                }
            break;
    	}   
    }
    
    private class ImageUploaderTask extends AsyncTask<String, Integer, Boolean> {
    	@Override
    	protected void onPreExecute(){
    		simpleWaitDialog = ProgressDialog.show(MainActivity.this, "Wait", "Uploading Image");
    	}
    	@Override
    	protected Boolean doInBackground(String... params) {
    		String image_name = params[0];
    		String url = params[1];
    		return ImageUploadUtility.uploadSingleImage(image_name, url);
    	}
    	@Override
    	protected void onPostExecute(Boolean result){
    		simpleWaitDialog.dismiss();
    		if(!result) {
    			AlertDialog error = new AlertDialog.Builder(MainActivity.this).create();
    			error.setMessage("No se subio la imagen :(");
    			error.setTitle("ERROR!!!");
    			error.show();
    		}
    	}
    }

}
