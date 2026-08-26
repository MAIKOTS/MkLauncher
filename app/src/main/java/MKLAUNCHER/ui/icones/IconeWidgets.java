package MKLAUNCHER.ui.icones;

import android.graphics.*;
import android.graphics.drawable.Drawable;

public class IconeWidgets extends Drawable {
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public IconeWidgets() {
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(6);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeJoin(Paint.Join.ROUND);
    }

    @Override
    public void draw(Canvas canvas) {
        Rect b = getBounds();
        
        // Tamanho total da grade centralizada
        float size = b.width() * 0.65f;
        float left = b.centerX() - (size / 2f);
        float top = b.centerY() - (size / 2f);
        
        float gap = 6f; // Espaçamento entre os widgets
        float cornerRadius = 8f; // Arredondamento dos cantos
        
        float halfWidth = (size - gap) / 2f;
        float bigHeight = (size - gap) * 0.58f;
        float smallHeight = (size - gap) * 0.42f;

        // 1. Bloco Superior Esquerdo (Grande)
        RectF rectTopLeft = new RectF(
                left, 
                top, 
                left + halfWidth, 
                top + bigHeight
        );
        canvas.drawRoundRect(rectTopLeft, cornerRadius, cornerRadius, paint);

        // 2. Bloco Inferior Esquerdo (Pequeno)
        RectF rectBottomLeft = new RectF(
                left, 
                top + bigHeight + gap, 
                left + halfWidth, 
                top + size
        );
        canvas.drawRoundRect(rectBottomLeft, cornerRadius, cornerRadius, paint);

        // 3. Bloco Superior Direito (Pequeno)
        RectF rectTopRight = new RectF(
                left + halfWidth + gap, 
                top, 
                left + size, 
                top + smallHeight
        );
        canvas.drawRoundRect(rectTopRight, cornerRadius, cornerRadius, paint);

        // 4. Bloco Inferior Direito (Grande)
        RectF rectBottomRight = new RectF(
                left + halfWidth + gap, 
                top + smallHeight + gap, 
                left + size, 
                top + size
        );
        canvas.drawRoundRect(rectBottomRight, cornerRadius, cornerRadius, paint);
    }

    @Override public int getIntrinsicWidth() { return 100; }
    @Override public int getIntrinsicHeight() { return 100; }
    @Override public void setAlpha(int a) { paint.setAlpha(a); }
    @Override public void setColorFilter(ColorFilter cf) { paint.setColorFilter(cf); }
    @Override public int getOpacity() { return PixelFormat.TRANSLUCENT; }
}
