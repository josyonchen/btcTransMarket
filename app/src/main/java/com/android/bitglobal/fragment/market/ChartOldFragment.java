package com.android.bitglobal.fragment.market;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.android.bitglobal.R;
import com.android.bitglobal.entity.HttpResult;
import com.android.bitglobal.entity.MarketDatasResult;
import com.android.bitglobal.entity.TickerArrayResult;
import com.android.bitglobal.entity.TickerData;
import com.android.bitglobal.fragment.BaseFragment;
import com.android.bitglobal.http.HttpMethods;
import com.android.bitglobal.tool.L;
import com.android.bitglobal.tool.SpTools;
import com.android.bitglobal.tool.StringUtils;
import com.android.bitglobal.tool.Utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.LineChartView;
import rx.Subscriber;

/**
 * Created by Administrator on 2017/7/7.
 */

public class ChartOldFragment extends BaseFragment {
    private Activity context;
    private int step = 5 * 60;                       //图表数据间隔
    private int chartDataSize = 72;
    // private final String  kChartDataSize = "3000";                          //图表数据总条数

    private String TYPE = "HOUR";
    private String currencyType = "ETH";
    private String exchangeType = "BTC";
    @BindView(R.id.radionButton01)
    RadioButton mRadionButton01;

    @BindView(R.id.radionButton02)
    RadioButton mRadionButton02;

    @BindView(R.id.radionButton03)
    RadioButton mRadionButton03;

    @BindView(R.id.radionButton04)
    RadioButton mRadionButton04;

    @BindView(R.id.market_title)
    TextView market_title;

    @BindView(R.id.market_price)
    TextView market_price;

    @BindView(R.id.market_rose)
    TextView market_rose;

    @BindView(R.id.high_price)
    TextView hight_price;

    @BindView(R.id.low_price)
    TextView low_price;

    @BindView(R.id.vol_price)
    TextView vol_price;


    @BindView(R.id.day_rose)
    TextView day_rose;

    @BindView(R.id.week_rose)
    TextView week_rose;

    @BindView(R.id.month_rose)
    TextView month_rose;

    @BindView(R.id.vol_type)
    TextView vol_type;

    @BindView(R.id.market_linechart)
    LineChartView market_linechart;

    @BindView(R.id.image_full)
    ImageView image_full;

    @BindView(R.id.market_chart_time_01)
    TextView market_chart_time_01;
    @BindView(R.id.market_chart_time_02)
    TextView market_chart_time_02;
    @BindView(R.id.market_chart_time_03)
    TextView market_chart_time_03;
    @BindView(R.id.market_chart_time_04)
    TextView market_chart_time_04;
    @BindView(R.id.market_chart_time_05)
    TextView market_chart_time_05;
    @BindView(R.id.market_chart_time_06)
    TextView market_chart_time_06;
    @BindView(R.id.market_chart_time_07)
    TextView market_chart_time_07;


    private Unbinder unbinder;
    private Timer timerChart = null;  //刷新定时器
    private final int dataRefreshTime = 5 * 1000;//数据刷新间隔

    public void setCurrencyType(String currencyType) {
        this.currencyType = currencyType;
    }

    public void setExchangeType(String exchangeType) {
        this.exchangeType = exchangeType;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.market_chart_old_fragment, container, false);
        unbinder = ButterKnife.bind(this, view);
        context = this.getActivity();
        getMarketData();
        return view;
    }

    private void getMarketData() {

        Subscriber subscriber = new Subscriber<HttpResult<TickerArrayResult>>() {
            @Override
            public void onCompleted() {
                // UIHelper.ToastMessage(context,"获取成功！");
            }

            @Override
            public void onError(Throwable e) {
                // UIHelper.ToastMessage(context,"获取行情失败！");
            }

            @Override
            public void onNext(HttpResult<TickerArrayResult> tickerArrayResultHttpResult) {
                initData(tickerArrayResultHttpResult.getDatas().getMarketDatas());
            }
        };
        HttpMethods.getInstance(1).getTickerArray(subscriber, exchangeType, "['" + currencyType + "']", step + "", chartDataSize + "", SpTools.getlegalTender());
    }


    private void initData(List<MarketDatasResult> marketDatas) {
        TickerData ticker = marketDatas.get(0).getTicker();
        String str = "--%";
        if (ticker != null) {
            ticker.format();
            setTextAndColor(ticker.getRiseRate(), day_rose, true);
            setTextAndColor(ticker.getWeekRiseRate(), week_rose, true);
            setTextAndColor(ticker.getMonthRiseRate(), month_rose, true);
            setTextAndColor(ticker.getLast(), market_price, false);
            setTextAndColor(ticker.getRiseRate(), market_rose, true);
            market_title.setText(currencyType.toUpperCase() + "/" + exchangeType.toUpperCase());
            market_rose.setTextColor(ContextCompat.getColor(context, R.color.text_price_zero));
            market_price.setText(format(ticker.getLast(), Utils.getPrecisionExchange(currencyType, exchangeType)));
            hight_price.setText(ticker.getHigh());
            low_price.setText(ticker.getLow());
            vol_price.setText(ticker.getVol());
            vol_type.setText("Volume(" + currencyType.toUpperCase() + ")");
        } else {
            day_rose.setText(str);
            week_rose.setText(str);
            month_rose.setText(str);
            market_title.setText(currencyType.toUpperCase() + "/" + exchangeType.toUpperCase());
            market_price.setText(str);
            market_rose.setText(str);
            hight_price.setText(str);
            low_price.setText(str);
            vol_price.setText(str);
            vol_type.setText("Volume(" + currencyType.toUpperCase() + ")");
        }
        MarketDatasResult mMarketDatasResult = marketDatas.get(0);
        if (mMarketDatasResult != null) {
            createChartDataSet(mMarketDatasResult.getTline(),
                    mMarketDatasResult.getTicker().getLast(),
                    market_linechart, Color.parseColor("#fa748a"));
        }
        updateAxisXText();

    }

    private void updateAxisXText() {
        String str = "";
        ArrayList<String> times = new ArrayList<>();
        long time = System.currentTimeMillis();
        Calendar mCalendar = Calendar.getInstance();
        mCalendar.setTimeInMillis(time);
        int hour = mCalendar.get(Calendar.HOUR_OF_DAY);
        int min = mCalendar.get(Calendar.MINUTE);
        int day = mCalendar.get(Calendar.DAY_OF_MONTH);
        int month = mCalendar.get(Calendar.MONTH) + 1;
        switch (TYPE) {
            case "HOUR":
                min = (min / 5) * 5;
                for (int i = 0; i < 7; i++) {
                    if (hour < 10) {
                        str = "0" + hour;
                    } else {
                        str = "" + hour;
                    }
                    str += ":";
                    if (min < 10) {
                        str += "0" + min;
                    } else {
                        str += min;
                    }
                    times.add(str);
                    if (hour == 0) {
                        hour = 23;
                    } else {
                        hour--;
                    }
                }
                break;
            case "DAY":
                min = (min / 30) * 30;
                for (int i = 0; i < 7; i++) {
                    if (hour < 10) {
                        str = "0" + hour;
                    } else {
                        str = "" + hour;
                    }
                    str += ":";
                    if (min < 10) {
                        str += "0" + min;
                    } else {
                        str += min;
                    }
                    times.add(str);
                    hour = hour - 4;
                    if (hour < 0) {
                        hour += 24;
                    }
                }
                break;
            case "WEEK":
                str = getString(day, month);
                times.add(str);
                for (int i = 1; i < 7; i++) {
                    mCalendar.add(Calendar.DAY_OF_MONTH, -1);
                    month = mCalendar.get(Calendar.MONTH) + 1;
                    day = mCalendar.get(Calendar.DAY_OF_MONTH);
                    str = getString(day, month);
                    times.add(str);
                }
                break;
            case "MONTH":
                str = getString(day, month);
                times.add(str);
                for (int i = 1; i < 7; i++) {
                    mCalendar.add(Calendar.DAY_OF_MONTH, -5);
                    month = mCalendar.get(Calendar.MONTH) + 1;
                    day = mCalendar.get(Calendar.DAY_OF_MONTH);
                    str = getString(day, month);
                    times.add(str);
                }
                break;
            default:
                break;
        }
        market_chart_time_07.setText(times.get(0));
        market_chart_time_06.setText(times.get(1));
        market_chart_time_05.setText(times.get(2));
        market_chart_time_04.setText(times.get(3));
        market_chart_time_03.setText(times.get(4));
        market_chart_time_02.setText(times.get(5));
        market_chart_time_01.setText(times.get(6));
    }

    @NonNull
    private String getString(int day, int month) {
        String str;
        if (month < 10) {
            str = "0" + month;
        } else {
            str = "" + month;
        }
        str += "/";
        if (day < 10) {
            str += "0" + day;
        } else {
            str += day;
        }
        return str;
    }

    private void setTextAndColor(String value, TextView view, boolean isRate) {
        String str;
        int color = ContextCompat.getColor(context, R.color.text_price_zero);
        if (isRate) {
            str = "--%";
        } else {
            str = "0";
        }
        if (StringUtils.isEmpty(value)) {
            view.setText(str);
            view.setTextColor(color);
        } else {
            str = value;
            if (Double.compare(Double.valueOf(str), 0) == 0) {
                if (isRate) {
                    str = "0.00%";
                }
                color = ContextCompat.getColor(context, R.color.text_price_zero);
            } else if (Double.compare(Double.valueOf(str), 0) > 0) {
                if (isRate) {
                    str = "+" + str + "%";
                }
                color = ContextCompat.getColor(context, R.color.text_price_up);
            } else {
                if (isRate) {
                    str = str + "%";
                }
                color = ContextCompat.getColor(context, R.color.text_price_down);
            }
            view.setText(str);
            view.setTextColor(color);
        }
    }

    private void resetRadion(View view) {
        Resources res = this.getResources();
        switch (view.getId()) {
            case R.id.radionButton01:
                mRadionButton01.setBackground(res.getDrawable(R.drawable.investment_tab_left));
                mRadionButton02.setBackground(null);
                mRadionButton03.setBackground(null);
                mRadionButton04.setBackground(null);

                mRadionButton01.setTextColor(res.getColor(R.color.white));
                mRadionButton02.setTextColor(res.getColor(R.color.marker_detail_button_textsize_color));
                mRadionButton03.setTextColor(res.getColor(R.color.marker_detail_button_textsize_color));
                mRadionButton04.setTextColor(res.getColor(R.color.marker_detail_button_textsize_color));
                TYPE = "HOUR";
                step = 5 * 60;
                chartDataSize = 72;
                break;
            case R.id.radionButton02:
                mRadionButton01.setBackground(null);
                mRadionButton02.setBackground(res.getDrawable(R.drawable.investment_tab_mid));
                mRadionButton03.setBackground(null);
                mRadionButton04.setBackground(null);

                mRadionButton01.setTextColor(res.getColor(R.color.marker_detail_button_textsize_color));
                mRadionButton02.setTextColor(res.getColor(R.color.white));
                mRadionButton03.setTextColor(res.getColor(R.color.marker_detail_button_textsize_color));
                mRadionButton04.setTextColor(res.getColor(R.color.marker_detail_button_textsize_color));
                TYPE = "DAY";
                step = 30 * 60;
                chartDataSize = 72;
                break;
            case R.id.radionButton03:
                mRadionButton01.setBackground(null);
                mRadionButton02.setBackground(null);
                mRadionButton03.setBackground(res.getDrawable(R.drawable.investment_tab_mid));
                mRadionButton04.setBackground(null);

                mRadionButton01.setTextColor(res.getColor(R.color.marker_detail_button_textsize_color));
                mRadionButton02.setTextColor(res.getColor(R.color.marker_detail_button_textsize_color));
                mRadionButton03.setTextColor(res.getColor(R.color.white));
                mRadionButton04.setTextColor(res.getColor(R.color.marker_detail_button_textsize_color));

                TYPE = "WEEK";
                step = 2 * 60 * 60;
                chartDataSize = 72;
                break;
            case R.id.radionButton04:
                mRadionButton01.setBackground(null);
                mRadionButton02.setBackground(null);
                mRadionButton03.setBackground(null);
                mRadionButton04.setBackground(res.getDrawable(R.drawable.investment_tab_right));

                mRadionButton01.setTextColor(res.getColor(R.color.marker_detail_button_textsize_color));
                mRadionButton02.setTextColor(res.getColor(R.color.marker_detail_button_textsize_color));
                mRadionButton03.setTextColor(res.getColor(R.color.marker_detail_button_textsize_color));
                mRadionButton04.setTextColor(res.getColor(R.color.white));

                TYPE = "MONTH";
                step = 12 * 60 * 60;
                chartDataSize = 72;
                break;
        }

    }

    @OnClick({R.id.radionButton01, R.id.radionButton02, R.id.radionButton03,
            R.id.radionButton04, R.id.image_full, R.id.market_linechart})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.radionButton01:
            case R.id.radionButton02:
            case R.id.radionButton03:
            case R.id.radionButton04:
                resetRadion(v);
                getMarketData();
                break;
            case R.id.image_full:
//                UIHelper.showKDiagramNewActivity(context, currencyType, exchangeType);
                break;
            case R.id.market_linechart:
//                UIHelper.showKDiagramNewActivity(context, currencyType, exchangeType);
                break;

        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        stopTimer();
    }

    /**
     * 生成图表显示的数据
     *
     * @param data1
     */
    private void createChartDataSet(String[][] data1, String current, final lecho.lib.hellocharts.view.LineChartView chart, int color) {
        int showData = 6;
        List<Line> lines = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        List<PointValue> values = new ArrayList<>();
        List<AxisValue> axisValues = new ArrayList<>();
        float maxValue = 0;
        float miniValue = 0;
        if (data1 != null && data1.length > 0) {
            for (int j = 0; j < data1.length; ++j) {
                float price = Float.parseFloat(data1[data1.length - j - 1][4]);

                if (price > maxValue || j == 0) {
                    maxValue = price;
                }
                if (price < miniValue || j == 0) {
                    miniValue = price;
                }
                values.add(new PointValue(j, Float.parseFloat(data1[data1.length - j - 1][4])));//设置线的数据
                //subvalue.add(new SubcolumnValue(Float.parseFloat(data1[j][1]), Color.BLUE));// 设置柱子数据并为其设置颜色
                Long time = Long.parseLong(data1[data1.length - j - 1][0]) * 1000;
                // if(j%2==1){
                switch (TYPE) {
                    case "HOUR":
                        sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
                        break;
                    case "DAY":
                        sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
                        break;
                    case "WEEK":
                        sdf = new SimpleDateFormat("MM-dd", Locale.getDefault());
                        break;
                    case "MONTH":
                        sdf = new SimpleDateFormat("MM-dd", Locale.getDefault());
                        break;
                }

                if (data1.length >= showData) {
                    long lt = time;
                    String x = sdf.format(new Date(lt));
                    AxisValue mAxisValue = new AxisValue(j);
                    mAxisValue.setLabel(x);
                    axisValues.add(mAxisValue);
                } else {
                    long lt = time;
                    String x = sdf.format(new Date(lt));
                    axisValues.add(new AxisValue(j).setLabel(x));
                }
            }
            values.add(new PointValue(data1.length, Float.parseFloat(current)));//设置线的数据
        }
        Line line = new Line(values);
        line.setColor(color);//设置线颜色
        line.setHasPoints(false);//设置是否显示点
        line.setFilled(false);//设置是否填充
        line.setCubic(false);
        line.setStrokeWidth(1); // 设置线宽
        lines.add(line);
        LineChartData Linedata = new LineChartData(lines);//将线赋值给linedata
//        Axis AxisX = new Axis(axisValues).setHasLines(false);
//        AxisX.setTextSize(15);
//        Linedata.setAxisXBottom(AxisX);
        Axis axisY = new Axis().setHasLines(true).setInside(false);
        axisY.setTextSize(10).setMaxLabelChars(6);
        axisY.setTextColor(ContextCompat.getColor(context, R.color.text_color_gray));
        Linedata.setAxisYRight(axisY);
        chart.setZoomEnabled(false);
        chart.setScrollEnabled(false);
        chart.setLineChartData(Linedata);
        resetViewport(chart, values.size());
        //  chart.setOnClickListener(this);
        chart.setVisibility(View.VISIBLE);

    }

    private void resetViewport(LineChartView chart, int size) {
        L.d(size + "");
        // Reset viewport height range to (0,100)
        final Viewport v = new Viewport(chart.getMaximumViewport());
        v.bottom = v.bottom - (v.bottom * 0.1f);
        v.top = v.top * 1.1f;
        v.left = size - (chartDataSize + 1);
        v.right = size;
        chart.setMaximumViewport(v);
        chart.setCurrentViewport(v);
    }

    private static String dateFormatdateFormat1(String date, SimpleDateFormat sdf) {
        try {
            if (date == null || "".equals(date))
                return "";
            if (isNumeric(date))
                return sdf.format(Long.parseLong(date));
            else
                return date;
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 确定是否是时间戳
     *
     * @param str
     * @return
     */
    private static boolean isNumeric(String str) {
        if (str == null || "".equals(str))
            return false;
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if (!isNum.matches()) {
            return false;
        }
        return true;

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        context = getActivity();

    }


    /**
     * 定时器任务
     */
    class TransTimerTask extends TimerTask {

        private Handler mHandler;

        public TransTimerTask(Handler handler) {
            this.mHandler = handler;
        }

        @Override
        public void run() {
            // 需要做的事:发送消息
            Message message = new Message();
            message.what = 1;
            this.mHandler.sendMessage(message);
        }
    }

    ;
    private boolean hidden;

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        this.hidden = hidden;
        if (!hidden) {
            startTimer();
        } else {
            stopTimer();
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        if (!hidden) {
            startTimer();
        }
    }

    Handler handlerOfTrans = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                getMarketData();
            }
            super.handleMessage(msg);
        }

        ;
    };

    /**
     * 启动定时器
     */
    public void startTimer() {
        if (timerChart == null) {
            timerChart = new Timer();
            ChartOldFragment.TransTimerTask task = new TransTimerTask(handlerOfTrans);
            timerChart.schedule(task, 0, dataRefreshTime);
        }
    }

    /**
     * 停止定时器
     */
    public void stopTimer() {
        if (timerChart != null) {
            timerChart.cancel();
            timerChart = null;
        }
    }
}
