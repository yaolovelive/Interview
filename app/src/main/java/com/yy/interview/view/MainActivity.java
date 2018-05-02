package com.yy.interview.view;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.icu.text.TimeZoneFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.amap.api.services.geocoder.GeocodeAddress;
import com.amap.api.services.geocoder.GeocodeResult;
import com.yy.interview.R;
import com.yy.interview.model.entity.Interview;
import com.yy.interview.model.entity.LocationEntity;
import com.yy.interview.model.io.IInterviewDao;
import com.yy.interview.model.util.ValidateUtils;
import com.yy.interview.presente.GeocodeSearchPresent;
import com.yy.interview.presente.GeocodeSearchPresent.Callback;
import com.yy.interview.presente.IInterviewPresent;
import com.yy.interview.presente.InterviewPresent;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by 安Y安酱 on 2018/3/31.
 */

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;

    private View view_menu;

    private InterviewPresent interviewPresent;
    private Listener listener = new Listener();
    private List<Interview> interviews = new ArrayList<>();
    private InterviewAdapter adapter;

    private ActionBarView barView;
    private ListView lv_interview;

    private Integer pageNo = 1;
    private Integer pageSize = 10;
    private IInterviewDao.OrderBy orderBy = IInterviewDao.OrderBy.state;

    private Handler handler = new Handler();

    //权限
    private String[] permission = new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_WIFI_STATE};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        drawerLayout = (DrawerLayout) LayoutInflater.from(getApplicationContext()).inflate(R.layout.layout_main, null);
        setContentView(drawerLayout);
        init();
        initView();
    }

    AlertDialog dialog;

    private EditText et_company;
    private EditText et_address;
    private EditText et_calendar;
    private EditText et_date;
    private EditText et_phone;
    private GeocodeSearchPresent geocodeSearchPresent;

    private AlertDialog menuDialog;


    /**
     * 初始化控件
     */
    private void initView() {
        view_menu = drawerLayout.findViewById(R.id.view_menu);
        lv_interview = findViewById(R.id.lv_interview);
        lv_interview.setOnItemClickListener(listener);
        lv_interview.setAdapter(adapter);
        barView = findViewById(R.id.view_actionBar);

        barView.setBtnRightOnClickListener((v) -> {
            if (dialog == null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle(R.string.add_item);
                builder.setCancelable(false);
                View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.dialog_interview_add, null, false);
                et_company = view.findViewById(R.id.et_company);
                et_address = view.findViewById(R.id.et_address);
                et_calendar = view.findViewById(R.id.et_calendar);
                et_calendar.setOnClickListener(listener);
                et_date = view.findViewById(R.id.et_date);
                et_date.setOnClickListener(listener);
                et_phone = view.findViewById(R.id.et_phone);
                builder.setView(view);
                builder.setNegativeButton(R.string.cancel, (x, y) -> {
                    dialog.dismiss();
                });
                builder.setPositiveButton(R.string.add, null);
                dialog = builder.create();
            }
            dialog.show();
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener((x) -> {
                String company = et_company.getText() + "";
                String address = et_address.getText() + "";
                String calendar = et_calendar.getText() + "";
                String date = et_date.getText() + "";
                String phone = et_phone.getText() + "";
                if (ValidateUtils.isNULL(company)) {
                    showToast("请输入公司名");
                    et_company.requestFocus();
                    return;
                } else if (ValidateUtils.isNULL(address)) {
                    showToast("请输入面试地址");
                    et_address.requestFocus();
                    return;
                } else if (ValidateUtils.isNULL(calendar)) {
                    showToast("请选择面试日期");
                    return;
                } else if (ValidateUtils.isNULL(date)) {
                    showToast("选择面试时间");
                    return;
                }
                interviewPresent.addInterview(company, calendar + " " + date, address, phone);
            });
        });
        barView.setBtnLeftOnClickListener((v) -> {
            if (menuDialog == null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setSingleChoiceItems(new String[]{"开源地址", "通知提醒提前时间"}, -1, (x, y) -> {
                    switch (y) {
                        case 0:
                            menuDialog.dismiss();
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            Uri uri = Uri.parse("https://github.com/yaolovelive/Interview");
                            intent.setData(uri);
                            startActivity(intent);
                            break;
                        case 1:
                            menuDialog.dismiss();
                            AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
                            int ts = config.getInt("ts", 3);
                            NumberPicker numberPicker = new NumberPicker(MainActivity.this);
                            numberPicker.setMaxValue(6);
                            numberPicker.setMinValue(1);
                            numberPicker.setValue(ts);
                            numberPicker.setFocusable(true);
                            numberPicker.setFocusableInTouchMode(true);
                            builder1.setView(numberPicker);
                            builder1.setPositiveButton("确定", (a, b) -> {
                                SharedPreferences.Editor editor = config.edit();
                                editor.putInt("ts", numberPicker.getValue());
                                editor.commit();
                            });
                            builder1.setNegativeButton("关闭", (a, b) -> numDialog.dismiss());
                            numDialog = builder1.create();
                            numDialog.show();
                            break;
                    }
                });
                menuDialog = builder.create();
            }
            menuDialog.show();
        });
        lv_interview.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (interviews.size() > pageSize) {
                    if (lv_interview.getLastVisiblePosition() == totalItemCount - 1) {
                        interviewPresent.changeState(++pageNo, pageSize, orderBy);
                    }
                }
            }
        });

    }

    /**
     * 初始化对象
     */
    private void init() {
        adapter = new InterviewAdapter();
        interviewPresent = new InterviewPresent(MainActivity.this, listener, handler);
        interviewPresent.changeState(pageNo, pageSize, orderBy);
        geocodeSearchPresent = new GeocodeSearchPresent(getApplicationContext(), listener);
        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setMessage("加载中...请稍后");
        progressDialog.setCancelable(false);
        config = getSharedPreferences("config", 0);
    }

    /**
     * 监听事件类APSService
     */
    private class Listener implements IInterviewPresent, View.OnClickListener, AdapterView.OnItemClickListener, Callback {
        @Override
        public void showInterviews(List<Interview> interviews) {
            if (pageNo == 1) {
                MainActivity.this.interviews = interviews;
            } else {
                MainActivity.this.interviews.addAll(interviews);
            }
            adapter.notifyDataSetChanged();
        }

        @Override
        public void addCallback(boolean result) {
            if (result) {
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                    dialog = null;
                }
                showToast("添加成功");
                interviewPresent.changeState(pageNo, pageSize, orderBy);
//                interviewPresent.getNotInterviews(pageNo,pageSize,orderBy);
            }
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.et_calendar:
                    Calendar calendar = Calendar.getInstance();
                    Date date = new Date();
                    calendar.setTime(date);
                    DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this, (x, y, m, d) -> {
                        et_calendar.setText(y + "-" + (m + 1) + "-" + d);
                    }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                    datePickerDialog.show();
                    break;
                case R.id.et_date:
                    Calendar calendar1 = Calendar.getInstance();
                    calendar1.setTime(new Date());
                    TimePickerDialog timePickerDialog = new TimePickerDialog(MainActivity.this, (x, h, m) -> {
                        et_date.setText((h < 10 ? "0" + h : h) + ":" + (m < 10 ? "0" + m : m));
                    }, calendar1.get(Calendar.HOUR_OF_DAY), calendar1.get(Calendar.MINUTE), true);
                    timePickerDialog.show();
                    break;
            }
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            chooseDialog(position).show();
        }

        @Override
        public void onGeocodeSearchError() {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            showToast("出现未知错误!");
        }

        @Override
        public void getAddressSuccess(LocationEntity locationEntity) {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            Intent intent = new Intent("activity.getway");
            intent.putExtra("locationInfo", locationEntity);
            startActivity(intent);
        }
    }

    private AlertDialog chooseDislog = null;
    private AlertDialog dialog_del = null;

    public AlertDialog chooseDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setItems(new String[]{"路径规划", "取消面试"}, (x, y) -> {
            if (y == 0) {
                for (int i = 0; i < permission.length; i++) {
                    int result = ContextCompat.checkSelfPermission(getApplicationContext(), permission[i]);
                    if (result != PackageManager.PERMISSION_GRANTED) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            requestPermissions(permission, 0x001);
                            return;
                        }
                    }
                }
                progressDialog.show();
                new Thread(() -> geocodeSearchPresent.search(interviews.get(position).getInterviewAddress())).start();
            } else if (y == 1) {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
                builder1.setTitle("删除提示");
                builder1.setMessage("确认删除该该条记录?");
                builder1.setNegativeButton("确认删除", (a, b) -> {
                    interviewPresent.cancalInterView(interviews.get(position).getId());
                    showToast("删除成功!");
                    interviewPresent.getNotInterviews(pageNo, pageSize, orderBy);
                    dialog_del.dismiss();
                });
                builder1.setPositiveButton("点错了", (a, b) -> {
                    dialog_del.dismiss();
                });
                dialog_del = builder1.create();
                dialog_del.show();
            }
            if (chooseDislog.isShowing()) {
                chooseDislog.dismiss();
            }
        });
        builder.setCancelable(true);
        chooseDislog = builder.create();
        return chooseDislog;
    }


    private ProgressDialog progressDialog;

    /**
     * ListView适配器
     */
    private class InterviewAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return interviews.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            Wrapper wrapper = null;
            if (view == null) {
                view = LayoutInflater.from(MainActivity.this).inflate(R.layout.item_list_intervview, parent, false);
                wrapper = new Wrapper(view);
                view.setTag(wrapper);
            } else {
                wrapper = (Wrapper) view.getTag();
            }
            TextView tv_id = wrapper.getTv_id();
            TextView tv_company = wrapper.getTv_company();
            TextView tv_address = wrapper.getTv_address();
            TextView tv_time = wrapper.getTv_time();
            TextView tv_phone = wrapper.getTv_phone();
            TextView tv_state = wrapper.getTv_state();
            tv_id.setText(interviews.get(position).getId() + "");
            tv_company.setText(interviews.get(position).getCompanyName() + "");
            tv_address.setText(interviews.get(position).getInterviewAddress() + "");
            tv_time.setText(interviews.get(position).getInterviewTime() + "");
            tv_phone.setText(interviews.get(position).getLinkedPhone() + "");
            tv_state.setText(getState(interviews.get(position).getState(), tv_state) + "");
            return view;
        }
    }

    /**
     * 获取状态
     *
     * @param state
     * @return
     */
    private String getState(int state, TextView tv) {
        switch (state) {
            case 0:
                tv.setTextColor(Color.argb(0xff, 0xff, 0, 0));
                return "待面试";
            case 1:
                tv.setTextColor(Color.argb(0xff, 0, 0xff, 0));
                return "面试中";
            case 2:
                tv.setTextColor(Color.argb(0xff, 0, 0, 0));
                return "已面试";
        }
        return "????";
    }

    /**
     * ListView的持有者,优化
     */
    private class Wrapper {
        private View view;
        private TextView tv_id;
        private TextView tv_company;

        public TextView getTv_address() {
            if (tv_address == null)
                tv_address = view.findViewById(R.id.tv_address);
            return tv_address;
        }

        public TextView getTv_time() {
            if (tv_time == null)
                tv_time = view.findViewById(R.id.tv_time);
            return tv_time;
        }

        public TextView getTv_phone() {
            if (tv_phone == null)
                tv_phone = view.findViewById(R.id.tv_phone);
            return tv_phone;
        }

        public TextView getTv_state() {
            if (tv_state == null)
                tv_state = view.findViewById(R.id.tv_state);
            return tv_state;
        }

        private TextView tv_address;
        private TextView tv_time;
        private TextView tv_phone;
        private TextView tv_state;

        public Wrapper(View v) {
            this.view = v;
        }

        public TextView getTv_id() {
            if (tv_id == null) {
                tv_id = view.findViewById(R.id.tv_id);
            }
            return tv_id;
        }

        public TextView getTv_company() {
            if (tv_company == null) {
                tv_company = view.findViewById(R.id.tv_company);
            }
            return tv_company;
        }
    }

    /**
     * 退出方法
     */
    boolean canExit = false;
    private Timer timerExit;

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(view_menu)) {
            drawerLayout.closeDrawer(view_menu);
            return;
        }
        if (canExit) {
            finish();
        } else {
            showToast(R.string.tryagain);
            canExit = true;
            if (timerExit == null) {
                timerExit = new Timer();
                timerExit.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        canExit = false;
                        timerExit.cancel();
                        timerExit = null;
                    }
                }, 2000);
            }
        }
    }

    /**
     * 显示Toast
     *
     * @param msg
     */
    private void showToast(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    private void showToast(int resourceId) {
        Toast.makeText(getApplicationContext(), resourceId, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timerExit != null) {
            timerExit.cancel();
            timerExit = null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0x001, 0x001, 2, "通知提醒提前时间");
        menu.add(0x002, 0x002, 1, "开源地址");
        return super.onCreateOptionsMenu(menu);
    }

    private SharedPreferences config;
    private AlertDialog numDialog = null;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 0x002:
                Intent intent = new Intent(Intent.ACTION_VIEW);
                Uri uri = Uri.parse("https://github.com/yaolovelive/Interview");
                intent.setData(uri);
                startActivity(intent);
                break;
            case 0x001:
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                int ts = config.getInt("ts", 3);
                NumberPicker numberPicker = new NumberPicker(MainActivity.this);
                numberPicker.setMaxValue(6);
                numberPicker.setMinValue(1);
                numberPicker.setValue(ts);
                numberPicker.setFocusable(true);
                numberPicker.setFocusableInTouchMode(true);
                builder.setView(numberPicker);
                builder.setPositiveButton("确定", (x, y) -> {
                    SharedPreferences.Editor editor = config.edit();
                    editor.putInt("ts", numberPicker.getValue());
                    editor.commit();
                });
                builder.setNegativeButton("关闭", (x, y) -> numDialog.dismiss());
                numDialog = builder.create();
                numDialog.show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
