/*
 * Copyright (c) 2017 Razeware LLC
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * Notwithstanding the foregoing, you may not use, copy, modify, merge, publish, 
 * distribute, sublicense, create a derivative work, and/or sell copies of the 
 * Software in any work that is designed, intended, or marketed for pedagogical or 
 * instructional purposes related to programming, coding, application development, 
 * or information technology.  Permission for such use, copying, modification,
 * merger, publication, distribution, sublicensing, creation of derivative works, 
 * or sale is expressly withheld.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.carpool.tagalong.camerafilters;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.carpool.tagalong.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.android.gms.vision.face.Landmark;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static android.view.View.GONE;


public final class FaceActivity extends AppCompatActivity {

  private static final String TAG = "FaceActivity";

  private static final int RC_HANDLE_GMS = 9001;
  // permission request codes need to be < 256
  private static final int RC_HANDLE_CAMERA_PERM = 255;

  private CameraSource mCameraSource = null;
  private CameraSourcePreview mPreview;
  private GraphicOverlay mGraphicOverlay;
  private boolean mIsFrontFacing = true;
  private static final int MASK[] = {
          R.id.no_filter,
          R.id.hair,
          R.id.op,
          R.id.snap,
          R.id.glasses2,
          R.id.glasses3,
          R.id.glasses4,
          R.id.glasses5,
          R.id.mask,
          R.id.mask2,
          R.id.mask3,
          R.id.dog,
          R.id.cat2
  };
  private int typeFace = 0;


  // Activity event handlers
  // =======================

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Log.d(TAG, "onCreate called.");

    setContentView(R.layout.activity_face);

    mPreview = (CameraSourcePreview) findViewById(R.id.preview);
    mGraphicOverlay = (GraphicOverlay) findViewById(R.id.faceOverlay);
    final ImageButton button = (ImageButton) findViewById(R.id.change);
    button.setOnClickListener(mSwitchCameraButtonListener);

    if (savedInstanceState != null) {
      mIsFrontFacing = savedInstanceState.getBoolean("IsFrontFacing");
    }

    ImageButton face = (ImageButton) findViewById(R.id.face);
    face.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        if(findViewById(R.id.scrollView).getVisibility() == GONE){
          findViewById(R.id.scrollView).setVisibility(View.VISIBLE);
          ((ImageButton) findViewById(R.id.face)).setImageResource(R.drawable.face_select);
        }else{
          findViewById(R.id.scrollView).setVisibility(GONE);
          ((ImageButton) findViewById(R.id.face)).setImageResource(R.drawable.face);
        }
      }
    });

    ImageButton no_filter = (ImageButton) findViewById(R.id.no_filter);
    no_filter.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        findViewById(MASK[typeFace]).setBackgroundResource(R.drawable.round_background);
        typeFace = 0;
        findViewById(MASK[typeFace]).setBackgroundResource(R.drawable.round_background_select);
      }
    });

    ImageButton hair = (ImageButton) findViewById(R.id.hair);
    hair.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        findViewById(MASK[typeFace]).setBackgroundResource(R.drawable.round_background);
        typeFace = 1;
        findViewById(MASK[typeFace]).setBackgroundResource(R.drawable.round_background_select);
      }
    });

    ImageButton op = (ImageButton) findViewById(R.id.op);
    op.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        findViewById(MASK[typeFace]).setBackgroundResource(R.drawable.round_background);
        typeFace = 2;
        findViewById(MASK[typeFace]).setBackgroundResource(R.drawable.round_background_select);
      }
    });

    ImageButton snap = (ImageButton) findViewById(R.id.snap);
    snap.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        findViewById(MASK[typeFace]).setBackgroundResource(R.drawable.round_background);
        typeFace = 3;
        findViewById(MASK[typeFace]).setBackgroundResource(R.drawable.round_background_select);
      }
    });

    ImageButton glasses2 = (ImageButton) findViewById(R.id.glasses2);
    glasses2.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        findViewById(MASK[typeFace]).setBackgroundResource(R.drawable.round_background);
        typeFace = 4;
        findViewById(MASK[typeFace]).setBackgroundResource(R.drawable.round_background_select);
      }
    });

    ImageButton glasses3 = (ImageButton) findViewById(R.id.glasses3);
    glasses3.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        findViewById(MASK[typeFace]).setBackgroundResource(R.drawable.round_background);
        typeFace = 5;
        findViewById(MASK[typeFace]).setBackgroundResource(R.drawable.round_background_select);
      }
    });

    ImageButton glasses4 = (ImageButton) findViewById(R.id.glasses4);
    glasses4.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        findViewById(MASK[typeFace]).setBackgroundResource(R.drawable.round_background);
        typeFace = 6;
        findViewById(MASK[typeFace]).setBackgroundResource(R.drawable.round_background_select);
      }
    });

    ImageButton glasses5 = (ImageButton) findViewById(R.id.glasses5);
    glasses5.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        findViewById(MASK[typeFace]).setBackgroundResource(R.drawable.round_background);
        typeFace = 7;
        findViewById(MASK[typeFace]).setBackgroundResource(R.drawable.round_background_select);
      }
    });

    ImageButton mask = (ImageButton) findViewById(R.id.mask);
    mask.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        findViewById(MASK[typeFace]).setBackgroundResource(R.drawable.round_background);
        typeFace = 8;
        findViewById(MASK[typeFace]).setBackgroundResource(R.drawable.round_background_select);
      }
    });

    ImageButton mask2 = (ImageButton) findViewById(R.id.mask2);
    mask2.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        findViewById(MASK[typeFace]).setBackgroundResource(R.drawable.round_background);
        typeFace = 9;
        findViewById(MASK[typeFace]).setBackgroundResource(R.drawable.round_background_select);
      }
    });

    ImageButton mask3 = (ImageButton) findViewById(R.id.mask3);
    mask3.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        findViewById(MASK[typeFace]).setBackgroundResource(R.drawable.round_background);
        typeFace = 10;
        findViewById(MASK[typeFace]).setBackgroundResource(R.drawable.round_background_select);
      }
    });

    ImageButton dog = (ImageButton) findViewById(R.id.dog);
    dog.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        findViewById(MASK[typeFace]).setBackgroundResource(R.drawable.round_background);
        typeFace = 11;
        findViewById(MASK[typeFace]).setBackgroundResource(R.drawable.round_background_select);
      }
    });

    ImageButton cat2 = (ImageButton) findViewById(R.id.cat2);
    cat2.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        findViewById(MASK[typeFace]).setBackgroundResource(R.drawable.round_background);
        typeFace = 12;
        findViewById(MASK[typeFace]).setBackgroundResource(R.drawable.round_background_select);
      }
    });

    ImageButton button1= (ImageButton) findViewById(R.id.change);
    button.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
      }
    });

    // Start using the camera if permission has been granted to this app,
    // otherwise ask for permission to use it.
    int rc = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
    if (rc == PackageManager.PERMISSION_GRANTED) {
      createCameraSource();
    } else {
      requestCameraPermission();
    }
  }

  private View.OnClickListener mSwitchCameraButtonListener = new View.OnClickListener() {
    public void onClick(View v) {
      mIsFrontFacing = !mIsFrontFacing;

      if (mCameraSource != null) {
        mCameraSource.release();
        mCameraSource = null;
      }

      createCameraSource();
      startCameraSource();
    }
  };

  @Override
  protected void onResume() {
    super.onResume();
    Log.d(TAG, "onResume called.");

    startCameraSource();
  }

  @Override
  protected void onPause() {
    super.onPause();

    mPreview.stop();
  }

  @Override
  public void onSaveInstanceState(Bundle savedInstanceState) {
    super.onSaveInstanceState(savedInstanceState);
    savedInstanceState.putBoolean("IsFrontFacing", mIsFrontFacing);
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();

    if (mCameraSource != null) {
      mCameraSource.release();
    }
  }

  // Handle camera permission requests
  // =================================

  private void requestCameraPermission() {
    Log.w(TAG, "Camera permission not acquired. Requesting permission.");

    final String[] permissions = new String[]{Manifest.permission.CAMERA};
    if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
      Manifest.permission.CAMERA)) {
      ActivityCompat.requestPermissions(this, permissions, RC_HANDLE_CAMERA_PERM);
      return;
    }

    final Activity thisActivity = this;
    View.OnClickListener listener = new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        ActivityCompat.requestPermissions(thisActivity, permissions, RC_HANDLE_CAMERA_PERM);
      }
    };
    Snackbar.make(mGraphicOverlay, R.string.permission_camera_rationale,
      Snackbar.LENGTH_INDEFINITE)
      .setAction(R.string.ok, listener)
      .show();
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                         @NonNull int[] grantResults) {
    if (requestCode != RC_HANDLE_CAMERA_PERM) {
      Log.d(TAG, "Got unexpected permission result: " + requestCode);
      super.onRequestPermissionsResult(requestCode, permissions, grantResults);
      return;
    }

    if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
      // We have permission to access the camera, so create the camera source.
      Log.d(TAG, "Camera permission granted - initializing camera source.");
      createCameraSource();
      return;
    }

    // If we've reached this part of the method, it means that the user hasn't granted the app
    // access to the camera. Notify the user and exit.
    Log.e(TAG, "Permission not granted: results len = " + grantResults.length +
      " Result code = " + (grantResults.length > 0 ? grantResults[0] : "(empty)"));
    DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
      public void onClick(DialogInterface dialog, int id) {
        finish();
      }
    };
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setTitle(R.string.app_name)
      .setMessage(R.string.no_camera_permission)
      .setPositiveButton(R.string.disappointed_ok, listener)
      .show();
  }

  // Camera source
  // =============

  private void createCameraSource() {
    Log.d(TAG, "createCameraSource called.");

    Context context = getApplicationContext();
    FaceDetector detector = createFaceDetector(context);

    int facing = CameraSource.CAMERA_FACING_FRONT;
    if (!mIsFrontFacing) {
      facing = CameraSource.CAMERA_FACING_BACK;
    }

    // The camera source is initialized to use either the front or rear facing camera.  We use a
    // relatively low resolution for the camera preview, since this is sufficient for this app
    // and the face detector will run faster at lower camera resolutions.
    //
    // However, note that there is a speed/accuracy trade-off with respect to choosing the
    // camera resolution.  The face detector will run faster with lower camera resolutions,
    // but may miss smaller faces, landmarks, or may not correctly detect eyes open/closed in
    // comparison to using higher camera resolutions.  If you have any of these issues, you may
    // want to increase the resolution.
    mCameraSource = new CameraSource.Builder(context, detector)
      .setFacing(facing)
      .setRequestedPreviewSize(320, 240)
      .setRequestedFps(60.0f)
      .setAutoFocusEnabled(true)
      .build();
  }

  private void startCameraSource() {
    Log.d(TAG, "startCameraSource called.");

    // Make sure that the device has Google Play services available.
    int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(
            getApplicationContext());
    if (code != ConnectionResult.SUCCESS) {
      Dialog dlg = GoogleApiAvailability.getInstance().getErrorDialog(this, code, RC_HANDLE_GMS);
      dlg.show();
    }

    if (mCameraSource != null) {
      try {
        mPreview.start(mCameraSource, mGraphicOverlay);
      } catch (IOException e) {
        Log.e(TAG, "Unable to start camera source.", e);
        mCameraSource.release();
        mCameraSource = null;
      }
    }
  }

  // Face detector
  // =============

  /**
   *  Create the face detector, and check if it's ready for use.
   */
  @NonNull
  private FaceDetector createFaceDetector(final Context context) {
    Log.d(TAG, "createFaceDetector called.");

    FaceDetector detector = new FaceDetector.Builder(context)
      .setLandmarkType(FaceDetector.ALL_LANDMARKS)
      .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
      .setTrackingEnabled(true)
      .setMode(FaceDetector.FAST_MODE)
      .setProminentFaceOnly(mIsFrontFacing)
      .setMinFaceSize(mIsFrontFacing ? 0.35f : 0.15f)
      .build();

    MultiProcessor.Factory<Face> factory = new MultiProcessor.Factory<Face>() {
      @Override
      public Tracker<Face> create(Face face) {
        return new FaceTracker(mGraphicOverlay, context, mIsFrontFacing);
      }
    };

    Detector.Processor<Face> processor = new MultiProcessor.Builder<>(factory).build();
    detector.setProcessor(processor);

    if (!detector.isOperational()) {
      Log.w(TAG, "Face detector dependencies are not yet available.");

      // Check the device's storage.  If there's little available storage, the native
      // face detection library will not be downloaded, and the app won't work,
      // so notify the user.
      IntentFilter lowStorageFilter = new IntentFilter(Intent.ACTION_DEVICE_STORAGE_LOW);
      boolean hasLowStorage = registerReceiver(null, lowStorageFilter) != null;

      if (hasLowStorage) {
        Log.w(TAG, getString(R.string.low_storage_error));
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int id) {
            finish();
          }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.app_name)
          .setMessage(R.string.low_storage_error)
          .setPositiveButton(R.string.disappointed_ok, listener)
          .show();
      }
    }
    return detector;
  }

  class FaceTracker extends Tracker<Face> {

    private static final String TAG = "FaceTracker";

    private GraphicOverlay mOverlay;
    private FaceGraphic mFaceGraphic;
    private Context mContext;
    private boolean mIsFrontFacing;
    private FaceData mFaceData;

    // Subjects may move too quickly to for the system to detect their detect features,
    // or they may move so their features are out of the tracker's detection range.
    // This map keeps track of previously detected facial landmarks so that we can approximate
    // their locations when they momentarily "disappear".
    private Map<Integer, PointF> mPreviousLandmarkPositions = new HashMap<>();

    // As with facial landmarks, we keep track of the eye’s previous open/closed states
    // so that we can use them during those moments when they momentarily go undetected.
    private boolean mPreviousIsLeftEyeOpen = true;
    private boolean mPreviousIsRightEyeOpen = true;


    FaceTracker(GraphicOverlay overlay, Context context, boolean isFrontFacing) {
      mOverlay = overlay;
      mContext = context;
      mIsFrontFacing = isFrontFacing;
      mFaceData = new FaceData();
    }

    // Face detection event handlers
    // =============================

    /**
     *  Called when a new face is detected.
     *  We'll create a new graphic overlay whenever this happens.
     */
    @Override
    public void onNewItem(int id, Face face) {
      mFaceGraphic = new FaceGraphic(mOverlay, mContext, mIsFrontFacing, typeFace);
    }

    /**
     *  As detected faces are tracked over time, this method is called regularly to update
     *  their information. We'll collect the updated face information and use it
     *  to update the graphic overlay.
     */
    @Override
    public void onUpdate(FaceDetector.Detections<Face> detectionResults, Face face) {
      mOverlay.add(mFaceGraphic);
      updatePreviousLandmarkPositions(face);

      // Get face dimensions.
      mFaceData.setPosition(face.getPosition());
      mFaceData.setWidth(face.getWidth());
      mFaceData.setHeight(face.getHeight());

      // Get head angles.
      mFaceData.setEulerY(face.getEulerY());
      mFaceData.setEulerZ(face.getEulerZ());

      // Get the positions of facial landmarks.
      mFaceData.setLeftEyePosition(getLandmarkPosition(face, Landmark.LEFT_EYE));
      mFaceData.setRightEyePosition(getLandmarkPosition(face, Landmark.RIGHT_EYE));
      mFaceData.setMouthBottomPosition(getLandmarkPosition(face, Landmark.LEFT_CHEEK));
      mFaceData.setMouthBottomPosition(getLandmarkPosition(face, Landmark.RIGHT_CHEEK));
      mFaceData.setNoseBasePosition(getLandmarkPosition(face, Landmark.NOSE_BASE));
      mFaceData.setMouthBottomPosition(getLandmarkPosition(face, Landmark.LEFT_EAR));
      mFaceData.setMouthBottomPosition(getLandmarkPosition(face, Landmark.LEFT_EAR_TIP));
      mFaceData.setMouthBottomPosition(getLandmarkPosition(face, Landmark.RIGHT_EAR));
      mFaceData.setMouthBottomPosition(getLandmarkPosition(face, Landmark.RIGHT_EAR_TIP));
      mFaceData.setMouthLeftPosition(getLandmarkPosition(face, Landmark.LEFT_MOUTH));
      mFaceData.setMouthBottomPosition(getLandmarkPosition(face, Landmark.BOTTOM_MOUTH));
      mFaceData.setMouthRightPosition(getLandmarkPosition(face, Landmark.RIGHT_MOUTH));

      // Determine if eyes are open.
      final float EYE_CLOSED_THRESHOLD = 0.4f;
      float leftOpenScore = face.getIsLeftEyeOpenProbability();
      if (leftOpenScore == Face.UNCOMPUTED_PROBABILITY) {
        mFaceData.setLeftEyeOpen(mPreviousIsLeftEyeOpen);
      } else {
        mFaceData.setLeftEyeOpen(leftOpenScore > EYE_CLOSED_THRESHOLD);
        mPreviousIsLeftEyeOpen = mFaceData.isLeftEyeOpen();
      }
      float rightOpenScore = face.getIsRightEyeOpenProbability();
      if (rightOpenScore == Face.UNCOMPUTED_PROBABILITY) {
        mFaceData.setRightEyeOpen(mPreviousIsRightEyeOpen);
      } else {
        mFaceData.setRightEyeOpen(rightOpenScore > EYE_CLOSED_THRESHOLD);
        mPreviousIsRightEyeOpen = mFaceData.isRightEyeOpen();
      }

      // See if there's a smile!
      // Determine if person is smiling.
      final float SMILING_THRESHOLD = 0.8f;
      mFaceData.setSmiling(face.getIsSmilingProbability() > SMILING_THRESHOLD);

      mFaceGraphic.update(mFaceData,typeFace);
//      mFaceGraphic.updateFace(face,typeFace);
    }

    /**
     *  Called when a face momentarily goes undetected.
     */
    @Override
    public void onMissing(FaceDetector.Detections<Face> detectionResults) {
      mOverlay.remove(mFaceGraphic);
    }

    /**
     *  Called when a face is assumed to be out of camera view for good.
     */
    @Override
    public void onDone() {
      mOverlay.remove(mFaceGraphic);
    }

    // Facial landmark utility methods
    // ===============================

    /** Given a face and a facial landmark position,
     *  return the coordinates of the landmark if known,
     *  or approximated coordinates (based on prior data) if not.
     */
    private PointF getLandmarkPosition(Face face, int landmarkId) {
      for (Landmark landmark : face.getLandmarks()) {
        if (landmark.getType() == landmarkId) {
          return landmark.getPosition();
        }
      }

      PointF landmarkPosition = mPreviousLandmarkPositions.get(landmarkId);
      if (landmarkPosition == null) {
        return null;
      }

      float x = face.getPosition().x + (landmarkPosition.x * face.getWidth());
      float y = face.getPosition().y + (landmarkPosition.y * face.getHeight());
      return new PointF(x, y);
    }

    private void updatePreviousLandmarkPositions(Face face) {
      for (Landmark landmark : face.getLandmarks()) {
        PointF position = landmark.getPosition();
        float xProp = (position.x - face.getPosition().x) / face.getWidth();
        float yProp = (position.y - face.getPosition().y) / face.getHeight();
        mPreviousLandmarkPositions.put(landmark.getType(), new PointF(xProp, yProp));
      }
    }
  }

}