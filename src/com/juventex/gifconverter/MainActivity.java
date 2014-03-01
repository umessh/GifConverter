package com.juventex.gifconverter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

public class MainActivity extends Activity {

	private static final int REQ_CODE_PICK_IMAGE = 100;
	ImageView curImageView;
	int curPostion;
	GridView gridview;
	String imgPaths[]={"","","","",""};	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SeekBar delayBar = (SeekBar) findViewById(R.id.delayBar);
        delayBar.setMax(1000);
        gridview = (GridView) findViewById(R.id.gridview);
        gridview.setAdapter(new ImageAdapter(this));
        
        
        gridview.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
            	Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
            	photoPickerIntent.setType("image/*");
            	startActivityForResult(photoPickerIntent, REQ_CODE_PICK_IMAGE);  
            	curImageView = (ImageView) v;
            	curPostion = position;
                //Toast.makeText(MainActivity.this, "" + position, Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    public void onClick(View v)
    {
    	ByteArrayOutputStream bos = new ByteArrayOutputStream();
    	AnimatedGifEncoder animGif = new AnimatedGifEncoder();
    	animGif.start(bos);
    	animGif.setRepeat(0);
    	EditText fileText = (EditText) findViewById(R.id.fileText);
    	SeekBar delayBar = (SeekBar) findViewById(R.id.delayBar);
    	int fps=delayBar.getProgress();
    	animGif.setDelay(fps);
    	Bitmap anim1;//anim2,anim3,anim4,anim5;
    	for(int i=0;i<5;i++)
    	{
    		//Toast.makeText(this, imgPaths[i]+"start", Toast.LENGTH_SHORT);
    		anim1 = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(imgPaths[i]), 512, 512);//BitmapFactory.decodeResource(getResources(), R.drawable.anim1);
        	//Toast.makeText(this, imgPaths[i]+"finish", Toast.LENGTH_SHORT);
    		animGif.addFrame(anim1);
        	anim1.recycle();
    	}
    	/*anim1 = BitmapFactory.decodeResource(getResources(), R.drawable.anim1);
    	animGif.addFrame(anim1);
    	anim1.recycle();
    	anim2 = BitmapFactory.decodeResource(getResources(), R.drawable.anim2);
    	animGif.addFrame(anim2);
    	anim2.recycle();
    	anim3 = BitmapFactory.decodeResource(getResources(), R.drawable.anim3);
    	animGif.addFrame(anim3);
    	anim3.recycle();
    	anim4 = BitmapFactory.decodeResource(getResources(), R.drawable.anim4);
    	animGif.addFrame(anim4);
    	anim4.recycle();*/

    	animGif.finish();
    	//Toast.makeText(this, "file creation start", Toast.LENGTH_SHORT);
    	save(bos,fileText.getText().toString());
    	//Toast.makeText(this, "file creation finish", Toast.LENGTH_SHORT);
    }
    
    private void save(ByteArrayOutputStream os, String fileName){
        FileOutputStream fos;
        try {
        	File folder = new File(Environment.getExternalStorageDirectory() + "/GifConverter");
        	boolean success = true;
        	if (!folder.exists()) {
        	    success = folder.mkdir();
        	}
        	if (success) {
	            fos = new FileOutputStream(Environment.getExternalStorageDirectory().getPath()+"/GifConverter/"+fileName+".gif");
	            os.writeTo(fos);
	            os.flush();
	            fos.flush();
	            os.close();
	            fos.close();
        	 } else {
         	    Toast.makeText(this, "Folder creation filed", Toast.LENGTH_SHORT).show();
         	}
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } 
       
    }
    
    protected void onActivityResult(int requestCode, int resultCode, 
    	       Intent imageReturnedIntent) {
    	    super.onActivityResult(requestCode, resultCode, imageReturnedIntent); 

    	    switch(requestCode) { 
    	    case REQ_CODE_PICK_IMAGE:
    	        if(resultCode == RESULT_OK){  
    	            Uri selectedImage = imageReturnedIntent.getData();
    	            String[] filePathColumn = {MediaStore.Images.Media.DATA};

    	            Cursor cursor = getContentResolver().query(
    	                               selectedImage, filePathColumn, null, null, null);
    	            cursor.moveToFirst();

    	            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
    	            String filePath = cursor.getString(columnIndex);
    	            cursor.close();
    	            Bitmap yourSelectedImage = BitmapFactory.decodeFile(filePath);
    	            curImageView.setImageBitmap(ThumbnailUtils.extractThumbnail(yourSelectedImage, 120, 120));
    	            imgPaths[curPostion] = filePath;
    	            Toast.makeText(MainActivity.this, "" + selectedImage, Toast.LENGTH_SHORT).show();
    	        }
    	    }
    	}
}
