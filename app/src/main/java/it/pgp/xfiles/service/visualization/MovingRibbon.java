package it.pgp.xfiles.service.visualization;

/**
 * Created by pgp on 10/07/17
 * Overlay progress bar (in addition to the foreground service notification)
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import it.pgp.xfiles.R;
import it.pgp.xfiles.utils.Pair;

public class MovingRibbon extends ProgressIndicator {

    public ProgressBar pb;
    public TextView pbSpeed;
    public TextView pbDataAmount;

    public long lastProgressTime;
    public Pair<Long,Long> lastProgress;

    public MovingRibbon(final Context context) {
        super(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        oView = inflater.inflate(R.layout.ribbon_one, null);

        pb = oView.findViewById(R.id.pbInner);
        pbSpeed = oView.findViewById(R.id.pbSpeed);
        pbDataAmount = oView.findViewById(R.id.pbDataAmount);
        lastProgressTime = System.currentTimeMillis();

        pb.setMax(100);
        pb.setIndeterminate(false);
        pb.setBackgroundColor(0x6c00ff00);

        oView.setOnTouchListener(this);

        addViewToOverlay(oView, getDpHeightAdjustedParams(BASE_RIBBON_DP*2, ViewType.CONTAINER));

        topLeftView = new View(context);
        addViewToOverlay(topLeftView, ViewType.ANCHOR.getParams());
    }

    @Override
    public void setProgress(Pair<Long,Long>... values) {
        pb.setProgress((int) Math.round(values[0].i * 100.0 / values[0].j));
        if(lastProgress == null) {
            lastProgressTime = System.currentTimeMillis();
            lastProgress = values[0];
            pbSpeed.setText("0 Mbps");
        }
        else {
            long dt = lastProgressTime;
            lastProgressTime = System.currentTimeMillis();
            dt = lastProgressTime - dt;

            long ds = lastProgress.i;
            lastProgress = values[0];
            ds = lastProgress.i - ds;

            double speedMbps = ds/(dt*1000.0);
            // TODO can we have a single TextView, and a single String.format aligning speed at the start and data amount at line end?
            pbSpeed.setText(String.format("%.2f Mbps",speedMbps));
            pbDataAmount.setText(String.format("%.2f Mb",lastProgress.i/1000000.0));
        }
    }
}
