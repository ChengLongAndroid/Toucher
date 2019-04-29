package com.lightingcontour.toucher;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.constraint.ConstraintLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class MainService extends Service {

    private static final String TAG = "MainService";

    ExpandableListView expandableListView;
    ConstraintLayout toucherLayout;
    LinearLayout linearLayout;
    WindowManager.LayoutParams params;
    WindowManager windowManager;
    public String[] groupString = {"第1组", "第2组", "第3组", "第4组", "第5组", "第6组", "第7组"};
    public String[][] childString = {
            {"4532564326", "6453276542", "12343452", "54325432", "54326555"},
            {"4532564326", "6453276542", "12343452", "54325432", "45325432"},
            {"4532564326", "6453276542", "12343452", "54325432", "43125426"},
            {"4532564326", "6453276542", "12343452", "54325432", "65437754"},
            {"4532564326", "6453276542", "12343452", "54325432", "453265642"},
            {"4532564326", "6453276542", "12343452", "54325432", "345214312"},
            {"4532564326", "6453276542", "12343452", "54325432", "453254555"}};
//    private String data[] = {"456786546","456475646","452425425","543264264","5432647","7542654254","54275642645","t4255432554","543264354","bb","cc","dd","aa","bb","cc","dd"};//假数据
//    ListView listView;
    ImageButton imageButton1;
    Button button;
    //状态栏高度.
    int statusBarHeight = -1;

    //不与Activity进行绑定.
    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        Log.i(TAG,"MainService Created");
        createToucher();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void createToucher()
    {
        //赋值WindowManager&LayoutParam.
        params = new WindowManager.LayoutParams();
        windowManager = (WindowManager) getApplication().getSystemService(Context.WINDOW_SERVICE);
        //设置type.系统提示型窗口，一般都在应用程序窗口之上.
        //Android8.0行为变更，对8.0进行适配https://developer.android.google.cn/about/versions/oreo/android-8.0-changes#o-apps
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1)
        {
            params.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        }else {
            params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        }
        //设置效果为背景透明.
        params.format = PixelFormat.RGBA_8888;
        //设置flags.不可聚焦及不可使用按钮对悬浮窗进行操控.
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

        //设置窗口初始停靠位置.
        params.gravity = Gravity.LEFT | Gravity.TOP;
        params.x = 0;
        params.y = 0;

        //设置悬浮窗口长宽数据.
        params.width = 400;
        params.height = 600;

        LayoutInflater inflater = LayoutInflater.from(getApplication());
        //获取浮动窗口视图所在布局.
        toucherLayout = (ConstraintLayout) inflater.inflate(R.layout.toucherlayout,null);
        linearLayout =(LinearLayout) inflater.inflate(R.layout.test1,null);
        //添加toucherlayout
        windowManager.addView(toucherLayout,params);

        Log.i(TAG,"toucherlayout-->left:" + toucherLayout.getLeft());
        Log.i(TAG,"toucherlayout-->right:" + toucherLayout.getRight());
        Log.i(TAG,"toucherlayout-->top:" + toucherLayout.getTop());
        Log.i(TAG,"toucherlayout-->bottom:" + toucherLayout.getBottom());

        //主动计算出当前View的宽高信息.
        toucherLayout.measure(View.MeasureSpec.UNSPECIFIED,View.MeasureSpec.UNSPECIFIED);
        linearLayout.measure(View.MeasureSpec.UNSPECIFIED,View.MeasureSpec.UNSPECIFIED);
        //用于检测状态栏高度.
        int resourceId = getResources().getIdentifier("status_bar_height","dimen","android");
        if (resourceId > 0)
        {
            statusBarHeight = getResources().getDimensionPixelSize(resourceId);
        }
        Log.i(TAG,"状态栏高度为:" + statusBarHeight);

//        listView = (ListView)linearLayout.findViewById(R.id.list_item);
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,data);//新建并配置ArrayAapeter
//        listView.setAdapter(adapter);

        expandableListView = (ExpandableListView)linearLayout.findViewById(R.id.expend_list);
        expandableListView.setAdapter(new MyExtendableListViewAdapter(groupString, childString));
        //设置分组的监听
        expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                Toast.makeText(getApplicationContext(), groupString[groupPosition], Toast.LENGTH_SHORT).show();
                return false;
            }
        });
        //设置子项布局监听
        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                Toast.makeText(getApplicationContext(), childString[groupPosition][childPosition], Toast.LENGTH_SHORT).show();
   //获取剪贴板管理器：
                ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
// 创建普通字符型ClipData
                ClipData mClipData = ClipData.newPlainText("Label",  childString[groupPosition][childPosition]);
// 将ClipData内容放到系统剪贴板里。
                cm.setPrimaryClip(mClipData);

                Toast.makeText(MainService.this,"数据"+ childString[groupPosition][childPosition]+"已复制",Toast.LENGTH_SHORT).show();


                return true;

            }
        });




//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                //获取剪贴板管理器：
//                ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
//// 创建普通字符型ClipData
//                ClipData mClipData = ClipData.newPlainText("Label", data[i]);
//// 将ClipData内容放到系统剪贴板里。
//                cm.setPrimaryClip(mClipData);
//
//                Toast.makeText(MainService.this,"数据"+data[i]+"已复制",Toast.LENGTH_SHORT).show();
//            }
//        });


        button =(Button) linearLayout.findViewById(R.id.butTach);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                windowManager.removeView(linearLayout);
                windowManager.addView(toucherLayout,params);
            }
        });

        button.setOnTouchListener(new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            params.x = (int) event.getRawX() - 80;
            params.y = (int) event.getRawY() - 80 - statusBarHeight;
            windowManager.updateViewLayout(linearLayout,params);
            return false;
        }
    });


        //浮动窗口按钮.
        imageButton1 = (ImageButton) toucherLayout.findViewById(R.id.imageButton1);

        imageButton1.setOnClickListener(new View.OnClickListener() {
            long[] hints = new long[2];
            @Override
            public void onClick(View v) {
                Log.i(TAG,"点击了");
                System.arraycopy(hints,1,hints,0,hints.length -1);
                hints[hints.length -1] = SystemClock.uptimeMillis();
                if (SystemClock.uptimeMillis() - hints[0] >= 700)
                {
                    Log.i(TAG,"要执行");
                    Toast.makeText(MainService.this,"长按关闭",Toast.LENGTH_SHORT).show();
//                    startActivity(new Intent(MainService.this,Main2Activity.class));

                    windowManager.removeView(toucherLayout);
                    windowManager.addView(linearLayout,params);



                }else
                {
                    Log.i(TAG,"即将关闭");
                    stopSelf();
                }
            }
        });

        imageButton1.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                stopSelf();
                return false;
            }
        });

        imageButton1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                params.x = (int) event.getRawX() -15;
                params.y = (int) event.getRawY() -20 - statusBarHeight;
                windowManager.updateViewLayout(toucherLayout,params);
                return false;
            }
        });
    }

    @Override
    public void onDestroy()
    {
        if (imageButton1 != null)
        {
            windowManager.removeView(toucherLayout);
        }
        super.onDestroy();
    }
}
