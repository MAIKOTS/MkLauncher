package MKLAUNCHER.ui.icones;

import android.graphics.*;
import android.graphics.drawable.Drawable;

public class IconeRecentes extends Drawable {
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public IconeRecentes() {
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(6); // Espessura igual à de Widgets
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeJoin(Paint.Join.ROUND);
    }

    @Override
    public void draw(Canvas canvas) {
        Rect b = getBounds();
        float radius = b.width() / 3.2f;

        // Limites do arco circular
        RectF arc = new RectF(
                b.centerX() - radius,
                b.centerY() - radius,
                b.centerX() + radius,
                b.centerY() + radius
        );

        // Desenha 270 graus do arco (começando do topo, sentido horário)
        canvas.drawArc(arc, -90, 270, false, paint);

        // Desenha a seta na ponta superior
        float arrowX = b.centerX();
        float arrowY = b.centerY() - radius;
        float arrowSize = radius * 0.35f;

        Path arrowPath = new Path();
        arrowPath.moveTo(arrowX - arrowSize, arrowY - (arrowSize * 0.5f));
        arrowPath.lineTo(arrowX, arrowY);
        arrowPath.lineTo(arrowX - arrowSize, arrowY + (arrowSize * 0.8f));

        canvas.drawPath(arrowPath, paint);
    }

    @Override public int getIntrinsicWidth() { return 100; }
    @Override public int getIntrinsicHeight() { return 100; }
    @Override public void setAlpha(int a) { paint.setAlpha(a); }
    @Override public void setColorFilter(ColorFilter cf) { paint.setColorFilter(cf); }
    @Override public int getOpacity() { return PixelFormat.TRANSLUCENT; }
}
