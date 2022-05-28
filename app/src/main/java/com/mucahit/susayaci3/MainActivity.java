package com.mucahit.susayaci3;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;


public class MainActivity extends AppCompatActivity {
    private ImageView scannedImage;
    private TextView scannedData;
    Bitmap sImageBitmap;
    ActivityResultLauncher<Intent> activityCameraStarter;
    ActivityResultLauncher<String> permissionCamera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        registerLauncher();
        Button scanBtn = findViewById(R.id.scanBtn);
        scannedImage = findViewById(R.id.scannedImage);
        scannedData = findViewById(R.id.scannedData);

        scanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
                    if(ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,Manifest.permission.CAMERA)){
                        Snackbar.make(view, "Kameraya erişimek için izin gerekli.", Snackbar.LENGTH_INDEFINITE).setAction("İzin ver", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                permissionCamera.launch(Manifest.permission.CAMERA);
                            }
                        }).show();
                    } else{
                        permissionCamera.launch(Manifest.permission.CAMERA);
                    }
                } else {
                    Intent camOpen = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    activityCameraStarter.launch(camOpen);
                }
            }
        });

    }

    private void scanData() {
        InputImage image = InputImage.fromBitmap(sImageBitmap,0);
        TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
        Task<Text> result = recognizer.process(image).addOnSuccessListener(new OnSuccessListener<Text>() {
            @Override
            public void onSuccess(Text text) {
                StringBuilder result = new StringBuilder();
                for(Text.TextBlock block: text.getTextBlocks()){
                    String blockText = block.getText() + " ";
                    Point[] blockCornerPoint = block.getCornerPoints();
                    Rect blockFrame = block.getBoundingBox();
                    for(Text.Line line: block.getLines()){
                        String lineText = line.getText() +  " ";
                        Point[] lineCornerPoint = line.getCornerPoints();
                        Rect lineRect = line.getBoundingBox();
                        for(Text.Element element : line.getElements()){
                            String elementText = element.getText();
                            Point[] elementCornerPoints = element.getCornerPoints();
                            Rect elementFrame = element.getBoundingBox();
                        }
                    } result.append(blockText);
                } scannedData.setText(result);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, "Resim tarama işlemi başarısız.", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void registerLauncher(){
        activityCameraStarter = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if(result.getResultCode() == RESULT_OK){
                    if(result.getData() != null){
                        Bundle b = result.getData().getExtras();
                        sImageBitmap = (Bitmap)b.get("data");
                        scannedImage.setImageBitmap(sImageBitmap);
                        scanData();
                    }
                } else if(result.getResultCode() == RESULT_CANCELED){
                    Toast.makeText(MainActivity.this,"Fotoğraf çekilemedi!", Toast.LENGTH_LONG).show();
                }
            }
        });
        permissionCamera = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean result) {
                if(result){
                    Intent intentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    activityCameraStarter.launch(intentCamera);
                }
            }
        });
    }
}


/*    DEGİSİK METİNLER GİRİYORUM OLACAK MI DENEMEDEYİZ    */