package google.example.appv5.appreconocimientov5;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnFailureListener;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.cloud.FirebaseVisionCloudDetectorOptions;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.label.FirebaseVisionCloudImageLabelerOptions;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabel;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabeler;
import com.google.firebase.ml.vision.label.FirebaseVisionOnDeviceImageLabelerOptions;
import com.wonderkiln.camerakit.CameraKit;
import com.wonderkiln.camerakit.CameraKitError;
import com.wonderkiln.camerakit.CameraKitEvent;
import com.wonderkiln.camerakit.CameraKitEventListener;
import com.wonderkiln.camerakit.CameraKitImage;
import com.wonderkiln.camerakit.CameraKitVideo;
import com.wonderkiln.camerakit.CameraView;

import java.util.List;

import dmax.dialog.SpotsDialog;
import es.dmoral.toasty.Toasty;
import google.example.appv5.appreconocimientov5.helper.InternetCheck;


public class Usuario extends Activity {

    private CardView cardCamara;
    private LottieAnimationView lottieAnimationView;
    CameraView cameraview;
    AlertDialog waitingDialog;

    @Override
    protected void onResume() {
        super.onResume();
        cameraview.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        cameraview.stop();
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);

        waitingDialog = new SpotsDialog.Builder().setMessage("Dame un momento...")
                .setContext(this)
                .setCancelable(false)
                .build();

        lottieAnimationView = findViewById(R.id.lottie);
        lottieAnimationView.setAnimation("camera.json");

        cameraview = (CameraView) findViewById(R.id.camera_view);
        cameraview.addCameraKitListener(new CameraKitEventListener() {

            @Override
            public void onEvent(CameraKitEvent cameraKitEvent) {

            }

            @Override
            public void onError(CameraKitError cameraKitError) {

            }

            @Override
            public void onImage(CameraKitImage cameraKitImage) {
                waitingDialog.show();
                Bitmap bitmap = cameraKitImage.getBitmap();
                bitmap = Bitmap.createScaledBitmap(bitmap, cameraview.getWidth(),cameraview.getHeight(),false);
                cameraview.stop();
                runDetector(bitmap);
            }

            @Override
            public void onVideo(CameraKitVideo cameraKitVideo) {

            }
        });

        lottieAnimationView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lottieAnimationView.playAnimation();
                cameraview.start();
                cameraview.setFocus(CameraKit.Constants.FOCUS_TAP);
                cameraview.captureImage();

            }
        });


    }

    private void runDetector(final Bitmap bitmap ) {

        final FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap);

        new InternetCheck(new InternetCheck.Consumer() {
            @Override
            public void accept(boolean internet) {
                if(internet){
                    FirebaseVisionCloudImageLabelerOptions options =
                            new FirebaseVisionCloudImageLabelerOptions.Builder()
                                    .build();
                    FirebaseVisionImageLabeler detector = FirebaseVision.getInstance().getCloudImageLabeler(options);

                    detector.processImage(image)
                            .addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionImageLabel>>() {
                                @Override
                                public void onSuccess(List<FirebaseVisionImageLabel> firebaseVisionCloudLabels) {
                                    processDataResultCloud(firebaseVisionCloudLabels);
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d("EDMTERROR", e.getMessage());
                                }
                            });
                }
                else{
                    FirebaseVisionOnDeviceImageLabelerOptions options =
                            new FirebaseVisionOnDeviceImageLabelerOptions.Builder()
                                    .setConfidenceThreshold(0.8f)
                                    .build();
                    FirebaseVisionImageLabeler detector = FirebaseVision.getInstance().getOnDeviceImageLabeler(options);

                    detector.processImage(image)
                            .addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionImageLabel>>() {
                                @Override
                                public void onSuccess(List<FirebaseVisionImageLabel> firebaseVisionLabels) {
                                    processDataResult(firebaseVisionLabels);

                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d("EDMTERROR", e.getMessage());
                                }
                            });
                }
            }
        });
    }

//    Online
    private void processDataResultCloud(List<FirebaseVisionImageLabel> firebaseVisionCloudLabels) {
        for (FirebaseVisionImageLabel label : firebaseVisionCloudLabels){
            Toasty.success(this,"" + label.getText(),Toast.LENGTH_SHORT,false).show();
        }
        if (waitingDialog.isShowing())
            waitingDialog.dismiss();
    }

//    Offline
    private void processDataResult(List<FirebaseVisionImageLabel> firebaseVisionCloudLabels) {
        for (FirebaseVisionImageLabel label : firebaseVisionCloudLabels){
            Toasty.info(this,"" + label.getText(),Toast.LENGTH_SHORT,false).show();
        }
        if (waitingDialog.isShowing())
            waitingDialog.dismiss();
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
//        Intent EXIT = new Intent(Intent.ACTION_MAIN);
//        EXIT.addCategory(Intent.CATEGORY_HOME);
//        EXIT.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        startActivity(EXIT);
//        System.exit(0);

        final AlertDialog.Builder builder = new AlertDialog.Builder(Usuario.this);
        builder.setMessage("Estás seguro de que quieres salir?");
        builder.setCancelable(true);
        builder.setNegativeButton("Si", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                Intent EXIT = new Intent(Intent.ACTION_MAIN);
                EXIT.addCategory(Intent.CATEGORY_HOME);
                EXIT.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(EXIT);
                System.exit(0);
            }
        });
        builder.setPositiveButton("Volver!", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }



}
