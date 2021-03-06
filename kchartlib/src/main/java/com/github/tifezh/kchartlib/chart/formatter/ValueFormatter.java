package com.github.tifezh.kchartlib.chart.formatter;

import com.github.tifezh.kchartlib.chart.impl.IValueFormatter;

/**
 * Value格式化类
 * Created by tifezh on 2016/6/21.
 */

public class ValueFormatter implements IValueFormatter {
    @Override
    public String format(float value, int precision) {
        return String.format("%." + precision + "f", value);
    }
}
