package MKLAUNCHER.ui.telas;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class TelaRecentes {

    public static class TaskModel {
        public int persistentId;
        public String label;
        public Bitmap thumbnail;
        public Intent baseIntent;

        public TaskModel(int persistentId, String label, Bitmap thumbnail, Intent baseIntent) {
            this.persistentId = persistentId;
            this.label = label;
            this.thumbnail = thumbnail;
            this.baseIntent = baseIntent;
        }
    }

    public static View create(final Context ctx) {
        LinearLayout mainLayout = new LinearLayout(ctx);
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        mainLayout.setBackgroundColor(Color.parseColor("#121212"));
        mainLayout.setLayoutParams(new ViewGroup.LayoutParams(-1, -1));

        // Título da Tela
        TextView tvTitle = new TextView(ctx);
        tvTitle.setText("Aplicativos Recentes");
        tvTitle.setTextSize(22);
        tvTitle.setTypeface(null, Typeface.BOLD);
        tvTitle.setTextColor(Color.WHITE);
        tvTitle.setPadding(32, 32, 32, 16);
        mainLayout.addView(tvTitle);

        List<TaskModel> tasks = getRecentTasks(ctx);

        if (tasks.isEmpty()) {
            TextView tvEmpty = new TextView(ctx);
            tvEmpty.setText("Nenhum aplicativo recente em execução.");
            tvEmpty.setTextColor(Color.GRAY);
            tvEmpty.setTextSize(16);
            tvEmpty.setGravity(Gravity.CENTER);
            tvEmpty.setLayoutParams(new LinearLayout.LayoutParams(-1, -1));
            mainLayout.addView(tvEmpty);
            return mainLayout;
        }

        GridView gridView = new GridView(ctx);
        gridView.setNumColumns(2);
        float density = ctx.getResources().getDisplayMetrics().density;
        int spacing = (int) (12 * density);
        gridView.setHorizontalSpacing(spacing);
        gridView.setVerticalSpacing(spacing);
        gridView.setPadding(spacing, spacing, spacing, spacing);

        RecentsAdapter adapter = new RecentsAdapter(ctx, tasks);
        gridView.setAdapter(adapter);

        mainLayout.addView(gridView);
        return mainLayout;
    }

    private static List<TaskModel> getRecentTasks(Context ctx) {
        List<TaskModel> taskList = new ArrayList<>();
        ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);

        if (am != null) {
            List<ActivityManager.RecentTaskInfo> recentTasks = am.getRecentTasks(15, ActivityManager.RECENT_IGNORE_UNAVAILABLE);
            for (ActivityManager.RecentTaskInfo info : recentTasks) {
                if (info.baseIntent != null) {
                    String label = "App";
                    Bitmap thumb = null;

                    if (info.taskDescription != null) {
                        if (info.taskDescription.getLabel() != null) {
                            label = info.taskDescription.getLabel();
                        }
                        // Utiliza o método público oficial em vez de getInMemoryBitmap()
                        thumb = info.taskDescription.getIcon();
                    }

                    if (label.equals("App") && info.baseIntent.getComponent() != null) {
                        label = info.baseIntent.getComponent().getPackageName();
                    }

                    taskList.add(new TaskModel(info.persistentId, label, thumb, info.baseIntent));
                }
            }
        }
        return taskList;
    }

    private static class RecentsAdapter extends BaseAdapter {
        private final Context context;
        private final List<TaskModel> tasks;

        public RecentsAdapter(Context context, List<TaskModel> tasks) {
            this.context = context;
            this.tasks = tasks;
        }

        @Override public int getCount() { return tasks.size(); }
        @Override public TaskModel getItem(int position) { return tasks.get(position); }
        @Override public long getItemId(int position) { return position; }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final TaskModel task = getItem(position);
            float density = context.getResources().getDisplayMetrics().density;

            LinearLayout card = new LinearLayout(context);
            card.setOrientation(LinearLayout.VERTICAL);
            card.setPadding(16, 16, 16, 16);

            GradientDrawable bg = new GradientDrawable();
            bg.setColor(Color.parseColor("#222222"));
            bg.setCornerRadius(20);
            bg.setStroke(2, Color.parseColor("#33FFFFFF"));
            card.setBackground(bg);

            // Cabeçalho do Card (Nome + Botão Fechar)
            LinearLayout header = new LinearLayout(context);
            header.setOrientation(LinearLayout.HORIZONTAL);
            header.setGravity(Gravity.CENTER_VERTICAL);

            TextView tvName = new TextView(context);
            tvName.setText(task.label);
            tvName.setTextColor(Color.WHITE);
            tvName.setTypeface(null, Typeface.BOLD);
            tvName.setLayoutParams(new LinearLayout.LayoutParams(0, -2, 1.0f));

            TextView btnClose = new TextView(context);
            btnClose.setText("✕");
            btnClose.setTextColor(Color.parseColor("#FF5555"));
            btnClose.setTextSize(18);
            btnClose.setPadding(12, 0, 12, 0);

            header.addView(tvName);
            header.addView(btnClose);

            // Thumbnail / Preview da Tela
            ImageView imgPreview = new ImageView(context);
            int previewHeight = (int) (140 * density);
            imgPreview.setLayoutParams(new LinearLayout.LayoutParams(-1, previewHeight));
            imgPreview.setPadding(0, 12, 0, 0);

            if (task.thumbnail != null) {
                imgPreview.setImageBitmap(task.thumbnail);
                imgPreview.setScaleType(ImageView.ScaleType.CENTER_CROP);
            } else {
                imgPreview.setBackgroundColor(Color.parseColor("#1A1A1A"));
            }

            card.addView(header);
            card.addView(imgPreview);

            // Ação de Reabrir a Task
            card.setOnClickListener(v -> {
                if (task.baseIntent != null) {
                    task.baseIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(task.baseIntent);
                }
            });

            // Ação de Encerrar a Task (Fechar)
            btnClose.setOnClickListener(v -> {
                ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
                if (am != null) {
                    try {
                        java.lang.reflect.Method method = ActivityManager.class.getMethod("removeTask", int.class);
                        method.invoke(am, task.persistentId);
                    } catch (Exception e) {
                        Toast.makeText(context, "Encerrado", Toast.LENGTH_SHORT).show();
                    }
                    tasks.remove(position);
                    notifyDataSetChanged();
                }
            });

            return card;
        }
    }
}
