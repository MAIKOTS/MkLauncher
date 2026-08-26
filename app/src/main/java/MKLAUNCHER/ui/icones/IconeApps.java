package MKLAUNCHER.ui.icones;

import android.graphics.*;
import android.graphics.drawable.Drawable;

public class IconeApps extends Drawable {
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public IconeApps() {
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL);
    }

    @Override
    public void draw(Canvas canvas) {
        Rect b = getBounds();
        float w = b.width() / 2.8f; // Ajustado para ser levemente menor e caber melhor
        float gap = 8f; // Espaçamento entre os quadrados
        
        // Desenha os 4 quadrados
        canvas.drawRect(b.centerX() - w - gap, b.centerY() - w - gap, b.centerX() - gap, b.centerY() - gap, paint);
        canvas.drawRect(b.centerX() + gap, b.centerY() - w - gap, b.centerX() + w + gap, b.centerY() - gap, paint);
        canvas.drawRect(b.centerX() - w - gap, b.centerY() + gap, b.centerX() - gap, b.centerY() + w + gap, paint);
        canvas.drawRect(b.centerX() + gap, b.centerY() + gap, b.centerX() + w + gap, b.centerY() + w + gap, paint);
    }

    @Override public int getIntrinsicWidth() { return 100; }
    @Override public int getIntrinsicHeight() { return 100; }
    @Override public void setAlpha(int a) { paint.setAlpha(a); }
    @Override public void setColorFilter(ColorFilter cf) { paint.setColorFilter(cf); }
    @Override public int getOpacity() { return PixelFormat.TRANSLUCENT; }
}
