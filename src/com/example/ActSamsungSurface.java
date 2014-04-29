package com.example;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TableRow.LayoutParams;


public class ActSamsungSurface extends FragmentActivity 
{
  static final String TAG_NAME = ActSamsungSurface.class.getSimpleName();
  private SamsungView surface = null;
  
  @Override
  public void onCreate(Bundle savedState)
  {
    super.onCreate(savedState);
    surface = new SamsungView(this);
    LinearLayout signScreen = buildSurfaceFrame();
    setContentView(signScreen);
  }
  
  private LinearLayout buildSurfaceFrame()
  {
    View signBg = null;
    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams
      (LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    signBg = buildDrawingBg();
    signBg.setLayoutParams(lp);
    
    LinearLayout signScreen = new LinearLayout(this);
    signScreen.setOrientation(LinearLayout.VERTICAL);
    signScreen.setGravity(Gravity.CENTER);
    lp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0);
    lp.weight = 1;
    signScreen.setLayoutParams(lp);
    Button btnClose = new Button(this);
    btnClose.setText("Close");
    
    btnClose.setOnClickListener(new OnClickListener()
    { @Override
      public void onClick(View v)
      { doClose(); }
    });
    
    signScreen.addView(btnClose);
    signScreen.addView(signBg);
    return signScreen;
  }
  
  /**
   * @return
   */
  private FrameLayout buildDrawingBg()
  {
    FrameLayout bgView = new FrameLayout(this);
    FrameLayout.LayoutParams lp; 
    lp = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
    lp.gravity = Gravity.TOP;
    bgView.addView((View)surface, lp);
    return bgView;
  }

  private void doClose()
  {
    Intent i = new Intent(this, ActSamsungSurface.class);
    setResult(RESULT_CANCELED, i);
    finish();
  }
} // class


