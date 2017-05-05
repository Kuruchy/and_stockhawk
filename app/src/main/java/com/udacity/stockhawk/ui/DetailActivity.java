package com.udacity.stockhawk.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.StockPref;
import com.udacity.stockhawk.utils.Utility;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import lecho.lib.hellocharts.formatter.SimpleAxisValueFormatter;
import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.listener.ViewportChangeListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.LineChartView;
import lecho.lib.hellocharts.view.PreviewLineChartView;

/**
 * Detail Activity Class.
 *
 * Creates the Detailed view for seeing the Stock's details, showing info data
 * and a Line Chart with Preview.
 * Uses {@link "github.com/lecho/hellochart/"} to simplify chart control.
 */
public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.container, new PlaceholderFragment()).commit();
        }
    }

    /**
     * A fragment containing a line chart and preview line chart.
     */
    public static class PlaceholderFragment extends Fragment {

        private LineChartView chart;
        private PreviewLineChartView previewChart;
        private LineChartData data;

        // Line Properties
        private boolean hasPoints = true;
        private ValueShape shape = ValueShape.CIRCLE;
        private boolean isCubic = true;
        private boolean hasLabelForSelected = true;

        TextView mStockSymbol;
        TextView mStockName;
        TextView mStockPrice;

        public static StockPref mStock;

        /**
         * Deep copy of data.
         */
        private LineChartData previewData;

        public PlaceholderFragment() {
        }

        /**
         * A function to get the values from the stock.
         */
        private static List<PointValue> getValues(){
            String hists[] = mStock.getStockHistory().split("\n");
            List<PointValue> values = new ArrayList<>();

            for (String h : hists){
                long mSec = Long.parseLong(h.split(", ")[0]);
                float val = Float.parseFloat(h.split(", ")[1]);

                values.add(new PointValue(mSec, val));
            }

            return values;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            setHasOptionsMenu(true);
            View rootView = inflater.inflate(R.layout.fragment_detail, container, false);


            Intent intentThatStartedThisActivity = getActivity().getIntent();

            mStockSymbol = (TextView) rootView.findViewById(R.id.tv_stock_symbol);
            mStockName = (TextView) rootView.findViewById(R.id.tv_stock_name);
            mStockPrice = (TextView) rootView.findViewById(R.id.tv_stock_price);

            // If the activity was called with a stock object as extra
            if (intentThatStartedThisActivity != null) {
                if (intentThatStartedThisActivity.hasExtra("stock")) {

                    mStock = intentThatStartedThisActivity.getParcelableExtra("stock");

                    mStockSymbol.setText(mStock.getStockSymbol());
                    mStockName.setText(mStock.getStockName());
                    mStockPrice.setText(Utility.createPrice(mStock.getStockPrice(), true));
                }
            }

            chart = (LineChartView) rootView.findViewById(R.id.chart_primary);
            previewChart = (PreviewLineChartView) rootView.findViewById(R.id.chart_preview);

            // Generate data for previewed chart and copy of that data for preview chart.
            generateDefaultData();

            chart.setLineChartData(data);

            // Disable zoom and scroll for previewed chart, visible chart ranges depends on preview
            // chart viewport
            chart.setZoomEnabled(false);
            chart.setScrollEnabled(false);
            chart.setValueSelectionEnabled(hasLabelForSelected);

            previewChart.setLineChartData(previewData);
            previewChart.setViewportChangeListener(new ViewportListener());

            previewX(false);

            return rootView;
        }

        @Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
            inflater.inflate(R.menu.preview_line_chart, menu);
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == R.id.action_reset) {
                generateDefaultData();
                chart.setLineChartData(data);
                previewChart.setLineChartData(previewData);
                previewX(true);
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
        /**
         * Generates the default data to load to chart
         */
        private void generateDefaultData() {

            List<PointValue> values = getValues();

            String hists[] = mStock.getStockHistory().split("\n");
            List<AxisValue> aVal = new ArrayList<>();
            List<AxisValue> pAVal = new ArrayList<>();

            for (String h : hists) {
                long mSec = Long.parseLong(h.split(", ")[0]);

                pAVal.add(new AxisValue(mSec).setLabel(""));
                aVal.add(new AxisValue(mSec).setLabel(new SimpleDateFormat("MM-yy").format(new Date(mSec))));
            }

            // Primary Data
            Line line = new Line(values);

            line.setHasPoints(hasPoints);
            line.setShape(shape);
            line.setPointColor(ChartUtils.COLOR_BLUE);
            line.setHasLabelsOnlyForSelected(hasLabelForSelected);
            line.setCubic(isCubic);

            List<Line> lines = new ArrayList<>();
            lines.add(line);

            data = new LineChartData(lines);
            data.setAxisXBottom(new Axis().setValues(aVal));
            data.setAxisYLeft(new Axis()
                    .setName("Price in $"));

            // Preview Data
            Line prevLine = new Line(values);

            prevLine.setColor(ChartUtils.DEFAULT_COLOR);
            prevLine.setHasPoints(false);

            List<Line> prevLines = new ArrayList<>();
            prevLines.add(prevLine);

            previewData = new LineChartData(prevLines);
            previewData.setAxisXBottom(new Axis().setValues(pAVal));
            previewData.setAxisYLeft(new Axis());
            previewData.getLines().get(0).setColor(ChartUtils.DEFAULT_DARKEN_COLOR);

        }

        /**
         * Sets zoom to chart and preview.
         *
         */
        private void previewX(boolean animate) {
            Viewport tempViewport = new Viewport(chart.getMaximumViewport());
            float dx = tempViewport.width() / 3;
            tempViewport.inset(dx, 0);
            if (animate) {
                previewChart.setCurrentViewportWithAnimation(tempViewport);
            } else {
                previewChart.setCurrentViewport(tempViewport);
            }
            previewChart.setZoomType(ZoomType.HORIZONTAL);

        }

        /**
         * Viewport listener for preview chart(lower one). in {@link #onViewportChanged(Viewport)} method change
         * viewport of upper chart.
         */
        private class ViewportListener implements ViewportChangeListener {

            @Override
            public void onViewportChanged(Viewport newViewport) {
                // don't use animation, it is unnecessary when using preview chart.
                chart.setCurrentViewport(newViewport);
            }

        }
    }
}
