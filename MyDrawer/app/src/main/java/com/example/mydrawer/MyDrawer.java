package com.example.mydrawer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.View;

public class MyDrawer extends View {
    private Paint paint = new Paint();

    public MyDrawer(Context context) {
        super(context);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawColor(Color.LTGRAY);

        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;
        int size = 400;
        int halfSize = size / 2;

        Path triangle = new Path();
        triangle.moveTo(centerX, centerY - halfSize);
        triangle.lineTo(centerX - halfSize, centerY + halfSize);
        triangle.lineTo(centerX + halfSize, centerY + halfSize);
        triangle.close();

        paint.setColor(Color.BLUE);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawPath(triangle, paint);

        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(10);
        canvas.drawPath(triangle, paint);

        paint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(centerX, centerY, 20, paint);

        paint.setStrokeWidth(15);
        canvas.drawLine(centerX, centerY + 20, centerX, centerY + 80, paint);
        canvas.drawLine(centerX, centerY + 80, centerX - 30, centerY + 130, paint);
        canvas.drawLine(centerX, centerY + 80, centerX + 30, centerY + 130, paint);

        canvas.drawLine(centerX, centerY + 40, centerX - 40, centerY + 60, paint);
        canvas.drawLine(centerX, centerY + 40, centerX + 40, centerY + 60, paint);

        paint.setStrokeWidth(20);
        for (int i = -2; i <= 2; i++) {
            canvas.drawLine(centerX - 80 + i * 40, centerY + 150, centerX - 80 + i * 40, centerY + 190, paint);
        }
    }
}