package MKLAUNCHER.ui.icones;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

public class IconeConfiguracoes extends Drawable {

    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public IconeConfiguracoes() {
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL);
    }

    @Override
    public void draw(Canvas canvas) {
        Rect bounds = getBounds();
        if (bounds.isEmpty()) return;

        float cx = bounds.exactCenterX();
        float cy = bounds.exactCenterY();
        float radius = Math.min(bounds.width(), bounds.height()) / 2f * 0.85f;

        if (radius <= 0) return;

        Path path = new Path();

        int numTeeth = 6;            // Número de dentes da engrenagem
        float outerRadius = radius;         // Raio externo dos dentes
        float innerRadius = radius * 0.72f; // Raio base do corpo
        float holeRadius  = radius * 0.32f; // Raio do furo central

        // Desenha a engrenagem externa com dentes intercalados
        for (int i = 0; i < numTeeth; i++) {
            double angle1 = Math.toRadians(i * 360.0 / numTeeth - 12);
            double angle2 = Math.toRadians(i * 360.0 / numTeeth - 6);
            double angle3 = Math.toRadians(i * 360.0 / numTeeth + 6);
            double angle4 = Math.toRadians(i * 360.0 / numTeeth + 12);

            float x1 = cx + (float) (innerRadius * Math.cos(angle1));
            float y1 = cy + (float) (innerRadius * Math.sin(angle1));

            float x2 = cx + (float) (outerRadius * Math.cos(angle2));
            float y2 = cy + (float) (outerRadius * Math.sin(angle2));

            float x3 = cx + (float) (outerRadius * Math.cos(angle3));
            float y3 = cy + (float) (outerRadius * Math.sin(angle3));

            float x4 = cx + (float) (innerRadius * Math.cos(angle4));
            float y4 = cy + (float) (innerRadius * Math.sin(angle4));

            if (i == 0) {
                path.moveTo(x1, y1);
            } else {
                path.lineTo(x1, y1);
            }

            path.lineTo(x2, y2);
            path.lineTo(x3, y3);
            path.lineTo(x4, y4);
        }
        path.close();

        // Recorta o furo do centro usando a regra WINDING/EVEN_ODD
        Path holePath = new Path();
        holePath.addCircle(cx, cy, holeRadius, Path.Direction.CCW);
        path.addPath(holePath);

        canvas.drawPath(path, paint);
    }

    @Override
    public void setAlpha(int alpha) {
        paint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
        paint.setColorFilter(colorFilter);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }
}
