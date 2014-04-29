package com.example;

import java.util.HashMap;

import com.samsung.spen.settings.SettingFillingInfo;
import com.samsung.spen.settings.SettingStrokeInfo;
import com.samsung.spen.settings.SettingTextInfo;
import com.samsung.spensdk.SCanvasConstants;
import com.samsung.spensdk.SCanvasView;
import com.samsung.spensdk.applistener.ColorPickerColorChangeListener;
import com.samsung.spensdk.applistener.HistoryUpdateListener;
import com.samsung.spensdk.applistener.SCanvasInitializeListener;
import com.samsung.spensdk.applistener.SCanvasModeChangedListener;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class SamsungView extends LinearLayout
{
  static final String TAG_NAME = SamsungView.class.getSimpleName();
  private static final int BG_COLOR = Color.argb(0, 0, 0, 0);
  
  private SCanvasView sCanvas;
  private FrameLayout layoutContainer;
  private RelativeLayout scanvasContainer;
  
  private ImageView tvPenOnly, tvUndo, tvRedo, tvFill;
  private ImageView tvPen, tvEraser, tvText;
  
  public SamsungView(Context context, AttributeSet attrs)
  {
    super(context, attrs);
    init(context);
  }
  
  public SamsungView(Context context)
  {
    super(context);
    init(context);
  }
  
  /**
   * @param context
   */
  private void init(Context context)
  {
    setOrientation(VERTICAL);
    LayoutInflater inf = (LayoutInflater)context.getSystemService
      (Context.LAYOUT_INFLATER_SERVICE);    
    inf.inflate(R.layout.spen_surface, this, true);
    
    scanvasContainer = (RelativeLayout)findViewById(R.id.scanvasContainer);
    layoutContainer = (FrameLayout)findViewById(R.id.layoutContainer);
    
    setGravity(Gravity.CENTER_HORIZONTAL);
    buildScanvas(context);
    buildButtons();
  }
  
  /**
   * @param context
   */
  private void buildScanvas(Context context)
  {
    sCanvas = new SCanvasView(context);
    RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams
      (LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    scanvasContainer.addView(sCanvas, lp);
    
    sCanvas.createSettingView
      (layoutContainer, getSettingResources(), getSettingLayoutStringResources());
    sCanvas.setSCanvasInitializeListener(initCB);
    sCanvas.setHistoryUpdateListener(historyCB);
    sCanvas.setSCanvasModeChangedListener(modeChangedCB);
  }
  
  /**
   */
  private void buildButtons()
  {
    tvUndo = (ImageView)findViewById(R.id.imgUndo);
    tvRedo = (ImageView)findViewById(R.id.imgRedo);    
    tvPen = (ImageView)findViewById(R.id.imgPen);
    tvPenOnly = (ImageView)findViewById(R.id.imgPenOnly);
    
    tvEraser = (ImageView)findViewById(R.id.imgEraser);
    tvText = (ImageView)findViewById(R.id.imgText);
    tvFill = (ImageView)findViewById(R.id.imgFill);
    
    tvUndo.setOnClickListener(undoRedoCB);
    tvRedo.setOnClickListener(undoRedoCB);
    tvPen.setOnClickListener(penCB);
    tvPenOnly.setOnClickListener(penOnlyCB);
    
    tvEraser.setOnClickListener(eraserCB);
    tvText.setOnClickListener(textCB);
    tvFill.setOnClickListener(fillCB);
    
    tvUndo.setEnabled(false);
    tvRedo.setEnabled(false);
    tvPen.setSelected(true);
  }
  
  // ---------------------------------------------------------------------------
  // ---------------------------------------------------------------------------
  private final SCanvasInitializeListener initCB = new SCanvasInitializeListener()
  { @Override
    public void onInitialized()
    {
      sCanvas.setBackgroundColor(BG_COLOR);
      updateModeState();
      
      sCanvas.setFingerControlPenDrawing(false);
      sCanvas.setColorPickerColorChangeListener(colorChangeCB);
    }
  };
  
  // ---------------------------------------------------------------------------
  // ---------------------------------------------------------------------------
  private final HistoryUpdateListener historyCB = new HistoryUpdateListener()
  {
    @Override
    public void onHistoryChanged(boolean undoable, boolean redoable)
    {
      tvUndo.setEnabled(undoable);
      tvRedo.setEnabled(redoable);
    }
  };
  
  // ---------------------------------------------------------------------------
  // ---------------------------------------------------------------------------
  private final SCanvasModeChangedListener modeChangedCB = new SCanvasModeChangedListener()
  { @Override
    public void onModeChanged(int mode)
    { updateModeState(); }

    @Override
    public void onMovingModeEnabled(boolean bEnableMovingMode)
    { updateModeState(); }

    @Override
    public void onColorPickerModeEnabled(boolean bEnableColorPickerMode)
    { updateModeState(); }
  };
  
  // ---------------------------------------------------------------------------
  // ---------------------------------------------------------------------------
  private final OnClickListener penOnlyCB = new OnClickListener()
  { @Override
    public void onClick(View v)
    {
      boolean bIsPenOnly = !sCanvas.isFingerControlPenDrawing(); 
      sCanvas.setFingerControlPenDrawing(bIsPenOnly);
  
      if(bIsPenOnly)
      { tvPenOnly.setImageResource(R.drawable.spen_selector_penonly); }
      else
      { tvPenOnly.setImageResource(R.drawable.spen_selector_penonly_n); }
    }
  };
  
  // ---------------------------------------------------------------------------
  // ---------------------------------------------------------------------------
  private final OnClickListener undoRedoCB = new OnClickListener()
  { @Override
    public void onClick(View v)
    {
      if(v.equals(tvUndo))
      { sCanvas.undo(); }
      else if(v.equals(tvRedo))
      { sCanvas.redo(); }
      
      boolean u = sCanvas.isUndoable(), r = sCanvas.isRedoable();
      tvUndo.setEnabled(u);
      tvRedo.setEnabled(r);
    }
  };
  
  // ---------------------------------------------------------------------------
  // ---------------------------------------------------------------------------
  private final OnClickListener penCB = new OnClickListener()
  { @Override
    public void onClick(View v)
    {
      if(sCanvas.getCanvasMode() == SCanvasConstants.SCANVAS_MODE_INPUT_PEN)
      {
        sCanvas.setSettingViewSizeOption
          (SCanvasConstants.SCANVAS_SETTINGVIEW_PEN,
           SCanvasConstants.SCANVAS_SETTINGVIEW_SIZE_NORMAL);
        sCanvas.toggleShowSettingView(SCanvasConstants.SCANVAS_SETTINGVIEW_PEN);
      }
      else
      {
        sCanvas.setCanvasMode(SCanvasConstants.SCANVAS_MODE_INPUT_PEN);
        sCanvas.showSettingView(SCanvasConstants.SCANVAS_SETTINGVIEW_PEN, false);
        updateModeState();
        Toast.makeText(getContext(), R.string.spen_menu_toast, Toast.LENGTH_SHORT).show();
      }
    }
  };
  
  // ---------------------------------------------------------------------------
  // ---------------------------------------------------------------------------
  private final OnClickListener eraserCB = new OnClickListener()
  { @Override
    public void onClick(View v)
    {
      if(sCanvas.getCanvasMode() == SCanvasConstants.SCANVAS_MODE_INPUT_ERASER)
      {
        sCanvas.setSettingViewSizeOption
          (SCanvasConstants.SCANVAS_SETTINGVIEW_ERASER,
           SCanvasConstants.SCANVAS_SETTINGVIEW_SIZE_NORMAL);
        sCanvas.toggleShowSettingView(SCanvasConstants.SCANVAS_SETTINGVIEW_ERASER);
      }
      else
      {
        sCanvas.setCanvasMode(SCanvasConstants.SCANVAS_MODE_INPUT_ERASER);
        sCanvas.showSettingView(SCanvasConstants.SCANVAS_SETTINGVIEW_ERASER, false);
        updateModeState();
        Toast.makeText(getContext(), R.string.spen_menu_toast, Toast.LENGTH_SHORT).show();
      }
    }
  };
  
  // ---------------------------------------------------------------------------
  // ---------------------------------------------------------------------------
  private final ColorPickerColorChangeListener colorChangeCB = new ColorPickerColorChangeListener()
  { @Override
    public void onColorPickerColorChanged(int color) 
    {
      int curMode = sCanvas.getCanvasMode();
      if(curMode==SCanvasConstants.SCANVAS_MODE_INPUT_PEN) 
      {
        SettingStrokeInfo strokeInfo = sCanvas.getSettingViewStrokeInfo();
        if(strokeInfo != null) 
        {
          strokeInfo.setStrokeColor(color);  
          sCanvas.setSettingViewStrokeInfo(strokeInfo);
        } 
      }
      else if(curMode==SCanvasConstants.SCANVAS_MODE_INPUT_TEXT)
      {
        SettingTextInfo textInfo = sCanvas.getSettingViewTextInfo();
        if(textInfo != null) 
        {
          textInfo.setTextColor(color);
          sCanvas.setSettingViewTextInfo(textInfo);
        }
      }
      else if(curMode==SCanvasConstants.SCANVAS_MODE_INPUT_FILLING) 
      {
        SettingFillingInfo fillingInfo = sCanvas.getSettingViewFillingInfo();
        if(fillingInfo != null) 
        {
          fillingInfo.setFillingColor(color);
          sCanvas.setSettingViewFillingInfo(fillingInfo);
        }
      } 
    }     
  };

  // ---------------------------------------------------------------------------
  // ---------------------------------------------------------------------------
  private final OnClickListener textCB = new OnClickListener()
  { @Override
    public void onClick(View v)
    {
      if(sCanvas.getCanvasMode() == SCanvasConstants.SCANVAS_MODE_INPUT_TEXT)
      {
        sCanvas.setSettingViewSizeOption
          (SCanvasConstants.SCANVAS_SETTINGVIEW_TEXT,
           SCanvasConstants.SCANVAS_SETTINGVIEW_SIZE_NORMAL);
        sCanvas.toggleShowSettingView(SCanvasConstants.SCANVAS_SETTINGVIEW_TEXT);
      }
      else
      {
        sCanvas.setCanvasMode(SCanvasConstants.SCANVAS_MODE_INPUT_TEXT);
        sCanvas.showSettingView(SCanvasConstants.SCANVAS_SETTINGVIEW_TEXT, false);
        updateModeState();
        Toast.makeText(getContext(), R.string.spen_text_toast, Toast.LENGTH_SHORT).show();
      }
    }
  };
  
  // ---------------------------------------------------------------------------
  // ---------------------------------------------------------------------------
  private final OnClickListener fillCB = new OnClickListener()
  { @Override
    public void onClick(View v)
    {
      if(sCanvas.getCanvasMode()==SCanvasConstants.SCANVAS_MODE_INPUT_FILLING)
      {
        sCanvas.setSettingViewSizeOption
          (SCanvasConstants.SCANVAS_SETTINGVIEW_FILLING,
           SCanvasConstants.SCANVAS_SETTINGVIEW_SIZE_NORMAL);
        sCanvas.toggleShowSettingView(SCanvasConstants.SCANVAS_SETTINGVIEW_FILLING);
      }
      else
      {
        sCanvas.setCanvasMode(SCanvasConstants.SCANVAS_MODE_INPUT_FILLING);
        sCanvas.showSettingView(SCanvasConstants.SCANVAS_SETTINGVIEW_FILLING, false);                    
        updateModeState();
        Toast.makeText(getContext(), R.string.spen_fill_toast, Toast.LENGTH_SHORT).show();
      }
    }
  };
  
  /**
   */
  public void updateModeState()
  {
    boolean isMovingMode = sCanvas.isMovingMode();
    boolean isColorPickerMode = sCanvas.isColorPickerMode();
    int curMode = sCanvas.getCanvasMode();   
    
    tvPen.setSelected(isMovingMode ? false : 
      curMode==SCanvasConstants.SCANVAS_MODE_INPUT_PEN);
    
    tvEraser.setSelected(isMovingMode? false : 
      curMode==SCanvasConstants.SCANVAS_MODE_INPUT_ERASER);
    
    tvText.setSelected(isMovingMode? false : 
      curMode==SCanvasConstants.SCANVAS_MODE_INPUT_TEXT);

    tvFill.setSelected(isMovingMode? false : 
      curMode==SCanvasConstants.SCANVAS_MODE_INPUT_FILLING);
    
    tvEraser.setSelected(isMovingMode? false : 
      curMode==SCanvasConstants.SCANVAS_MODE_INPUT_ERASER);
    
    tvEraser.setEnabled(!(isColorPickerMode));
  }
  
  /**
   * @return
   */
  public static HashMap<String, String> getSettingLayoutStringResources()
  {
    HashMap<String,String> resMap = new HashMap<String, String>();
    resMap.put(SCanvasConstants.USER_FONT_PATH1, "fonts/chococooky.ttf");
    resMap.put(SCanvasConstants.USER_FONT_PATH2, "fonts/rosemary.ttf");
    return resMap;
  }
  
  /**
   * @return
   */
  public static HashMap<String, Integer> getSettingResources()
  {
    HashMap<String,Integer> resMp = new HashMap<String, Integer>();
    
    resMp.put(SCanvasConstants.LAYOUT_PEN_SPINNER, 
      R.layout.spen_spinner);
    resMp.put(SCanvasConstants.LAYOUT_TEXT_SPINNER, 
      R.layout.spen_spinner_text);
    
    resMp.put(SCanvasConstants.LAYOUT_TEXT_SPINNER_TABLET,
      R.layout.spen_spinner_text_tablet);
    resMp.put(SCanvasConstants.LOCALE_PEN_SETTING_TITLE, 
      R.string.pen_settings);
    
    resMp.put(SCanvasConstants.LOCALE_PEN_SETTING_PRESET_EMPTY_MESSAGE, 
      R.string.pen_settings_preset_empty);
    resMp.put(SCanvasConstants.LOCALE_PEN_SETTING_PRESET_DELETE_TITLE, 
      R.string.pen_settings_preset_delete_title);
    
    resMp.put(SCanvasConstants.LOCALE_PEN_SETTING_PRESET_DELETE_MESSAGE, 
      R.string.pen_settings_preset_delete_msg);
    resMp.put(SCanvasConstants.LOCALE_PEN_SETTING_PRESET_EXIST_MESSAGE, 
      R.string.pen_settings_preset_exist);
    
    resMp.put(SCanvasConstants.LOCALE_PEN_SETTING_PRESET_MAXIMUM_MESSAGE, 
      R.string.pen_settings_preset_maximum_msg);
    resMp.put(SCanvasConstants.LOCALE_PEN_SETTING_CHINESE_BRUSH_TAB, 
      R.string.pen_settings_chinese_brush_tab);
    
    resMp.put(SCanvasConstants.LOCALE_PEN_SETTING_BEAUTIFY_BRUSH_TAB, 
      R.string.pen_settings_beautify_brush_tab);
    resMp.put(SCanvasConstants.LOCALE_PEN_SETTING_BEAUTIFY_BRUSH_RESET, 
      R.string.pen_settings_beautify_brush_reset);
    
    resMp.put(SCanvasConstants.LOCALE_PEN_SETTING_BEAUTIFY_BRUSH_CURSIVE, 
      R.string.pen_settings_beautify_cursive);
    resMp.put(SCanvasConstants.LOCALE_PEN_SETTING_BEAUTIFY_BRUSH_SUSTENANCE, 
      R.string.pen_settings_beautify_sustenance);
    
    resMp.put(SCanvasConstants.LOCALE_PEN_SETTING_BEAUTIFY_BRUSH_DUMMY, 
      R.string.pen_settings_beautify_dummy);
    resMp.put(SCanvasConstants.LOCALE_PEN_SETTING_BEAUTIFY_BRUSH_MODULATION, 
      R.string.pen_settings_beautify_modulation);
    
    resMp.put(SCanvasConstants.LOCALE_ERASER_SETTING_TITLE, 
      R.string.eraser_settings);
    resMp.put(SCanvasConstants.LOCALE_ERASER_SETTING_CLEARALL, 
      R.string.clear_all);
    
    resMp.put(SCanvasConstants.LOCALE_TEXT_SETTING_TITLE, 
      R.string.text_settings);
    resMp.put(SCanvasConstants.LOCALE_TEXT_SETTING_TAB_FONT, 
      R.string.text_settings_tab_font);
    
    resMp.put(SCanvasConstants.LOCALE_TEXT_SETTING_TAB_PARAGRAPH, 
      R.string.text_settings_tab_paragraph);
    resMp.put(SCanvasConstants.LOCALE_TEXT_SETTING_TAB_PARAGRAPH_ALIGN, 
      R.string.text_settings_tab_paragraph_align);
    
    resMp.put(SCanvasConstants.LOCALE_TEXT_SETTING_TAB_LIST, 
      R.string.text_settings_tab_list);
    resMp.put(SCanvasConstants.LOCALE_TEXTBOX_HINT, 
      R.string.textbox_hint);
    
    resMp.put(SCanvasConstants.LOCALE_TEXT_SETTING_ALIGN_LEFT, 
      R.string.align_left_desc);
    resMp.put(SCanvasConstants.LOCALE_TEXT_SETTING_ALIGN_CENTER, 
      R.string.align_center_desc);
    
    resMp.put(SCanvasConstants.LOCALE_TEXT_SETTING_ALIGN_RIGHT, 
      R.string.align_right_desc);
    resMp.put(SCanvasConstants.LOCALE_FILLING_SETTING_TITLE, 
      R.string.filling_settings);
    
    resMp.put(SCanvasConstants.LOCALE_SETTINGVIEW_CLOSE_DESCRIPTION, 
      R.string.settingview_close_btn_desc);
    resMp.put(SCanvasConstants.LOCALE_SETTINGVIEW_PRESET_ADD_DESCRIPTION, 
      R.string.settingview_preset_add_btn_desc);
    
    resMp.put(SCanvasConstants.LOCALE_SETTINGVIEW_PRESET_DELETE_DESCRIPTION, 
      R.string.settingview_preset_delete_btn_desc);
    return resMp;
  }
  
}
