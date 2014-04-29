package com.example;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

public class SamsungLayoutDemo extends FragmentActivity
{
  static final String TAG_NAME = SamsungLayoutDemo.class.getSimpleName();
  private boolean isOn = false;
  private ImageView bulb;
  
  private boolean hasBeenClicked = false;
  
  /**
   */
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    buildView();
  }
  
  /**
   */
  private void buildView()
  {
    setContentView(R.layout.layout_demo);
    bulb = (ImageView)findViewById(R.id.bulb);
    
    Button btnAct = (Button)findViewById(R.id.btnAct);
    btnAct.setOnClickListener(new OnClickListener()
    { @Override
      public void onClick(View v)
      {
        if(hasBeenClicked)
        { return; }
        hasBeenClicked = true;
        Intent i = new Intent(SamsungLayoutDemo.this, ActSamsungSurface.class);
        startActivityForResult(i, 1111);
      }
    });
    
    Button btnSwitch = (Button)findViewById(R.id.btnSwitch);
    btnSwitch.setOnClickListener(new OnClickListener()
    { @Override
      public void onClick(View v)
      {
        isOn = !isOn;
        if(isOn)
        { bulb.setImageResource(R.drawable.light_bulb_on); }
        else
        { bulb.setImageResource(R.drawable.light_bulb_off); }
      }
    });
  }
  
  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data)
  {
    hasBeenClicked = false;
  }
  
} // class




